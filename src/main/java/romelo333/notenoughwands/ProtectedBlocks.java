package romelo333.notenoughwands;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
import org.apache.commons.lang3.tuple.Pair;
import romelo333.notenoughwands.varia.Tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// @todo fabric
public class ProtectedBlocks /*extends AbstractWorldData<ProtectedBlocks>*/ {

    private static final String NAME = "NEWProtectedBlocks";

    // Persisted data
    private Map<GlobalCoordinate, Integer> blocks = new HashMap<>();       // Map from coordinate -> ID

    // Cache which caches the protected blocks per dimension and per chunk position.
    private Map<Pair<Integer, ChunkPos>,Set<BlockPos>> perDimPerChunkCache = new HashMap<>();

    private Map<Integer,Integer> counter = new HashMap<>(); // Keep track of number of protected blocks per ID
    private int lastId = 1;

    // Client side protected blocks.
    public static int clientSideWorld = Integer.MAX_VALUE;
    public static Map<ChunkPos, Set<BlockPos>> clientSideProtectedBlocks = new HashMap<>();

    public ProtectedBlocks(String name) {
//        super(name);
    }

//    @Override
    public void clear() {
        blocks.clear();
        perDimPerChunkCache.clear();
        counter.clear();
        lastId = -1;
    }

    public static ProtectedBlocks getProtectedBlocks(World world){
//        return getData(world, ProtectedBlocks.class, NAME);
        return null;    // @todo fabric
    }

    public static boolean isProtectedClientSide(World world, BlockPos pos){
        ChunkPos chunkPos = new ChunkPos(pos);
        if (!clientSideProtectedBlocks.containsKey(chunkPos)) {
            return false;
        }
        Set<BlockPos> positions = clientSideProtectedBlocks.get(chunkPos);
        return positions.contains(pos);
    }

    protected void save() {
        // @todo fabric: dummy
    }

    public int getNewId() {
        lastId++;
        save();
        return lastId-1;
    }

    private void decrementProtection(Integer oldId) {
        int cnt = counter.getOrDefault(oldId, 0);
        cnt--;
        counter.put(oldId, cnt);
    }

    private void incrementProtection(Integer newId) {
        int cnt = counter.getOrDefault(newId, 0);
        cnt++;
        counter.put(newId, cnt);
    }

    public int getProtectedBlockCount(int id) {
        return counter.getOrDefault(id, 0);
    }

    private int getMaxProtectedBlocks(int id) {
        if (id == -1) {
            return ModItems.masterProtectionWand.maximumProtectedBlocks;
        } else {
            return ModItems.protectionWand.maximumProtectedBlocks;
        }
    }

    public boolean protect(PlayerEntity player, World world, BlockPos pos, int id) {
        GlobalCoordinate key = new GlobalCoordinate(pos, world.dimension.getType().getRawId());
        if (id != -1 && blocks.containsKey(key)) {
            Tools.error(player, "This block is already protected!");
            return false;
        }
        if (blocks.containsKey(key)) {
            // Block is protected but we are using the master wand so we first clear the protection.
            decrementProtection(blocks.get(key));
        }

        int max = getMaxProtectedBlocks(id);
        if (max != 0 && getProtectedBlockCount(id) >= max) {
            Tools.error(player, "Maximum number of protected blocks reached!");
            return false;
        }

        blocks.put(key, id);
        clearCache(key);

        incrementProtection(id);

        save();
        return true;
    }

    public boolean unprotect(PlayerEntity player, World world, BlockPos pos, int id) {
        GlobalCoordinate key = new GlobalCoordinate(pos, world.dimension.getType().getRawId());
        if (!blocks.containsKey(key)) {
            Tools.error(player, "This block is not protected!");
            return false;
        }
        if (id != -1 && blocks.get(key) != id) {
            Tools.error(player, "You have no permission to unprotect this block!");
            return false;
        }
        decrementProtection(blocks.get(key));
        blocks.remove(key);
        clearCache(key);
        save();
        return true;
    }

    public int clearProtections(World world, int id) {
        Set<GlobalCoordinate> toRemove = new HashSet<GlobalCoordinate>();
        for (Map.Entry<GlobalCoordinate, Integer> entry : blocks.entrySet()) {
            if (entry.getValue() == id) {
                toRemove.add(entry.getKey());
            }
        }

        int cnt = 0;
        for (GlobalCoordinate coordinate : toRemove) {
            cnt++;
            blocks.remove(coordinate);
            clearCache(coordinate);
        }
        counter.put(id, 0);

        save();
        return cnt;
    }

    public boolean isProtected(World world, BlockPos pos){
        return blocks.containsKey(new GlobalCoordinate(pos, world.dimension.getType().getRawId()));
    }

    public boolean hasProtections() {
        return !blocks.isEmpty();
    }

    public void fetchProtectedBlocks(Set<BlockPos> coordinates, World world, int x, int y, int z, float radius, int id) {
        radius *= radius;
        for (Map.Entry<GlobalCoordinate, Integer> entry : blocks.entrySet()) {
            if (entry.getValue() == id || (id == -2 && entry.getValue() != -1)) {
                GlobalCoordinate block = entry.getKey();
                if (block.getDimension() == world.dimension.getType().getRawId()) {
                    BlockPos c = block.getCoordinate();
                    float sqdist = (x - c.getX()) * (x - c.getX()) + (y - c.getY()) * (y - c.getY()) + (z - c.getZ()) * (z - c.getZ());
                    if (sqdist < radius) {
                        coordinates.add(c);
                    }
                }
            }
        }
    }

    private void clearCache(GlobalCoordinate pos) {
        ChunkPos chunkpos = new ChunkPos(pos.getCoordinate());
        perDimPerChunkCache.remove(Pair.of(pos.getDimension(), chunkpos));
    }

    public Map<ChunkPos,Set<BlockPos>> fetchProtectedBlocks(World world, BlockPos pos) {
        Map<ChunkPos,Set<BlockPos>> result = new HashMap<>();
        ChunkPos chunkpos = new ChunkPos(pos);

        fetchProtectedBlocks(result, world, new ChunkPos(chunkpos.x-1, chunkpos.z-1));
        fetchProtectedBlocks(result, world, new ChunkPos(chunkpos.x  , chunkpos.z-1));
        fetchProtectedBlocks(result, world, new ChunkPos(chunkpos.x+1, chunkpos.z-1));
        fetchProtectedBlocks(result, world, new ChunkPos(chunkpos.x-1, chunkpos.z  ));
        fetchProtectedBlocks(result, world, new ChunkPos(chunkpos.x  , chunkpos.z  ));
        fetchProtectedBlocks(result, world, new ChunkPos(chunkpos.x+1, chunkpos.z  ));
        fetchProtectedBlocks(result, world, new ChunkPos(chunkpos.x-1, chunkpos.z+1));
        fetchProtectedBlocks(result, world, new ChunkPos(chunkpos.x  , chunkpos.z+1));
        fetchProtectedBlocks(result, world, new ChunkPos(chunkpos.x+1, chunkpos.z+1));

        return result;
    }

    public void fetchProtectedBlocks(Map<ChunkPos,Set<BlockPos>> allresults, World world, ChunkPos chunkpos) {
        Pair<Integer, ChunkPos> key = Pair.of(world.dimension.getType().getRawId(), chunkpos);
        if (perDimPerChunkCache.containsKey(key)) {
            allresults.put(chunkpos, perDimPerChunkCache.get(key));
            return;
        }

        Set<BlockPos> result = new HashSet<>();

        for (Map.Entry<GlobalCoordinate, Integer> entry : blocks.entrySet()) {
            GlobalCoordinate block = entry.getKey();
            if (block.getDimension() == world.dimension.getType().getRawId()) {
                ChunkPos bc = new ChunkPos(block.getCoordinate());
                if (bc.equals(chunkpos)) {
                    result.add(block.getCoordinate());
                }
            }
        }
        allresults.put(chunkpos, result);
        perDimPerChunkCache.put(key, result);
    }

//    @Override
    public void readFromNBT(CompoundTag tagCompound) {
        lastId = tagCompound.getInt("lastId");
        blocks.clear();
        perDimPerChunkCache.clear();;
        counter.clear();
        ListTag list = tagCompound.getList("blocks", 10);
        for (int i = 0; i<list.size();i++){
            CompoundTag tc = list.getCompoundTag(i);
            GlobalCoordinate block = new GlobalCoordinate(new BlockPos(tc.getInt("x"),tc.getInt("y"),tc.getInt("z")),tc.getInt("dim"));
            int id = tc.getInt("id");
            blocks.put(block, id);
            incrementProtection(id);
        }
    }

//    @Override
    public CompoundTag writeToNBT(CompoundTag tagCompound) {
        tagCompound.putInt("lastId", lastId);
        ListTag list = new ListTag();
        for (Map.Entry<GlobalCoordinate, Integer> entry : blocks.entrySet()) {
            GlobalCoordinate block = entry.getKey();
            CompoundTag tc = new CompoundTag();
            tc.putInt("x", block.getCoordinate().getX());
            tc.putInt("y", block.getCoordinate().getY());
            tc.putInt("z", block.getCoordinate().getZ());
            tc.putInt("dim", block.getDimension());
            tc.putInt("id", entry.getValue());
            list.add(tc);
        }
        tagCompound.put("blocks",list);
        return tagCompound;
    }
}
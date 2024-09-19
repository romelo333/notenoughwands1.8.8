package romelo333.notenoughwands.modules.protectionwand;

import mcjty.lib.varia.LevelTools;
import mcjty.lib.worlddata.AbstractWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;
import romelo333.notenoughwands.varia.Tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProtectedBlocks extends AbstractWorldData<ProtectedBlocks> {

    private static final String NAME = "NEWProtectedBlocks";

    // Persisted data
    private Map<GlobalPos, Integer> blocks = new HashMap<>();       // Map from coordinate -> ID

    // Cache which caches the protected blocks per dimension and per chunk position.
    private Map<Pair<ResourceKey<Level>,ChunkPos>,Set<BlockPos>> perDimPerChunkCache = new HashMap<>();

    private Map<Integer,Integer> counter = new HashMap<>(); // Keep track of number of protected blocks per ID
    private int lastId = 1;

    // Client side protected blocks.
    public static ResourceKey<Level> clientSideWorld = null;
    public static Map<ChunkPos, Set<BlockPos>> clientSideProtectedBlocks = new HashMap<>();

    public ProtectedBlocks() {
        super();
    }

    public ProtectedBlocks(CompoundTag tag) {
        lastId = tag.getInt("lastId");
        blocks.clear();
        perDimPerChunkCache.clear();;
        counter.clear();
        ListTag list = tag.getList("blocks", Tag.TAG_COMPOUND);
        for (int i = 0; i<list.size();i++){
            CompoundTag tc = list.getCompound(i);
            String dim = tc.getString("dim");
            GlobalPos block = GlobalPos.of(LevelTools.getId(ResourceLocation.parse(dim)), new BlockPos(tc.getInt("x"),tc.getInt("y"),tc.getInt("z")));
            int id = tc.getInt("id");
            blocks.put(block, id);
            incrementProtection(id);
        }
    }


//    @Override
//    public void clear() {
//        blocks.clear();
//        perDimPerChunkCache.clear();
//        counter.clear();
//        lastId = -1;
//    }

    public static ProtectedBlocks getProtectedBlocks(Level world){
        return getData(world, ProtectedBlocks::new, ProtectedBlocks::new, NAME);
    }

    public static boolean isProtectedClientSide(Level world, BlockPos pos){
        ChunkPos chunkPos = new ChunkPos(pos);
        if (!clientSideProtectedBlocks.containsKey(chunkPos)) {
            return false;
        }
        Set<BlockPos> positions = clientSideProtectedBlocks.get(chunkPos);
        return positions.contains(pos);
    }

    public int getNewId() {
        lastId++;
        save();
        return lastId-1;
    }

    private void decrementProtection(Integer oldId) {
        int cnt = counter.containsKey(oldId) ? counter.get(oldId) : 0;
        cnt--;
        counter.put(oldId, cnt);
    }

    private void incrementProtection(Integer newId) {
        int cnt = counter.containsKey(newId) ? counter.get(newId) : 0;
        cnt++;
        counter.put(newId, cnt);
    }

    public int getProtectedBlockCount(int id) {
        return counter.containsKey(id) ? counter.get(id) : 0;
    }

    private int getMaxProtectedBlocks(int id) {
        if (id == -1) {
            return 0;   // Master protection has no limit
        } else {
            return ProtectionWandConfiguration.maximumProtectedBlocks.get();
        }
    }

    public boolean protect(Player player, Level world, BlockPos pos, int id) {
        GlobalPos key = GlobalPos.of(world.dimension(), pos);
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

    public boolean unprotect(Player player, Level world, BlockPos pos, int id) {
        GlobalPos key = GlobalPos.of(world.dimension(), pos);
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

    public int clearProtections(Level world, int id) {
        Set<GlobalPos> toRemove = new HashSet<GlobalPos>();
        for (Map.Entry<GlobalPos, Integer> entry : blocks.entrySet()) {
            if (entry.getValue() == id) {
                toRemove.add(entry.getKey());
            }
        }

        int cnt = 0;
        for (GlobalPos coordinate : toRemove) {
            cnt++;
            blocks.remove(coordinate);
            clearCache(coordinate);
        }
        counter.put(id, 0);

        save();
        return cnt;
    }

    public boolean isProtected(Level world, BlockPos pos){
        return blocks.containsKey(GlobalPos.of(world.dimension(), pos));
    }

    public boolean hasProtections() {
        return !blocks.isEmpty();
    }

    public void fetchProtectedBlocks(Set<BlockPos> coordinates, Level world, int x, int y, int z, float radius, int id) {
        radius *= radius;
        for (Map.Entry<GlobalPos, Integer> entry : blocks.entrySet()) {
            if (entry.getValue() == id || (id == -2 && entry.getValue() != -1)) {
                GlobalPos block = entry.getKey();
                if (block.dimension().equals(world.dimension())) {
                    BlockPos c = block.pos();
                    float sqdist = (x - c.getX()) * (x - c.getX()) + (y - c.getY()) * (y - c.getY()) + (z - c.getZ()) * (z - c.getZ());
                    if (sqdist < radius) {
                        coordinates.add(c);
                    }
                }
            }
        }
    }

    private void clearCache(GlobalPos pos) {
        ChunkPos chunkpos = new ChunkPos(pos.pos());
        perDimPerChunkCache.remove(Pair.of(pos.dimension(), chunkpos));
    }

    public Map<ChunkPos,Set<BlockPos>> fetchProtectedBlocks(Level world, BlockPos pos) {
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

    public void fetchProtectedBlocks(Map<ChunkPos,Set<BlockPos>> allresults, Level world, ChunkPos chunkpos) {
        Pair<ResourceKey<Level>, ChunkPos> key = Pair.of(world.dimension(), chunkpos);
        if (perDimPerChunkCache.containsKey(key)) {
            allresults.put(chunkpos, perDimPerChunkCache.get(key));
            return;
        }

        Set<BlockPos> result = new HashSet<>();

        for (Map.Entry<GlobalPos, Integer> entry : blocks.entrySet()) {
            GlobalPos block = entry.getKey();
            if (block.dimension().equals(world.dimension())) {
                ChunkPos bc = new ChunkPos(block.pos());
                if (bc.equals(chunkpos)) {
                    result.add(block.pos());
                }
            }
        }
        allresults.put(chunkpos, result);
        perDimPerChunkCache.put(key, result);
    }

    //TODO load doesn't exist in McjtyLib
    //@Override


    @Override
    public CompoundTag save(CompoundTag tagCompound, HolderLookup.Provider provider) {
        tagCompound.putInt("lastId", lastId);
        ListTag list = new ListTag();
        for (Map.Entry<GlobalPos, Integer> entry : blocks.entrySet()) {
            GlobalPos block = entry.getKey();
            CompoundTag tc = new CompoundTag();
            tc.putInt("x", block.pos().getX());
            tc.putInt("y", block.pos().getY());
            tc.putInt("z", block.pos().getZ());
            tc.putString("dim", block.dimension().location().toString());
            tc.putInt("id", entry.getValue());
            list.add(tc);
        }
        tagCompound.put("blocks",list);
        return tagCompound;
    }
}
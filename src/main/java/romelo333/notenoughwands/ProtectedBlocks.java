package romelo333.notenoughwands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import romelo333.notenoughwands.varia.GlobalCoordinate;
import romelo333.notenoughwands.varia.Tools;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProtectedBlocks extends WorldSavedData{
    public static final String NAME = "NEWProtectedBlocks";
    private static ProtectedBlocks instance;

    // Persisted data
    private Map<GlobalCoordinate, Integer> blocks = new HashMap<GlobalCoordinate, Integer>();       // Map from coordinate -> ID
    private Map<Integer,Integer> counter = new HashMap<Integer, Integer>(); // Keep track of number of protected blocks per ID
    private int lastId = 1;

    public ProtectedBlocks(String name) {
        super(name);
    }

    public void save (World world){
        world.getMapStorage().setData(NAME, this);
        markDirty();
    }

    public static ProtectedBlocks getProtectedBlocks (World world){
        if (world.isRemote){
            return null;
        }
        if (instance != null){
            return instance;
        }
        instance = (ProtectedBlocks)world.getMapStorage().loadData(ProtectedBlocks.class, NAME);
        if (instance == null){
            instance = new ProtectedBlocks(NAME);
        }
        return instance;
    }

    public int getNewId(World world) {
        lastId++;
        save(world);
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
//        if (id == -1) {
//            return ModItems.masterProtectionWand.maximumProtectedBlocks;
//        } else {
//            return ModItems.protectionWand.maximumProtectedBlocks;
//        }
        // @todo IMPLEMENT THIS AGAIN
        return 0;
    }

    public boolean protect(EntityPlayer player, World world, int x, int y, int z, int id) {
        GlobalCoordinate key = new GlobalCoordinate(x, y, z, world.provider.getDimensionId());
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
        incrementProtection(id);

        save(world);
        return true;
    }

    public boolean unprotect(EntityPlayer player, World world, int x, int y, int z, int id) {
        GlobalCoordinate key = new GlobalCoordinate(x, y, z, world.provider.getDimensionId());
        if (!blocks.containsKey(key)) {
            Tools.error(player, "This block is not prorected!");
            return false;
        }
        if (id != -1 && blocks.get(key) != id) {
            Tools.error(player, "You have no permission to unprotect this block!");
            return false;
        }
        decrementProtection(blocks.get(key));
        blocks.remove(key);
        save(world);
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
        }
        counter.put(id, 0);

        save(world);
        return cnt;
    }

    public boolean isProtected(World world, BlockPos pos){
        return blocks.containsKey(new GlobalCoordinate(pos, world.provider.getDimensionId()));
    }

    public boolean hasProtections() {
        return !blocks.isEmpty();
    }

    public void fetchProtectedBlocks(Set<BlockPos> coordinates, World world, int x, int y, int z, float radius, int id) {
        radius *= radius;
        for (Map.Entry<GlobalCoordinate, Integer> entry : blocks.entrySet()) {
            if (entry.getValue() == id || (id == -2 && entry.getValue() != -1)) {
                GlobalCoordinate block = entry.getKey();
                if (block.getDim() == world.provider.getDimensionId()) {
                    float sqdist = (x - block.getX()) * (x - block.getX()) + (y - block.getY()) * (y - block.getY()) + (z - block.getZ()) * (z - block.getZ());
                    if (sqdist < radius) {
                        coordinates.add(block);
                    }
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        lastId = tagCompound.getInteger("lastId");
        blocks.clear();
        counter.clear();
        NBTTagList list = tagCompound.getTagList("blocks", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i<list.tagCount();i++){
            NBTTagCompound tc = list.getCompoundTagAt(i);
            GlobalCoordinate block = new GlobalCoordinate(tc.getInteger("x"),tc.getInteger("y"),tc.getInteger("z"),tc.getInteger("dim"));
            int id = tc.getInteger("id");
            blocks.put(block, id);
            incrementProtection(id);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("lastId", lastId);
        NBTTagList list = new NBTTagList();
        for (Map.Entry<GlobalCoordinate, Integer> entry : blocks.entrySet()) {
            GlobalCoordinate block = entry.getKey();
            NBTTagCompound tc = new NBTTagCompound();
            tc.setInteger("x", block.getX());
            tc.setInteger("y", block.getY());
            tc.setInteger("z", block.getZ());
            tc.setInteger("dim", block.getDim());
            tc.setInteger("id", entry.getValue());
            list.appendTag(tc);
        }
        tagCompound.setTag("blocks",list);
    }
}
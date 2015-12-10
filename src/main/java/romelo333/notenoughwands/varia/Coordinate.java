package romelo333.notenoughwands.varia;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3i;

public class Coordinate {
    private final int x;
    private final int y;
    private final int z;

    public Coordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coordinate add(EnumFacing dir) {
        Vec3i vec = dir.getDirectionVec();
        return new Coordinate(x + vec.getX(), y + vec.getY(), z + vec.getZ());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coordinate that = (Coordinate) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        return z == that.z;

    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }
}

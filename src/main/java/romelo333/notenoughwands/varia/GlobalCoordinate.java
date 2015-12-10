package romelo333.notenoughwands.varia;

import net.minecraft.util.BlockPos;

public class GlobalCoordinate extends BlockPos {
    private final int dim;

    public GlobalCoordinate(int x, int y, int z, int dim) {
        super(x, y, z);
        this.dim = dim;
    }

    public int getDim() {
        return dim;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GlobalCoordinate that = (GlobalCoordinate) o;

        if (dim != that.dim) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + dim;
        return result;
    }
}
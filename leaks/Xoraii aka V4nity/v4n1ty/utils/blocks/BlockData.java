package v4n1ty.utils.blocks;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockData {

    public final BlockPos pos;
    public final EnumFacing face;

    BlockData(BlockPos pos, EnumFacing face) {
        this.pos = pos;
        this.face = face;
    }
}
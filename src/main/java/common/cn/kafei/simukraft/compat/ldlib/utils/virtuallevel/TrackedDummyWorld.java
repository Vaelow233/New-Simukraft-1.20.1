package common.cn.kafei.simukraft.compat.ldlib.utils.virtuallevel;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TrackedDummyWorld extends com.lowdragmc.lowdraglib.utils.TrackedDummyWorld {
    public boolean setBlockAndUpdate(BlockPos pos, BlockState state) {
        return setBlock(pos, state, 3, 512);
    }
}

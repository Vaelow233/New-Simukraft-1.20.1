package common.cn.kafei.simukraft.compat.ldlib.gui.factory;

import common.cn.kafei.simukraft.compat.ldlib.gui.ui.ModularUI;
import net.minecraft.world.entity.player.Player;

public interface IContainerUIHolder {
    ModularUI createUI(Player player);

    default boolean isStillValid(Player player) {
        return true;
    }
}

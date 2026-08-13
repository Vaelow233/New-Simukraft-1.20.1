package common.cn.kafei.simukraft.compat.ldlib.gui.ui;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import common.cn.kafei.simukraft.compat.ldlib.gui.factory.IContainerUIHolder;
import net.minecraft.world.entity.player.Player;

public final class HolderAdapter implements IUIHolder {
    private final IContainerUIHolder delegate;
    private final Player player;

    public HolderAdapter(IContainerUIHolder delegate, Player player) {
        this.delegate = delegate;
        this.player = player;
    }

    public IContainerUIHolder delegate() {
        return delegate;
    }

    @Override
    public com.lowdragmc.lowdraglib.gui.modular.ModularUI createUI(Player player) {
        return delegate.createUI(player).bindHolder(delegate).unwrap();
    }

    @Override
    public boolean isInvalid() {
        return !delegate.isStillValid(player);
    }

    @Override
    public boolean isRemote() {
        return player != null && player.level().isClientSide();
    }

    @Override
    public void markAsDirty() {
    }
}

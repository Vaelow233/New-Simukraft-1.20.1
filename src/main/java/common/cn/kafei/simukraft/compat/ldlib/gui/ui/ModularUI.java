package common.cn.kafei.simukraft.compat.ldlib.gui.ui;

import com.lowdragmc.lowdraglib.gui.modular.IUIHolder;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import common.cn.kafei.simukraft.compat.ldlib.gui.factory.IContainerUIHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.List;

public final class ModularUI {
    private static final int SERVER_FALLBACK_WIDTH = 384;
    private static final int SERVER_FALLBACK_HEIGHT = 238;

    private final UI ui;
    private IContainerUIHolder holder;
    private com.lowdragmc.lowdraglib.gui.modular.ModularUI delegate;
    private boolean closeOnEsc = true;
    private boolean closeOnInventoryKey = true;
    public final Player player;

    public ModularUI(UI ui) {
        this(ui, Minecraft.getInstance().player);
    }

    public ModularUI(UI ui, Player player) {
        this.ui = ui == null ? UI.empty() : ui;
        this.player = player;
        this.ui.root().attachModularUI(this);
    }

    public static ModularUI of(UI ui, Player player) {
        return new ModularUI(ui, player);
    }

    public ModularUI shouldCloseOnEsc(boolean closeOnEsc) {
        this.closeOnEsc = closeOnEsc;
        return this;
    }

    public ModularUI shouldCloseOnKeyInventory(boolean closeOnInventoryKey) {
        this.closeOnInventoryKey = closeOnInventoryKey;
        return this;
    }

    public boolean shouldCloseOnEsc() {
        return closeOnEsc;
    }

    public boolean shouldCloseOnInventoryKey() {
        return closeOnInventoryKey;
    }

    public ModularUI bindHolder(IContainerUIHolder holder) {
        if (delegate != null) {
            throw new IllegalStateException("Cannot bind a UI holder after the LowDragLib UI was created");
        }
        this.holder = holder;
        return this;
    }

    public UIElement root() {
        return ui.root();
    }

    public void prepare(int screenWidth, int screenHeight) {
        UIElement root = ui.root();
        int width = root.requestedWidth(screenWidth);
        int height = root.requestedHeight(screenHeight);
        width = width < 0 ? screenWidth : width;
        height = height < 0 ? screenHeight : height;
        root.setSelfPosition(new Position(0, 0));
        root.setSize(new Size(width, height));
        root.resolveLayout();
    }

    public com.lowdragmc.lowdraglib.gui.modular.ModularUI unwrap() {
        if (delegate != null) {
            return delegate;
        }
        UIElement root = ui.root();
        int screenWidth = SERVER_FALLBACK_WIDTH;
        int screenHeight = SERVER_FALLBACK_HEIGHT;
        if (player != null && player.level().isClientSide()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft != null && minecraft.getWindow() != null) {
                screenWidth = Math.max(1, minecraft.getWindow().getGuiScaledWidth());
                screenHeight = Math.max(1, minecraft.getWindow().getGuiScaledHeight());
            }
        }
        prepare(screenWidth, screenHeight);
        int width = root.getSizeWidth();
        int height = root.getSizeHeight();
        root.markCompatibilityGroupClientSide();
        IUIHolder actualHolder = holder == null ? IUIHolder.EMPTY : new HolderAdapter(holder, player);
        delegate = new com.lowdragmc.lowdraglib.gui.modular.ModularUI(root, actualHolder, player);
        delegate.registerCloseListener(root::dispose);
        if (width == screenWidth && height == screenHeight) {
            delegate.setFullScreen();
        }
        return delegate;
    }

    public void setHoverTooltip(List<Component> tooltip, ItemStack stack, Font font, Object ingredient) {
        if (delegate != null && delegate.getModularUIGui() != null) {
            delegate.getModularUIGui().setHoverTooltip(tooltip, stack, font,
                    ingredient instanceof TooltipComponent component ? component : null);
        }
    }
}

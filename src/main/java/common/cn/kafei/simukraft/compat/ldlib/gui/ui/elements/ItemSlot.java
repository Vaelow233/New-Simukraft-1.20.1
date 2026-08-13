package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.slot.LocalSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class ItemSlot extends UIElement {
    private final DirectSlotWidget delegate;
    private final SlotStyle slotStyle = new SlotStyle();

    public ItemSlot(Slot slot) {
        delegate = new DirectSlotWidget(slot);
        if (slot instanceof LocalSlot) delegate.setClientSideWidget();
        addWidget(delegate);
    }

    public ItemSlot slotStyle(Consumer<SlotStyle> consumer) {
        consumer.accept(slotStyle);
        delegate.setDrawHoverTips(slotStyle.showItemTooltips);
        if (slotStyle.slotOverlay != null) delegate.setOverlay(slotStyle.slotOverlay);
        return this;
    }

    public Slot getSlot() {
        return delegate.getHandler();
    }

    public ItemSlot setItem(ItemStack stack) {
        return setItem(stack, true);
    }

    public ItemSlot setItem(ItemStack stack, boolean notify) {
        delegate.setItem(stack == null ? ItemStack.EMPTY : stack, notify);
        return this;
    }

    @Override
    protected void afterLayout() {
        delegate.setSelfPosition(new Position(0, 0));
        delegate.setSize(new Size(getSizeWidth(), getSizeHeight()));
    }

    public final class SlotStyle {
        private IGuiTexture slotOverlay = IGuiTexture.EMPTY;
        private boolean showItemTooltips = true;

        public SlotStyle slotOverlay(IGuiTexture value) { slotOverlay = value; return this; }
        public SlotStyle showItemTooltips(boolean value) { showItemTooltips = value; return this; }
    }

    private static final class DirectSlotWidget extends SlotWidget {
        private DirectSlotWidget(Slot slot) {
            super();
            updateSlot(slot);
        }
    }
}

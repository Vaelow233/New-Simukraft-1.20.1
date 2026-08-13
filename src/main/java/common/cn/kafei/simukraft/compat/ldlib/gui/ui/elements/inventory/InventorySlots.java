package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements.inventory;

import common.cn.kafei.simukraft.compat.ldlib.gui.ui.ModularUI;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements.ItemSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventorySlots extends UIElement {
    private final List<ItemSlot> slots = new ArrayList<>();
    private Consumer<ItemSlot> slotConsumer = ignored -> { };
    private boolean initialized;

    public InventorySlots apply(Consumer<ItemSlot> consumer) {
        slotConsumer = consumer == null ? ignored -> { } : consumer;
        slots.forEach(slotConsumer);
        return this;
    }

    @Override
    public void attachModularUI(ModularUI modularUI) {
        if (!initialized && modularUI != null && modularUI.player != null) {
            initialized = true;
            build(modularUI.player.getInventory());
        }
        super.attachModularUI(modularUI);
    }

    private void build(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addInventorySlot(inventory, column + (row + 1) * 9, column * 18, row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            addInventorySlot(inventory, column, column * 18, 58);
        }
    }

    private void addInventorySlot(Inventory inventory, int index, int x, int y) {
        ItemSlot itemSlot = new ItemSlot(new Slot(inventory, index, 0, 0));
        itemSlot.layout(layout -> layout.positionType(dev.vfyjxf.taffy.style.TaffyPosition.ABSOLUTE)
                .left(x).top(y).width(18).height(18));
        slotConsumer.accept(itemSlot);
        slots.add(itemSlot);
        addChild(itemSlot);
    }
}

package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.UIEvents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Tab extends UIElement {
    public final Label text = new Label();
    private Runnable selectedCallback;
    private Runnable unselectedCallback;
    private boolean selected;

    public Tab() {
        text.setAllowHitTest(false);
        text.layout(layout -> layout.positionType(dev.vfyjxf.taffy.style.TaffyPosition.ABSOLUTE)
                .left(0).top(0).widthPercent(100).heightPercent(100));
        addChild(text);
        addEventListener(UIEvents.MOUSE_DOWN, event -> {
            if (event.button == 0) setSelected(true);
        });
    }

    public Tab setText(Component component) { text.setText(component); return this; }
    public Tab setText(String value) { text.setText(value); return this; }
    public Tab setText(String value, boolean translate) { return setText(translate ? Component.translatable(value) : Component.literal(value)); }
    public Tab setDynamicText(Supplier<Component> supplier) { text.setDynamicText(supplier); return this; }
    public Tab textStyle(Consumer<TextStyle> consumer) { text.textStyle(consumer); return this; }
    public Tab tabStyle(Consumer<TabStyle> consumer) { consumer.accept(new TabStyle()); return this; }
    public Tab setOnTabSelected(Runnable callback) { selectedCallback = callback; return this; }
    public Tab setOnTabUnselected(Runnable callback) { unselectedCallback = callback; return this; }

    public void setSelected(boolean selected) {
        if (this.selected == selected) return;
        this.selected = selected;
        if (selected && selectedCallback != null) selectedCallback.run();
        if (!selected && unselectedCallback != null) unselectedCallback.run();
    }

    public boolean isSelected() { return selected; }

    public static final class TabStyle {
    }
}

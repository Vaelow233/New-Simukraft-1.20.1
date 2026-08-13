package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.UIEvent;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.UIEvents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class Button extends UIElement {
    public final Label text = new Label();
    private final ButtonStyle buttonStyle = new ButtonStyle();
    private boolean selected;

    public Button() {
        buttonStyle(style -> style
                .baseTexture(buttonTexture(0xFF52525A, 0xFFAAAAAA))
                .hoverTexture(buttonTexture(0xFF55565B, 0xFFFFFFFF))
                .pressedTexture(buttonTexture(0xFF4779C4, 0xFF9EC4F8))
                .selectedTexture(buttonTexture(0xFF4779C4, 0xFF9EC4F8)));
        text.layout(layout -> layout.positionType(dev.vfyjxf.taffy.style.TaffyPosition.ABSOLUTE)
                .left(0).top(0).widthPercent(100).heightPercent(100));
        text.setAllowHitTest(false);
        addChild(text);
        addEventListener(UIEvents.MOUSE_DOWN, event -> {
            if (event.button == 0 && !selected && buttonStyle.pressedTexture != null) {
                setBackground(buttonStyle.pressedTexture);
            }
        });
        addEventListener(UIEvents.MOUSE_UP, event -> applyRestingTexture());
    }

    @Override
    public Button addClass(String className) {
        super.addClass(className);
        if ("simukraft_large_button".equals(className)) {
            buttonStyle(style -> style
                    .baseTexture(buttonTexture(0xFF49494F, 0xFFAAAAAA))
                    .hoverTexture(buttonTexture(0xFF55565B, 0xFFFFFFFF))
                    .pressedTexture(buttonTexture(0xFF4779C4, 0xFF9EC4F8))
                    .selectedTexture(buttonTexture(0xFF4779C4, 0xFF9EC4F8)));
        } else if ("simukraft_card_button".equals(className)) {
            buttonStyle(style -> style
                    .baseTexture(buttonTexture(0xFF55565B, 0xFFFFFFFF))
                    .hoverTexture(buttonTexture(0xFF62636A, 0xFFFFFFFF))
                    .pressedTexture(buttonTexture(0xFF4779C4, 0xFF9EC4F8))
                    .selectedTexture(buttonTexture(0xFF4779C4, 0xFF9EC4F8)));
        }
        return this;
    }

    public Button setText(Component component) {
        text.setText(component);
        text.setDisplay(true);
        return this;
    }

    public Button setText(String value) {
        text.setText(value);
        text.setDisplay(true);
        return this;
    }

    public Button setText(String value, boolean translate) {
        return setText(translate ? Component.translatable(value) : Component.literal(value));
    }

    public Button noText() {
        text.setDisplay(false);
        return this;
    }

    public Button enableText() {
        text.setDisplay(true);
        return this;
    }

    public Button setOnClick(Consumer<UIEvent> listener) {
        addEventListener(UIEvents.MOUSE_DOWN, event -> {
            if (event.button == 0) {
                listener.accept(event);
                // Some actions replace or rebuild their UI before MOUSE_UP reaches this button.
                // Restore a controlled resting state immediately so the pressed texture cannot stick.
                applyRestingTexture();
            }
        });
        return this;
    }

    public Button setSelected(boolean selected) {
        this.selected = selected;
        applyRestingTexture();
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public Button setOnServerClick(Consumer<UIEvent> listener) {
        return setOnClick(listener);
    }

    public Button textStyle(Consumer<TextStyle> consumer) {
        text.textStyle(consumer);
        return this;
    }

    public Button buttonStyle(Consumer<ButtonStyle> consumer) {
        consumer.accept(buttonStyle);
        applyRestingTexture();
        return this;
    }

    @Override
    public Button setActive(boolean active) {
        super.setActive(active);
        return this;
    }

    public static final class ButtonStyle {
        private IGuiTexture baseTexture;
        private IGuiTexture hoverTexture;
        private IGuiTexture pressedTexture;
        private IGuiTexture selectedTexture;

        public ButtonStyle baseTexture(IGuiTexture value) { baseTexture = value; return this; }
        public ButtonStyle hoverTexture(IGuiTexture value) { hoverTexture = value; return this; }
        public ButtonStyle pressedTexture(IGuiTexture value) { pressedTexture = value; return this; }
        public ButtonStyle selectedTexture(IGuiTexture value) { selectedTexture = value; return this; }
    }

    private void applyRestingTexture() {
        IGuiTexture resting = selected && buttonStyle.selectedTexture != null
                ? buttonStyle.selectedTexture
                : buttonStyle.baseTexture;
        if (resting != null) {
            setBackground(resting);
        }
        IGuiTexture hover = selected && buttonStyle.selectedTexture != null
                ? buttonStyle.selectedTexture
                : buttonStyle.hoverTexture;
        if (hover != null) {
            setHoverTexture(hover);
        }
    }

    private static IGuiTexture buttonTexture(int fill, int border) {
        return new GuiTextureGroup(new ColorRectTexture(fill), new ColorBorderTexture(1, border));
    }
}

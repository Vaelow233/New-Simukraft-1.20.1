package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.UIEvents;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.rendering.GUIContext;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class TextField extends UIElement {
    private final TextFieldWidget delegate;
    private final TextFieldStyle textFieldStyle = new TextFieldStyle();
    private Consumer<String> responder = ignored -> { };
    private String value = "";
    private boolean focused;

    public TextField() {
        delegate = new TextFieldWidget(0, 0, 1, 1, () -> value, this::onValueChanged);
        delegate.setBordered(false);
        delegate.setClientSideWidget();
        addWidget(delegate);
    }

    public TextField textFieldStyle(Consumer<TextFieldStyle> consumer) {
        consumer.accept(textFieldStyle);
        delegate.setTextColor(textFieldStyle.textColor);
        return this;
    }

    public TextFieldStyle getTextFieldStyle() {
        return textFieldStyle;
    }

    public TextField setText(String text) {
        return setText(text, true);
    }

    public TextField setText(String text, boolean notify) {
        value = text == null ? "" : text;
        delegate.setCurrentString(value);
        if (notify) {
            responder.accept(value);
        }
        onRawTextUpdate();
        return this;
    }

    public TextField setValue(String text) {
        return setText(text);
    }

    public TextField setValue(String text, boolean notify) {
        return setText(text, notify);
    }

    public String getValue() {
        String current = delegate.getCurrentString();
        return current == null ? value : current;
    }

    public String getText() {
        return getValue();
    }

    public String getRawText() {
        return getValue();
    }

    public TextField setTextResponder(Consumer<String> responder) {
        this.responder = responder == null ? ignored -> { } : responder;
        delegate.setTextResponder(this::onValueChanged);
        return this;
    }

    public TextField setMaxStringLength(int maxLength) {
        delegate.setMaxStringLength(Math.max(0, maxLength));
        return this;
    }

    public TextField setAnyString() {
        delegate.setValidator(value -> value);
        return this;
    }

    public TextField setTextValidator(Predicate<String> validator) {
        delegate.setValidator(candidate -> validator.test(candidate) ? candidate : value);
        return this;
    }

    public TextField setTextRegexValidator(String regex) {
        return setTextValidator(candidate -> candidate.matches(regex));
    }

    public TextField setNumbersOnlyInt(int min, int max) { delegate.setNumbersOnly(min, max); return this; }
    public TextField setNumbersOnlyLong(long min, long max) { delegate.setNumbersOnly(min, max); return this; }
    public TextField setNumbersOnlyFloat(float min, float max) { delegate.setNumbersOnly(min, max); return this; }
    public TextField setNumbersOnlyDouble(double min, double max) { delegate.setNumbersOnly((float) min, (float) max); return this; }
    public TextField setNumbersOnlyByte(byte min, byte max) { delegate.setNumbersOnly(min, max); return this; }
    public TextField setNumbersOnlyShort(short min, short max) { delegate.setNumbersOnly(min, max); return this; }
    public TextField setWheelDur(float duration) { delegate.setWheelDur(duration); return this; }
    public TextField setWheelDur(int steps, float duration) { delegate.setWheelDur(steps, duration); return this; }
    public TextField setCompoundTagOnly() { delegate.setCompoundTagOnly(); return this; }
    public TextField setResourceLocationOnly() { delegate.setResourceLocationOnly(); return this; }

    public boolean isEditable() {
        return isActive();
    }

    public boolean isFocused() {
        return focused || delegate.isFocus();
    }

    public int getCursorPos() {
        return getRawText().length();
    }

    public int getSelectionStart() {
        return getCursorPos();
    }

    public int getSelectionEnd() {
        return getCursorPos();
    }

    public float getDisplayOffset() {
        return 0.0F;
    }

    public void setCursor(int position) {
    }

    public void setSelection(int start, int end) {
    }

    protected void onRawTextUpdate() {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean focused = isFocused();
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        this.focused = button == 0 && isMouseOverElement(mouseX, mouseY);
        if (!focused && isFocused()) {
            fire(UIEvents.FOCUS, mouseX, mouseY, button, 0);
        } else if (focused && !isFocused()) {
            fire(UIEvents.BLUR, mouseX, mouseY, button, 0);
        }
        return handled;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean handled = super.charTyped(codePoint, modifiers);
        fire(UIEvents.CHAR_TYPED, 0, 0, -1, 0);
        return handled;
    }

    @Override
    public void drawBackgroundAdditional(GUIContext context) {
        int x = Math.round(getPositionX());
        int y = Math.round(getPositionY());
        int width = Math.round(getSizeWidth());
        int height = Math.round(getSizeHeight());
        if (width <= 1 || height <= 1) {
            return;
        }

        int borderColor = isFocused() ? 0xFF9EC4F8 : (isActive() ? 0xFF9A9AA0 : 0xFF68686E);
        context.graphics.fill(x, y, x + width, y + height, borderColor);
        context.graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0xFF202024);

        Component placeholder = textFieldStyle.placeholder;
        if (getValue().isEmpty() && !placeholder.getString().isEmpty()) {
            int textY = y + Math.max(1, (height - context.mc.font.lineHeight) / 2);
            context.graphics.drawString(context.mc.font, placeholder, x + 4, textY, 0xFFA8A8AE, false);
        }
    }

    @Override
    protected void afterLayout() {
        delegate.setSelfPosition(new Position(0, 0));
        delegate.setSize(new Size(getSizeWidth(), getSizeHeight()));
    }

    private void onValueChanged(String next) {
        value = next == null ? "" : next;
        responder.accept(value);
        onRawTextUpdate();
    }

    public final class TextFieldStyle {
        private int textColor = 0xFFFFFFFF;
        private int cursorColor = 0xFFFFFFFF;
        private boolean textShadow;
        private float fontSize = 9.0F;
        private Component placeholder = Component.empty();
        private IGuiTexture focusOverlay = IGuiTexture.EMPTY;

        public TextFieldStyle textColor(int value) { textColor = value; delegate.setTextColor(value); return this; }
        public TextFieldStyle cursorColor(int value) { cursorColor = value; return this; }
        public TextFieldStyle textShadow(boolean value) { textShadow = value; return this; }
        public TextFieldStyle fontSize(float value) { fontSize = value; return this; }
        public TextFieldStyle placeholder(Component value) { placeholder = value == null ? Component.empty() : value; return this; }
        public TextFieldStyle focusOverlay(IGuiTexture value) { focusOverlay = value; return this; }
        public float fontSize() { return fontSize; }
        public Component placeholder() { return placeholder; }
    }
}

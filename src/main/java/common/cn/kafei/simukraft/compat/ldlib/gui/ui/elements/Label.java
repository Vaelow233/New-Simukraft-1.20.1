package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.data.Horizontal;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.data.Vertical;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.rendering.GUIContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Label extends UIElement {
    private Component value = Component.empty();
    private Supplier<Component> dynamicText;
    private final TextStyle textStyle = new TextStyle();

    public Label setText(Component text) {
        value = text == null ? Component.empty() : text;
        dynamicText = null;
        return this;
    }

    public Label setText(String text) {
        return setText(Component.literal(text == null ? "" : text));
    }

    public Label setValue(Component text) {
        return setText(text);
    }

    public Component getValue() {
        return dynamicText == null ? value : dynamicText.get();
    }

    public Label setDynamicText(Supplier<Component> supplier) {
        dynamicText = supplier;
        return this;
    }

    public Label textStyle(Consumer<TextStyle> consumer) {
        consumer.accept(textStyle);
        return this;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    @Override
    public int estimatePreferredWidth(int parentWidth) {
        int requested = requestedWidth(parentWidth);
        if (requested >= 0) {
            return requested;
        }
        return Minecraft.getInstance().font.width(getValue());
    }

    @Override
    public int estimatePreferredHeight(int parentHeight) {
        int requested = requestedHeight(parentHeight);
        if (requested >= 0) {
            return requested;
        }
        return Minecraft.getInstance().font.lineHeight;
    }

    @Override
    public void drawBackgroundAdditional(GUIContext context) {
        Font font = Minecraft.getInstance().font;
        Component text = getValue();
        if (textStyle.textWrap == common.cn.kafei.simukraft.compat.ldlib.gui.ui.data.TextWrap.WRAP) {
            drawWrapped(context, font, text);
            return;
        }
        int textWidth = font.width(text);
        int x = getPositionX();
        int y = getPositionY();
        if (textStyle.horizontal == Horizontal.CENTER) {
            x += Math.max(0, (getSizeWidth() - textWidth) / 2);
        } else if (textStyle.horizontal == Horizontal.RIGHT) {
            x += Math.max(0, getSizeWidth() - textWidth);
        }
        if (textStyle.vertical == Vertical.CENTER) {
            y += Math.max(0, (getSizeHeight() - font.lineHeight) / 2);
        } else if (textStyle.vertical == Vertical.BOTTOM) {
            y += Math.max(0, getSizeHeight() - font.lineHeight);
        }
        context.enableScissor(getPositionX(), getPositionY(), getSizeWidth(), getSizeHeight());
        try {
            if (textStyle.textWrap == common.cn.kafei.simukraft.compat.ldlib.gui.ui.data.TextWrap.HOVER_ROLL
                    && textWidth > getSizeWidth() && isMouseOverElement(context.mouseX, context.mouseY)) {
                float speed = textStyle.rollSpeed <= 0 ? 0.8F : textStyle.rollSpeed;
                int overflow = textWidth - getSizeWidth() + 8;
                int cycle = Math.max(1, overflow * 2);
                int phase = (int) ((System.currentTimeMillis() * speed / 50.0F) % cycle);
                x = getPositionX() - (phase <= overflow ? phase : cycle - phase);
            }
            context.graphics.drawString(font, text, x, y, textStyle.textColor, textStyle.textShadow);
        } finally {
            context.disableScissor();
        }
    }

    private void drawWrapped(GUIContext context, Font font, Component text) {
        List<FormattedCharSequence> lines = font.split(text, Math.max(1, getSizeWidth()));
        int totalHeight = lines.size() * font.lineHeight;
        int y = getPositionY();
        if (textStyle.vertical == Vertical.CENTER) y += Math.max(0, (getSizeHeight() - totalHeight) / 2);
        if (textStyle.vertical == Vertical.BOTTOM) y += Math.max(0, getSizeHeight() - totalHeight);
        context.enableScissor(getPositionX(), getPositionY(), getSizeWidth(), getSizeHeight());
        try {
            for (FormattedCharSequence line : lines) {
                int x = getPositionX();
                int lineWidth = font.width(line);
                if (textStyle.horizontal == Horizontal.CENTER) x += Math.max(0, (getSizeWidth() - lineWidth) / 2);
                if (textStyle.horizontal == Horizontal.RIGHT) x += Math.max(0, getSizeWidth() - lineWidth);
                context.graphics.drawString(font, line, x, y, textStyle.textColor, textStyle.textShadow);
                y += font.lineHeight;
            }
        } finally {
            context.disableScissor();
        }
    }
}

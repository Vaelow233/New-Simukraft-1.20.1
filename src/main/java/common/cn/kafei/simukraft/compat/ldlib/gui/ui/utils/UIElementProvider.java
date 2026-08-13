package common.cn.kafei.simukraft.compat.ldlib.gui.ui.utils;

import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements.Label;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

@FunctionalInterface
public interface UIElementProvider<T> {
    UIElement apply(T value);

    static <T> UIElementProvider<T> text(Function<T, Component> labeler) {
        return value -> new Label().setText(labeler.apply(value));
    }
}

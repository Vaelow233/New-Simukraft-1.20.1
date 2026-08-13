package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import com.lowdragmc.lowdraglib.gui.widget.SwitchWidget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

import java.util.function.Consumer;

public class Switch extends UIElement {
    private final SwitchWidget delegate;
    private boolean on;
    private BooleanConsumer changed = ignored -> { };

    public Switch() {
        delegate = new SwitchWidget(0, 0, 1, 1, (click, pressed) -> {
            on = pressed;
            changed.accept(pressed);
        });
        addWidget(delegate);
    }

    public Switch setOn(boolean value) { return setOn(value, true); }

    public Switch setOn(boolean value, boolean notify) {
        on = value;
        delegate.setPressed(value);
        if (notify) changed.accept(value);
        return this;
    }

    public Switch setValue(Boolean value, boolean notify) { return setOn(Boolean.TRUE.equals(value), notify); }
    public Boolean getValue() { return on; }
    public boolean isOn() { return on; }
    public Switch setOnSwitchChanged(BooleanConsumer listener) { changed = listener == null ? ignored -> { } : listener; return this; }
    public Switch switchStyle(Consumer<SwitchStyle> consumer) { consumer.accept(new SwitchStyle()); return this; }

    @Override
    protected void afterLayout() {
        delegate.setSelfPosition(new Position(0, 0));
        delegate.setSize(new Size(getSizeWidth(), getSizeHeight()));
    }

    public static final class SwitchStyle {
    }
}

package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.utils.UIElementProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Selector<T> extends UIElement {
    private final SelectorWidget delegate = new SelectorWidget();
    private final SelectorStyle selectorStyle = new SelectorStyle();
    private List<T> candidates = List.of();
    private T selected;
    private Consumer<T> changed = ignored -> { };
    private UIElementProvider<T> provider;

    public Selector() {
        delegate.setOnChanged(this::selectFromText);
        addWidget(delegate);
    }

    public Selector<T> setCandidates(List<T> values) {
        candidates = values == null ? List.of() : List.copyOf(values);
        delegate.setCandidates(candidates.stream().map(String::valueOf).toList());
        return this;
    }

    public Selector<T> setCandidateUIProvider(UIElementProvider<T> provider) { this.provider = provider; return this; }
    public Selector<T> setSelected(T value) { return setSelected(value, true); }

    public Selector<T> setSelected(T value, boolean notify) {
        selected = value;
        delegate.setValue(String.valueOf(value));
        if (notify) changed.accept(value);
        return this;
    }

    public Selector<T> setValue(T value, boolean notify) { return setSelected(value, notify); }
    public T getValue() { return selected; }
    public Selector<T> setOnValueChanged(Consumer<T> listener) { changed = listener == null ? ignored -> { } : listener; return this; }

    public Selector<T> selectorStyle(Consumer<SelectorStyle> consumer) {
        consumer.accept(selectorStyle);
        delegate.setMaxCount(selectorStyle.maxItemCount);
        return this;
    }

    @Override
    protected void afterLayout() {
        delegate.setSelfPosition(new Position(0, 0));
        delegate.setSize(new Size(getSizeWidth(), getSizeHeight()));
    }

    private void selectFromText(String text) {
        for (T candidate : candidates) {
            if (String.valueOf(candidate).equals(text)) {
                selected = candidate;
                changed.accept(candidate);
                break;
            }
        }
    }

    public final class SelectorStyle {
        private int maxItemCount = 6;
        private boolean closeAfterSelect = true;

        public SelectorStyle maxItemCount(int value) { maxItemCount = Math.max(1, value); return this; }
        public SelectorStyle closeAfterSelect(boolean value) { closeAfterSelect = value; return this; }
    }
}

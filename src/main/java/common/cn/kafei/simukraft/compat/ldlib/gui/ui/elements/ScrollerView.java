package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.data.ScrollDisplay;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.data.ScrollerMode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScrollerView extends UIElement {
    private final DraggableScrollableWidgetGroup delegate = new DraggableScrollableWidgetGroup(0, 0, 1, 1);
    private final List<UIElement> scrollChildren = new ArrayList<>();
    private final ScrollerViewStyle scrollerStyle = new ScrollerViewStyle();
    public final UIElement verticalContainer = new UIElement();
    public final UIElement viewPort = new UIElement();
    public final UIElement viewContainer = new UIElement();

    public ScrollerView() {
        delegate.setUseScissor(true).setScrollable(true).setDraggable(false);
        addWidget(delegate);
    }

    public ScrollerView scrollerStyle(Consumer<ScrollerViewStyle> consumer) {
        consumer.accept(scrollerStyle);
        applyMode();
        return this;
    }

    public ScrollerView addScrollViewChild(UIElement child) {
        if (child != null) {
            scrollChildren.add(child);
            delegate.addWidget(child);
            child.attachModularUI(getModularUI());
        }
        return this;
    }

    public ScrollerView addScrollViewChildren(UIElement... children) {
        if (children != null) {
            for (UIElement child : children) addScrollViewChild(child);
        }
        return this;
    }

    public ScrollerView viewContainer(Consumer<UIElement> consumer) { consumer.accept(viewContainer); return this; }
    public ScrollerView viewPort(Consumer<UIElement> consumer) { consumer.accept(viewPort); return this; }
    public ScrollerView verticalContainer(Consumer<UIElement> consumer) { consumer.accept(verticalContainer); return this; }

    @Override
    public void attachModularUI(common.cn.kafei.simukraft.compat.ldlib.gui.ui.ModularUI modularUI) {
        super.attachModularUI(modularUI);
        for (UIElement child : scrollChildren) child.attachModularUI(modularUI);
    }

    @Override
    protected void afterLayout() {
        delegate.setSelfPosition(new Position(0, 0));
        delegate.setSize(new Size(getSizeWidth(), getSizeHeight()));
        int y = 0;
        for (UIElement child : scrollChildren) {
            int width = child.requestedWidth(getSizeWidth());
            int height = child.estimatePreferredHeight(getSizeHeight());
            width = width < 0 ? getSizeWidth() : width;
            height = Math.max(getSizeHeight(), height);
            child.setSelfPosition(new Position(0, y));
            child.setSize(new Size(width, height));
            child.resolveLayout();
            y += height;
        }
        delegate.computeMax();
    }

    @Override
    protected void onRemoved() {
        for (UIElement child : List.copyOf(scrollChildren)) child.dispose();
    }

    private void applyMode() {
        DraggableScrollableWidgetGroup.ScrollWheelDirection direction = scrollerStyle.mode == ScrollerMode.HORIZONTAL
                ? DraggableScrollableWidgetGroup.ScrollWheelDirection.HORIZONTAL
                : DraggableScrollableWidgetGroup.ScrollWheelDirection.VERTICAL;
        delegate.setScrollWheelDirection(direction);
    }

    public static final class ScrollerViewStyle {
        private ScrollerMode mode = ScrollerMode.VERTICAL;
        private ScrollDisplay verticalDisplay = ScrollDisplay.AUTO;
        private ScrollDisplay horizontalDisplay = ScrollDisplay.AUTO;

        public ScrollerViewStyle mode(ScrollerMode value) { mode = value; return this; }
        public ScrollerViewStyle verticalScrollDisplay(ScrollDisplay value) { verticalDisplay = value; return this; }
        public ScrollerViewStyle horizontalScrollDisplay(ScrollDisplay value) { horizontalDisplay = value; return this; }
    }
}

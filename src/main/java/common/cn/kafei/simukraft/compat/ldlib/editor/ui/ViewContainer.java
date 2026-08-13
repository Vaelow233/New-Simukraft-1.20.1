package common.cn.kafei.simukraft.compat.ldlib.editor.ui;

import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements.Tab;
import dev.vfyjxf.taffy.style.FlexDirection;

import java.util.LinkedHashMap;
import java.util.Map;

public class ViewContainer extends UIElement {
    private final UIElement tabs = new UIElement();
    private final UIElement content = new UIElement();
    private final Map<View, Tab> views = new LinkedHashMap<>();
    private View selected;

    public ViewContainer() {
        layout(layout -> layout.flexDirection(FlexDirection.COLUMN));
        tabs.layout(layout -> layout.widthPercent(100).height(24).flexDirection(FlexDirection.ROW));
        content.layout(layout -> layout.widthPercent(100).flex(1));
        addChild(tabs);
        addChild(content);
    }

    public ViewContainer addView(View view) {
        if (view == null || views.containsKey(view)) return this;
        Tab tab = view.createTab();
        tab.addEventListener(common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.UIEvents.MOUSE_DOWN,
                event -> selectView(view));
        views.put(view, tab);
        view.container = this;
        view.setDisplay(false);
        tabs.addChild(tab);
        content.addChild(view);
        if (selected == null) selectView(view);
        return this;
    }

    public boolean hasView(View view) { return views.containsKey(view); }

    public ViewContainer selectView(View view) {
        if (!views.containsKey(view)) return this;
        selected = view;
        for (Map.Entry<View, Tab> entry : views.entrySet()) {
            boolean isSelected = entry.getKey() == view;
            entry.getKey().setDisplay(isSelected);
            entry.getValue().setSelected(isSelected);
        }
        return this;
    }

    public void removeView(View view) {
        Tab tab = views.remove(view);
        if (tab != null) tabs.removeChild(tab);
        content.removeChild(view);
        view.container = null;
        if (selected == view) {
            selected = null;
            views.keySet().stream().findFirst().ifPresent(this::selectView);
        }
    }
}

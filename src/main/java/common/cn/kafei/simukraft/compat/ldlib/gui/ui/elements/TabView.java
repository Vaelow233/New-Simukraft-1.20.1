package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import dev.vfyjxf.taffy.style.FlexDirection;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TabView extends UIElement {
    public final UIElement tabHeaderContainer = new UIElement();
    public final ScrollerView tabScroller = new ScrollerView();
    public final UIElement tabContentContainer = new UIElement();
    private final Map<Tab, UIElement> tabContents = new LinkedHashMap<>();
    private Tab selectedTab;
    private Consumer<Tab> selectedListener = ignored -> { };

    public TabView() {
        layout(layout -> layout.flexDirection(FlexDirection.COLUMN));
        tabHeaderContainer.layout(layout -> layout.widthPercent(100).height(24).flexDirection(FlexDirection.ROW));
        tabContentContainer.layout(layout -> layout.widthPercent(100).flex(1));
        addChild(tabHeaderContainer);
        addChild(tabContentContainer);
    }

    public TabView addTab(Tab tab, UIElement content) { return addTab(tab, content, tabContents.size()); }

    public TabView addTab(Tab tab, UIElement content, int index) {
        tabContents.put(tab, content);
        tabHeaderContainer.addChildAt(tab, Math.max(0, Math.min(index, tabHeaderContainer.getChildren().size())));
        content.setDisplay(false);
        tabContentContainer.addChild(content);
        tab.addEventListener(common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.UIEvents.MOUSE_DOWN,
                event -> {
                    if (event.button == 0) selectTab(tab);
                });
        if (selectedTab == null) selectTab(tab);
        return this;
    }

    public TabView removeTab(Tab tab) {
        UIElement content = tabContents.remove(tab);
        tabHeaderContainer.removeChild(tab);
        if (content != null) tabContentContainer.removeChild(content);
        if (selectedTab == tab) {
            selectedTab = null;
            tabContents.keySet().stream().findFirst().ifPresent(this::selectTab);
        }
        return this;
    }

    public TabView clear() {
        tabContents.clear();
        selectedTab = null;
        tabHeaderContainer.clearAllChildren();
        tabContentContainer.clearAllChildren();
        return this;
    }

    public TabView selectTab(Tab tab) {
        if (tab == null || !tabContents.containsKey(tab)) return this;
        if (selectedTab != null && selectedTab != tab) selectedTab.setSelected(false);
        selectedTab = tab;
        for (Map.Entry<Tab, UIElement> entry : tabContents.entrySet()) {
            entry.getValue().setDisplay(entry.getKey() == tab);
        }
        if (!tab.isSelected()) tab.setSelected(true);
        selectedListener.accept(tab);
        return this;
    }

    public TabView tabHeaderContainer(Consumer<UIElement> consumer) { consumer.accept(tabHeaderContainer); return this; }
    public TabView tabScroller(Consumer<ScrollerView> consumer) { consumer.accept(tabScroller); return this; }
    public TabView tabContentContainer(Consumer<UIElement> consumer) { consumer.accept(tabContentContainer); return this; }
    public Map<Tab, UIElement> getTabContents() { return Map.copyOf(tabContents); }
    public TabView setOnTabSelected(Consumer<Tab> listener) { selectedListener = listener == null ? ignored -> { } : listener; return this; }
    public Tab getSelectedTab() { return selectedTab; }
}

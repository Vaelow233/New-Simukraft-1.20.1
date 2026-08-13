package common.cn.kafei.simukraft.compat.ldlib.gui.ui;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.data.Transform2D;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.DragHandler;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.UIEvent;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.UIEvents;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.rendering.GUIContext;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.style.BasicStyle;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.style.LayoutStyle;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.style.animation.StyleAnimation;
import dev.vfyjxf.taffy.style.AlignContent;
import dev.vfyjxf.taffy.style.AlignItems;
import dev.vfyjxf.taffy.style.FlexDirection;
import dev.vfyjxf.taffy.style.FlexWrap;
import dev.vfyjxf.taffy.style.TaffyPosition;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class UIElement extends WidgetGroup {
    private final LayoutStyle layoutStyle = new LayoutStyle();
    private final BasicStyle basicStyle = new BasicStyle(this);
    private final List<UIElement> children = new ArrayList<>();
    private final Map<String, List<Consumer<UIEvent>>> listeners = new HashMap<>();
    private final Set<String> classes = new HashSet<>();
    private ModularUI modularUI;
    private boolean allowHitTest = true;
    private boolean overflowVisible = true;
    private boolean layoutDirty = true;
    private boolean disposed;
    private int zIndex;
    private Transform2D transform = Transform2D.identity();
    private DragHandler activeDrag;
    private double dragStartX;
    private double dragStartY;

    public UIElement() {
        super(0, 0, 0, 0);
    }

    public LayoutStyle getLayoutStyle() {
        return layoutStyle;
    }

    public UIElement layout(Consumer<LayoutStyle> consumer) {
        consumer.accept(layoutStyle);
        markLayoutDirty();
        return this;
    }

    public UIElement style(Consumer<BasicStyle> consumer) {
        consumer.accept(basicStyle);
        return this;
    }

    public UIElement transform(Consumer<Transform2D> consumer) {
        Transform2D next = transform.copy();
        consumer.accept(next);
        setTransform(next);
        return this;
    }

    public void setTransform(Transform2D transform) {
        this.transform = transform == null ? Transform2D.identity() : transform.copy();
        markLayoutDirty();
    }

    public Transform2D getTransform() {
        return transform.copy();
    }

    public StyleAnimation animation() {
        return new StyleAnimation(this);
    }

    public UIElement addChild(UIElement child) {
        if (child == null || child == this) {
            return this;
        }
        children.add(child);
        addWidget(child);
        child.attachModularUI(modularUI);
        reorderChildren();
        markLayoutDirty();
        return this;
    }

    public UIElement addChildren(UIElement... elements) {
        if (elements != null) {
            for (UIElement element : elements) {
                addChild(element);
            }
        }
        return this;
    }

    public UIElement addChildAt(UIElement child, int index) {
        if (child == null || child == this) {
            return this;
        }
        int safeIndex = Math.max(0, Math.min(index, children.size()));
        children.add(safeIndex, child);
        addWidget(safeIndex, child);
        child.attachModularUI(modularUI);
        reorderChildren();
        markLayoutDirty();
        return this;
    }

    public boolean removeChild(UIElement child) {
        if (!children.remove(child)) {
            return false;
        }
        child.dispose();
        removeWidget(child);
        markLayoutDirty();
        return true;
    }

    public boolean removeSelf() {
        if (getParent() instanceof UIElement parent) {
            return parent.removeChild(this);
        }
        return false;
    }

    public void clearAllChildren() {
        for (UIElement child : List.copyOf(children)) child.dispose();
        children.clear();
        clearAllWidgets();
        markLayoutDirty();
    }

    public List<UIElement> getChildren() {
        return List.copyOf(children);
    }

    public UIElement addClass(String className) {
        if (className != null && !className.isBlank()) {
            classes.add(className);
            applyClassStyle(className);
        }
        return this;
    }

    private void applyClassStyle(String className) {
        switch (className) {
            case "simukraft_shell_panel" -> setBackground(new ColorRectTexture(0x80000000));
            case "simukraft_panel", "simukraft_grid_panel" -> setBackground(new GuiTextureGroup(
                    new ColorRectTexture(0xEE34343A), new ColorBorderTexture(1, 0xFF85858A)));
            case "simukraft_badge", "simukraft_card_slot" -> setBackground(new GuiTextureGroup(
                    new ColorRectTexture(0xFF55565B), new ColorBorderTexture(1, 0xFFDDDDDD)));
            case "simukraft_card_shadow" -> setBackground(new ColorRectTexture(0x88000000));
            case "simukraft_card_content_panel" -> setBackground(new GuiTextureGroup(
                    new ColorRectTexture(0x1AFFFFFF), new ColorBorderTexture(1, 0x55FFFFFF)));
            default -> { }
        }
    }

    public UIElement addClasses(String... classNames) {
        if (classNames != null) {
            for (String className : classNames) {
                addClass(className);
            }
        }
        return this;
    }

    public boolean hasClass(String className) {
        return classes.contains(className);
    }

    public UIElement setAllowHitTest(boolean allowHitTest) {
        this.allowHitTest = allowHitTest;
        return this;
    }

    public UIElement setOverflowVisible(boolean overflowVisible) {
        this.overflowVisible = overflowVisible;
        return this;
    }

    public UIElement setDisplay(boolean displayed) {
        boolean changed = isVisible() != displayed || isActive() != displayed;
        setVisible(displayed);
        setActive(displayed);
        if (changed) {
            markLayoutDirty();
        }
        return this;
    }

    public boolean isDisplayed() {
        return isVisible();
    }

    public UIElement disabled() {
        setActive(false);
        return this;
    }

    public UIElement selfCall(Consumer<UIElement> consumer) {
        consumer.accept(this);
        return this;
    }

    public UIElement addEventListener(String eventName, Consumer<UIEvent> listener) {
        listeners.computeIfAbsent(eventName, ignored -> new ArrayList<>()).add(listener);
        return this;
    }

    public UIElement addEventListener(String eventName, Consumer<UIEvent> listener, boolean capture) {
        return addEventListener(eventName, listener);
    }

    public void removeEventListener(String eventName, Consumer<UIEvent> listener) {
        List<Consumer<UIEvent>> eventListeners = listeners.get(eventName);
        if (eventListeners != null) eventListeners.remove(listener);
    }

    public DragHandler startDrag(Object draggingObject, IGuiTexture draggingTexture) {
        activeDrag = new DragHandler(draggingObject);
        return activeDrag;
    }

    public ModularUI getModularUI() {
        return modularUI;
    }

    public float getContentX() {
        return getPositionX() + layoutStyle.paddingLeft;
    }

    public float getContentY() {
        return getPositionY() + layoutStyle.paddingTop;
    }

    public float getContentWidth() {
        return Math.max(0, getSizeWidth() - layoutStyle.paddingLeft - layoutStyle.paddingRight);
    }

    public float getContentHeight() {
        return Math.max(0, getSizeHeight() - layoutStyle.paddingTop - layoutStyle.paddingBottom);
    }

    public void drawBackgroundAdditional(GUIContext context) {
    }

    protected void onRemoved() {
    }

    public void dispose() {
        if (disposed) return;
        disposed = true;
        activeDrag = null;
        setActive(false);
        setVisible(false);
        for (UIElement child : List.copyOf(children)) child.dispose();
        fire(UIEvents.REMOVED, 0, 0, -1, 0);
        onRemoved();
    }

    @Override
    public void drawInBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (disposed) {
            return;
        }
        if (getParent() == null) {
            resolveLayout();
        }
        boolean scissor = !overflowVisible && getSizeWidth() > 0 && getSizeHeight() > 0;
        if (scissor) {
            graphics.enableScissor(getPositionX(), getPositionY(),
                    getPositionX() + getSizeWidth(), getPositionY() + getSizeHeight());
        }
        try {
            drawBackgroundTexture(graphics, mouseX, mouseY);
            drawBackgroundAdditional(new GUIContext(graphics, mouseX, mouseY, partialTick));
            drawWidgetsBackground(graphics, mouseX, mouseY, partialTick);
        } finally {
            if (scissor) graphics.disableScissor();
        }
    }

    @Override
    public void updateScreen() {
        if (disposed) {
            return;
        }
        super.updateScreen();
        fire(UIEvents.TICK, 0, 0, -1, 0);
    }

    @Override
    public boolean isMouseOverElement(double mouseX, double mouseY) {
        return !disposed && allowHitTest && super.isMouseOverElement(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (disposed) {
            return false;
        }
        boolean childHandled = super.mouseClicked(mouseX, mouseY, button);
        if (!isActive() || !isMouseOverElement(mouseX, mouseY)) {
            return childHandled;
        }
        dragStartX = mouseX;
        dragStartY = mouseY;
        UIEvent event = fire(UIEvents.MOUSE_DOWN, mouseX, mouseY, button, 0);
        return childHandled || event.hasHandler;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (disposed) {
            return false;
        }
        boolean childHandled = super.mouseReleased(mouseX, mouseY, button);
        UIEvent released = fire(UIEvents.MOUSE_UP, mouseX, mouseY, button, 0);
        if (activeDrag != null) {
            fire(UIEvents.DRAG_END, mouseX, mouseY, button, 0);
            activeDrag = null;
        }
        return childHandled || released.hasHandler;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (disposed) {
            return false;
        }
        boolean childHandled = super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        if (activeDrag == null) {
            return childHandled;
        }
        UIEvent source = fire(UIEvents.DRAG_SOURCE_UPDATE, mouseX, mouseY, button, 0);
        UIEvent update = fire(UIEvents.DRAG_UPDATE, mouseX, mouseY, button, 0);
        return childHandled || source.hasHandler || update.hasHandler;
    }

    @Override
    public boolean mouseWheelMove(double mouseX, double mouseY, double wheelDelta) {
        if (disposed) {
            return false;
        }
        boolean childHandled = super.mouseWheelMove(mouseX, mouseY, wheelDelta);
        if (!isMouseOverElement(mouseX, mouseY)) {
            return childHandled;
        }
        UIEvent event = fire(UIEvents.MOUSE_WHEEL, mouseX, mouseY, -1, wheelDelta);
        return childHandled || event.hasHandler;
    }

    protected void afterLayout() {
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
        if (getParent() instanceof UIElement parent) parent.reorderChildren();
    }

    private void reorderChildren() {
        widgets.sort(Comparator.comparingInt(widget -> widget instanceof UIElement element ? element.zIndex : 0));
        children.sort(Comparator.comparingInt(UIElement::getZIndex));
    }

    public void attachModularUI(ModularUI modularUI) {
        this.modularUI = modularUI;
        for (UIElement child : children) {
            child.attachModularUI(modularUI);
        }
    }

    public void markCompatibilityGroupClientSide() {
        isClientSideWidget = true;
        for (UIElement child : children) child.markCompatibilityGroupClientSide();
    }

    public void resolveLayout() {
        if (!layoutDirty) {
            return;
        }
        layoutChildren();
        layoutDirty = false;
    }

    public int requestedWidth(int parentWidth) {
        return clampDimension(resolveDimension(layoutStyle.width, layoutStyle.widthPercent, parentWidth),
                layoutStyle.minWidth, layoutStyle.maxWidth);
    }

    public int requestedHeight(int parentHeight) {
        return clampDimension(resolveDimension(layoutStyle.height, layoutStyle.heightPercent, parentHeight),
                layoutStyle.minHeight, layoutStyle.maxHeight);
    }

    private void layoutChildren() {
        int contentWidth = Math.max(0, Math.round(getContentWidth()));
        int contentHeight = Math.max(0, Math.round(getContentHeight()));
        List<UIElement> flow = children.stream()
                .filter(UIElement::isVisible)
                .filter(child -> child.layoutStyle.positionType != TaffyPosition.ABSOLUTE)
                .toList();
        for (UIElement child : children) {
            if (child.isVisible() && child.layoutStyle.positionType == TaffyPosition.ABSOLUTE) {
                layoutAbsolute(child, contentWidth, contentHeight);
            }
        }
        if (flow.isEmpty()) {
            afterLayout();
            return;
        }
        boolean column = layoutStyle.flexDirection == FlexDirection.COLUMN;
        if (layoutStyle.flexWrap == FlexWrap.WRAP && !column) {
            layoutWrappedRows(flow, contentWidth, contentHeight);
        } else {
            layoutLine(flow, contentWidth, contentHeight, column);
        }
        afterLayout();
    }

    private void layoutAbsolute(UIElement child, int contentWidth, int contentHeight) {
        LayoutStyle style = child.layoutStyle;
        int width = child.requestedWidth(contentWidth);
        int height = child.requestedHeight(contentHeight);
        if (width < 0 && height >= 0 && !Float.isNaN(style.aspectRatio)) {
            width = Math.round(height * style.aspectRatio);
        }
        if (height < 0 && width >= 0 && !Float.isNaN(style.aspectRatio) && style.aspectRatio != 0) {
            height = Math.round(width / style.aspectRatio);
        }
        if (width < 0 && !Float.isNaN(style.left) && !Float.isNaN(style.right)) {
            width = Math.max(0, contentWidth - Math.round(style.left + style.right));
        }
        if (height < 0 && !Float.isNaN(style.top) && !Float.isNaN(style.bottom)) {
            height = Math.max(0, contentHeight - Math.round(style.top + style.bottom));
        }
        width = width < 0 ? contentWidth : width;
        height = height < 0 ? contentHeight : height;
        int x = !Float.isNaN(style.left) ? Math.round(style.left)
                : !Float.isNaN(style.right) ? contentWidth - width - Math.round(style.right) : 0;
        int y = !Float.isNaN(style.top) ? Math.round(style.top)
                : !Float.isNaN(style.bottom) ? contentHeight - height - Math.round(style.bottom) : 0;
        place(child, x, y, width, height);
    }

    private void layoutLine(List<UIElement> flow, int contentWidth, int contentHeight, boolean column) {
        int availableMain = column ? contentHeight : contentWidth;
        int availableCross = column ? contentWidth : contentHeight;
        int gap = Math.round(column ? layoutStyle.rowGap : layoutStyle.columnGap);
        float flexTotal = 0;
        int fixed = gap * Math.max(0, flow.size() - 1);
        for (UIElement child : flow) {
            LayoutStyle style = child.layoutStyle;
            int requested = column ? child.requestedHeight(contentHeight) : child.requestedWidth(contentWidth);
            if (requested < 0 && style.flexGrow <= 0) {
                requested = column
                        ? child.estimatePreferredHeight(contentHeight)
                        : child.estimatePreferredWidth(contentWidth);
            }
            fixed += Math.max(0, requested) + Math.round(column
                    ? style.marginTop + style.marginBottom : style.marginLeft + style.marginRight);
            if (requested < 0) {
                flexTotal += Math.max(0, style.flexGrow);
            }
        }
        int remaining = Math.max(0, availableMain - fixed);
        int totalMain = fixed + (flexTotal > 0 ? remaining : 0);
        float cursor = justifyOffset(layoutStyle.justifyContent, availableMain, totalMain);
        float dynamicGap = gap;
        if (layoutStyle.justifyContent == AlignContent.SPACE_BETWEEN && flow.size() > 1 && availableMain > totalMain) {
            dynamicGap += (float) (availableMain - totalMain) / (flow.size() - 1);
        }
        for (UIElement child : flow) {
            LayoutStyle style = child.layoutStyle;
            int main = column ? child.requestedHeight(contentHeight) : child.requestedWidth(contentWidth);
            if (main < 0 && style.flexGrow <= 0) {
                main = column
                        ? child.estimatePreferredHeight(contentHeight)
                        : child.estimatePreferredWidth(contentWidth);
            }
            if (main < 0) {
                main = flexTotal > 0 ? Math.round(remaining * Math.max(0, style.flexGrow) / flexTotal) : 0;
            }
            int cross = column ? child.requestedWidth(contentWidth) : child.requestedHeight(contentHeight);
            if (cross < 0 && !Float.isNaN(style.aspectRatio) && style.aspectRatio != 0) {
                cross = column ? Math.round(main * style.aspectRatio) : Math.round(main / style.aspectRatio);
            }
            AlignItems align = style.alignSelf != null ? style.alignSelf : layoutStyle.alignItems;
            if (cross < 0) {
                cross = align == AlignItems.STRETCH
                        ? availableCross
                        : column
                        ? child.estimatePreferredWidth(contentWidth)
                        : child.estimatePreferredHeight(contentHeight);
            }
            float mainBefore = column ? style.marginTop : style.marginLeft;
            float mainAfter = column ? style.marginBottom : style.marginRight;
            float crossBefore = column ? style.marginLeft : style.marginTop;
            float crossAfter = column ? style.marginRight : style.marginBottom;
            cursor += mainBefore;
            int crossPos = alignOffset(align, availableCross, cross + Math.round(crossBefore + crossAfter)) + Math.round(crossBefore);
            if (column) {
                place(child, crossPos, Math.round(cursor), cross, main);
            } else {
                place(child, Math.round(cursor), crossPos, main, cross);
            }
            cursor += main + mainAfter + dynamicGap;
        }
    }

    private void layoutWrappedRows(List<UIElement> flow, int contentWidth, int contentHeight) {
        int x = 0;
        int y = 0;
        int lineHeight = 0;
        int columnGap = Math.round(layoutStyle.columnGap);
        int rowGap = Math.round(layoutStyle.rowGap);
        for (UIElement child : flow) {
            LayoutStyle style = child.layoutStyle;
            int width = child.requestedWidth(contentWidth);
            int height = child.requestedHeight(contentHeight);
            width = width < 0 ? child.estimatePreferredWidth(contentWidth) : width;
            height = height < 0 ? child.estimatePreferredHeight(contentHeight) : height;
            width = Math.max(0, Math.min(width, contentWidth));
            height = Math.max(0, height);
            int occupiedWidth = width + Math.round(style.marginLeft + style.marginRight);
            if (x > 0 && x + occupiedWidth > contentWidth) {
                x = 0;
                y += lineHeight + rowGap;
                lineHeight = 0;
            }
            int childX = x + Math.round(style.marginLeft);
            int childY = y + Math.round(style.marginTop);
            place(child, childX, childY, width, height);
            x += occupiedWidth + columnGap;
            lineHeight = Math.max(lineHeight, height + Math.round(style.marginTop + style.marginBottom));
        }
    }

    private void place(UIElement child, int x, int y, int width, int height) {
        child.setSelfPosition(new Position(
                Math.round(layoutStyle.paddingLeft + x + child.transform.translateX()),
                Math.round(layoutStyle.paddingTop + y + child.transform.translateY())));
        child.setSize(new Size(Math.max(0, width), Math.max(0, height)));
        child.layoutDirty = true;
        child.resolveLayout();
    }

    private void markLayoutDirty() {
        layoutDirty = true;
        if (getParent() instanceof UIElement parent) {
            parent.markLayoutDirty();
        }
    }

    protected UIEvent fire(String eventName, double x, double y, int button, double deltaY) {
        UIEvent event = new UIEvent(this, x, y, button, deltaY, dragStartX, dragStartY, activeDrag);
        List<Consumer<UIEvent>> eventListeners = listeners.get(eventName);
        if (eventListeners == null || eventListeners.isEmpty()) {
            event.hasHandler = false;
            return event;
        }
        for (Consumer<UIEvent> listener : List.copyOf(eventListeners)) {
            listener.accept(event);
            if (event.isPropagationStopped()) {
                break;
            }
        }
        return event;
    }

    private static int resolveDimension(float pixels, float percent, int parentSize) {
        if (!Float.isNaN(pixels)) {
            return Math.max(0, Math.round(pixels));
        }
        if (!Float.isNaN(percent)) {
            return Math.max(0, Math.round(parentSize * percent / 100.0F));
        }
        return -1;
    }

    public int estimatePreferredWidth(int parentWidth) {
        int requested = requestedWidth(parentWidth);
        if (requested >= 0) {
            return requested;
        }
        boolean column = layoutStyle.flexDirection == FlexDirection.COLUMN;
        int measured = 0;
        for (UIElement child : children) {
            if (!child.isVisible() || child.layoutStyle.positionType == TaffyPosition.ABSOLUTE) {
                continue;
            }
            int childWidth = child.estimatePreferredWidth(parentWidth);
            if (column) {
                measured = Math.max(measured, childWidth);
            } else {
                measured += childWidth + Math.round(child.layoutStyle.marginLeft + child.layoutStyle.marginRight);
            }
        }
        if (!column && children.size() > 1) {
            measured += Math.round(layoutStyle.columnGap) * (children.size() - 1);
        }
        return measured + Math.round(layoutStyle.paddingLeft + layoutStyle.paddingRight);
    }

    public int estimatePreferredHeight(int parentHeight) {
        int requested = requestedHeight(parentHeight);
        if (requested >= 0) {
            return requested;
        }
        boolean column = layoutStyle.flexDirection == FlexDirection.COLUMN;
        int measured = 0;
        int count = 0;
        for (UIElement child : children) {
            if (!child.isVisible() || child.layoutStyle.positionType == TaffyPosition.ABSOLUTE) {
                continue;
            }
            int childHeight = child.estimatePreferredHeight(parentHeight);
            if (column) {
                measured += childHeight + Math.round(child.layoutStyle.marginTop + child.layoutStyle.marginBottom);
            } else {
                measured = Math.max(measured, childHeight);
            }
            count++;
        }
        if (column && count > 1) {
            measured += Math.round(layoutStyle.rowGap) * (count - 1);
        }
        return measured + Math.round(layoutStyle.paddingTop + layoutStyle.paddingBottom);
    }

    private static int clampDimension(int value, float min, float max) {
        if (value < 0) {
            return value;
        }
        if (!Float.isNaN(min)) {
            value = Math.max(value, Math.round(min));
        }
        if (!Float.isNaN(max)) {
            value = Math.min(value, Math.round(max));
        }
        return value;
    }

    private static int alignOffset(AlignItems align, int available, int occupied) {
        if (align == AlignItems.CENTER) {
            return Math.max(0, (available - occupied) / 2);
        }
        if (align == AlignItems.FLEX_END) {
            return Math.max(0, available - occupied);
        }
        return 0;
    }

    private static float justifyOffset(AlignContent align, int available, int occupied) {
        if (align == AlignContent.CENTER) {
            return Math.max(0, (available - occupied) / 2.0F);
        }
        if (align == AlignContent.FLEX_END) {
            return Math.max(0, available - occupied);
        }
        return 0;
    }
}

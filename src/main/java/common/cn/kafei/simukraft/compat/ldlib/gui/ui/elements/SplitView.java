package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.UIEvents;
import dev.vfyjxf.taffy.style.TaffyPosition;

public abstract class SplitView extends UIElement {
    public final UIElement first = new UIElement();
    public final UIElement second = new UIElement();
    protected float percentage = 0.5F;
    protected float borderSize = 2.0F;
    protected float minPercentage;
    protected float maxPercentage = 1.0F;

    protected SplitView() {
        first.layout(layout -> layout.positionType(TaffyPosition.ABSOLUTE));
        second.layout(layout -> layout.positionType(TaffyPosition.ABSOLUTE));
        addChild(first);
        addChild(second);
    }

    public SplitView first(UIElement child) { first.clearAllChildren(); first.addChild(child); return this; }
    public SplitView second(UIElement child) { second.clearAllChildren(); second.addChild(child); return this; }
    public SplitView left(UIElement child) { return first(child); }
    public SplitView right(UIElement child) { return second(child); }
    public abstract SplitView setPercentage(float percentage);
    public float getPercentage() { return percentage; }
    public float getBorderSize() { return borderSize; }
    public SplitView setBorderSize(float value) { borderSize = value; return this; }
    public float getMinPercentage() { return minPercentage; }
    public SplitView setMinPercentage(float value) { minPercentage = value; return this; }
    public float getMaxPercentage() { return maxPercentage; }
    public SplitView setMaxPercentage(float value) { maxPercentage = value; return this; }

    public static class Horizontal extends SplitView {
        public Horizontal() {
            addEventListener(UIEvents.MOUSE_DOWN, event -> {
                int divider = Math.round(getPositionX() + (getSizeWidth() - borderSize) * percentage);
                if (event.button == 0 && Math.abs(event.x - divider) <= Math.max(2.0F, borderSize)) {
                    event.target.startDrag(null, null);
                    event.stopPropagation();
                }
            });
            addEventListener(UIEvents.DRAG_UPDATE, event -> {
                if (getSizeWidth() > 0) setPercentage((float) ((event.x - getPositionX()) / getSizeWidth()));
            });
        }

        @Override
        public Horizontal setPercentage(float percentage) {
            this.percentage = Math.max(minPercentage, Math.min(maxPercentage, percentage));
            resolveLayout();
            return this;
        }

        @Override
        protected void afterLayout() {
            int firstWidth = Math.max(0, Math.round((getSizeWidth() - borderSize) * percentage));
            first.setSelfPosition(new Position(0, 0));
            first.setSize(new Size(firstWidth, getSizeHeight()));
            first.resolveLayout();
            second.setSelfPosition(new Position(Math.round(firstWidth + borderSize), 0));
            second.setSize(new Size(Math.max(0, Math.round(getSizeWidth() - firstWidth - borderSize)), getSizeHeight()));
            second.resolveLayout();
        }
    }
}

package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.widget.ProgressWidget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;

import java.util.function.Consumer;

public class ProgressBar extends UIElement {
    private final ProgressWidget delegate;
    public final Label label = new Label();
    public final UIElement barContainer = new UIElement();
    public final UIElement barBackground = new UIElement();
    public final UIElement bar = new UIElement();
    private float minValue;
    private float maxValue = 1.0F;
    private float progress;

    public ProgressBar() {
        delegate = new ProgressWidget(this::getNormalizedValue, 0, 0, 1, 1)
                .setProgressTexture(new ColorRectTexture(0xFF353535), new ColorRectTexture(0xFF4AA3DF));
        addWidget(delegate);
        label.setAllowHitTest(false);
        label.layout(layout -> layout.positionType(dev.vfyjxf.taffy.style.TaffyPosition.ABSOLUTE)
                .left(0).top(0).widthPercent(100).heightPercent(100));
        addChild(label);
    }

    public ProgressBar setMinValue(float value) { minValue = value; return this; }
    public ProgressBar setMaxValue(float value) { maxValue = value; return this; }
    public ProgressBar setRange(float min, float max) { minValue = min; maxValue = max; return this; }
    public ProgressBar setProgress(float value) { progress = value; return this; }
    public ProgressBar setValue(Float value) { progress = value == null ? minValue : value; return this; }
    public Float getValue() { return progress; }

    public float getNormalizedValue() {
        float range = maxValue - minValue;
        return range == 0 ? 0 : Math.max(0, Math.min(1, (progress - minValue) / range));
    }

    public float getNormalizedValue(float value) {
        float range = maxValue - minValue;
        return range == 0 ? 0 : Math.max(0, Math.min(1, (value - minValue) / range));
    }

    public ProgressBar progressBarStyle(Consumer<ProgressBarStyle> consumer) {
        consumer.accept(new ProgressBarStyle());
        return this;
    }

    public ProgressBar label(Consumer<Label> consumer) { consumer.accept(label); return this; }
    public ProgressBar barContainer(Consumer<UIElement> consumer) { consumer.accept(barContainer); return this; }
    public ProgressBar bar(Consumer<UIElement> consumer) { consumer.accept(bar); return this; }

    @Override
    protected void afterLayout() {
        delegate.setSelfPosition(new Position(0, 0));
        delegate.setSize(new Size(getSizeWidth(), getSizeHeight()));
    }

    public static final class ProgressBarStyle {
    }
}

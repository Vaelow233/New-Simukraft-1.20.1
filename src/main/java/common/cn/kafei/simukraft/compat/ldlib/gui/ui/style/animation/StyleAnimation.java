package common.cn.kafei.simukraft.compat.ldlib.gui.ui.style.animation;

import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.data.Transform2D;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.UIEvent;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.UIEvents;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.style.PropertyRegistry;

import java.util.function.Consumer;
import java.util.function.Function;

public final class StyleAnimation {
    private final UIElement element;
    private Transform2D target;
    private Consumer<StyleAnimation> finished;
    private float duration;
    private Function<Float, Float> easing = value -> value;

    public StyleAnimation(UIElement element) {
        this.element = element;
    }

    public StyleAnimation duration(float duration) {
        this.duration = Math.max(0.0F, duration);
        return this;
    }

    public StyleAnimation ease(Function<Float, Float> easing) {
        if (easing != null) this.easing = easing;
        return this;
    }

    public <T> StyleAnimation style(PropertyRegistry.Property<T> property, T value) {
        if (property == PropertyRegistry.TRANSFORM_2D && value instanceof Transform2D transform) {
            target = transform.copy();
        }
        return this;
    }

    public StyleAnimation onFinished(Consumer<StyleAnimation> callback) {
        finished = callback;
        return this;
    }

    public ISubscription start() {
        if (target == null) return () -> { };
        Transform2D start = element.getTransform();
        int totalTicks = Math.max(1, Math.round(duration * 20.0F));
        int[] elapsedTicks = {0};
        boolean[] active = {true};
        @SuppressWarnings("unchecked")
        Consumer<UIEvent>[] listener = new Consumer[1];
        listener[0] = event -> {
            if (!active[0]) return;
            float progress = Math.min(1.0F, ++elapsedTicks[0] / (float) totalTicks);
            float eased = Math.max(0.0F, Math.min(1.0F, easing.apply(progress)));
            float x = start.translateX() + (target.translateX() - start.translateX()) * eased;
            float y = start.translateY() + (target.translateY() - start.translateY()) * eased;
            element.setTransform(Transform2D.identity().translate(x, y));
            if (progress >= 1.0F) {
                active[0] = false;
                element.removeEventListener(UIEvents.TICK, listener[0]);
                if (finished != null) finished.accept(this);
            }
        };
        element.addEventListener(UIEvents.TICK, listener[0]);
        return () -> {
            active[0] = false;
            element.removeEventListener(UIEvents.TICK, listener[0]);
        };
    }
}

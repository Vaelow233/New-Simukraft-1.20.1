package common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements;

import com.lowdragmc.lowdraglib.gui.widget.SceneWidget;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.function.Consumer;

public class Scene extends UIElement {
    private SceneWidget delegate;
    private boolean tickWorld = true;

    public Scene createScene(Level level) {
        if (delegate != null) removeWidget(delegate);
        delegate = new SceneWidget(0, 0, 1, 1, level);
        addWidget(delegate);
        return this;
    }

    public Scene useCacheBuffer() { if (delegate != null) delegate.useCacheBuffer(); return this; }
    public Scene useCacheBuffer(boolean value) { if (delegate != null) delegate.useCacheBuffer(value); return this; }
    public Scene useOrtho() { if (delegate != null) delegate.useOrtho(); return this; }
    public Scene useOrtho(boolean value) { if (delegate != null) delegate.useOrtho(value); return this; }
    public Scene setTickWorld(boolean value) { tickWorld = value; return this; }
    public Scene setRenderFacing(boolean value) { if (delegate != null) delegate.setRenderFacing(value); return this; }
    public Scene setRenderSelect(boolean value) { if (delegate != null) delegate.setRenderSelect(value); return this; }
    public Scene setShowHoverBlockTips(boolean value) { if (delegate != null) delegate.setHoverTips(value); return this; }
    public Scene setDraggable(boolean value) { if (delegate != null) delegate.setDraggable(value); return this; }
    public Scene setScalable(boolean value) { if (delegate != null) delegate.setScalable(value); return this; }
    public Scene setIntractable(boolean value) { if (delegate != null) delegate.setIntractable(value); return this; }
    public Scene setRenderedCore(Collection<BlockPos> positions) { if (delegate != null) delegate.setRenderedCore(positions); return this; }
    public Scene setRenderedCore(Collection<BlockPos> positions, Object hook) { return setRenderedCore(positions); }
    public Scene setRenderedCore(Collection<BlockPos> positions, Object hook, boolean ignored) { return setRenderedCore(positions); }
    public Scene setCameraYawAndPitch(float yaw, float pitch) { if (delegate != null) delegate.setCameraYawAndPitch(yaw, pitch); return this; }
    public Scene setZoom(float zoom) { if (delegate != null) delegate.setZoom(zoom); return this; }
    public Scene setCenter(Vector3f center) { if (delegate != null) delegate.setCenter(center); return this; }
    public Scene setOrthoRange(float range) { if (delegate != null) delegate.setOrthoRange(range); return this; }
    public Scene setBeforeWorldRender(Consumer<Scene> callback) {
        if (delegate != null && callback != null) delegate.setBeforeWorldRender(ignored -> callback.accept(this));
        return this;
    }
    public Scene setAfterWorldRender(Consumer<Scene> callback) {
        if (delegate != null && callback != null) delegate.setAfterWorldRender(ignored -> callback.accept(this));
        return this;
    }
    public SceneWidget getDelegate() { return delegate; }

    @Override
    protected void afterLayout() {
        if (delegate != null) {
            delegate.setSelfPosition(new Position(0, 0));
            delegate.setSize(new Size(getSizeWidth(), getSizeHeight()));
        }
    }
}

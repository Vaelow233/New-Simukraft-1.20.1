package client.cn.kafei.simukraft.mixin;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Guards LowDragLib 1 drag handling for client-only widgets that have no container-backed GUI. */
@Mixin(value = Widget.class, remap = false)
@OnlyIn(Dist.CLIENT)
public abstract class MixinLdlibWidget {
    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void simukraft$ignoreDragWithoutGui(double mouseX, double mouseY, int button,
                                                double dragX, double dragY,
                                                CallbackInfoReturnable<Boolean> callbackInfo) {
        ModularUI gui = ((Widget) (Object) this).getGui();
        if (gui == null || gui.getModularUIGui() == null) {
            callbackInfo.setReturnValue(false);
        }
    }
}

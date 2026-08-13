package client.cn.kafei.simukraft.mixin;

import client.cn.kafei.simukraft.client.compat.LdlibTextFieldImeCompat;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements.TextField;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.event.UIEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TextField.class, remap = false)
@OnlyIn(Dist.CLIENT)
public abstract class MixinLdlibTextField {
    @Unique
    private Object simukraft$imeProxy;

    /** simukraft$installImeCompat: 为 LowDragLib 1 文本框适配层安装 IMBlocker 软兼容事件。 */
    @Inject(method = "<init>", at = @At("TAIL"))
    private void simukraft$installImeCompat(CallbackInfo callbackInfo) {
        TextField field = (TextField) (Object) this;
        field.addEventListener(UIEvents.FOCUS, event -> LdlibTextFieldImeCompat.onFocusGained(simukraft$imeProxy(field)));
        field.addEventListener(UIEvents.BLUR, event -> LdlibTextFieldImeCompat.onFocusLost(simukraft$imeProxy(field)));
        field.addEventListener(UIEvents.CHAR_TYPED, event -> LdlibTextFieldImeCompat.onFocusProbe(field, simukraft$imeProxy(field), event));
    }

    /** simukraft$updateImeCursorAfterSetCursor: 光标移动后刷新输入法候选框位置。 */
    @Inject(method = "setCursor", at = @At("TAIL"))
    private void simukraft$updateImeCursorAfterSetCursor(int pos, CallbackInfo callbackInfo) {
        LdlibTextFieldImeCompat.onCursorChanged((TextField) (Object) this);
    }

    /** simukraft$updateImeCursorAfterSetSelection: 选区变化后刷新输入法候选框位置。 */
    @Inject(method = "setSelection", at = @At("TAIL"))
    private void simukraft$updateImeCursorAfterSetSelection(int start, int end, CallbackInfo callbackInfo) {
        LdlibTextFieldImeCompat.onCursorChanged((TextField) (Object) this);
    }

    /** simukraft$updateImeCursorAfterTextChanged: 文本变化后刷新输入法候选框位置。 */
    @Inject(method = "onRawTextUpdate", at = @At("TAIL"))
    private void simukraft$updateImeCursorAfterTextChanged(CallbackInfo callbackInfo) {
        LdlibTextFieldImeCompat.onCursorChanged((TextField) (Object) this);
    }

    /** simukraft$imeProxy: 延迟创建当前文本框的 IMBlocker 动态代理。 */
    @Unique
    private Object simukraft$imeProxy(TextField field) {
        if (simukraft$imeProxy == null) {
            simukraft$imeProxy = LdlibTextFieldImeCompat.createProxy(field);
        }
        return simukraft$imeProxy;
    }
}

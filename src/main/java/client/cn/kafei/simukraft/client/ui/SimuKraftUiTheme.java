package client.cn.kafei.simukraft.client.ui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import common.cn.kafei.simukraft.SimuKraft;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UI;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("Null")
@OnlyIn(Dist.CLIENT)
public final class SimuKraftUiTheme {
    public static final ResourceLocation DEFAULT_STYLESHEET = ResourceLocation.fromNamespaceAndPath(SimuKraft.MOD_ID, "ui/ore.lss");
    public static final int CITY_CORE_BACKGROUND_COLOR = 0xFF444444;
    public static final int TEXT_PRIMARY_COLOR = 0xFFFFFFFF;
    public static final int TEXT_SECONDARY_COLOR = 0xFFE6E6E6;
    public static final int TEXT_SUCCESS_COLOR = 0xFF66FF66;
    public static final int TEXT_WARNING_COLOR = 0xFFFFFF55;
    public static final int TEXT_ERROR_COLOR = 0xFFFF7777;
    public static final int TEXT_INFO_COLOR = 0xFF66FFFF;
    public static final int TEXT_MUTED_COLOR = 0xFFBDBDBD;
    public static final int CARD_TEXT_COLOR = 0xFFFFFFFF;

    private SimuKraftUiTheme() {
    }

    /** 创建由 LowDragLib 1 控件承载的 UI。 */
    public static UI createUi(UIElement root) {
        return UI.of(root);
    }

    public static UI createUi(UIElement root, ResourceLocation stylesheet) {
        return UI.of(root);
    }

    /** 灰色全覆盖主面板：只负责背景，不参与点击，避免遮挡按钮。 */
    public static UIElement createShellPanel(int screenWidth, int screenHeight) {
        return new UIElement()
                .setAllowHitTest(false)
                .layout(layout -> {
                    layout.positionType(dev.vfyjxf.taffy.style.TaffyPosition.ABSOLUTE);
                    layout.left(0);
                    layout.top(0);
                    layout.width(screenWidth);
                    layout.height(screenHeight);
                })
                .addClass("simukraft_shell_panel");
    }

    /** 城市核心同色背景层：先添加背景，再添加文本和按钮，保证文字永远在上层。 */
    public static UIElement createCityCoreBackground(int screenWidth, int screenHeight) {
        return createCityCoreBackground(0, 0, screenWidth, screenHeight);
    }

    public static UIElement createCityCoreBackground(int left, int top, int width, int height) {
        return new UIElement()
                .setAllowHitTest(false)
                .layout(layout -> {
                    layout.positionType(dev.vfyjxf.taffy.style.TaffyPosition.ABSOLUTE);
                    layout.left(left);
                    layout.top(top);
                    layout.width(width);
                    layout.height(height);
                })
                .style(style -> style.backgroundTexture(new ColorRectTexture(CITY_CORE_BACKGROUND_COLOR)));
    }

    /** 创建选中白框：按钮外侧留 1px 间隔，白框自身 1px。 */
    /** 创建绝对定位装饰层：只负责视觉层次，不拦截鼠标事件。 */
    public static UIElement createDecorationLayer(int left, int top, int width, int height, String className) {
        return new UIElement()
                .setAllowHitTest(false)
                .layout(layout -> {
                    layout.positionType(dev.vfyjxf.taffy.style.TaffyPosition.ABSOLUTE);
                    layout.left(left);
                    layout.top(top);
                    layout.width(Math.max(1, width));
                    layout.height(Math.max(1, height));
                })
                .addClass(className);
    }

    public static UIElement createSelectionBorder(int width, int height) {
        return new UIElement()
                .setAllowHitTest(false)
                .layout(layout -> {
                    layout.positionType(dev.vfyjxf.taffy.style.TaffyPosition.ABSOLUTE);
                    layout.left(0);
                    layout.top(0);
                    layout.width(width);
                    layout.height(height);
                })
                .style(style -> style.backgroundTexture(new ColorBorderTexture(-1, 0xFFFFFFFF)));
    }

}

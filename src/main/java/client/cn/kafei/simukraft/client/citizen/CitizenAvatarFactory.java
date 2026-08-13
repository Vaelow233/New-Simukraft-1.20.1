package client.cn.kafei.simukraft.client.citizen;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import common.cn.kafei.simukraft.SimuKraft;
import com.lowdragmc.lowdraglib.gui.texture.ColorBorderTexture;
import com.lowdragmc.lowdraglib.gui.texture.ColorRectTexture;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("null")
@OnlyIn(Dist.CLIENT)
public final class CitizenAvatarFactory {
    private static final int FRAME_BACKGROUND = 0xFF7A8085;
    private static final int FRAME_INNER = 0xFF646A6F;
    private static final String MOD_ID = "simukraft";

    private CitizenAvatarFactory() {
    }

    public static UIElement createHead(String skinPath, int borderColor) {
        UIElement head = new UIElement();
        try {
            head.style(style -> style.backgroundTexture(new GuiTextureGroup(
                    new ColorRectTexture(FRAME_BACKGROUND),
                    new ColorRectTexture(FRAME_INNER).scale(0.92f),
                    createAvatarTexture(skinPath),
                    new ColorBorderTexture(1, borderColor)
            )));
        } catch (Exception exception) {
            SimuKraft.LOGGER.error("Simukraft: Failed to create citizen avatar skinPath={} borderColor={}", skinPath, Integer.toHexString(borderColor), exception);
            head.style(style -> style.backgroundTexture(new GuiTextureGroup(
                    new ColorRectTexture(FRAME_BACKGROUND),
                    new ColorBorderTexture(1, borderColor)
            )));
        }
        return head;
    }

    public static boolean isValidSkinPath(String skinPath) {
        return skinPath != null && !skinPath.isBlank() && !skinPath.contains("..") && !skinPath.startsWith("/");
    }

    private static IGuiTexture createAvatarTexture(String skinPath) {
        if (!isValidSkinPath(skinPath)) {
            return new ColorRectTexture(0xFF8A9298).scale(0.78f);
        }
        try {
            ResourceLocation textureLocation = resolveSkinTexture(skinPath);
            return (graphics, mouseX, mouseY, x, y, width, height) -> drawAvatar(graphics, textureLocation, x, y, width, height);
        } catch (Exception exception) {
            SimuKraft.LOGGER.error("Simukraft: Failed to create custom-draw avatar texture for skinPath={}", skinPath, exception);
            return new ColorRectTexture(0xFF8A9298).scale(0.78f);
        }
    }

    private static ResourceLocation resolveSkinTexture(String skinPath) {
        String normalized = skinPath.replace('\\', '/').trim();
        if (normalized.startsWith(MOD_ID + ":")) {
            normalized = normalized.substring((MOD_ID + ":").length());
        }
        if (normalized.startsWith("assets/" + MOD_ID + "/")) {
            normalized = normalized.substring(("assets/" + MOD_ID + "/").length());
        }
        if (normalized.endsWith(".png")) {
            normalized = normalized.substring(0, normalized.length() - 4);
        }
        if (!normalized.startsWith("textures/")) {
            normalized = normalized.startsWith("entity/") ? "textures/" + normalized : "textures/entity/" + normalized;
        }
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, normalized + ".png");
    }

    private static void drawAvatar(GuiGraphics graphics, ResourceLocation textureLocation, float x, float y, int width, int height) {
        try {
            int drawX = Math.round(x + width * 0.04F);
            int drawY = Math.round(y + height * 0.04F);
            int drawWidth = Math.max(1, Math.round(width * 0.92F));
            int drawHeight = Math.max(1, Math.round(height * 0.92F));
            drawFaceLayer(graphics, textureLocation, drawX, drawY, drawWidth, drawHeight, 8, 8, 8, 8);
            drawFaceLayer(graphics, textureLocation, drawX, drawY, drawWidth, drawHeight, 40, 8, 8, 8);
        } catch (Exception exception) {
            SimuKraft.LOGGER.error("Simukraft: Failed to draw avatar texture {}", textureLocation, exception);
        }
    }

    private static void drawFaceLayer(GuiGraphics graphics, ResourceLocation textureLocation, int x, int y, int width, int height,
                                      int u, int v, int regionWidth, int regionHeight) {
        graphics.blit(textureLocation, x, y, width, height, u, v, regionWidth, regionHeight, 64, 64);
    }
}

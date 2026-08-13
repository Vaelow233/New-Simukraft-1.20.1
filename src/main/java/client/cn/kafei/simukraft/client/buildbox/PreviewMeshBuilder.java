package client.cn.kafei.simukraft.client.buildbox;

import com.mojang.blaze3d.vertex.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("null")
@OnlyIn(Dist.CLIENT)
public final class PreviewMeshBuilder {
    private PreviewMeshBuilder() {
    }

    public static PreviewMesh build(List<PreviewBlockData> allBlocks) {
        if (allBlocks == null || allBlocks.isEmpty()) {
            return PreviewMesh.EMPTY;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return PreviewMesh.EMPTY;
        }

        MeshLayerBuilder solid = new MeshLayerBuilder();
        MeshLayerBuilder cutoutMipped = new MeshLayerBuilder();
        MeshLayerBuilder cutout = new MeshLayerBuilder();
        MeshLayerBuilder translucent = new MeshLayerBuilder();
        MeshLayerBuilder tripwire = new MeshLayerBuilder();
        List<PreviewBlockData> entityBlocks = new ArrayList<>();
        BlockPos meshOrigin = findMeshOrigin(allBlocks);
        // 预览视图只暴露建筑自身方块，避免模型剔除读取真实世界相邻方块。
        BlockAndTintGetter previewView = new PreviewBlockView(minecraft.level, allBlocks);

        ModelBlockRenderer modelRenderer = minecraft.getBlockRenderer().getModelRenderer();

        try {
            for (PreviewBlockData block : allBlocks) {
                BlockState state = block.state();
                if (state.isAir()) {
                    continue;
                }
                RenderShape renderShape = state.getRenderShape();
                boolean hasBlockEntity = state.getBlock() instanceof EntityBlock;
                if (renderShape == RenderShape.INVISIBLE && !hasBlockEntity) {
                    continue;
                }
                BakedModel model = minecraft.getBlockRenderer().getBlockModel(state);
                if (renderShape == RenderShape.ENTITYBLOCK_ANIMATED
                        || (renderShape == RenderShape.INVISIBLE && hasBlockEntity)
                        || model.isCustomRenderer()) {
                    // 箱子、床等方块实体渲染不进普通 mesh，后续如需显示可单独处理。
                    entityBlocks.add(block);
                    continue;
                }

                for (RenderType renderType : model.getRenderTypes(state, RandomSource.create(42L), ModelData.EMPTY)) {
                    MeshLayerBuilder layerBuilder = selectLayer(renderType, solid, cutoutMipped, cutout, translucent, tripwire);
                    if (layerBuilder == null) {
                        continue;
                    }

                    RandomSource random = RandomSource.create();
                    PoseStack poseStack = new PoseStack();
                    // 顶点坐标相对 meshOrigin 存储，渲染时整体平移，减少浮点精度问题。
                    poseStack.translate(block.pos().getX() - meshOrigin.getX(), block.pos().getY() - meshOrigin.getY(), block.pos().getZ() - meshOrigin.getZ());
                    modelRenderer.tesselateBlock(
                            previewView,
                            model,
                            state,
                            block.pos(),
                            poseStack,
                            layerBuilder,
                            true,
                            random,
                            state.getSeed(block.pos()),
                            net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                            ModelData.EMPTY,
                            renderType
                    );
                }
            }

            return new PreviewMesh(meshOrigin, solid.upload(), cutoutMipped.upload(), cutout.upload(), translucent.upload(), tripwire.upload(), entityBlocks);
        } finally {
            solid.close();
            cutoutMipped.close();
            cutout.close();
            translucent.close();
            tripwire.close();
        }
    }

    private static BlockPos findMeshOrigin(List<PreviewBlockData> allBlocks) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (PreviewBlockData block : allBlocks) {
            BlockPos pos = block.pos();
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
        }

        if (minX == Integer.MAX_VALUE) {
            return BlockPos.ZERO;
        }

        return new BlockPos(minX, minY, minZ);
    }

    private static MeshLayerBuilder selectLayer(RenderType renderType, MeshLayerBuilder solid, MeshLayerBuilder cutoutMipped, MeshLayerBuilder cutout, MeshLayerBuilder translucent, MeshLayerBuilder tripwire) {
        if (renderType == null) {
            return solid;
        }
        if (renderType == RenderType.translucent()) {
            return translucent;
        }
        if (renderType == RenderType.cutoutMipped()) {
            return cutoutMipped;
        }
        if (renderType == RenderType.cutout()) {
            return cutout;
        }
        if (renderType == RenderType.tripwire()) {
            return tripwire;
        }
        if (renderType == RenderType.solid()) {
            return solid;
        }
        // 部分模组 RenderType 不是原版单例，只能退回到名字判断。
        String name = renderType.toString().toLowerCase();
        if (name.contains("translucent")) {
            return translucent;
        }
        if (name.contains("cutout_mipped") || name.contains("cutoutmipped")) {
            return cutoutMipped;
        }
        if (name.contains("cutout")) {
            return cutout;
        }
        if (name.contains("tripwire")) {
            return tripwire;
        }
        return solid;
    }

    private static final class PreviewBlockView implements BlockAndTintGetter {
        private final BlockAndTintGetter delegate;
        private final Map<Long, BlockState> states = new HashMap<>();

        private PreviewBlockView(BlockAndTintGetter delegate, List<PreviewBlockData> blocks) {
            this.delegate = delegate;
            for (PreviewBlockData block : blocks) {
                if (!block.state().isAir()) {
                    states.put(block.pos().asLong(), block.state());
                }
            }
        }

        @Override
        public BlockEntity getBlockEntity(BlockPos pos) {
            return null;
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            // 未在投影内的坐标返回空气，使内部贴合面能被正确剔除。
            return states.getOrDefault(pos.asLong(), Blocks.AIR.defaultBlockState());
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return getBlockState(pos).getFluidState();
        }

        @Override
        public int getHeight() {
            return delegate.getHeight();
        }

        @Override
        public int getMinBuildHeight() {
            return delegate.getMinBuildHeight();
        }

        @Override
        public float getShade(Direction direction, boolean shade) {
            return delegate.getShade(direction, shade);
        }

        @Override
        public LevelLightEngine getLightEngine() {
            return delegate.getLightEngine();
        }

        @Override
        public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
            return delegate.getBlockTint(pos, colorResolver);
        }

        @Override
        public int getBrightness(LightLayer lightLayer, BlockPos pos) {
            return LightTexture.FULL_BRIGHT;
        }
    }

    private static final class MeshLayerBuilder implements com.mojang.blaze3d.vertex.VertexConsumer, AutoCloseable {
        private static final int CAPACITY = 256 * 1024;
        private final BufferBuilder buffer = new BufferBuilder(CAPACITY);

        private MeshLayerBuilder() {
            buffer.begin(
                    VertexFormat.Mode.QUADS,
                    DefaultVertexFormat.BLOCK
            );
        }

        private VertexBuffer upload() {
            BufferBuilder.RenderedBuffer rendered = buffer.endOrDiscardIfEmpty();
            if (rendered == null) {
                return null;
            }
            VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
            try {
                vertexBuffer.bind();
                vertexBuffer.upload(rendered);
                return vertexBuffer;
            } catch (RuntimeException | Error exception) {
                vertexBuffer.close();
                throw exception;
            } finally {
                VertexBuffer.unbind();
            }
        }

        @Override
        public void close() {
            if (buffer.building()) {
                BufferBuilder.RenderedBuffer unused = buffer.endOrDiscardIfEmpty();
                if (unused != null) {
                    unused.release();
                }
            }
            buffer.clear();
        }

        @Override
        public com.mojang.blaze3d.vertex.VertexConsumer vertex(double x, double y, double z) {
            return buffer.vertex(x, y, z);
        }

        @Override
        public com.mojang.blaze3d.vertex.VertexConsumer color(int red, int green, int blue, int alpha) {
            return buffer.color(red, green, blue, alpha);
        }

        @Override
        public com.mojang.blaze3d.vertex.VertexConsumer uv(float u, float v) {
            return buffer.uv(u, v);
        }

        @Override
        public com.mojang.blaze3d.vertex.VertexConsumer overlayCoords(int u, int v) {
            return buffer.overlayCoords(u | v << 16);
        }

        @Override
        public com.mojang.blaze3d.vertex.VertexConsumer uv2(int u, int v) {
            return buffer.uv2(LightTexture.FULL_BRIGHT & 0xFFFF, LightTexture.FULL_BRIGHT >> 16 & 0xFFFF);
        }

        @Override
        public com.mojang.blaze3d.vertex.VertexConsumer normal(float x, float y, float z) {
            return buffer.normal(x, y, z);
        }

        @Override
        public void endVertex() {
            buffer.endVertex();
        }

        @Override
        public void defaultColor(int red, int green, int blue, int alpha) {
            buffer.defaultColor(red, green, blue, alpha);
        }

        @Override
        public void unsetDefaultColor() {
            buffer.unsetDefaultColor();
        }
    }
}

package common.cn.kafei.simukraft.network.farmland;

import common.cn.kafei.simukraft.farmland.FarmlandBoxData;
import common.cn.kafei.simukraft.farmland.FarmlandBoxManager;
import common.cn.kafei.simukraft.farmland.FarmlandPlot;
import common.cn.kafei.simukraft.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import static common.cn.kafei.simukraft.network.ModNetwork.CHANNEL;
import java.util.function.Supplier;

/**
 * 悬停查询农田盒已保存作业区域：客户端视线对准农田盒超过 1 秒时发起，服务端回包供客户端渲染线框。
 * 仅展示用途，不改任何数据。
 */

@SuppressWarnings("Null")
public record FarmlandBoxBoundsRequestPacket(BlockPos pos) {

    public static void encode(FarmlandBoxBoundsRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    public static FarmlandBoxBoundsRequestPacket decode(FriendlyByteBuf buffer) {
        return new FarmlandBoxBoundsRequestPacket(buffer.readBlockPos());
    }

    public static void handle(FarmlandBoxBoundsRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        BlockPos pos = packet.pos();
        // 视野范围内、且确实是农田盒才回应，避免被滥用探测。
        if (!player.blockPosition().closerThan(pos, 96.0D) || !level.getBlockState(pos).is(ModBlocks.NSUK_FARMLAND_BOX.get())) {
            return;
        }
        FarmlandBoxData data = FarmlandBoxManager.get(level).get(pos);
        FarmlandPlot plot = data != null ? data.plot() : null;
        if (plot != null) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new FarmlandBoxBoundsResponsePacket(pos, true, plot.min(), plot.max()));
        } else {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new FarmlandBoxBoundsResponsePacket(pos, false, BlockPos.ZERO, BlockPos.ZERO));
        }
    }
}

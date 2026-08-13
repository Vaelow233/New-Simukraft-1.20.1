package common.cn.kafei.simukraft.network.logistics;

import common.cn.kafei.simukraft.logistics.LogisticsControlBoxService;
import common.cn.kafei.simukraft.network.rts.RtsRemoteMenuAccess;
import common.cn.kafei.simukraft.network.toast.InfoToastService;
import common.cn.kafei.simukraft.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import static common.cn.kafei.simukraft.network.ModNetwork.CHANNEL;
import java.util.function.Supplier;

@SuppressWarnings("Null")
public record LogisticsServerBoxOpenRequestPacket(BlockPos pos) {

    public static void encode(LogisticsServerBoxOpenRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    public static LogisticsServerBoxOpenRequestPacket decode(FriendlyByteBuf buffer) {
        return new LogisticsServerBoxOpenRequestPacket(buffer.readBlockPos());
    }

    public static void handle(LogisticsServerBoxOpenRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            openFor(level, context.get().getSender(), packet.pos());
        }
    }

    /** openFor: 校验物流服务器盒并发送界面快照。 */
    public static void openFor(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (!player.blockPosition().closerThan(pos, 16.0D) && !RtsRemoteMenuAccess.hasAccess(player, pos)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.logistics.too_far"));
            return;
        }
        if (!level.getBlockState(pos).is(ModBlocks.LOGISTICS_SERVER_BOX.get())) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.logistics.server_not_found"));
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), LogisticsServerBoxOpenResponsePacket.from(LogisticsControlBoxService.buildServerView(level, pos)));
    }
}

package common.cn.kafei.simukraft.network.industrial;

import common.cn.kafei.simukraft.industrial.IndustrialControlBoxService;
import common.cn.kafei.simukraft.network.rts.RtsRemoteMenuAccess;
import common.cn.kafei.simukraft.network.toast.InfoToastService;
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
public record IndustrialControlBoxOpenRequestPacket(BlockPos pos) {

    public static void encode(IndustrialControlBoxOpenRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    public static IndustrialControlBoxOpenRequestPacket decode(FriendlyByteBuf buffer) {
        return new IndustrialControlBoxOpenRequestPacket(buffer.readBlockPos());
    }

    public static void handle(IndustrialControlBoxOpenRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            openFor(level, context.get().getSender(), packet.pos());
        }
    }

    public static void openFor(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (!player.blockPosition().closerThan(pos, 16.0D) && !RtsRemoteMenuAccess.hasAccess(player, pos)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.build_box.too_far"));
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), IndustrialControlBoxOpenResponsePacket.from(IndustrialControlBoxService.buildView(level, pos)));
    }
}

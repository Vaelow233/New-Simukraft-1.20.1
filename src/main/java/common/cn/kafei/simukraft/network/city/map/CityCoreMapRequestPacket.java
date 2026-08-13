package common.cn.kafei.simukraft.network.city.map;

import common.cn.kafei.simukraft.network.city.CityNetworkViewFactory;
import common.cn.kafei.simukraft.network.city.core.CityCoreAccessValidator;
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
public record CityCoreMapRequestPacket(BlockPos pos) {

    public static void encode(CityCoreMapRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    public static CityCoreMapRequestPacket decode(FriendlyByteBuf buffer) {
        return new CityCoreMapRequestPacket(buffer.readBlockPos());
    }

    public static void handle(CityCoreMapRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            sendMap(level, context.get().getSender(), packet.pos());
        }
    }

    public static void sendMap(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (!CityCoreAccessValidator.requireAccess(level, player, pos)) {
            return;
        }
        CityCoreMapResponsePacket response = CityNetworkViewFactory.buildMapResponse(level, pos, player.getUUID());
        if (response != null) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), response);
        } else {
            InfoToastService.warning(player, Component.translatable("message.simukraft.city_core.not_found"));
        }
    }
}

package common.cn.kafei.simukraft.network.city.member;

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
public record CityCoreMembersRequestPacket(BlockPos pos) {

    public static void encode(CityCoreMembersRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    public static CityCoreMembersRequestPacket decode(FriendlyByteBuf buffer) {
        return new CityCoreMembersRequestPacket(buffer.readBlockPos());
    }

    public static void handle(CityCoreMembersRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            sendMembers(level, context.get().getSender(), packet.pos());
        }
    }

    public static void sendMembers(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (!CityCoreAccessValidator.requireAccess(level, player, pos)) {
            return;
        }
        CityCoreMembersResponsePacket response = CityNetworkViewFactory.buildMembersResponse(level, pos, player.getUUID());
        if (response != null) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), response);
        } else {
            InfoToastService.warning(player, Component.translatable("message.simukraft.city_core.not_found"));
        }
    }
}

package common.cn.kafei.simukraft.network.city.core;

import common.cn.kafei.simukraft.network.city.CityNetworkViewFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import static common.cn.kafei.simukraft.network.ModNetwork.CHANNEL;
import java.util.function.Supplier;

@SuppressWarnings("Null")
public record CityCoreOpenRequestPacket(BlockPos pos) {

    public static void encode(CityCoreOpenRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    public static CityCoreOpenRequestPacket decode(FriendlyByteBuf buffer) {
        return new CityCoreOpenRequestPacket(buffer.readBlockPos());
    }

    public static void handle(CityCoreOpenRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            openFor(level, context.get().getSender(), packet.pos());
        }
    }

    public static void openFor(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (!CityCoreAccessValidator.requireAccess(level, player, pos)) {
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), CityNetworkViewFactory.buildOpenResponse(level, pos, player.getUUID()));
    }
}

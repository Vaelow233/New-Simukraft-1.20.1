package common.cn.kafei.simukraft.network.farmland;

import common.cn.kafei.simukraft.farmland.FarmlandBoxService;
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
public record FarmlandBoxOpenRequestPacket(BlockPos pos) {

    public static void encode(FarmlandBoxOpenRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    public static FarmlandBoxOpenRequestPacket decode(FriendlyByteBuf buffer) {
        return new FarmlandBoxOpenRequestPacket(buffer.readBlockPos());
    }

    public static void handle(FarmlandBoxOpenRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            openFor(level, context.get().getSender(), packet.pos());
        }
    }

    public static void openFor(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (!player.blockPosition().closerThan(pos, 8.0D) && !RtsRemoteMenuAccess.hasAccess(player, pos)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.farmland_box.too_far"));
            return;
        }
        if (!level.getBlockState(pos).is(ModBlocks.NSUK_FARMLAND_BOX.get())) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.farmland_box.not_found"));
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), FarmlandBoxOpenResponsePacket.from(FarmlandBoxService.buildView(level, pos)));
    }
}

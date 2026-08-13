package common.cn.kafei.simukraft.network.building.controlbox;

import common.cn.kafei.simukraft.building.controlbox.ResidentialControlBoxService;
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
public record ResidentialControlBoxOpenRequestPacket(BlockPos pos) {

    public static void encode(ResidentialControlBoxOpenRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    public static ResidentialControlBoxOpenRequestPacket decode(FriendlyByteBuf buffer) {
        return new ResidentialControlBoxOpenRequestPacket(buffer.readBlockPos());
    }

    public static void handle(ResidentialControlBoxOpenRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            openFor(level, context.get().getSender(), packet.pos());
        }
    }

    public static void openFor(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (!player.blockPosition().closerThan(pos, 8.0D) && !RtsRemoteMenuAccess.hasAccess(player, pos)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.residential_control_box.too_far"));
            return;
        }
        if (!level.getBlockState(pos).is(ModBlocks.RESIDENTIAL_CONTROL_BOX.get())) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.residential_control_box.not_found"));
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), ResidentialControlBoxOpenResponsePacket.from(ResidentialControlBoxService.buildView(level, pos)));
    }
}

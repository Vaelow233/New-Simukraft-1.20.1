package common.cn.kafei.simukraft.network.farmland;

import common.cn.kafei.simukraft.city.CityService;
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
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("Null")
public record FarmlandBoxSetCropPacket(BlockPos pos, String cropId) {

    public static void encode(FarmlandBoxSetCropPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeUtf(packet.cropId(), 32);
    }

    public static FarmlandBoxSetCropPacket decode(FriendlyByteBuf buffer) {
        return new FarmlandBoxSetCropPacket(buffer.readBlockPos(), buffer.readUtf(32));
    }

    public static void handle(FarmlandBoxSetCropPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        BlockPos pos = packet.pos();
        if (!player.blockPosition().closerThan(pos, 8.0D) && !RtsRemoteMenuAccess.hasAccess(player, pos)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.farmland_box.too_far"));
            return;
        }
        if (!level.getBlockState(pos).is(ModBlocks.NSUK_FARMLAND_BOX.get())) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.farmland_box.not_found"));
            return;
        }
        UUID cityId = FarmlandBoxService.cityIdFor(level, pos);
        if (cityId == null || !CityService.canManageCity(level, cityId, player.getUUID())) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.farmland_box.no_permission"));
            return;
        }
        FarmlandBoxService.setCrop(level, pos, packet.cropId());
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), FarmlandBoxOpenResponsePacket.from(FarmlandBoxService.buildView(level, pos)));
    }
}

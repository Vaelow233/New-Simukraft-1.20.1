package common.cn.kafei.simukraft.network.city.chunk;

import common.cn.kafei.simukraft.city.CityClaimService;
import common.cn.kafei.simukraft.city.CityService;
import common.cn.kafei.simukraft.city.group.CityGroupMessageService;
import common.cn.kafei.simukraft.network.city.core.CityCoreAccessValidator;
import common.cn.kafei.simukraft.network.city.map.CityCoreMapRequestPacket;
import common.cn.kafei.simukraft.network.hud.HudSyncService;
import common.cn.kafei.simukraft.network.toast.InfoToastService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record CityChunkPurchasePacket(BlockPos pos, int chunkX, int chunkZ) {

    public static void encode(CityChunkPurchasePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeVarInt(packet.chunkX());
        buffer.writeVarInt(packet.chunkZ());
    }

    public static CityChunkPurchasePacket decode(FriendlyByteBuf buffer) {
        return new CityChunkPurchasePacket(buffer.readBlockPos(), buffer.readVarInt(), buffer.readVarInt());
    }

    public static void handle(CityChunkPurchasePacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() == null || !(context.get().getSender().level() instanceof ServerLevel level)) {
            return;
        }
        ServerPlayer player = context.get().getSender();
        if (!CityCoreAccessValidator.canAccess(level, player, packet.pos())) {
            return;
        }
        CityService.findCityByCorePos(level, packet.pos()).ifPresent(city -> {
            CityClaimService.ClaimResult result = CityClaimService.buyChunk(level, player, city, packet.chunkX(), packet.chunkZ());
            if (result.success()) {
                CityGroupMessageService.successToCity(level, city.cityId(), result.message());
            } else {
                InfoToastService.warning(player, result.message());
            }
            CityCoreMapRequestPacket.sendMap(level, player, packet.pos());
            if (result.success()) {
                CityChunkSyncService.syncToAll(level);
            }
            HudSyncService.syncToCityGroup(level, city.cityId(), true);
        });
    }
}

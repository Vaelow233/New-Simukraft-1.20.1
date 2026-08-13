package common.cn.kafei.simukraft.network.city.core;

import common.cn.kafei.simukraft.SimuKraft;
import common.cn.kafei.simukraft.citizen.CitizenService;
import common.cn.kafei.simukraft.city.CityChunkManager;
import common.cn.kafei.simukraft.city.CityData;
import common.cn.kafei.simukraft.city.CityService;
import common.cn.kafei.simukraft.city.group.CityGroupMessageService;
import common.cn.kafei.simukraft.network.city.CityNetworkViewFactory;
import common.cn.kafei.simukraft.network.city.chunk.CityChunkSyncService;
import common.cn.kafei.simukraft.network.hud.HudSyncService;
import common.cn.kafei.simukraft.network.toast.InfoToastService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import static common.cn.kafei.simukraft.network.ModNetwork.CHANNEL;
import java.util.function.Supplier;

@SuppressWarnings("Null")
public record CityCoreCreateCityPacket(BlockPos pos, String cityName) {

    public static void encode(CityCoreCreateCityPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeUtf(packet.cityName(), 64);
    }

    public static CityCoreCreateCityPacket decode(FriendlyByteBuf buffer) {
        return new CityCoreCreateCityPacket(buffer.readBlockPos(), buffer.readUtf(64));
    }

    public static void handle(CityCoreCreateCityPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            createCity(level, context.get().getSender(), packet.pos(), packet.cityName());
        }
    }

    private static void createCity(ServerLevel level, ServerPlayer player, BlockPos pos, String rawCityName) {
        if (!player.blockPosition().closerThan(pos, 8.0D)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.city_core.too_far"));
            return;
        }
        if (CityService.hasCityAtCorePos(level, pos)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.city_core.already_bound"));
            CityCoreOpenRequestPacket.openFor(level, player, pos);
            return;
        }
        if (CityService.findPlayerCity(level, player.getUUID()).isPresent()) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.city_core.player_has_city"));
            CityCoreOpenRequestPacket.openFor(level, player, pos);
            return;
        }
        String cityName = CityService.normalizeCityName(rawCityName);
        if (!CityService.isValidCityName(cityName)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.city_core.invalid_name"));
            CityCoreOpenRequestPacket.openFor(level, player, pos);
            return;
        }
        if (CityService.hasCityNamed(level, cityName)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.city_core.name_exists"));
            CityCoreOpenRequestPacket.openFor(level, player, pos);
            return;
        }
        CityChunkManager chunkManager = CityChunkManager.get(level);
        ChunkPos centerChunk = new ChunkPos(pos);
        if (!chunkManager.isAreaAvailable(centerChunk)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.city_core.chunks_occupied"));
            CityCoreOpenRequestPacket.openFor(level, player, pos);
            return;
        }
        CityData city = CityService.createCity(level, cityName, player.getUUID(), player.getGameProfile().getName(), pos);
        if (!chunkManager.assignInitialArea(city.cityId(), centerChunk)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.city_core.chunks_occupied"));
            CityCoreOpenRequestPacket.openFor(level, player, pos);
            return;
        }
        CitizenService.spawnCitizen(level, pos.above(), city.cityId(), true);
        CityGroupMessageService.successToCity(level, city.cityId(), Component.translatable("message.simukraft.city_core.created", city.cityName()));
        CityGroupMessageService.sendToCity(level, city.cityId(), Component.translatable("message.simukraft.city_core.initial_chunks_claimed"));
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), CityNetworkViewFactory.buildCreatedCityResponse(level, pos, city, player.getUUID()));
        CityChunkSyncService.syncToAll(level);
        HudSyncService.syncToCityGroup(level, city.cityId(), true);
    }
}

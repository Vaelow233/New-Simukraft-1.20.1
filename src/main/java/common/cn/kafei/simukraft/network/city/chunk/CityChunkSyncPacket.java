package common.cn.kafei.simukraft.network.city.chunk;

import common.cn.kafei.simukraft.network.clientbound.ClientboundNetworkBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@SuppressWarnings("null")
public record CityChunkSyncPacket(UUID currentCityId,
                                  Map<UUID, Set<Long>> cityChunks,
                                  Map<UUID, CityCoreEntry> cityCores) {

    public static void encode(CityChunkSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.currentCityId() != null);
        if (packet.currentCityId() != null) {
            buffer.writeUUID(packet.currentCityId());
        }
        buffer.writeVarInt(packet.cityChunks().size());
        packet.cityChunks().forEach((cityId, chunks) -> {
            buffer.writeUUID(cityId);
            buffer.writeVarInt(chunks.size());
            chunks.forEach(buffer::writeLong);
        });
        buffer.writeVarInt(packet.cityCores().size());
        packet.cityCores().forEach((cityId, core) -> {
            buffer.writeUUID(cityId);
            buffer.writeBlockPos(core.pos());
            buffer.writeUtf(core.cityName(), 64);
        });
    }

    public static CityChunkSyncPacket decode(FriendlyByteBuf buffer) {
        UUID currentCityId = buffer.readBoolean() ? buffer.readUUID() : null;
        int cityCount = buffer.readVarInt();
        Map<UUID, Set<Long>> cityChunks = new ConcurrentHashMap<>();
        for (int cityIndex = 0; cityIndex < cityCount; cityIndex++) {
            UUID cityId = buffer.readUUID();
            int chunkCount = buffer.readVarInt();
            Set<Long> chunks = ConcurrentHashMap.newKeySet();
            for (int chunkIndex = 0; chunkIndex < chunkCount; chunkIndex++) {
                chunks.add(buffer.readLong());
            }
            cityChunks.put(cityId, Set.copyOf(chunks));
        }
        int coreCount = buffer.readVarInt();
        Map<UUID, CityCoreEntry> cityCores = new ConcurrentHashMap<>();
        for (int coreIndex = 0; coreIndex < coreCount; coreIndex++) {
            cityCores.put(buffer.readUUID(), new CityCoreEntry(buffer.readBlockPos(), buffer.readUtf(64)));
        }
        return new CityChunkSyncPacket(currentCityId, Map.copyOf(cityChunks), Map.copyOf(cityCores));
    }

    public static void handle(CityChunkSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientboundNetworkBridge.handleCityChunkSync(packet));
    }

    public record CityCoreEntry(BlockPos pos, String cityName) {
    }
}

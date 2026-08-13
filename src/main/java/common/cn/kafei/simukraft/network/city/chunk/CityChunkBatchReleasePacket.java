package common.cn.kafei.simukraft.network.city.chunk;

import common.cn.kafei.simukraft.city.CityChunkManager;
import common.cn.kafei.simukraft.city.CityService;
import common.cn.kafei.simukraft.city.group.CityGroupMessageService;
import common.cn.kafei.simukraft.network.city.core.CityCoreAccessValidator;
import common.cn.kafei.simukraft.network.city.map.CityCoreMapRequestPacket;
import common.cn.kafei.simukraft.network.hud.HudSyncService;
import common.cn.kafei.simukraft.network.toast.InfoToastService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("Null")
public record CityChunkBatchReleasePacket(BlockPos pos, List<ChunkEntry> chunks) {
    private static final int MAX_CHUNKS = 256;

    public CityChunkBatchReleasePacket {
        chunks = chunks == null ? List.of() : List.copyOf(chunks.size() > MAX_CHUNKS ? chunks.subList(0, MAX_CHUNKS) : chunks);
    }

    public static void encode(CityChunkBatchReleasePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeVarInt(packet.chunks().size());
        for (ChunkEntry chunk : packet.chunks()) {
            buffer.writeVarInt(chunk.chunkX());
            buffer.writeVarInt(chunk.chunkZ());
        }
    }

    public static CityChunkBatchReleasePacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        int size = buffer.readVarInt();
        if (size < 0 || size > MAX_CHUNKS) {
            throw new IllegalArgumentException("Invalid city chunk batch release size: " + size);
        }
        List<ChunkEntry> chunks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            chunks.add(new ChunkEntry(buffer.readVarInt(), buffer.readVarInt()));
        }
        return new CityChunkBatchReleasePacket(pos, chunks);
    }

    public static void handle(CityChunkBatchReleasePacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() == null) {
            return;
        }
        ServerPlayer player = context.get().getSender();
        ServerLevel serverLevel = player.serverLevel();
        if (!CityCoreAccessValidator.canAccess(serverLevel, player, packet.pos())) {
            return;
        }
        CityService.findCityByCorePos(serverLevel, packet.pos()).ifPresent(city -> {
            CityChunkManager chunkManager = CityChunkManager.get(serverLevel);
            long coreChunkLong = ChunkPos.asLong(packet.pos().getX() >> 4, packet.pos().getZ() >> 4);
            int released = 0;
            for (ChunkEntry chunk : packet.chunks()) {
                long chunkLong = ChunkPos.asLong(chunk.chunkX(), chunk.chunkZ());
                if (chunkLong == coreChunkLong) continue;
                if (chunkManager.unclaimChunk(city.cityId(), chunkLong)) {
                    released++;
                }
            }
            if (released > 0) {
                Component message = Component.translatable("message.simukraft.city_chunk.batch_released", released);
                CityGroupMessageService.successToCity(serverLevel, city.cityId(), message);
                CityChunkSyncService.syncToAll(serverLevel);
                HudSyncService.syncToCityGroup(serverLevel, city.cityId(), true);
            } else {
                InfoToastService.warning(player, Component.translatable("message.simukraft.city_chunk.release_failed"));
            }
            CityCoreMapRequestPacket.sendMap(serverLevel, player, packet.pos());
        });
    }

    public record ChunkEntry(int chunkX, int chunkZ) {
    }
}

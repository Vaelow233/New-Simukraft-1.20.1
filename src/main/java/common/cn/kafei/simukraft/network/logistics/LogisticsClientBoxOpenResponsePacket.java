package common.cn.kafei.simukraft.network.logistics;

import common.cn.kafei.simukraft.logistics.LogisticsControlBoxService;
import common.cn.kafei.simukraft.logistics.LogisticsPortData;
import common.cn.kafei.simukraft.network.clientbound.ClientboundNetworkBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("null")
public record LogisticsClientBoxOpenResponsePacket(BlockPos boxPos,
                                                   boolean hasCity,
                                                   UUID cityId,
                                                   String cityName,
                                                   UUID clientId,
                                                   String name,
                                                   List<LogisticsPortData> ports,
                                                   List<LogisticsControlBoxService.ChannelEntry> channels) {

    public static LogisticsClientBoxOpenResponsePacket from(LogisticsControlBoxService.ClientView view) {
        return new LogisticsClientBoxOpenResponsePacket(view.boxPos(), view.hasCity(), view.cityId(), view.cityName(), view.clientId(), view.name(), view.ports(), view.channels());
    }

    public static void encode(LogisticsClientBoxOpenResponsePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.boxPos());
        buffer.writeBoolean(packet.hasCity());
        LogisticsServerBoxOpenResponsePacket.writeUuid(buffer, packet.cityId());
        LogisticsServerBoxOpenResponsePacket.writeUuid(buffer, packet.clientId());
        buffer.writeUtf(packet.cityName(), 256);
        buffer.writeUtf(packet.name(), 256);
        buffer.writeVarInt(packet.ports().size());
        for (LogisticsPortData port : packet.ports()) {
            buffer.writeUtf(port.id(), 64);
            buffer.writeUtf(port.name(), 64);
            buffer.writeUtf(port.kind(), 32);
            buffer.writeBlockPos(port.pos());
        }
        buffer.writeVarInt(packet.channels().size());
        for (LogisticsControlBoxService.ChannelEntry channel : packet.channels()) {
            LogisticsServerBoxOpenResponsePacket.writeChannel(buffer, channel);
        }
    }

    public static LogisticsClientBoxOpenResponsePacket decode(FriendlyByteBuf buffer) {
        BlockPos boxPos = buffer.readBlockPos();
        boolean hasCity = buffer.readBoolean();
        UUID cityId = LogisticsServerBoxOpenResponsePacket.readUuid(buffer);
        UUID clientId = LogisticsServerBoxOpenResponsePacket.readUuid(buffer);
        String cityName = buffer.readUtf(256);
        String name = buffer.readUtf(256);
        List<LogisticsPortData> ports = new ArrayList<>();
        int portCount = buffer.readVarInt();
        for (int i = 0; i < portCount; i++) {
            ports.add(new LogisticsPortData(buffer.readUtf(64), buffer.readUtf(64), buffer.readUtf(32), buffer.readBlockPos()));
        }
        List<LogisticsControlBoxService.ChannelEntry> channels = new ArrayList<>();
        int channelCount = buffer.readVarInt();
        for (int i = 0; i < channelCount; i++) {
            channels.add(LogisticsServerBoxOpenResponsePacket.readChannel(buffer));
        }
        return new LogisticsClientBoxOpenResponsePacket(boxPos, hasCity, cityId, cityName, clientId, name, List.copyOf(ports), List.copyOf(channels));
    }

    public static void handle(LogisticsClientBoxOpenResponsePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientboundNetworkBridge.handleLogisticsClientBoxOpenResponse(packet));
    }
}

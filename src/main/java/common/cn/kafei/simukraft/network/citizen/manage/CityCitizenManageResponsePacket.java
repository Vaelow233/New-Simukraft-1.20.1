package common.cn.kafei.simukraft.network.citizen.manage;

import common.cn.kafei.simukraft.network.clientbound.ClientboundNetworkBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * CityCitizenManageResponsePacket: 城市核心“市民管理”界面的市民列表快照（服务端 -> 客户端）。
 */
@SuppressWarnings("Null")
public record CityCitizenManageResponsePacket(BlockPos pos, String cityName, boolean canManage, List<CitizenEntry> citizens) {

    public CityCitizenManageResponsePacket {
        cityName = cityName != null ? cityName : "";
        citizens = citizens == null ? List.of() : List.copyOf(citizens);
    }

    public static void encode(CityCitizenManageResponsePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeUtf(packet.cityName(), 64);
        buffer.writeBoolean(packet.canManage());
        buffer.writeVarInt(packet.citizens().size());
        for (CitizenEntry entry : packet.citizens()) {
            buffer.writeUUID(entry.citizenId());
            buffer.writeUtf(entry.name(), 64);
            buffer.writeUtf(entry.jobKey(), 256);
            buffer.writeUtf(entry.workStatusKey(), 256);
            buffer.writeVarInt(entry.age());
            buffer.writeUtf(entry.gender(), 16);
            buffer.writeUtf(entry.skinPath() != null ? entry.skinPath() : "", 256);
        }
    }

    public static CityCitizenManageResponsePacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        String cityName = buffer.readUtf(64);
        boolean canManage = buffer.readBoolean();
        int size = buffer.readVarInt();
        List<CitizenEntry> citizens = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            citizens.add(new CitizenEntry(
                    buffer.readUUID(),
                    buffer.readUtf(64),
                    buffer.readUtf(256),
                    buffer.readUtf(256),
                    buffer.readVarInt(),
                    buffer.readUtf(16),
                    buffer.readUtf(256)));
        }
        return new CityCitizenManageResponsePacket(pos, cityName, canManage, citizens);
    }

    public static void handle(CityCitizenManageResponsePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientboundNetworkBridge.handleCityCitizenManageResponse(packet));
    }

    /** CitizenEntry: 单个市民的展示信息。jobKey/workStatusKey 为可本地化的翻译键。 */
    public record CitizenEntry(UUID citizenId, String name, String jobKey, String workStatusKey, int age, String gender, String skinPath) {
    }
}

package common.cn.kafei.simukraft.network.hud;

import common.cn.kafei.simukraft.city.CityPermissionLevel;
import common.cn.kafei.simukraft.network.clientbound.ClientboundNetworkBridge;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record HudSyncPacket(int currentDay, int worldPopulation, String cityName, double cityFunds, int cityPopulation, CityPermissionLevel permissionLevel, boolean creativeMode) {

    public static void encode(HudSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.currentDay());
        buffer.writeInt(packet.worldPopulation());
        buffer.writeUtf(packet.cityName(), 64);
        buffer.writeDouble(packet.cityFunds());
        buffer.writeInt(packet.cityPopulation());
        buffer.writeUtf(packet.permissionLevel().name(), 16);
        buffer.writeBoolean(packet.creativeMode());
    }

    public static HudSyncPacket decode(FriendlyByteBuf buffer) {
        return new HudSyncPacket(
                buffer.readInt(),
                buffer.readInt(),
                buffer.readUtf(64),
                buffer.readDouble(),
                buffer.readInt(),
                CityPermissionLevel.fromName(buffer.readUtf(16)),
                buffer.readBoolean()
        );
    }

    public static void handle(HudSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientboundNetworkBridge.handleHudSync(packet));
    }
}
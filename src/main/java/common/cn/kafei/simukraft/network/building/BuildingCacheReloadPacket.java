package common.cn.kafei.simukraft.network.building;

import common.cn.kafei.simukraft.network.clientbound.ClientboundNetworkBridge;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record BuildingCacheReloadPacket() {

    public static void handle(BuildingCacheReloadPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientboundNetworkBridge.handleBuildingCacheReload(packet));
    }

    public static void encode(BuildingCacheReloadPacket packet, FriendlyByteBuf buffer) {
    }

    public static BuildingCacheReloadPacket decode(FriendlyByteBuf buffer) {
        return new BuildingCacheReloadPacket();
    }
}

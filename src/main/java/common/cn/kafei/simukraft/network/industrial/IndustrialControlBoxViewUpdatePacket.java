package common.cn.kafei.simukraft.network.industrial;

import common.cn.kafei.simukraft.industrial.IndustrialControlBoxView;
import common.cn.kafei.simukraft.network.clientbound.ClientboundNetworkBridge;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record IndustrialControlBoxViewUpdatePacket(IndustrialControlBoxOpenResponsePacket view) {

    public static IndustrialControlBoxViewUpdatePacket from(IndustrialControlBoxView view) {
        return new IndustrialControlBoxViewUpdatePacket(IndustrialControlBoxOpenResponsePacket.from(view));
    }

    public static void encode(IndustrialControlBoxViewUpdatePacket packet, FriendlyByteBuf buffer) {
        IndustrialControlBoxOpenResponsePacket.encode(packet.view(), buffer);
    }

    public static IndustrialControlBoxViewUpdatePacket decode(FriendlyByteBuf buffer) {
        return new IndustrialControlBoxViewUpdatePacket(IndustrialControlBoxOpenResponsePacket.decode(buffer));
    }

    public static void handle(IndustrialControlBoxViewUpdatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientboundNetworkBridge.handleIndustrialControlBoxViewUpdate(packet));
    }
}

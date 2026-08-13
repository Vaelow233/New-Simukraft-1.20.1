package common.cn.kafei.simukraft.network.building.controlbox;

import common.cn.kafei.simukraft.building.controlbox.ResidentialControlBoxView;
import common.cn.kafei.simukraft.network.clientbound.ClientboundNetworkBridge;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record ResidentialControlBoxViewUpdatePacket(ResidentialControlBoxOpenResponsePacket view) {

    public static ResidentialControlBoxViewUpdatePacket from(ResidentialControlBoxView view) {
        return new ResidentialControlBoxViewUpdatePacket(ResidentialControlBoxOpenResponsePacket.from(view));
    }

    public static void encode(ResidentialControlBoxViewUpdatePacket packet, FriendlyByteBuf buffer) {
        ResidentialControlBoxOpenResponsePacket.encode(packet.view(), buffer);
    }

    public static ResidentialControlBoxViewUpdatePacket decode(FriendlyByteBuf buffer) {
        return new ResidentialControlBoxViewUpdatePacket(ResidentialControlBoxOpenResponsePacket.decode(buffer));
    }

    public static void handle(ResidentialControlBoxViewUpdatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientboundNetworkBridge.handleResidentialControlBoxViewUpdate(packet));
    }
}

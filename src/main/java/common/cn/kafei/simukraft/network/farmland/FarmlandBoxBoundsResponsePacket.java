package common.cn.kafei.simukraft.network.farmland;

import common.cn.kafei.simukraft.network.clientbound.ClientboundNetworkBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record FarmlandBoxBoundsResponsePacket(BlockPos pos, boolean hasPlot, BlockPos min, BlockPos max) {

    public static void encode(FarmlandBoxBoundsResponsePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeBoolean(packet.hasPlot());
        buffer.writeBlockPos(packet.min());
        buffer.writeBlockPos(packet.max());
    }

    public static FarmlandBoxBoundsResponsePacket decode(FriendlyByteBuf buffer) {
        return new FarmlandBoxBoundsResponsePacket(buffer.readBlockPos(), buffer.readBoolean(), buffer.readBlockPos(), buffer.readBlockPos());
    }

    public static void handle(FarmlandBoxBoundsResponsePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientboundNetworkBridge.handleFarmlandBoxBoundsResponse(packet));
    }
}

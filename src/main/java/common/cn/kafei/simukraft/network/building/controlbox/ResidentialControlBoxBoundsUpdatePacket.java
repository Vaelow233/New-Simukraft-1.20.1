package common.cn.kafei.simukraft.network.building.controlbox;

import common.cn.kafei.simukraft.network.clientbound.ClientboundNetworkBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("null")
public record ResidentialControlBoxBoundsUpdatePacket(BlockPos controlBoxPos,
                                                      boolean hasBuildingBounds,
                                                      BlockPos boundsMin,
                                                      BlockPos boundsMax,
                                                      List<BlockPos> residentialPoiPositions) {

    public static void encode(ResidentialControlBoxBoundsUpdatePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.controlBoxPos());
        buffer.writeBoolean(packet.hasBuildingBounds());
        buffer.writeBlockPos(packet.boundsMin());
        buffer.writeBlockPos(packet.boundsMax());
        buffer.writeVarInt(packet.residentialPoiPositions().size());
        packet.residentialPoiPositions().forEach(buffer::writeBlockPos);
    }

    public static ResidentialControlBoxBoundsUpdatePacket decode(FriendlyByteBuf buffer) {
        BlockPos controlBoxPos = buffer.readBlockPos();
        boolean hasBuildingBounds = buffer.readBoolean();
        BlockPos boundsMin = buffer.readBlockPos();
        BlockPos boundsMax = buffer.readBlockPos();
        int poiSize = buffer.readVarInt();
        List<BlockPos> residentialPoiPositions = new ArrayList<>(poiSize);
        for (int index = 0; index < poiSize; index++) {
            residentialPoiPositions.add(buffer.readBlockPos());
        }
        return new ResidentialControlBoxBoundsUpdatePacket(controlBoxPos, hasBuildingBounds, boundsMin, boundsMax, List.copyOf(residentialPoiPositions));
    }

    public static void handle(ResidentialControlBoxBoundsUpdatePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientboundNetworkBridge.handleResidentialControlBoxBoundsUpdate(packet));
    }
}

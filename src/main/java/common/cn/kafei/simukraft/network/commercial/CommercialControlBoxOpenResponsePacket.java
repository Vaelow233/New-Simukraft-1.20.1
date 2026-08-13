package common.cn.kafei.simukraft.network.commercial;

import common.cn.kafei.simukraft.commercial.CommercialControlBoxView;
import common.cn.kafei.simukraft.network.clientbound.ClientboundNetworkBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("null")
public record CommercialControlBoxOpenResponsePacket(BlockPos boxPos,
                                                     boolean hasBuilding,
                                                     String buildingName,
                                                     boolean definitionValid,
                                                     String definitionName,
                                                     String statusKey,
                                                     String statusText,
                                                     boolean running,
                                                     boolean hasWorker,
                                                     UUID workerId,
                                                     String workerName,
                                                     double cityBalance,
                                                     boolean hasBuildingBounds,
                                                     BlockPos boundsMin,
                                                     BlockPos boundsMax,
                                                     boolean integrityAvailable,
                                                     double integrityPercent,
                                                     int integrityRepairableBlocks,
                                                     int integrityManualRepairBlocks,
                                                     double integrityRepairCost) {

    public static CommercialControlBoxOpenResponsePacket from(CommercialControlBoxView view) {
        return new CommercialControlBoxOpenResponsePacket(
                view.boxPos(),
                view.hasBuilding(),
                view.buildingName(),
                view.definitionValid(),
                view.definitionName(),
                view.statusKey(),
                view.statusText(),
                view.running(),
                view.hasWorker(),
                view.workerId(),
                view.workerName(),
                view.cityBalance(),
                view.hasBuildingBounds(),
                view.boundsMin(),
                view.boundsMax(),
                view.integrityAvailable(),
                view.integrityPercent(),
                view.integrityRepairableBlocks(),
                view.integrityManualRepairBlocks(),
                view.integrityRepairCost()
        );
    }

    /** encode: 写入商业控制箱视图响应。 */
    public static void encode(CommercialControlBoxOpenResponsePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.boxPos());
        buffer.writeBoolean(packet.hasBuilding());
        buffer.writeUtf(packet.buildingName(), 256);
        buffer.writeBoolean(packet.definitionValid());
        buffer.writeUtf(packet.definitionName(), 256);
        buffer.writeUtf(packet.statusKey(), 256);
        buffer.writeUtf(packet.statusText(), 256);
        buffer.writeBoolean(packet.running());
        buffer.writeBoolean(packet.hasWorker());
        if (packet.hasWorker() && packet.workerId() != null) {
            buffer.writeUUID(packet.workerId());
        }
        buffer.writeUtf(packet.workerName(), 256);
        buffer.writeDouble(packet.cityBalance());
        buffer.writeBoolean(packet.hasBuildingBounds());
        buffer.writeBlockPos(packet.boundsMin());
        buffer.writeBlockPos(packet.boundsMax());
        buffer.writeBoolean(packet.integrityAvailable());
        buffer.writeDouble(packet.integrityPercent());
        buffer.writeVarInt(packet.integrityRepairableBlocks());
        buffer.writeVarInt(packet.integrityManualRepairBlocks());
        buffer.writeDouble(packet.integrityRepairCost());
    }

    /** decode: 读取商业控制箱视图响应。 */
    public static CommercialControlBoxOpenResponsePacket decode(FriendlyByteBuf buffer) {
        BlockPos boxPos = buffer.readBlockPos();
        boolean hasBuilding = buffer.readBoolean();
        String buildingName = buffer.readUtf(256);
        boolean definitionValid = buffer.readBoolean();
        String definitionName = buffer.readUtf(256);
        String statusKey = buffer.readUtf(256);
        String statusText = buffer.readUtf(256);
        boolean running = buffer.readBoolean();
        boolean hasWorker = buffer.readBoolean();
        UUID workerId = hasWorker ? buffer.readUUID() : null;
        String workerName = buffer.readUtf(256);
        double cityBalance = buffer.readDouble();
        boolean hasBuildingBounds = buffer.readBoolean();
        BlockPos boundsMin = buffer.readBlockPos();
        BlockPos boundsMax = buffer.readBlockPos();
        boolean integrityAvailable = buffer.readBoolean();
        double integrityPercent = buffer.readDouble();
        int integrityRepairableBlocks = buffer.readVarInt();
        int integrityManualRepairBlocks = buffer.readVarInt();
        double integrityRepairCost = buffer.readDouble();
        return new CommercialControlBoxOpenResponsePacket(boxPos, hasBuilding, buildingName, definitionValid, definitionName,
                statusKey, statusText, running, hasWorker, workerId, workerName, cityBalance, hasBuildingBounds,
                boundsMin, boundsMax, integrityAvailable, integrityPercent, integrityRepairableBlocks,
                integrityManualRepairBlocks, integrityRepairCost);
    }

    /** handle: 分发商业控制箱视图到客户端 UI。 */
    public static void handle(CommercialControlBoxOpenResponsePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientboundNetworkBridge.handleCommercialControlBoxOpenResponse(packet));
    }

}

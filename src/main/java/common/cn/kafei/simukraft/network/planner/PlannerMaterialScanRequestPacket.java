package common.cn.kafei.simukraft.network.planner;

import common.cn.kafei.simukraft.config.ServerConfig;
import common.cn.kafei.simukraft.material.GenericContainerAccess;
import common.cn.kafei.simukraft.network.toast.InfoToastService;
import common.cn.kafei.simukraft.planner.PlanOperation;
import common.cn.kafei.simukraft.planner.PlanningTaskData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import static common.cn.kafei.simukraft.network.ModNetwork.CHANNEL;
import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("null")
public record PlannerMaterialScanRequestPacket(BlockPos buildBoxPos,
                                               BlockPos min,
                                               BlockPos max,
                                               PlanOperation operation) {

    public static void encode(PlannerMaterialScanRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.buildBoxPos());
        buffer.writeBlockPos(packet.min());
        buffer.writeBlockPos(packet.max());
        buffer.writeEnum(packet.operation());
    }

    public static PlannerMaterialScanRequestPacket decode(FriendlyByteBuf buffer) {
        return new PlannerMaterialScanRequestPacket(buffer.readBlockPos(), buffer.readBlockPos(), buffer.readBlockPos(), buffer.readEnum(PlanOperation.class));
    }

    public static void handle(PlannerMaterialScanRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (packet.operation() == PlanOperation.REMOVE) {
            return;
        }
        PlannerNetworkValidation.PlannerContext plannerContext = PlannerNetworkValidation.validatePlanner(player, level, packet.buildBoxPos());
        if (plannerContext == null) {
            return;
        }
        BlockPos min = PlannerNetworkValidation.min(packet.min(), packet.max());
        BlockPos max = PlannerNetworkValidation.max(packet.min(), packet.max());
        int volume = PlanningTaskData.volume(min, max);
        if (volume <= 0 || volume > ServerConfig.plannerMaxVolume()) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.plan_area.too_big", ServerConfig.plannerMaxVolume()));
            return;
        }
        if (!PlannerNetworkValidation.selectionNearBuildBox(packet.buildBoxPos(), min, max)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.plan_area.too_far"));
            return;
        }

        List<PlannerMaterialScanResponsePacket.ContainerBlocks> containers = new ArrayList<>();
        for (BlockPos containerPos : PlannerNetworkValidation.adjacentContainers(level, packet.buildBoxPos())) {
            Map<String, Integer> blocks = scanContainerBlocks(level, containerPos);
            containers.add(new PlannerMaterialScanResponsePacket.ContainerBlocks(containerPos, blocks));
        }
        containers.sort(Comparator.comparing(container -> container.pos().asLong()));

        Map<String, Integer> sourceBlocks = packet.operation() == PlanOperation.REPLACE ? scanSelectionBlocks(level, min, max) : Map.of();
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PlannerMaterialScanResponsePacket(packet.buildBoxPos(), min, max, packet.operation(), containers, sourceBlocks));
    }

    private static Map<String, Integer> scanContainerBlocks(ServerLevel level, BlockPos containerPos) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (GenericContainerAccess.SlotSnapshot slot : GenericContainerAccess.snapshotSlots(level, containerPos)) {
            if (slot.stack().getItem() instanceof BlockItem blockItem) {
                String blockId = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).toString();
                counts.merge(blockId, slot.stack().getCount(), Integer::sum);
            }
        }
        return sortCounts(counts);
    }

    private static Map<String, Integer> scanSelectionBlocks(ServerLevel level, BlockPos min, BlockPos max) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (!level.isLoaded(pos)) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) {
                String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
                counts.merge(blockId, 1, Integer::sum);
            }
        }
        return sortCounts(counts);
    }

    private static Map<String, Integer> sortCounts(Map<String, Integer> counts) {
        Map<String, Integer> sorted = new LinkedHashMap<>();
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> sorted.put(entry.getKey(), entry.getValue()));
        return sorted;
    }
}

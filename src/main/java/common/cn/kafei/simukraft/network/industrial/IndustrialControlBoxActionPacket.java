package common.cn.kafei.simukraft.network.industrial;

import common.cn.kafei.simukraft.building.BuildingIntegrityService;
import common.cn.kafei.simukraft.building.PlacedBuildingRecord;
import common.cn.kafei.simukraft.industrial.IndustrialControlBoxService;
import common.cn.kafei.simukraft.network.rts.RtsRemoteMenuAccess;
import common.cn.kafei.simukraft.network.toast.InfoToastService;
import common.cn.kafei.simukraft.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import static common.cn.kafei.simukraft.network.ModNetwork.CHANNEL;
import java.util.Locale;
import java.util.function.Supplier;

@SuppressWarnings("Null")
public record IndustrialControlBoxActionPacket(BlockPos pos, Action action, String recipeId) {
    public static void encode(IndustrialControlBoxActionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeEnum(packet.action());
        buffer.writeUtf(packet.recipeId(), 256);
    }

    public static IndustrialControlBoxActionPacket decode(FriendlyByteBuf buffer) {
        return new IndustrialControlBoxActionPacket(buffer.readBlockPos(), buffer.readEnum(Action.class), buffer.readUtf(256));
    }

    public static void handle(IndustrialControlBoxActionPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            ServerPlayer player = context.get().getSender();
            if (!player.blockPosition().closerThan(packet.pos(), 16.0D)
                    && !RtsRemoteMenuAccess.hasAccess(player, packet.pos())) {
                InfoToastService.warning(player, Component.translatable("message.simukraft.industrial_control_box.too_far"));
                return;
            }
            if (!level.getBlockState(packet.pos()).is(ModBlocks.INDUSTRIAL_CONTROL_BOX.get())) {
                InfoToastService.warning(player, Component.translatable("message.simukraft.industrial_control_box.not_found"));
                return;
            }
            switch (packet.action()) {
                case SELECT_RECIPE -> IndustrialControlBoxService.selectRecipe(level, packet.pos(), packet.recipeId());
                case TOGGLE_RUN -> IndustrialControlBoxService.toggleRunning(level, packet.pos());
                case FIRE -> IndustrialControlBoxService.fireWorker(level, packet.pos());
                case REPAIR_BUILDING -> repairBuilding(level, player, packet.pos());
            }
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), IndustrialControlBoxViewUpdatePacket.from(IndustrialControlBoxService.buildView(level, packet.pos())));
        }
    }

    private static void repairBuilding(ServerLevel level, ServerPlayer player, BlockPos pos) {
        PlacedBuildingRecord building = IndustrialControlBoxService.resolveBuilding(level, pos);
        BuildingIntegrityService.RepairResult result = BuildingIntegrityService.repair(level, player, building);
        switch (result.status()) {
            case SUCCESS -> InfoToastService.success(player, repairSuccessMessage(result));
            case NO_REPAIR_NEEDED -> InfoToastService.success(player, Component.translatable("message.simukraft.building_integrity.no_repair_needed"));
            case NOT_ENOUGH_FUNDS -> InfoToastService.warning(player, Component.translatable("message.simukraft.building_integrity.not_enough_funds", money(result.cost())));
            case MATERIALS_REQUIRED -> InfoToastService.warning(player, Component.translatable("message.simukraft.building_integrity.materials_required", result.manualRepairBlocks()));
            case UNAVAILABLE -> InfoToastService.warning(player, Component.translatable("message.simukraft.building_integrity.unavailable"));
            case NO_BUILDING -> InfoToastService.warning(player, Component.translatable("message.simukraft.building_integrity.no_building"));
        }
    }

    private static Component repairSuccessMessage(BuildingIntegrityService.RepairResult result) {
        if (result.manualRepairBlocks() > 0) {
            return Component.translatable("message.simukraft.building_integrity.repaired_with_manual", result.repairedBlocks(), money(result.cost()), result.manualRepairBlocks());
        }
        return Component.translatable("message.simukraft.building_integrity.repaired", result.repairedBlocks(), money(result.cost()));
    }

    private static String money(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    public enum Action {
        SELECT_RECIPE,
        TOGGLE_RUN,
        FIRE,
        REPAIR_BUILDING
    }
}

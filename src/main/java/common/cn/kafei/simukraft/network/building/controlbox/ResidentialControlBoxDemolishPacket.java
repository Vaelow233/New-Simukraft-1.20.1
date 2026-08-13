package common.cn.kafei.simukraft.network.building.controlbox;

import common.cn.kafei.simukraft.building.PlacedBuildingDemolitionService;
import common.cn.kafei.simukraft.building.PlacedBuildingRecord;
import common.cn.kafei.simukraft.building.controlbox.ResidentialControlBoxService;
import common.cn.kafei.simukraft.network.rts.RtsRemoteMenuAccess;
import common.cn.kafei.simukraft.city.CityData;
import common.cn.kafei.simukraft.city.CityManager;
import common.cn.kafei.simukraft.city.CityPermissionLevel;
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
import java.util.function.Supplier;

@SuppressWarnings("Null")
public record ResidentialControlBoxDemolishPacket(BlockPos pos) {

    public static void encode(ResidentialControlBoxDemolishPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    public static ResidentialControlBoxDemolishPacket decode(FriendlyByteBuf buffer) {
        return new ResidentialControlBoxDemolishPacket(buffer.readBlockPos());
    }

    public static void handle(ResidentialControlBoxDemolishPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            handleFor(level, context.get().getSender(), packet.pos());
        }
    }

    private static void handleFor(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (!player.blockPosition().closerThan(pos, 8.0D) && !RtsRemoteMenuAccess.hasAccess(player, pos)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.residential_control_box.too_far"));
            return;
        }
        if (!level.getBlockState(pos).is(ModBlocks.RESIDENTIAL_CONTROL_BOX.get())) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.residential_control_box.not_found"));
            return;
        }
        PlacedBuildingRecord building = ResidentialControlBoxService.findBuilding(level, pos);
        if (building == null) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.residential_control_box.no_building"));
            return;
        }
        // 鉴权：OP 或城市官员及以上权限
        if (!player.hasPermissions(2)) {
            if (building.cityId() == null) {
                InfoToastService.warning(player, Component.translatable("message.simukraft.no_permission"));
                return;
            }
            CityData city = CityManager.get(level).getCity(building.cityId()).orElse(null);
            if (city == null || !city.hasPermission(player.getUUID(), CityPermissionLevel.OFFICIAL)) {
                InfoToastService.warning(player, Component.translatable("message.simukraft.no_permission"));
                return;
            }
        }
        if (PlacedBuildingDemolitionService.demolish(level, building)) {
            InfoToastService.success(player, Component.translatable("message.simukraft.residential_control_box.demolished"));
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), ResidentialControlBoxOpenResponsePacket.empty(pos));
        }
    }
}

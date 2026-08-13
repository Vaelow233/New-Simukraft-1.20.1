package common.cn.kafei.simukraft.network;

import common.cn.kafei.simukraft.SimuKraft;
import common.cn.kafei.simukraft.network.building.BuildBoxStartConstructionPacket;
import common.cn.kafei.simukraft.network.building.BuildingCacheReloadPacket;
import common.cn.kafei.simukraft.network.building.controlbox.*;
import common.cn.kafei.simukraft.network.citizen.info.CitizenBehaviorActionPacket;
import common.cn.kafei.simukraft.network.citizen.manage.CityCitizenManageActionPacket;
import common.cn.kafei.simukraft.network.citizen.manage.CityCitizenManageRequestPacket;
import common.cn.kafei.simukraft.network.citizen.manage.CityCitizenManageResponsePacket;
import common.cn.kafei.simukraft.network.city.chunk.CityChunkBatchPurchasePacket;
import common.cn.kafei.simukraft.network.city.chunk.CityChunkBatchReleasePacket;
import common.cn.kafei.simukraft.network.city.chunk.CityChunkPurchasePacket;
import common.cn.kafei.simukraft.network.city.chunk.CityChunkSyncPacket;
import common.cn.kafei.simukraft.network.city.core.CityCoreCreateCityPacket;
import common.cn.kafei.simukraft.network.city.core.CityCoreManageCityPacket;
import common.cn.kafei.simukraft.network.city.core.CityCoreOpenRequestPacket;
import common.cn.kafei.simukraft.network.city.core.CityCoreOpenResponsePacket;
import common.cn.kafei.simukraft.network.city.map.CityCoreMapRequestPacket;
import common.cn.kafei.simukraft.network.city.map.CityCoreMapResponsePacket;
import common.cn.kafei.simukraft.network.city.member.CityCoreMemberActionPacket;
import common.cn.kafei.simukraft.network.city.member.CityCoreMembersRequestPacket;
import common.cn.kafei.simukraft.network.city.member.CityCoreMembersResponsePacket;
import common.cn.kafei.simukraft.network.commercial.*;
import common.cn.kafei.simukraft.network.config.ServerConfigSavePacket;
import common.cn.kafei.simukraft.network.farmland.*;
import common.cn.kafei.simukraft.network.hud.HudSyncPacket;
import common.cn.kafei.simukraft.network.industrial.*;
import common.cn.kafei.simukraft.network.logistics.*;
import common.cn.kafei.simukraft.network.manifest.ManifestTogglePacket;
import common.cn.kafei.simukraft.network.medical.MedicalControlBoxDemolishPacket;
import common.cn.kafei.simukraft.network.medical.MedicalControlBoxOpenRequestPacket;
import common.cn.kafei.simukraft.network.medical.MedicalControlBoxOpenResponsePacket;
import common.cn.kafei.simukraft.network.npc.hire.NpcHireAssignPacket;
import common.cn.kafei.simukraft.network.npc.hire.NpcHireFirePacket;
import common.cn.kafei.simukraft.network.npc.hire.NpcHireListRequestPacket;
import common.cn.kafei.simukraft.network.npc.hire.NpcHireListResponsePacket;
import common.cn.kafei.simukraft.network.npc.state.EmploymentStateRequestPacket;
import common.cn.kafei.simukraft.network.npc.state.EmploymentStateResponsePacket;
import common.cn.kafei.simukraft.network.path.NpcPathDebugRequestPacket;
import common.cn.kafei.simukraft.network.path.NpcPathDebugSyncPacket;
import common.cn.kafei.simukraft.network.planner.CreatePlanningTaskPacket;
import common.cn.kafei.simukraft.network.planner.PlannerMaterialScanRequestPacket;
import common.cn.kafei.simukraft.network.planner.PlannerMaterialScanResponsePacket;
import common.cn.kafei.simukraft.network.toast.InfoToastPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("Null")
public final class ModNetwork {
    private static final String NETWORK_VERSION = "23";

    public static SimpleChannel CHANNEL;

    private ModNetwork() {
    }

    public static void registerPayload() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                ResourceLocation.fromNamespaceAndPath(SimuKraft.MOD_ID,
                "main"),
                () -> NETWORK_VERSION,
                NETWORK_VERSION::equals,
                NETWORK_VERSION::equals
        );
        registerMessage(1,
                CityCoreOpenRequestPacket.class,
                CityCoreOpenRequestPacket::encode,
                CityCoreOpenRequestPacket::decode,
                CityCoreOpenRequestPacket::handle
        );
        registerMessage(2,
                CityCoreCreateCityPacket.class,
                CityCoreCreateCityPacket::encode,
                CityCoreCreateCityPacket::decode,
                CityCoreCreateCityPacket::handle
        );
        registerMessage(3,
                CityCoreManageCityPacket.class,
                CityCoreManageCityPacket::encode,
                CityCoreManageCityPacket::decode,
                CityCoreManageCityPacket::handle
        );
        registerMessage(4,
                CityCoreMembersRequestPacket.class,
                CityCoreMembersRequestPacket::encode,
                CityCoreMembersRequestPacket::decode,
                CityCoreMembersRequestPacket::handle
        );
        registerMessage(5,
                CityCoreMemberActionPacket.class,
                CityCoreMemberActionPacket::encode,
                CityCoreMemberActionPacket::decode,
                CityCoreMemberActionPacket::handle
        );
        registerMessage(6,
                CityCitizenManageRequestPacket.class,
                CityCitizenManageRequestPacket::encode,
                CityCitizenManageRequestPacket::decode,
                CityCitizenManageRequestPacket::handle
        );
        registerMessage(7,
                CityCitizenManageActionPacket.class,
                CityCitizenManageActionPacket::encode,
                CityCitizenManageActionPacket::decode,
                CityCitizenManageActionPacket::handle
        );
        registerMessage(8,
                CitizenBehaviorActionPacket.class,
                CitizenBehaviorActionPacket::encode,
                CitizenBehaviorActionPacket::decode,
                CitizenBehaviorActionPacket::handle
        );
        registerMessage(9,
                CityCoreMapRequestPacket.class,
                CityCoreMapRequestPacket::encode,
                CityCoreMapRequestPacket::decode,
                CityCoreMapRequestPacket::handle
        );
        registerMessage(10,
                CityChunkPurchasePacket.class,
                CityChunkPurchasePacket::encode,
                CityChunkPurchasePacket::decode,
                CityChunkPurchasePacket::handle
        );
        registerMessage(11,
                CityChunkBatchPurchasePacket.class,
                CityChunkBatchPurchasePacket::encode,
                CityChunkBatchPurchasePacket::decode,
                CityChunkBatchPurchasePacket::handle
        );
        registerMessage(12,
                CityChunkBatchReleasePacket.class,
                CityChunkBatchReleasePacket::encode,
                CityChunkBatchReleasePacket::decode,
                CityChunkBatchReleasePacket::handle
        );
        registerMessage(13,
                EmploymentStateRequestPacket.class,
                EmploymentStateRequestPacket::encode,
                EmploymentStateRequestPacket::decode,
                EmploymentStateRequestPacket::handle
        );
        registerMessage(14,
                NpcHireListRequestPacket.class,
                NpcHireListRequestPacket::encode,
                NpcHireListRequestPacket::decode,
                NpcHireListRequestPacket::handle
        );
        registerMessage(15,
                NpcHireAssignPacket.class,
                NpcHireAssignPacket::encode,
                NpcHireAssignPacket::decode,
                NpcHireAssignPacket::handle
        );
        registerMessage(16,
                NpcHireFirePacket.class,
                NpcHireFirePacket::encode,
                NpcHireFirePacket::decode,
                NpcHireFirePacket::handle
        );
        registerMessage(17,
                BuildBoxStartConstructionPacket.class,
                BuildBoxStartConstructionPacket::encode,
                BuildBoxStartConstructionPacket::decode,
                BuildBoxStartConstructionPacket::handle
        );
        registerMessage(18,
                ResidentialControlBoxOpenRequestPacket.class,
                ResidentialControlBoxOpenRequestPacket::encode,
                ResidentialControlBoxOpenRequestPacket::decode,
                ResidentialControlBoxOpenRequestPacket::handle
        );
        registerMessage(19,
                ResidentialControlBoxDemolishPacket.class,
                ResidentialControlBoxDemolishPacket::encode,
                ResidentialControlBoxDemolishPacket::decode,
                ResidentialControlBoxDemolishPacket::handle
        );
        registerMessage(20,
                ResidentialControlBoxOccupancyPacket.class,
                ResidentialControlBoxOccupancyPacket::encode,
                ResidentialControlBoxOccupancyPacket::decode,
                ResidentialControlBoxOccupancyPacket::handle
        );
        registerMessage(21,
                FarmlandBoxOpenRequestPacket.class,
                FarmlandBoxOpenRequestPacket::encode,
                FarmlandBoxOpenRequestPacket::decode,
                FarmlandBoxOpenRequestPacket::handle
        );
        registerMessage(22,
                FarmlandBoxActionPacket.class,
                FarmlandBoxActionPacket::encode,
                FarmlandBoxActionPacket::decode,
                FarmlandBoxActionPacket::handle
        );
        registerMessage(23,
                FarmlandBoxSetCropPacket.class,
                FarmlandBoxSetCropPacket::encode,
                FarmlandBoxSetCropPacket::decode,
                FarmlandBoxSetCropPacket::handle
        );
        registerMessage(24,
                FarmlandBoxSetAreaPacket.class,
                FarmlandBoxSetAreaPacket::encode,
                FarmlandBoxSetAreaPacket::decode,
                FarmlandBoxSetAreaPacket::handle
        );
        registerMessage(25,
                FarmlandBoxBoundsRequestPacket.class,
                FarmlandBoxBoundsRequestPacket::encode,
                FarmlandBoxBoundsRequestPacket::decode,
                FarmlandBoxBoundsRequestPacket::handle
        );
        registerMessage(26,
                IndustrialControlBoxOpenRequestPacket.class,
                IndustrialControlBoxOpenRequestPacket::encode,
                IndustrialControlBoxOpenRequestPacket::decode,
                IndustrialControlBoxOpenRequestPacket::handle
        );
        registerMessage(27,
                IndustrialControlBoxActionPacket.class,
                IndustrialControlBoxActionPacket::encode,
                IndustrialControlBoxActionPacket::decode,
                IndustrialControlBoxActionPacket::handle
        );
        registerMessage(28,
                IndustrialControlBoxDemolishPacket.class,
                IndustrialControlBoxDemolishPacket::encode,
                IndustrialControlBoxDemolishPacket::decode,
                IndustrialControlBoxDemolishPacket::handle
        );
        registerMessage(29,
                CommercialControlBoxOpenRequestPacket.class,
                CommercialControlBoxOpenRequestPacket::encode,
                CommercialControlBoxOpenRequestPacket::decode,
                CommercialControlBoxOpenRequestPacket::handle
        );
        registerMessage(30,
                MedicalControlBoxOpenRequestPacket.class,
                MedicalControlBoxOpenRequestPacket::encode,
                MedicalControlBoxOpenRequestPacket::decode,
                MedicalControlBoxOpenRequestPacket::handle
        );
        registerMessage(31,
                MedicalControlBoxDemolishPacket.class,
                MedicalControlBoxDemolishPacket::encode,
                MedicalControlBoxDemolishPacket::decode,
                MedicalControlBoxDemolishPacket::handle
        );
        registerMessage(32,
                CommercialControlBoxActionPacket.class,
                CommercialControlBoxActionPacket::encode,
                CommercialControlBoxActionPacket::decode,
                CommercialControlBoxActionPacket::handle
        );
        registerMessage(33,
                CommercialControlBoxDemolishPacket.class,
                CommercialControlBoxDemolishPacket::encode,
                CommercialControlBoxDemolishPacket::decode,
                CommercialControlBoxDemolishPacket::handle
        );
        registerMessage(34,
                CommercialTradePacket.class,
                CommercialTradePacket::encode,
                CommercialTradePacket::decode,
                CommercialTradePacket::handle
        );
        registerMessage(35,
                LogisticsServerBoxOpenRequestPacket.class,
                LogisticsServerBoxOpenRequestPacket::encode,
                LogisticsServerBoxOpenRequestPacket::decode,
                LogisticsServerBoxOpenRequestPacket::handle
        );
        registerMessage(36,
                LogisticsClientBoxOpenRequestPacket.class,
                LogisticsClientBoxOpenRequestPacket::encode,
                LogisticsClientBoxOpenRequestPacket::decode,
                LogisticsClientBoxOpenRequestPacket::handle
        );
        registerMessage(37,
                LogisticsBoxActionPacket.class,
                LogisticsBoxActionPacket::encode,
                LogisticsBoxActionPacket::decode,
                LogisticsBoxActionPacket::handle
        );
        registerMessage(38,
                LogisticsWarehouseGridOpenRequestPacket.class,
                LogisticsWarehouseGridOpenRequestPacket::encode,
                LogisticsWarehouseGridOpenRequestPacket::decode,
                LogisticsWarehouseGridOpenRequestPacket::handle
        );
        registerMessage(39,
                LogisticsWarehouseGridRequestPacket.class,
                LogisticsWarehouseGridRequestPacket::encode,
                LogisticsWarehouseGridRequestPacket::decode,
                LogisticsWarehouseGridRequestPacket::handle
        );
        registerMessage(40,
                LogisticsWarehouseGridExtractPacket.class,
                LogisticsWarehouseGridExtractPacket::encode,
                LogisticsWarehouseGridExtractPacket::decode,
                LogisticsWarehouseGridExtractPacket::handle
        );
        registerMessage(41,
                LogisticsWarehouseGridInsertPacket.class,
                LogisticsWarehouseGridInsertPacket::encode,
                LogisticsWarehouseGridInsertPacket::decode,
                LogisticsWarehouseGridInsertPacket::handle
        );
        registerMessage(42,
                LogisticsWarehouseGridShiftClickPacket.class,
                LogisticsWarehouseGridShiftClickPacket::encode,
                LogisticsWarehouseGridShiftClickPacket::decode,
                LogisticsWarehouseGridShiftClickPacket::handle
        );
        registerMessage(43,
                PlannerMaterialScanRequestPacket.class,
                PlannerMaterialScanRequestPacket::encode,
                PlannerMaterialScanRequestPacket::decode,
                PlannerMaterialScanRequestPacket::handle
        );
        registerMessage(44,
                CreatePlanningTaskPacket.class,
                CreatePlanningTaskPacket::encode,
                CreatePlanningTaskPacket::decode,
                CreatePlanningTaskPacket::handle
        );
        registerMessage(45,
                NpcPathDebugRequestPacket.class,
                NpcPathDebugRequestPacket::encode,
                NpcPathDebugRequestPacket::decode,
                NpcPathDebugRequestPacket::handle
        );
        registerMessage(46,
                ManifestTogglePacket.class,
                ManifestTogglePacket::encode,
                ManifestTogglePacket::decode,
                ManifestTogglePacket::handle
        );
        registerMessage(47,
                ServerConfigSavePacket.class,
                ServerConfigSavePacket::encode,
                ServerConfigSavePacket::decode,
                ServerConfigSavePacket::handle
        );
        registerMessage(48,
                CityCoreOpenResponsePacket.class,
                CityCoreOpenResponsePacket::encode,
                CityCoreOpenResponsePacket::decode,
                CityCoreOpenResponsePacket::handle
        );
        registerMessage(49,
                CityCoreMembersResponsePacket.class,
                CityCoreMembersResponsePacket::encode,
                CityCoreMembersResponsePacket::decode,
                CityCoreMembersResponsePacket::handle
        );
        registerMessage(50,
                CityCitizenManageResponsePacket.class,
                CityCitizenManageResponsePacket::encode,
                CityCitizenManageResponsePacket::decode,
                CityCitizenManageResponsePacket::handle
        );
        registerMessage(51,
                CityCoreMapResponsePacket.class,
                CityCoreMapResponsePacket::encode,
                CityCoreMapResponsePacket::decode,
                CityCoreMapResponsePacket::handle
        );
        registerMessage(52,
                CityChunkSyncPacket.class,
                CityChunkSyncPacket::encode,
                CityChunkSyncPacket::decode,
                CityChunkSyncPacket::handle
        );
        registerMessage(53,
                NpcHireListResponsePacket.class,
                NpcHireListResponsePacket::encode,
                NpcHireListResponsePacket::decode,
                NpcHireListResponsePacket::handle
        );
        registerMessage(54,
                EmploymentStateResponsePacket.class,
                EmploymentStateResponsePacket::encode,
                EmploymentStateResponsePacket::decode,
                EmploymentStateResponsePacket::handle
        );
        registerMessage(55,
                HudSyncPacket.class,
                HudSyncPacket::encode,
                HudSyncPacket::decode,
                HudSyncPacket::handle
        );
        registerMessage(56,
                BuildingCacheReloadPacket.class,
                BuildingCacheReloadPacket::encode,
                BuildingCacheReloadPacket::decode,
                BuildingCacheReloadPacket::handle
        );
        registerMessage(57,
                ResidentialControlBoxBoundsUpdatePacket.class,
                ResidentialControlBoxBoundsUpdatePacket::encode,
                ResidentialControlBoxBoundsUpdatePacket::decode,
                ResidentialControlBoxBoundsUpdatePacket::handle
        );
        registerMessage(58,
                ResidentialControlBoxViewUpdatePacket.class,
                ResidentialControlBoxViewUpdatePacket::encode,
                ResidentialControlBoxViewUpdatePacket::decode,
                ResidentialControlBoxViewUpdatePacket::handle
        );
        registerMessage(59,
                ResidentialControlBoxOpenResponsePacket.class,
                ResidentialControlBoxOpenResponsePacket::encode,
                ResidentialControlBoxOpenResponsePacket::decode,
                ResidentialControlBoxOpenResponsePacket::handle
        );
        registerMessage(60,
                FarmlandBoxOpenResponsePacket.class,
                FarmlandBoxOpenResponsePacket::encode,
                FarmlandBoxOpenResponsePacket::decode,
                FarmlandBoxOpenResponsePacket::handle
        );
        registerMessage(61,
                FarmlandBoxBoundsResponsePacket.class,
                FarmlandBoxBoundsResponsePacket::encode,
                FarmlandBoxBoundsResponsePacket::decode,
                FarmlandBoxBoundsResponsePacket::handle
        );
        registerMessage(62,
                IndustrialControlBoxOpenResponsePacket.class,
                IndustrialControlBoxOpenResponsePacket::encode,
                IndustrialControlBoxOpenResponsePacket::decode,
                IndustrialControlBoxOpenResponsePacket::handle
        );
        registerMessage(63,
                IndustrialControlBoxViewUpdatePacket.class,
                IndustrialControlBoxViewUpdatePacket::encode,
                IndustrialControlBoxViewUpdatePacket::decode,
                IndustrialControlBoxViewUpdatePacket::handle
        );
        registerMessage(64,
                CommercialControlBoxOpenResponsePacket.class,
                CommercialControlBoxOpenResponsePacket::encode,
                CommercialControlBoxOpenResponsePacket::decode,
                CommercialControlBoxOpenResponsePacket::handle
        );
        registerMessage(65,
                MedicalControlBoxOpenResponsePacket.class,
                MedicalControlBoxOpenResponsePacket::encode,
                MedicalControlBoxOpenResponsePacket::decode,
                MedicalControlBoxOpenResponsePacket::handle
        );
        registerMessage(66,
                CommercialTradeOpenResponsePacket.class,
                CommercialTradeOpenResponsePacket::encode,
                CommercialTradeOpenResponsePacket::decode,
                CommercialTradeOpenResponsePacket::handle
        );
        registerMessage(67,
                LogisticsServerBoxOpenResponsePacket.class,
                LogisticsServerBoxOpenResponsePacket::encode,
                LogisticsServerBoxOpenResponsePacket::decode,
                LogisticsServerBoxOpenResponsePacket::handle
        );
        registerMessage(68,
                LogisticsClientBoxOpenResponsePacket.class,
                LogisticsClientBoxOpenResponsePacket::encode,
                LogisticsClientBoxOpenResponsePacket::decode,
                LogisticsClientBoxOpenResponsePacket::handle
        );
        registerMessage(69,
                LogisticsWarehouseGridResponsePacket.class,
                LogisticsWarehouseGridResponsePacket::encode,
                LogisticsWarehouseGridResponsePacket::decode,
                LogisticsWarehouseGridResponsePacket::handle
        );
        registerMessage(70,
                PlannerMaterialScanResponsePacket.class,
                PlannerMaterialScanResponsePacket::encode,
                PlannerMaterialScanResponsePacket::decode,
                PlannerMaterialScanResponsePacket::handle
        );
        registerMessage(71,
                NpcPathDebugSyncPacket.class,
                NpcPathDebugSyncPacket::encode,
                NpcPathDebugSyncPacket::decode,
                NpcPathDebugSyncPacket::handle
        );
        registerMessage(72,
                InfoToastPacket.class,
                InfoToastPacket::encode,
                InfoToastPacket::decode,
                InfoToastPacket::handle
        );
    }

    private static <MSG> void registerMessage(int discriminator,
                                              Class<MSG> messageType,
                                              BiConsumer<MSG, FriendlyByteBuf> encoder,
                                              Function<FriendlyByteBuf, MSG> decoder,
                                              BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler) {
        CHANNEL.registerMessage(discriminator, messageType, encoder, decoder, (message, contextSupplier) -> {
            NetworkEvent.Context context = contextSupplier.get();
            try {
                if (context.getDirection().getReceptionSide().isServer()) {
                    context.enqueueWork(() -> handler.accept(message, () -> context));
                } else {
                    handler.accept(message, () -> context);
                }
            } finally {
                context.setPacketHandled(true);
            }
        });
    }
}

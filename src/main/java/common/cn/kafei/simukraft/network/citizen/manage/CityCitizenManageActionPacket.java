package common.cn.kafei.simukraft.network.citizen.manage;

import common.cn.kafei.simukraft.citizen.CitizenData;
import common.cn.kafei.simukraft.citizen.CitizenManager;
import common.cn.kafei.simukraft.citizen.CitizenService;
import common.cn.kafei.simukraft.city.CityData;
import common.cn.kafei.simukraft.city.CityService;
import common.cn.kafei.simukraft.job.CitizenEmploymentService;
import common.cn.kafei.simukraft.network.city.core.CityCoreAccessValidator;
import common.cn.kafei.simukraft.network.toast.InfoToastService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * CityCitizenManageActionPacket: 在“市民管理”界面对指定市民执行解雇/流放（客户端 -> 服务端）。
 */
@SuppressWarnings("Null")
public record CityCitizenManageActionPacket(BlockPos pos, Action action, UUID citizenId) {
    public enum Action {
        DISMISS,
        EXILE
    }

    public static void encode(CityCitizenManageActionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeEnum(packet.action());
        buffer.writeUUID(packet.citizenId());
    }

    public static CityCitizenManageActionPacket decode(FriendlyByteBuf buffer) {
        return new CityCitizenManageActionPacket(buffer.readBlockPos(), buffer.readEnum(Action.class), buffer.readUUID());
    }

    public static void handle(CityCitizenManageActionPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            handleAction(level, context.get().getSender(), packet);
        }
    }

    private static void handleAction(ServerLevel level, ServerPlayer player, CityCitizenManageActionPacket packet) {
        if (!CityCoreAccessValidator.requireAccess(level, player, packet.pos())) {
            return;
        }
        Optional<CityData> cityOptional = CityService.findCityByCorePosForPlayer(level, packet.pos(), player.getUUID());
        if (cityOptional.isEmpty()) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.city_core.not_found"));
            return;
        }
        CityData city = cityOptional.get();
        if (!CityService.canManageCity(city, player.getUUID())) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.citizen_manage.no_permission"));
            return;
        }
        Optional<CitizenData> citizenOptional = CitizenService.findCitizen(level, packet.citizenId());
        if (citizenOptional.isEmpty() || citizenOptional.get().dead() || !CitizenService.belongsToCity(citizenOptional.get(), city.cityId())) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.citizen_manage.target_invalid"));
            return;
        }
        String name = citizenName(citizenOptional.get());
        switch (packet.action()) {
            case DISMISS -> {
                Optional<CitizenData> fired = CitizenEmploymentService.fire(level, packet.citizenId(), null, null, null, "city_core_dismiss");
                if (fired.isPresent()) {
                    InfoToastService.success(player, Component.translatable("message.simukraft.citizen_manage.dismissed", name));
                } else {
                    InfoToastService.warning(player, Component.translatable("message.simukraft.citizen_manage.dismiss_failed", name));
                }
            }
            case EXILE -> {
                CitizenManager.get(level).removeCitizen(packet.citizenId());
                InfoToastService.success(player, Component.translatable("message.simukraft.citizen_manage.exiled", name));
            }
        }
        CityCitizenManageRequestPacket.sendCitizens(level, player, packet.pos());
    }

    private static String citizenName(CitizenData citizen) {
        String name = citizen.name();
        return name != null && !name.isBlank() ? name : "Unknown";
    }
}

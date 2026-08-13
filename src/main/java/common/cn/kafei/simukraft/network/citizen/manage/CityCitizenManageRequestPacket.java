package common.cn.kafei.simukraft.network.citizen.manage;

import common.cn.kafei.simukraft.citizen.CitizenData;
import common.cn.kafei.simukraft.citizen.CitizenService;
import common.cn.kafei.simukraft.city.CityData;
import common.cn.kafei.simukraft.city.CityService;
import common.cn.kafei.simukraft.network.city.core.CityCoreAccessValidator;
import common.cn.kafei.simukraft.network.toast.InfoToastService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import static common.cn.kafei.simukraft.network.ModNetwork.CHANNEL;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * CityCitizenManageRequestPacket: 请求打开城市核心“市民管理”界面（客户端 -> 服务端）。
 */
@SuppressWarnings("Null")
public record CityCitizenManageRequestPacket(BlockPos pos) {

    public static void encode(CityCitizenManageRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    public static CityCitizenManageRequestPacket decode(FriendlyByteBuf buffer) {
        return new CityCitizenManageRequestPacket(buffer.readBlockPos());
    }

    public static void handle(CityCitizenManageRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            sendCitizens(level, context.get().getSender(), packet.pos());
        }
    }

    /** sendCitizens: 构建并下发该城市当前在册（存活）市民列表，actions 复用此方法刷新界面。 */
    public static void sendCitizens(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (!CityCoreAccessValidator.requireAccess(level, player, pos)) {
            return;
        }
        Optional<CityData> cityOptional = CityService.findCityByCorePosForPlayer(level, pos, player.getUUID());
        if (cityOptional.isEmpty()) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.city_core.not_found"));
            return;
        }
        CityData city = cityOptional.get();
        boolean canManage = CityService.canManageCity(city, player.getUUID());
        List<CityCitizenManageResponsePacket.CitizenEntry> entries = CitizenService.listCitizensByCity(level, city.cityId()).stream()
                .filter(citizen -> !citizen.dead())
                .sorted(Comparator.comparing(CitizenData::name, String.CASE_INSENSITIVE_ORDER))
                .map(citizen -> new CityCitizenManageResponsePacket.CitizenEntry(
                        citizen.uuid(),
                        citizen.name(),
                        citizen.jobType().translationKey(),
                        citizen.workStatus(),
                        citizen.age(),
                        citizen.gender(),
                        citizen.skinPath()))
                .toList();
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new CityCitizenManageResponsePacket(pos, city.cityName(), canManage, entries));
    }
}

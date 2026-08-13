package common.cn.kafei.simukraft.network.npc.hire;

import common.cn.kafei.simukraft.citizen.CitizenData;
import common.cn.kafei.simukraft.citizen.CitizenService;
import common.cn.kafei.simukraft.city.group.CityGroupMessageService;
import common.cn.kafei.simukraft.commercial.CommercialConstants;
import common.cn.kafei.simukraft.commercial.CommercialControlBoxService;
import common.cn.kafei.simukraft.city.group.CityGroupMessageService;
import common.cn.kafei.simukraft.network.commercial.CommercialControlBoxOpenResponsePacket;
import common.cn.kafei.simukraft.job.CitizenEmploymentService;
import common.cn.kafei.simukraft.logistics.LogisticsConstants;
import common.cn.kafei.simukraft.logistics.LogisticsControlBoxService;
import common.cn.kafei.simukraft.network.logistics.LogisticsServerBoxOpenResponsePacket;
import common.cn.kafei.simukraft.medical.MedicalControlBoxService;
import common.cn.kafei.simukraft.medical.MedicalService;
import common.cn.kafei.simukraft.mineraldrilling.MineralDrillingConstants;
import common.cn.kafei.simukraft.mineraldrilling.MineralDrillingControlBoxService;
import common.cn.kafei.simukraft.mineraldrilling.MineralDrillingMenuProvider;
import common.cn.kafei.simukraft.network.medical.MedicalControlBoxOpenResponsePacket;
import common.cn.kafei.simukraft.network.toast.InfoToastService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import static common.cn.kafei.simukraft.network.ModNetwork.CHANNEL;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("Null")
public record NpcHireFirePacket(BlockPos sourcePos, String sourceType, String role, UUID citizenId) {

    public static void encode(NpcHireFirePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.sourcePos());
        buffer.writeUtf(packet.sourceType(), 32);
        buffer.writeUtf(packet.role(), 32);
        buffer.writeUUID(packet.citizenId());
    }

    public static NpcHireFirePacket decode(FriendlyByteBuf buffer) {
        return new NpcHireFirePacket(buffer.readBlockPos(), buffer.readUtf(32), buffer.readUtf(32), buffer.readUUID());
    }

    public static void handle(NpcHireFirePacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            ServerPlayer player = context.get().getSender();
            NpcHireAccessValidator.SourceContext access = NpcHireAccessValidator.validateSource(player, level, packet.sourcePos(), packet.sourceType(), packet.role());
            if (access == null) {
                return;
            }
            Optional<CitizenData> citizenOptional = CitizenService.findCitizen(level, packet.citizenId());
            if (citizenOptional.isEmpty()) {
                InfoToastService.warning(player, Component.translatable("message.simukraft.hire_npc.not_found"));
                return;
            }
            CitizenData citizen = citizenOptional.get();
            if (!NpcHireAccessValidator.canFireCitizen(player, access, citizen)) {
                return;
            }
            CitizenEmploymentService.fire(level, citizen.uuid(), access.sourceType(), access.role(), access.sourcePos(), access.role() + "_fired");
            if (CommercialConstants.HIRE_SOURCE_TYPE.equals(access.sourceType())) {
                CommercialControlBoxService.fireWorker(level, access.sourcePos());
                CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), CommercialControlBoxOpenResponsePacket.from(CommercialControlBoxService.buildView(level, access.sourcePos())));
            }
            if (MineralDrillingConstants.HIRE_SOURCE_TYPE.equals(access.sourceType())) {
                MineralDrillingControlBoxService.fireWorker(level, access.sourcePos());
                MineralDrillingMenuProvider.open(level, player, access.sourcePos());
            }
            if (LogisticsConstants.SERVER_SOURCE_TYPE.equals(access.sourceType())) {
                CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), LogisticsServerBoxOpenResponsePacket.from(LogisticsControlBoxService.buildServerView(level, access.sourcePos())));
            }
            if (MedicalControlBoxService.HIRE_SOURCE_TYPE.equals(access.sourceType())) {
                MedicalService.releasePatientsForControlBox(level, access.sourcePos());
                CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), MedicalControlBoxOpenResponsePacket.from(MedicalControlBoxService.buildView(level, access.sourcePos())));
            }
            CityGroupMessageService.successToCity(level, access.cityId(), Component.translatable("message.simukraft.fire_npc.success", citizen.name()));
        }
    }
}

package common.cn.kafei.simukraft.network.npc.hire;

import common.cn.kafei.simukraft.citizen.CitizenLevelService;
import common.cn.kafei.simukraft.citizen.CitizenService;
import common.cn.kafei.simukraft.citizen.CitizenSkillSnapshot;
import common.cn.kafei.simukraft.citizen.CitizenTeleportService;
import common.cn.kafei.simukraft.entity.CitizenEntity;
import common.cn.kafei.simukraft.job.CitizenEmploymentService;
import common.cn.kafei.simukraft.job.CityJobMobilityService;
import common.cn.kafei.simukraft.job.CityJobType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import static common.cn.kafei.simukraft.network.ModNetwork.CHANNEL;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("Null")
public record NpcHireListRequestPacket(BlockPos sourcePos, String sourceType, String role) {

    public static void encode(NpcHireListRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.sourcePos());
        buffer.writeUtf(packet.sourceType(), 32);
        buffer.writeUtf(packet.role(), 32);
    }

    public static NpcHireListRequestPacket decode(FriendlyByteBuf buffer) {
        return new NpcHireListRequestPacket(buffer.readBlockPos(), buffer.readUtf(32), buffer.readUtf(32));
    }

    public static void handle(NpcHireListRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            ServerPlayer player = context.get().getSender();
            NpcHireAccessValidator.SourceContext access = NpcHireAccessValidator.validateSource(player, level, packet.sourcePos(), packet.sourceType(), packet.role());
            if (access == null) {
                return;
            }
            UUID workplaceId = CitizenEmploymentService.workplaceId(access.sourceType(), access.role(), access.sourcePos());
            UUID assignedCitizenId = CitizenService.findAssignedCitizen(level, workplaceId);
            CityJobType requestedJobType = CityJobMobilityService.resolveHireRole(access.role());
            List<NpcHireListResponsePacket.HireCandidate> candidates = CitizenService.listHireableCitizens(level).stream()
                    .filter(data -> NpcHireAccessValidator.isHireCandidateForSource(data, access))
                    .map(data -> {
                        CitizenSkillSnapshot skill = CitizenLevelService.snapshot(data, requestedJobType);
                        CitizenEntity entity = CitizenTeleportService.findCitizenEntity(level, data.uuid());
                        return new NpcHireListResponsePacket.HireCandidate(
                                data.uuid(),
                                data.name(),
                                data.gender(),
                                data.age(),
                                data.health(),
                                entity != null ? entity.getHungerValue() : CitizenEntity.DEFAULT_HUNGER,
                                data.skinPath(),
                                data.jobType().name(),
                                data.workStatus(),
                                skill.level(),
                                skill.xp(),
                                skill.maxLevel()
                        );
                    })
                    .toList();
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new NpcHireListResponsePacket(access.sourcePos(), access.sourceType(), access.role(), assignedCitizenId, candidates));
        }
    }
}

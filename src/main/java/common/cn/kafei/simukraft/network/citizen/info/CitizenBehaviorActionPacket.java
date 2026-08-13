package common.cn.kafei.simukraft.network.citizen.info;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIContainer;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.HolderAdapter;
import common.cn.kafei.simukraft.citizen.CitizenInfoMenuHolder;
import common.cn.kafei.simukraft.entity.CitizenEntity;
import common.cn.kafei.simukraft.path.CitizenNavigationService;
import common.cn.kafei.simukraft.network.rts.RtsRemoteCitizenAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/** NPC 信息界面的跟随与原地停留操作。 */
public record CitizenBehaviorActionPacket(UUID citizenId, Action action) {

    public enum Action {
        TOGGLE_FOLLOW,
        TOGGLE_STAY
    }

    /** encode：写入目标 UUID 和有限枚举序号。 */
    public static void encode(CitizenBehaviorActionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.citizenId());
        buffer.writeEnum(packet.action());
    }

    /** decode：读取目标 UUID 和操作类型。 */
    public static CitizenBehaviorActionPacket decode(FriendlyByteBuf buffer) {
        return new CitizenBehaviorActionPacket(buffer.readUUID(), buffer.readEnum(Action.class));
    }

    /** handle：仅允许当前打开对应 NPC 容器且仍在八格内的玩家修改行为。 */
    public static void handle(CitizenBehaviorActionPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null
                || !(player.level() instanceof ServerLevel level)
                || !(player.containerMenu instanceof ModularUIContainer menu)
                || !(menu.getModularUI().holder instanceof HolderAdapter adapter)
                || !(adapter.delegate() instanceof CitizenInfoMenuHolder holder)
                || !holder.citizenId().equals(packet.citizenId())) {
            return;
        }
        CitizenEntity citizen = holder.owner();
        if (citizen == null || !citizen.isAlive() || citizen.level() != level
                || (player.distanceToSqr(citizen) > 64.0D
                && !RtsRemoteCitizenAccess.hasInfoAccess(player, packet.citizenId()))) {
            return;
        }
        if (packet.action() == Action.TOGGLE_STAY) {
            citizen.setStayInPlace(!citizen.isStayInPlace());
            if (citizen.isStayInPlace()) {
                CitizenNavigationService.stop(level, citizen.getUUID());
            }
            return;
        }
        UUID playerId = player.getUUID();
        citizen.setFollowPlayerId(playerId.equals(citizen.getFollowPlayerId()) ? null : playerId);
    }
}

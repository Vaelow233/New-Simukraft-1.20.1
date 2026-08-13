package common.cn.kafei.simukraft.network.path;

import common.cn.kafei.simukraft.path.CitizenNavigationService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record NpcPathDebugRequestPacket(boolean visible) {

    public static void encode(NpcPathDebugRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.visible());
    }

    public static NpcPathDebugRequestPacket decode(FriendlyByteBuf buffer) {
        return new NpcPathDebugRequestPacket(buffer.readBoolean());
    }

    public static void handle(NpcPathDebugRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level && packet.visible()) {
            CitizenNavigationService.syncDebugPaths(level, context.get().getSender());
        }
    }
}

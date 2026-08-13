package common.cn.kafei.simukraft.network.logistics;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record LogisticsWarehouseGridRequestPacket(BlockPos pos) {

    /** encode: 写入仓库快照请求坐标。 */
    public static void encode(LogisticsWarehouseGridRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    /** decode: 读取仓库快照请求坐标。 */
    public static LogisticsWarehouseGridRequestPacket decode(FriendlyByteBuf buffer) {
        return new LogisticsWarehouseGridRequestPacket(buffer.readBlockPos());
    }

    /** handle: 校验权限后直接返回仓库物品快照，不打开容器 Menu。 */
    public static void handle(LogisticsWarehouseGridRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (LogisticsWarehouseGridPackets.prepareOpen(level, player, packet.pos())) {
            LogisticsWarehouseGridPackets.sendSnapshot(level, player, packet.pos());
        }
    }
}

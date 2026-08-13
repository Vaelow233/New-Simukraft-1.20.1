package common.cn.kafei.simukraft.network.logistics;

import common.cn.kafei.simukraft.logistics.menu.LogisticsWarehouseGridMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record LogisticsWarehouseGridOpenRequestPacket(BlockPos pos) {

    /** encode: 写入要打开的物流服务端盒坐标。 */
    public static void encode(LogisticsWarehouseGridOpenRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    /** decode: 读取要打开的物流服务端盒坐标。 */
    public static LogisticsWarehouseGridOpenRequestPacket decode(FriendlyByteBuf buffer) {
        return new LogisticsWarehouseGridOpenRequestPacket(buffer.readBlockPos());
    }

    /** handle: 服务端校验后打开仓库 Menu。 */
    public static void handle(LogisticsWarehouseGridOpenRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            openFor(level, context.get().getSender(), packet.pos());
        }
    }

    /** openFor: 供方块右键直接打开仓库 Menu。 */
    public static void openFor(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (LogisticsWarehouseGridPackets.prepareOpen(level, player, pos)) {
            LogisticsWarehouseGridMenuProvider.open(player, pos);
        }
    }
}

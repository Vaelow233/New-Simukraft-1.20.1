package common.cn.kafei.simukraft.network.logistics;

import common.cn.kafei.simukraft.logistics.LogisticsWarehouseInventoryService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record LogisticsWarehouseGridInsertPacket(BlockPos pos) {

    /** encode: 写入仓库存入目标坐标。 */
    public static void encode(LogisticsWarehouseGridInsertPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    /** decode: 读取仓库存入目标坐标。 */
    public static LogisticsWarehouseGridInsertPacket decode(FriendlyByteBuf buffer) {
        return new LogisticsWarehouseGridInsertPacket(buffer.readBlockPos());
    }

    /** handle: 服务端把鼠标手上的物品存入仓库。 */
    public static void handle(LogisticsWarehouseGridInsertPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (!LogisticsWarehouseGridPackets.prepareOpen(level, player, packet.pos())) {
            return;
        }
        ItemStack carried = player.containerMenu.getCarried();
        if (carried.isEmpty()) {
            return;
        }
        ItemStack remaining = LogisticsWarehouseInventoryService.insert(level, packet.pos(), carried.copy());
        int inserted = carried.getCount() - remaining.getCount();
        if (inserted <= 0) {
            return;
        }
        carried.shrink(inserted);
        if (carried.isEmpty()) {
            player.containerMenu.setCarried(ItemStack.EMPTY);
        }
        player.containerMenu.broadcastChanges();
        LogisticsWarehouseGridPackets.sendSnapshot(level, player, packet.pos());
    }
}

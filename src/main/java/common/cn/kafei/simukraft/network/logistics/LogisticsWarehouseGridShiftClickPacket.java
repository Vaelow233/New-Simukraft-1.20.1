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
public record LogisticsWarehouseGridShiftClickPacket(BlockPos pos, ItemStack target) {

    /** encode: 写入快速取出的目标物品原型。 */
    public static void encode(LogisticsWarehouseGridShiftClickPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeItem(packet.target());
    }

    /** decode: 读取快速取出的目标物品原型。 */
    public static LogisticsWarehouseGridShiftClickPacket decode(FriendlyByteBuf buffer) {
        return new LogisticsWarehouseGridShiftClickPacket(buffer.readBlockPos(), buffer.readItem());
    }

    /** handle: 服务端把仓库物品直接转移到玩家背包。 */
    public static void handle(LogisticsWarehouseGridShiftClickPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (!LogisticsWarehouseGridPackets.prepareOpen(level, player, packet.pos()) || packet.target().isEmpty()) {
            return;
        }
        ItemStack extracted = LogisticsWarehouseInventoryService.extract(level, packet.pos(), packet.target(),
                Math.min(64, packet.target().getMaxStackSize()));
        if (extracted.isEmpty()) {
            return;
        }
        ItemStack remaining = LogisticsWarehouseInventoryService.insertIntoPlayerInventory(player.getInventory(), extracted);
        if (!remaining.isEmpty()) {
            ItemStack returned = LogisticsWarehouseInventoryService.insert(level, packet.pos(), remaining);
            if (!returned.isEmpty()) {
                player.drop(returned, false);
            }
        }
        player.containerMenu.broadcastChanges();
        LogisticsWarehouseGridPackets.sendSnapshot(level, player, packet.pos());
    }
}

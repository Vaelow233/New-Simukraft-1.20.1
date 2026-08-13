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
public record LogisticsWarehouseGridExtractPacket(BlockPos pos, ItemStack target, int count) {

    /** encode: 写入要从仓库取出的物品原型和数量。 */
    public static void encode(LogisticsWarehouseGridExtractPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeItem(packet.target());
        buffer.writeVarInt(Math.max(1, packet.count()));
    }

    /** decode: 读取要从仓库取出的物品原型和数量。 */
    public static LogisticsWarehouseGridExtractPacket decode(FriendlyByteBuf buffer) {
        return new LogisticsWarehouseGridExtractPacket(buffer.readBlockPos(), buffer.readItem(), buffer.readVarInt());
    }

    /** handle: 服务端从仓库取出一组物品到鼠标手上。 */
    public static void handle(LogisticsWarehouseGridExtractPacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (!LogisticsWarehouseGridPackets.prepareOpen(level, player, packet.pos()) || !player.containerMenu.getCarried().isEmpty()) {
            return;
        }
        ItemStack extracted = LogisticsWarehouseInventoryService.extract(level, packet.pos(), packet.target(), packet.count());
        if (extracted.isEmpty()) {
            return;
        }
        player.containerMenu.setCarried(extracted);
        player.containerMenu.broadcastChanges();
        LogisticsWarehouseGridPackets.sendSnapshot(level, player, packet.pos());
    }
}

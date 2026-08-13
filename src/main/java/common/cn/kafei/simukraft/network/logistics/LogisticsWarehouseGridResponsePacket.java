package common.cn.kafei.simukraft.network.logistics;

import common.cn.kafei.simukraft.network.clientbound.ClientboundNetworkBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("null")
public record LogisticsWarehouseGridResponsePacket(BlockPos pos,
                                                   List<ItemStack> items,
                                                   List<BlockPos> containerPositions,
                                                   List<Integer> actualCounts) {
    private static final int MAX_ITEMS = 4096;

    public LogisticsWarehouseGridResponsePacket {
        items = items != null ? List.copyOf(items) : List.of();
        containerPositions = containerPositions != null ? List.copyOf(containerPositions) : List.of();
        actualCounts = actualCounts != null ? List.copyOf(actualCounts) : List.of();
    }

    /** encode: 写入仓库物品、容器位置和真实数量。 */
    public static void encode(LogisticsWarehouseGridResponsePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeVarInt(packet.items().size());
        for (ItemStack stack : packet.items()) {
            buffer.writeItem(stack);
        }
        buffer.writeVarInt(packet.containerPositions().size());
        for (BlockPos pos : packet.containerPositions()) {
            buffer.writeBlockPos(pos);
        }
        buffer.writeVarInt(packet.actualCounts().size());
        for (int count : packet.actualCounts()) {
            buffer.writeVarInt(count);
        }
    }

    /** decode: 读取仓库物品、容器位置和真实数量。 */
    public static LogisticsWarehouseGridResponsePacket decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        int itemCount = Math.max(0, buffer.readVarInt());
        List<ItemStack> items = new ArrayList<>(Math.min(itemCount, MAX_ITEMS));
        for (int i = 0; i < itemCount; i++) {
            ItemStack stack = buffer.readItem();
            if (i < MAX_ITEMS) {
                items.add(stack);
            }
        }
        int positionCount = Math.max(0, buffer.readVarInt());
        List<BlockPos> positions = new ArrayList<>(Math.min(positionCount, MAX_ITEMS));
        for (int i = 0; i < positionCount; i++) {
            BlockPos containerPos = buffer.readBlockPos();
            if (i < MAX_ITEMS) {
                positions.add(containerPos);
            }
        }
        int countSize = Math.max(0, buffer.readVarInt());
        List<Integer> counts = new ArrayList<>(Math.min(countSize, MAX_ITEMS));
        for (int i = 0; i < countSize; i++) {
            int count = buffer.readVarInt();
            if (i < MAX_ITEMS) {
                counts.add(count);
            }
        }
        return new LogisticsWarehouseGridResponsePacket(pos, items, positions, counts);
    }

    /** handle: 客户端把仓库快照交给当前仓库屏幕。 */
    public static void handle(LogisticsWarehouseGridResponsePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientboundNetworkBridge.handleLogisticsWarehouseGridResponse(packet));
    }
}

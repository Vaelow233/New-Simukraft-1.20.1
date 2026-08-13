package common.cn.kafei.simukraft.network.npc.state;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record EmploymentStateRequestPacket(BlockPos sourcePos, String sourceType) {

    public static void encode(EmploymentStateRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.sourcePos());
        buffer.writeUtf(packet.sourceType(), 32);
    }

    public static EmploymentStateRequestPacket decode(FriendlyByteBuf buffer) {
        return new EmploymentStateRequestPacket(buffer.readBlockPos(), buffer.readUtf(32));
    }

    public static void handle(EmploymentStateRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        EmploymentStateResponsePacket.handleRequest(packet, context);
    }
}

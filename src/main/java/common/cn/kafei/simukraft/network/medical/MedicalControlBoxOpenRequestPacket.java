package common.cn.kafei.simukraft.network.medical;

import common.cn.kafei.simukraft.medical.MedicalControlBoxService;
import common.cn.kafei.simukraft.network.rts.RtsRemoteMenuAccess;
import common.cn.kafei.simukraft.network.toast.InfoToastService;
import common.cn.kafei.simukraft.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import static common.cn.kafei.simukraft.network.ModNetwork.CHANNEL;
import java.util.function.Supplier;

/** 客户端请求打开医疗控制箱。 */
public record MedicalControlBoxOpenRequestPacket(BlockPos pos) {

    /** encode：写入控制箱坐标。 */
    public static void encode(MedicalControlBoxOpenRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    /** decode：读取控制箱坐标。 */
    public static MedicalControlBoxOpenRequestPacket decode(FriendlyByteBuf buffer) {
        return new MedicalControlBoxOpenRequestPacket(buffer.readBlockPos());
    }

    /** handle：在服务端校验并打开医疗控制箱。 */
    public static void handle(MedicalControlBoxOpenRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            openFor(level, context.get().getSender(), packet.pos());
        }
    }

    /** openFor：校验距离和方块后发送只读视图。 */
    public static void openFor(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (!player.blockPosition().closerThan(pos, 16.0D) && !RtsRemoteMenuAccess.hasAccess(player, pos)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.medical_control_box.too_far"));
            return;
        }
        if (!level.getBlockState(pos).is(ModBlocks.MEDICAL_CONTROL_BOX.get())) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.medical_control_box.not_found"));
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), MedicalControlBoxOpenResponsePacket.from(MedicalControlBoxService.buildView(level, pos)));
    }
}

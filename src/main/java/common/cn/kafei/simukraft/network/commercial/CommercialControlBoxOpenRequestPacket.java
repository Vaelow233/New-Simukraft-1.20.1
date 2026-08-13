package common.cn.kafei.simukraft.network.commercial;

import common.cn.kafei.simukraft.commercial.CommercialControlBoxService;
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

@SuppressWarnings("Null")
public record CommercialControlBoxOpenRequestPacket(BlockPos pos) {

    /** encode: 写入打开商业控制箱请求。 */
    public static void encode(CommercialControlBoxOpenRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
    }

    /** decode: 读取打开商业控制箱请求。 */
    public static CommercialControlBoxOpenRequestPacket decode(FriendlyByteBuf buffer) {
        return new CommercialControlBoxOpenRequestPacket(buffer.readBlockPos());
    }

    /** handle: 处理客户端打开商业控制箱请求。 */
    public static void handle(CommercialControlBoxOpenRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        if (context.get().getSender() != null && context.get().getSender().level() instanceof ServerLevel level) {
            openFor(level, context.get().getSender(), packet.pos());
        }
    }

    /** openFor: 校验距离和方块后向玩家发送商业控制箱视图。 */
    public static void openFor(ServerLevel level, ServerPlayer player, BlockPos pos) {
        if (!player.blockPosition().closerThan(pos, 16.0D) && !RtsRemoteMenuAccess.hasAccess(player, pos)) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.commercial_control_box.too_far"));
            return;
        }
        if (!level.getBlockState(pos).is(ModBlocks.COMMERCIAL_CONTROL_BOX.get())) {
            InfoToastService.warning(player, Component.translatable("message.simukraft.commercial_control_box.not_found"));
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), CommercialControlBoxOpenResponsePacket.from(CommercialControlBoxService.buildView(level, pos)));
    }
}

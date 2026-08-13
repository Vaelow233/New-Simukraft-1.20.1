package common.cn.kafei.simukraft.network.commercial;

import common.cn.kafei.simukraft.commercial.CommercialControlBoxService;
import common.cn.kafei.simukraft.commercial.CommercialTradeAccessValidator;
import common.cn.kafei.simukraft.commercial.CommercialTradeService;
import common.cn.kafei.simukraft.network.toast.InfoToastService;
import common.cn.kafei.simukraft.network.rts.RtsRemoteCitizenAccess;
import common.cn.kafei.simukraft.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import static common.cn.kafei.simukraft.network.ModNetwork.CHANNEL;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("Null")
public record CommercialTradePacket(BlockPos pos, UUID workerId, String offerId, int count, boolean quickMove) {

    public CommercialTradePacket(BlockPos pos, UUID workerId, String offerId, int count) {
        this(pos, workerId, offerId, count, true);
    }

    /** encode: 写入玩家商业交易请求。 */
    public static void encode(CommercialTradePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos());
        buffer.writeUUID(packet.workerId());
        buffer.writeUtf(packet.offerId(), 256);
        buffer.writeVarInt(packet.count());
        buffer.writeBoolean(packet.quickMove());
    }

    /** decode: 读取玩家商业交易请求。 */
    public static CommercialTradePacket decode(FriendlyByteBuf buffer) {
        return new CommercialTradePacket(buffer.readBlockPos(), buffer.readUUID(), buffer.readUtf(256), buffer.readVarInt(), buffer.readBoolean());
    }

    /** handle: 在服务端执行 NPC 商业交易并刷新交易视图。 */
    public static void handle(CommercialTradePacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null && context.get().getSender().level() instanceof ServerLevel level) {
            if (!CommercialTradeAccessValidator.isValidWorker(level, packet.pos(), packet.workerId())
                    || (!CommercialTradeAccessValidator.isTradeReachable(level, player, packet.pos(), packet.workerId()))
                    && !RtsRemoteCitizenAccess.hasTradeAccess(player, packet.pos(), packet.workerId())) {
                InfoToastService.warning(player, Component.translatable("message.simukraft.commercial_control_box.too_far"));
                return;
            }
            if (!level.getBlockState(packet.pos()).is(ModBlocks.COMMERCIAL_CONTROL_BOX.get())) {
                InfoToastService.warning(player, Component.translatable("message.simukraft.commercial_control_box.not_found"));
                return;
            }
            CommercialTradeService.TradeResult result = CommercialTradeService.executePlayerTrade(level, player, packet.pos(), packet.offerId(), packet.count(), packet.quickMove());
            if (result.success()) {
                InfoToastService.success(player, result.message());
                if (!result.carriedStack().isEmpty()) {
                    player.containerMenu.setCarried(result.carriedStack().copy());
                }
                player.containerMenu.broadcastChanges();
                player.inventoryMenu.broadcastChanges();
                CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), CommercialTradeOpenResponsePacket.from(CommercialControlBoxService.buildTradeView(level, packet.pos(), packet.workerId())));
            } else {
                InfoToastService.warning(player, result.message());
            }
        }
    }

}

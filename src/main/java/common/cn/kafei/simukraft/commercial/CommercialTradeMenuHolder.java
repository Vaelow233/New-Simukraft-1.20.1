package common.cn.kafei.simukraft.commercial;

import common.cn.kafei.simukraft.compat.ldlib.gui.factory.IContainerUIHolder;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.ModularUI;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UI;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.UIElement;
import common.cn.kafei.simukraft.compat.ldlib.gui.ui.elements.inventory.InventorySlots;
import common.cn.kafei.simukraft.network.commercial.CommercialTradeOpenResponsePacket;
import common.cn.kafei.simukraft.network.rts.RtsRemoteCitizenAccess;
import dev.vfyjxf.taffy.style.TaffyPosition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings("Null")
public final class CommercialTradeMenuHolder implements IContainerUIHolder {
    private final CommercialTradeOpenResponsePacket packet;

    public CommercialTradeMenuHolder(CommercialTradeOpenResponsePacket packet) {
        this.packet = packet;
    }

    public CommercialTradeOpenResponsePacket packet() {
        return packet;
    }

    /** createUI: 创建包含真实玩家背包槽位的 LDLib 交易 UI。 */
    @Override
    public ModularUI createUI(Player player) {
        if (player == null || !player.level().isClientSide()) {
            UIElement root = new UIElement().layout(layout -> layout.width(276).height(166));
            InventorySlots slots = new InventorySlots();
            slots.layout(layout -> layout.positionType(TaffyPosition.ABSOLUTE)
                    .left(107).top(83).width(162).height(76));
            root.addChild(slots);
            return ModularUI.of(UI.of(root), player);
        }
        return ModularUI.of(UI.of(new CommercialTradeUiRoot(packet)), player);
    }

    /** isStillValid: 校验容器使用期间玩家仍在交易范围内。 */
    @Override
    public boolean isStillValid(Player player) {
        if (player == null || player.level().isClientSide()) {
            return true;
        }
        if (packet.boxPos() == null || packet.workerId() == null) {
            return false;
        }
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.level() instanceof ServerLevel level) {
            return CommercialTradeAccessValidator.canUseTradeMenu(level, serverPlayer, packet.boxPos(), packet.workerId())
                    || RtsRemoteCitizenAccess.hasTradeAccess(serverPlayer, packet.boxPos(), packet.workerId());
        }
        return false;
    }
}

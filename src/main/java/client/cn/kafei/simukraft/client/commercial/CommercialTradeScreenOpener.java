package client.cn.kafei.simukraft.client.commercial;

import common.cn.kafei.simukraft.network.commercial.CommercialTradeOpenResponsePacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class CommercialTradeScreenOpener {
    private CommercialTradeScreenOpener() {
    }

    /** open: 兼容旧交易响应包；实际界面现在由服务端容器菜单打开。 */
    public static void open(CommercialTradeOpenResponsePacket packet) {
        // NPC 购买界面由 LowDragLib 1 UIFactory 打开，客户端包不再直接创建 Screen。
    }
}

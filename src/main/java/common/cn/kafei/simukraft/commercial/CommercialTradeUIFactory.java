package common.cn.kafei.simukraft.commercial;

import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import common.cn.kafei.simukraft.SimuKraft;
import common.cn.kafei.simukraft.network.commercial.CommercialTradeOpenResponsePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class CommercialTradeUIFactory extends UIFactory<CommercialTradeMenuHolder> {
    public static final CommercialTradeUIFactory INSTANCE = new CommercialTradeUIFactory();

    private CommercialTradeUIFactory() {
        super(ResourceLocation.fromNamespaceAndPath(SimuKraft.MOD_ID, "commercial_trade"));
    }

    @Override
    protected com.lowdragmc.lowdraglib.gui.modular.ModularUI createUITemplate(CommercialTradeMenuHolder holder, Player player) {
        return holder.createUI(player).bindHolder(holder).unwrap();
    }

    @Override
    protected CommercialTradeMenuHolder readHolderFromSyncData(FriendlyByteBuf buffer) {
        return new CommercialTradeMenuHolder(CommercialTradeOpenResponsePacket.decode(buffer));
    }

    @Override
    protected void writeHolderToSyncData(FriendlyByteBuf buffer, CommercialTradeMenuHolder holder) {
        CommercialTradeOpenResponsePacket.encode(holder.packet(), buffer);
    }
}

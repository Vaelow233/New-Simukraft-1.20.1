package common.cn.kafei.simukraft.citizen;

import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import common.cn.kafei.simukraft.SimuKraft;
import common.cn.kafei.simukraft.network.citizen.info.CitizenInfoResponsePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public final class CitizenInfoUIFactory extends UIFactory<CitizenInfoMenuHolder> {
    public static final CitizenInfoUIFactory INSTANCE = new CitizenInfoUIFactory();

    private CitizenInfoUIFactory() {
        super(ResourceLocation.fromNamespaceAndPath(SimuKraft.MOD_ID, "citizen_info"));
    }

    @Override
    protected com.lowdragmc.lowdraglib.gui.modular.ModularUI createUITemplate(CitizenInfoMenuHolder holder, Player player) {
        return holder.createUI(player).bindHolder(holder).unwrap();
    }

    @Override
    protected CitizenInfoMenuHolder readHolderFromSyncData(FriendlyByteBuf buffer) {
        return new CitizenInfoMenuHolder(CitizenInfoResponsePacket.decode(buffer), new CitizenInventory(), null);
    }

    @Override
    protected void writeHolderToSyncData(FriendlyByteBuf buffer, CitizenInfoMenuHolder holder) {
        CitizenInfoResponsePacket.encode(buffer, holder.packet());
    }
}

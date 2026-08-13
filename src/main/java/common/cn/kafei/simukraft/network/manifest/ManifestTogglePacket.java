package common.cn.kafei.simukraft.network.manifest;

import common.cn.kafei.simukraft.item.ManifestItem;
import common.cn.kafei.simukraft.registry.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record ManifestTogglePacket(InteractionHand hand, int index, boolean checked) {

    private static final int MAX_MATERIAL_INDEX = 4096;

    public static void encode(ManifestTogglePacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.hand() == InteractionHand.OFF_HAND);
        buffer.writeVarInt(packet.index());
        buffer.writeBoolean(packet.checked());
    }

    public static ManifestTogglePacket decode(FriendlyByteBuf buffer) {
        InteractionHand hand = buffer.readBoolean() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        return new ManifestTogglePacket(hand, buffer.readVarInt(), buffer.readBoolean());
    }

    public static void handle(ManifestTogglePacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null) {
            return;
        }
        if (packet.index() < 0 || packet.index() > MAX_MATERIAL_INDEX) {
            return;
        }
        ItemStack stack = player.getItemInHand(packet.hand());
        if (!stack.is(ModItems.MANIFEST.get())) {
            return;
        }
        if (packet.index() >= ManifestItem.getMaterials(stack).size()) {
            return;
        }
        ManifestItem.setChecked(stack, packet.index(), packet.checked());
        player.getInventory().setChanged();
        player.containerMenu.broadcastChanges();
    }
}

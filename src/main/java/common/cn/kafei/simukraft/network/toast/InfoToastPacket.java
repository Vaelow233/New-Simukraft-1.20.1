package common.cn.kafei.simukraft.network.toast;

import common.cn.kafei.simukraft.network.clientbound.ClientboundNetworkBridge;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("Null")
public record InfoToastPacket(Component title, Component message, String style, ItemStack iconStack) {

    public InfoToastPacket(Component title, Component message, String style) {
        this(title, message, style, ItemStack.EMPTY);
    }

    public InfoToastPacket {
        title = title != null ? title : Component.translatable("toast.simukraft.title");
        message = message != null ? message : Component.empty();
        style = style != null && !style.isBlank() ? style : "info";
        iconStack = iconStack != null ? iconStack.copy() : ItemStack.EMPTY;
    }

    public static void encode(InfoToastPacket packet, FriendlyByteBuf buffer) {
        buffer.writeComponent(packet.title());
        buffer.writeComponent(packet.message());
        buffer.writeUtf(packet.style(), 16);
        buffer.writeItem(packet.iconStack());
    }

    public static InfoToastPacket decode(FriendlyByteBuf buffer) {
        return new InfoToastPacket(
                buffer.readComponent(),
                buffer.readComponent(),
                buffer.readUtf(16),
                buffer.readItem()
        );
    }

    public static void handle(InfoToastPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientboundNetworkBridge.handleInfoToast(packet));
    }
}

package common.cn.kafei.simukraft.registry;

import common.cn.kafei.simukraft.SimuKraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("null")
public final class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, SimuKraft.MOD_ID);

    public static final RegistryObject<SoundEvent> BUILD_BOX_PLACE = registerSound("block.build_box.place");
    public static final RegistryObject<SoundEvent> BUILD_BOX_BREAK = registerSound("block.build_box.break");
    public static final RegistryObject<SoundEvent> BUILD_BOX_OPEN = registerSound("ui.build_box.open");
    public static final RegistryObject<SoundEvent> CITY_CORE_OPEN = registerSound("ui.city_core.open");
    public static final RegistryObject<SoundEvent> FARMLAND_BOX_PLACE = registerSound("block.farmland_box.place");
    public static final RegistryObject<SoundEvent> FARMLAND_BOX_BREAK = registerSound("block.farmland_box.break");
    public static final RegistryObject<SoundEvent> PLAYER_WAKE_UP = registerSound("player.wake_up");
    // FIRST_DREAM：旧版首次进入世界时播放的梦境音乐。
    public static final RegistryObject<SoundEvent> FIRST_DREAM = registerSound("music.first_dream");
    public static final RegistryObject<SoundEvent> MONEY_COLLECT = registerSound("money.collect");
    public static final RegistryObject<SoundEvent> CONSTRUCTION_COMPLETE = registerSound("construction.complete");

    private ModSoundEvents() {
    }

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }

    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(SimuKraft.MOD_ID, name)));
    }
}

package common.cn.kafei.simukraft.registry;

import common.cn.kafei.simukraft.entity.CitizenEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;

@SuppressWarnings("null")
public final class ModEntityAttributes {
    private ModEntityAttributes() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModEntityAttributes::onEntityAttributeCreation);
    }

    private static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CITIZEN.get(), CitizenEntity.createAttributes().build());
    }
}

package common.cn.kafei.simukraft.compat.ldlib;

import com.lowdragmc.lowdraglib.gui.factory.UIFactory;
import common.cn.kafei.simukraft.citizen.CitizenInfoUIFactory;
import common.cn.kafei.simukraft.commercial.CommercialTradeUIFactory;

public final class ModLowDragUIFactories {
    private static boolean registered;

    private ModLowDragUIFactories() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;
        UIFactory.register(CitizenInfoUIFactory.INSTANCE);
        UIFactory.register(CommercialTradeUIFactory.INSTANCE);
    }
}

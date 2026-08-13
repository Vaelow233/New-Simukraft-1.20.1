package common.cn.kafei.simukraft.registry;

import common.cn.kafei.simukraft.SimuKraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("null")
public final class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, SimuKraft.MOD_ID);

    public static final RegistryObject<FlowingFluid> SOURCE_MILK = FLUIDS.register("milk_fluid",
            () -> new ForgeFlowingFluid.Source(milkProperties()));
    public static final RegistryObject<FlowingFluid> FLOWING_MILK = FLUIDS.register("flowing_milk",
            () -> new ForgeFlowingFluid.Flowing(milkProperties()));

    public static final ForgeFlowingFluid.Properties MILK_PROPERTIES = new ForgeFlowingFluid.Properties(
            ModFluidTypes.MILK, SOURCE_MILK, FLOWING_MILK)
            .bucket(() -> Items.MILK_BUCKET)
            .slopeFindDistance(4)
            .levelDecreasePerBlock(1)
            .block(ModFluids::milkBlock);

    private ModFluids() {
    }

    public static void register(IEventBus modEventBus) {
        FLUIDS.register(modEventBus);
    }

    private static ForgeFlowingFluid.Properties milkProperties() {
        return MILK_PROPERTIES;
    }

    private static LiquidBlock milkBlock() {
        return ModBlocks.MILK_BLOCK.get();
    }
}

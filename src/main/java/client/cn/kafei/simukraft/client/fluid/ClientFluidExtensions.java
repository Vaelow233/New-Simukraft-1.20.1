package client.cn.kafei.simukraft.client.fluid;

import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import common.cn.kafei.simukraft.SimuKraft;
import common.cn.kafei.simukraft.registry.ModFluidTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

import static common.cn.kafei.simukraft.registry.ModFluidTypes.FLUID_TYPES;

@OnlyIn(Dist.CLIENT)
public final class ClientFluidExtensions {
    private static final ResourceLocation MILK_STILL = ResourceLocation.fromNamespaceAndPath(SimuKraft.MOD_ID, "block/milk_still");
    private static final ResourceLocation MILK_FLOWING = ResourceLocation.fromNamespaceAndPath(SimuKraft.MOD_ID, "block/milk_flow");

    public static final RegistryObject<FluidType> MILK =
            FLUID_TYPES.register("milk", () -> new FluidType(
                    FluidType.Properties.create()
                            .descriptionId("fluid.simukraft.milk")
                            .fallDistanceModifier(0.0F)
                            .canExtinguish(true)
                            .canConvertToSource(false)
                            .supportsBoating(true)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            ) {
                        @Override
                        public void initializeClient(
                                Consumer<IClientFluidTypeExtensions> consumer
                        ) {
                            consumer.accept(
                                    new IClientFluidTypeExtensions() {
                                        @Override
                                        public ResourceLocation getStillTexture() {
                                            return MILK_STILL;
                                        }

                                        @Override
                                        public ResourceLocation getFlowingTexture() {
                                            return MILK_FLOWING;
                                        }

                                        @Override
                                        public int getTintColor() {
                                            return 0xFFFFFFFF;
                                        }
                                    }
                            );
                        }
                    });

    private ClientFluidExtensions() {
    }
}

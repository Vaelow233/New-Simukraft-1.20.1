package common.cn.kafei.simukraft.crafting;

import common.cn.kafei.simukraft.item.ManifestItem;
import common.cn.kafei.simukraft.registry.ModItems;
import common.cn.kafei.simukraft.registry.ModRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

@SuppressWarnings("null")
public final class ManifestClearRecipe extends CustomRecipe {
    public ManifestClearRecipe(ResourceLocation location, CraftingBookCategory category) {
        super(location, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int manifestCount = 0;
        boolean hasData = false;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (!stack.is(ModItems.MANIFEST.get())) {
                return false;
            }
            manifestCount++;
            CompoundTag tag = stack.getTagElement(ManifestItem.TAG_MANIFEST_DATA);
            hasData = tag != null && !tag.isEmpty();
        }
        return manifestCount == 1 && hasData;
    }

    @Override
    public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
        return new ItemStack(ModItems.MANIFEST.get());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.MANIFEST_CLEAR.get();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registries) {
        return new ItemStack(ModItems.MANIFEST.get());
    }
}

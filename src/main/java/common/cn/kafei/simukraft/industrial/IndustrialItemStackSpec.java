package common.cn.kafei.simukraft.industrial;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.StringReader;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("null")
public record IndustrialItemStackSpec(String itemId,
                                      String itemTag,
                                      String potionId,
                                      String itemStackText,
                                      String customDataText,
                                      List<EnchantmentSpec> enchantments,
                                      List<EnchantmentSpec> storedEnchantments) {
    private static final String WATER_POTION = "minecraft:water";

    public IndustrialItemStackSpec {
        itemId = safe(itemId);
        itemTag = safe(itemTag);
        potionId = safe(potionId);
        itemStackText = safe(itemStackText);
        customDataText = safe(customDataText);
        enchantments = enchantments != null ? List.copyOf(enchantments) : List.of();
        storedEnchantments = storedEnchantments != null ? List.copyOf(storedEnchantments) : List.of();
    }

    public static IndustrialItemStackSpec empty() {
        return new IndustrialItemStackSpec("", "", "", "", "", List.of(), List.of());
    }

    public static IndustrialItemStackSpec of(String itemId, String potionId) {
        return new IndustrialItemStackSpec(itemId, "", potionId, "", "", List.of(), List.of());
    }

    public static IndustrialItemStackSpec itemStack(String itemStackText) {
        return new IndustrialItemStackSpec("", "", "", itemStackText, "", List.of(), List.of());
    }

    public static IndustrialItemStackSpec of(String itemId,
                                             String itemTag,
                                             String potionId,
                                             String itemStackText,
                                             String customDataText,
                                             List<EnchantmentSpec> enchantments,
                                             List<EnchantmentSpec> storedEnchantments) {
        return new IndustrialItemStackSpec(itemId, itemTag, potionId, itemStackText, customDataText, enchantments, storedEnchantments);
    }

    public static IndustrialItemStackSpec fromSerialized(String text) {
        String value = safe(text);
        if (value.isBlank()) {
            return empty();
        }
        if (!value.startsWith("{")) {
            if (value.startsWith("#")) {
                return new IndustrialItemStackSpec("", value.substring(1), "", "", "", List.of(), List.of());
            }
            return value.indexOf('{') > 0 ? itemStack(value) : of(value, "");
        }
        try {
            JsonObject object = JsonParser.parseString(value).getAsJsonObject();
            return new IndustrialItemStackSpec(
                    string(object, "item", ""),
                    stringAny(object, "", "tag", "itemTag", "item_tag"),
                    string(object, "potion", ""),
                    string(object, "itemStack", ""),
                    string(object, "customData", string(object, "nbt", "")),
                    parseEnchantments(object.getAsJsonArray("enchantments")),
                    parseEnchantments(object.getAsJsonArray("storedEnchantments"))
            );
        } catch (Exception exception) {
            return empty();
        }
    }

    public boolean isEmpty() {
        return itemId.isBlank() && itemTag.isBlank() && itemStackText.isBlank();
    }

    public boolean hasComplexConstraints() {
        return !itemStackText.isBlank()
                || (!itemTag.isBlank() && !itemId.isBlank())
                || !customDataText.isBlank()
                || !enchantments.isEmpty()
                || !storedEnchantments.isEmpty()
                || !potionId.isBlank();
    }

    public ItemStack stack(int count) {
        return stack(count, null);
    }

    public ItemStack stack(int count, @Nullable HolderLookup.Provider registries) {
        int safeCount = Math.max(1, count);
        if (!itemStackText.isBlank()) {
            ItemStack stack = parsedStack(safeCount, registries);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            if (!applyPotion(stack)
                    || !applyCustomData(stack)
                    || !applyEnchantments(stack, false, enchantments)
                    || !applyEnchantments(stack, true, storedEnchantments)) {
                return ItemStack.EMPTY;
            }
            return stack;
        }
        if (!itemTag.isBlank() && itemId.isBlank()) {
            return ItemStack.EMPTY;
        }
        Item item = itemById(itemId);
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(item, safeCount);
        if (!applyPotion(stack)
                || !applyCustomData(stack)
                || !applyEnchantments(stack, false, enchantments)
                || !applyEnchantments(stack, true, storedEnchantments)) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    public boolean matches(ItemStack stack) {
        return matches(stack, null);
    }

    public boolean matches(ItemStack stack, @Nullable HolderLookup.Provider registries) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (!itemStackText.isBlank()) {
            return matchesParsedStack(stack, registries)
                    && matchesItemTag(stack)
                    && matchesPotion(stack)
                    && matchesCustomData(stack)
                    && matchesEnchantments(stack, false, enchantments)
                    && matchesEnchantments(stack, true, storedEnchantments);
        }
        if (!itemId.isBlank()) {
            Item item = itemById(itemId);
            if (item == Items.AIR || stack.getItem() != item) {
                return false;
            }
        }
        if (!matchesItemTag(stack)) {
            return false;
        }
        return matchesPotion(stack)
                && matchesCustomData(stack)
                && matchesEnchantments(stack, false, enchantments)
                && matchesEnchantments(stack, true, storedEnchantments);
    }

    public String serialized() {
        if (!itemStackText.isBlank() && itemId.isBlank() && itemTag.isBlank() && potionId.isBlank()
                && customDataText.isBlank() && enchantments.isEmpty() && storedEnchantments.isEmpty()) {
            return itemStackText;
        }
        if (itemId.isBlank() && !itemTag.isBlank() && potionId.isBlank() && itemStackText.isBlank()
                && customDataText.isBlank() && enchantments.isEmpty() && storedEnchantments.isEmpty()) {
            return "#" + itemTag;
        }
        if (!hasComplexConstraints()) {
            return itemId;
        }
        JsonObject object = new JsonObject();
        if (!itemId.isBlank()) {
            object.addProperty("item", itemId);
        }
        if (!itemTag.isBlank()) {
            object.addProperty("tag", itemTag);
        }
        if (!potionId.isBlank()) {
            object.addProperty("potion", potionId);
        }
        if (!itemStackText.isBlank()) {
            object.addProperty("itemStack", itemStackText);
        }
        if (!customDataText.isBlank()) {
            object.addProperty("customData", customDataText);
        }
        if (!enchantments.isEmpty()) {
            object.add("enchantments", enchantmentsJson(enchantments));
        }
        if (!storedEnchantments.isEmpty()) {
            object.add("storedEnchantments", enchantmentsJson(storedEnchantments));
        }
        return object.toString();
    }

    public String displayKey() {
        return serialized();
    }

    public String displayItemId() {
        if (!itemId.isBlank()) {
            return itemId;
        }
        if (!itemTag.isBlank()) {
            return "#" + itemTag;
        }
        int nbtStart = itemStackText.indexOf('{');
        return nbtStart >= 0 ? itemStackText.substring(0, nbtStart) : itemStackText;
    }

    @Nullable
    private ItemInput parsedInput(@Nullable HolderLookup.Provider registries) {
        try {
            HolderLookup<Item> itemLookup = registries != null
                    ? registries.lookupOrThrow(Registries.ITEM)
                    : BuiltInRegistries.ITEM.asLookup();

            ItemParser.ItemResult result = ItemParser.parseForItem(itemLookup,
                    new StringReader(itemStackText)
            );
            return new ItemInput(result.item(), result.nbt());
        } catch (Exception exception) {
            return null;
        }
    }

    private ItemStack parsedStack(int count, @Nullable HolderLookup.Provider registries) {
        ItemInput input = parsedInput(registries);
        if (input == null) {
            return ItemStack.EMPTY;
        }
        try {
            return input.createItemStack(Math.max(1, count), false);
        } catch (Exception exception) {
            return ItemStack.EMPTY;
        }
    }

    private boolean matchesParsedStack(ItemStack stack, @Nullable HolderLookup.Provider registries) {
        ItemInput input = parsedInput(registries);
        return input != null && input.test(stack);
    }

    private boolean matchesItemTag(ItemStack stack) {
        if (itemTag.isBlank()) {
            return true;
        }
        try {
            ResourceLocation id = ResourceLocation.parse(itemTag);
            return stack.is(TagKey.create(Registries.ITEM, id));
        } catch (Exception exception) {
            return false;
        }
    }

    private boolean applyPotion(ItemStack stack) {
        if (potionId.isBlank()) {
            return true;
        }
        Potion potion = potion();
        if (potion == null) {
            return false;
        }
        PotionUtils.setPotion(stack, potion);
        return true;
    }

    private boolean matchesPotion(ItemStack stack) {
        if (potionId.isBlank()) {
            return true;
        }
        Potion expected = potion();
        return expected != null && PotionUtils.getPotion(stack) == expected;
    }

    private boolean applyCustomData(ItemStack stack) {
        if (customDataText.isBlank()) {
            return true;
        }
        try {
            CompoundTag customData = TagParser.parseTag(customDataText);
            stack.getOrCreateTag().merge(customData.copy());
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private boolean matchesCustomData(ItemStack stack) {
        if (customDataText.isBlank()) {
            return true;
        }
        try {
            CompoundTag expected = TagParser.parseTag(customDataText);

            return NbtUtils.compareNbt(expected, stack.getTag(), true);
        } catch (Exception exception) {
            return false;
        }
    }

    private boolean applyEnchantments(ItemStack stack,
                                      boolean stored,
                                      List<EnchantmentSpec> specs) {
        if (specs.isEmpty()) {
            return true;
        }
        Map<Enchantment, Integer> values = readEnchantments(stack, stored);
        for (EnchantmentSpec spec : specs) {
            Enchantment enchantment = enchantment(spec.id());
            if (enchantment == null) {
                return false;
            }
            values.put(enchantment, Math.max(1, spec.level()));
        }
        ListTag encoded = new ListTag();
        for (Map.Entry<Enchantment, Integer> entry : values.entrySet()) {
            ResourceLocation id = BuiltInRegistries.ENCHANTMENT.getKey(entry.getKey());
            encoded.add(EnchantmentHelper.storeEnchantment(id, entry.getValue()));
        }
        String key = stored ? "StoredEnchantments" : "Enchantments";
        stack.getOrCreateTag().put(key, encoded);
        return true;
    }

    private boolean matchesEnchantments(ItemStack stack,
                                        boolean stored,
                                        List<EnchantmentSpec> specs) {
        if (specs.isEmpty()) {
            return true;
        }
        Map<Enchantment, Integer> values = readEnchantments(stack, stored);
        for (EnchantmentSpec spec : specs) {
            Enchantment enchantment = enchantment(spec.id());
            if (enchantment == null
                    || values.getOrDefault(enchantment, 0) != Math.max(1, spec.level())) {
                return false;
            }
        }

        return true;
    }

    @Nullable
    private Potion potion() {
        ResourceLocation id = ResourceLocation.tryParse(potionId);
        if (id == null) {
            return null;
        }
        return BuiltInRegistries.POTION.getOptional(id).orElse(null);
    }

    private static Map<Enchantment, Integer> readEnchantments(ItemStack stack, boolean stored) {
        CompoundTag root = stack.getTag();
        if (root == null) {
            return new LinkedHashMap<>();
        }
        String key = stored ? "StoredEnchantments" : "Enchantments";
        ListTag encoded = root.getList(key, Tag.TAG_COMPOUND);
        return new LinkedHashMap<>(EnchantmentHelper.deserializeEnchantments(encoded));
    }

    @Nullable
    private static Enchantment enchantment(String text) {
        ResourceLocation id = ResourceLocation.tryParse(text);
        if (id == null) {
            return null;
        }
        return BuiltInRegistries.ENCHANTMENT.getOptional(id).orElse(null);
    }

    private static Item itemById(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return Items.AIR;
        }
        try {
            return BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(itemId)).orElse(Items.AIR);
        } catch (Exception exception) {
            return Items.AIR;
        }
    }

    private static JsonArray enchantmentsJson(List<EnchantmentSpec> specs) {
        JsonArray array = new JsonArray();
        for (EnchantmentSpec spec : specs) {
            JsonObject object = new JsonObject();
            object.addProperty("id", spec.id());
            object.addProperty("level", spec.level());
            array.add(object);
        }
        return array;
    }

    private static List<EnchantmentSpec> parseEnchantments(@Nullable JsonArray array) {
        if (array == null || array.isEmpty()) {
            return List.of();
        }
        List<EnchantmentSpec> specs = new ArrayList<>();
        for (JsonElement element : array) {
            if (element == null || !element.isJsonObject()) {
                continue;
            }
            JsonObject object = element.getAsJsonObject();
            String id = string(object, "id", "");
            int level = integer(object, "level", 1);
            if (!id.isBlank()) {
                specs.add(new EnchantmentSpec(id, level));
            }
        }
        return List.copyOf(specs);
    }

    private static String string(JsonObject object, String key, String fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            return object.get(key).getAsString();
        } catch (Exception exception) {
            return fallback;
        }
    }

    private static String stringAny(JsonObject object, String fallback, String... keys) {
        for (String key : keys) {
            String value = string(object, key, "");
            if (!value.isBlank()) {
                return value;
            }
        }
        return fallback;
    }

    private static int integer(JsonObject object, String key, int fallback) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return fallback;
        }
        try {
            return object.get(key).getAsInt();
        } catch (Exception exception) {
            return fallback;
        }
    }

    private static String safe(String text) {
        return text != null ? text.trim() : "";
    }

    public record EnchantmentSpec(String id, int level) {
        public EnchantmentSpec {
            id = safe(id);
            level = Math.max(1, level);
        }
    }
}

package net.pedroksl.ae2addonlib.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

/**
 * Class used to define an {@link Ingredient} with an amount.
 * Comes equipped with two child classes for handling {@link ItemStack} and {@link FluidStack}.
 * @param <T> Class of the Ingredient Type.
 * @param <P> Class of the Stack Type.
 */
public abstract class IngredientStack<T, P> {
    /**
     * The inner ingredient of this ingredient stack.
     */
    protected final T ingredient;
    /**
     * The inner amount of this ingredient stack.
     */
    protected int amount;

    IngredientStack(T ingredient, int amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }

    /**
     * Item ingredient factory. Creates an ingredient based on an {@link ItemStack}.
     * @param stack The stack to convert to an ingredient.
     * @return The converted ingredient.
     */
    public static Item of(ItemStack stack) {
        return new Item(Ingredient.of(stack), stack.getCount());
    }

    /**
     * Item ingredient factory. Creates an ingredient from a vanilla {@link Ingredient} and an amount.
     * @param ingredient The ingredient to convert.
     * @param amount The amount of the ingredient.
     * @return The converted ingredient.
     */
    public static Item of(Ingredient ingredient, int amount) {
        return new Item(ingredient, amount);
    }

    /**
     * Fluid ingredient factory. Create an ingredient from a {@link FluidStack}.
     * @param stack The stack to convert to an ingredient.
     * @return The converted ingredient.
     */
    public static Fluid of(FluidStack stack) {
        return new Fluid(stack.getFluid(), stack.getAmount());
    }

    /**
     * Getter for the ingredient.
     * @return The ingredient.
     */
    public T getIngredient() {
        return this.ingredient;
    }

    /**
     * Getter for the amount.
     * @return The amount.
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Consumes the amount based on the input stack.
     * @param stack The stack to consume.
     */
    @SuppressWarnings("unchecked")
    public void consume(P stack) {
        if (this.amount <= 0) {
            return;
        }
        if (this.test(stack)) {
            int from = getStackAmount(stack);
            if (from > this.amount) {
                setStackAmount(stack, from - this.amount);
                this.amount = 0;
            } else {
                setStackAmount(stack, 0);
                this.amount -= from;
            }
        }
    }

    /**
     * Check if the ingredient was fully consumed.
     * @return If the ingredient was fully consumed.
     */
    public boolean isEmpty() {
        return this.amount <= 0;
    }

    /**
     * Test if the input stack is the same as the ingredient's.
     * @param obj The input stack.
     * @return If the stacks match.
     */
    public abstract boolean test(P obj);

    /**
     * Get a copy of this {@link IngredientStack} for use in recipes.
     * @return The ingredient stack copy.
     */
    public abstract IngredientStack<T, P> sample();

    /**
     * Checks if the type of stack matches the ingredient stack's type.
     * @param obj The item stack to check for.
     * @return If the stack types match.
     */
    public abstract boolean checkType(Object obj);

    /**
     * Getter for the amount of a given stack.
     * @param stack The stack to get the amount of.
     * @return The amount of the input stack.
     */
    public abstract int getStackAmount(P stack);

    /**
     * Setter for the amount of a given stack.
     * @param stack  The stack to set the amount to.
     * @param amount The amount to set.
     */
    public abstract void setStackAmount(P stack, int amount);

    /**
     * Writes the ingredient stack to a buffer.
     * @param buffer The buffer to write to.
     */
    public abstract void toNetwork(FriendlyByteBuf buffer);

    /**
     * Write the ingredient stack to a json element.
     * @return The json element.
     */
    public abstract JsonElement toJson();

    /**
     * Helper class that defines an {@link IngredientStack} that handles items.
     */
    public static final class Item extends IngredientStack<Ingredient, ItemStack> {

        /**
         * The codec used to read/write this ingredient stack.
         */
        public static final Codec<Item> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(ExtraCodecs.JSON.fieldOf("item").forGetter(Item::toJson))
                        .apply(builder, Item::fromJson));

        /**
         * Constructor for an Item Ingredient Stack.
         * @param ingredient The ingredient of this ingredient stack.
         * @param amount The amount of this ingredient stack.
         */
        public Item(Ingredient ingredient, int amount) {
            super(ingredient, amount);
        }

        @Override
        public boolean test(ItemStack stack) {
            return this.ingredient.test(stack);
        }

        @Override
        public Item sample() {
            return new Item(this.ingredient, this.amount);
        }

        @Override
        public boolean checkType(Object obj) {
            return obj instanceof ItemStack;
        }

        @Override
        public int getStackAmount(ItemStack stack) {
            return stack.getCount();
        }

        @Override
        public void setStackAmount(ItemStack stack, int amount) {
            stack.setCount(amount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer) {
            this.ingredient.toNetwork(buffer);
            buffer.writeInt(this.amount);
        }

        @Override
        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            json.add("ingredient", this.ingredient.toJson());
            json.addProperty("amount", this.amount);
            return json;
        }

        /**
         * Reads an Item Ingredient Stack from a buffer.
         * @param buffer The buffer to read.
         * @return The ingredient stack.
         */
        public static Item fromNetwork(FriendlyByteBuf buffer) {
            var ingredient = Ingredient.fromNetwork(buffer);
            var amount = buffer.readInt();
            return new Item(ingredient, amount);
        }

        /**
         * Reads an Item Ingredient Stack from a json element.
         * @param json The json element to read.
         * @return The ingredient stack.
         */
        public static Item fromJson(@Nullable JsonElement json) {
            if (json != null && !json.isJsonNull()) {
                if (json.isJsonObject()) {
                    var jsonObj = json.getAsJsonObject();
                    var ingredient = Ingredient.fromJson(jsonObj.get("ingredient"), false);
                    var amount = jsonObj.get("amount").getAsInt();
                    return new Item(ingredient, amount);
                } else {
                    throw new JsonSyntaxException("Expected item to be object");
                }
            } else {
                throw new JsonSyntaxException("Item cannot be null");
            }
        }
    }

    /**
     * Helper class that defines an {@link IngredientStack} that handles fluids.
     */
    public static class Fluid extends IngredientStack<net.minecraft.world.level.material.Fluid, FluidStack> {

        /**
         * The codec used to read/write this ingredient stack.
         */
        public static final Codec<Fluid> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(FluidStack.CODEC.fieldOf("fluidStack").forGetter(Fluid::getStack))
                        .apply(builder, Fluid::new));

        /**
         * Constructor for a Fluid Ingredient Stack from a {@link FluidStack}.
         * @param stack The stack of this ingredient stack.
         */
        public Fluid(FluidStack stack) {
            super(stack.getFluid(), stack.getAmount());
        }

        /**
         * Constructor for a Fluid Ingredient Stack.
         * @param fluid The ingredient of this ingredient stack.
         * @param amount The amount of this ingredient stack.
         */
        public Fluid(net.minecraft.world.level.material.Fluid fluid, int amount) {
            super(fluid, amount);
        }

        @Override
        public boolean test(FluidStack stack) {
            return this.ingredient == stack.getFluid();
        }

        @Override
        public Fluid sample() {
            return new Fluid(this.ingredient, this.amount);
        }

        @Override
        public boolean checkType(Object obj) {
            return obj instanceof FluidStack;
        }

        @Override
        public int getStackAmount(FluidStack stack) {
            return stack.getAmount();
        }

        @Override
        public void setStackAmount(FluidStack stack, int amount) {
            stack.setAmount(amount);
        }

        /**
         * Creates a new {@link FluidStack} using this class' components.
         * @return The fluid stack.
         */
        public FluidStack getStack() {
            return new FluidStack(this.ingredient, this.amount);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer) {
            buffer.writeJsonWithCodec(CODEC, this);
        }

        @Override
        public JsonElement toJson() {
            return CODEC.encodeStart(JsonOps.INSTANCE, this).result().get().getAsJsonObject();
        }

        /**
         * Reads a Fluid Ingredient Stack from a buffer.
         * @param buffer The buffer to read.
         * @return The ingredient stack.
         */
        public static Fluid fromNetwork(FriendlyByteBuf buffer) {
            return buffer.readJsonWithCodec(CODEC);
        }

        /**
         * Reads a Fluid Ingredient Stack from a json element.
         * @param json The json element to read.
         * @return The ingredient stack.
         */
        public static Fluid fromJson(@Nullable JsonElement json) {
            return CODEC.parse(JsonOps.INSTANCE, json).result().get();
        }
    }
}

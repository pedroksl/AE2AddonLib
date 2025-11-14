package net.pedroksl.ae2addonlib.recipes;

import java.util.function.Predicate;

import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jetbrains.annotations.Contract;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;

/**
 * Class used to define an {@link Ingredient} with an amount.
 * Comes equipped with two child classes for handling {@link ItemStack} and {@link FluidStack}.
 * @param <T> Class of the Ingredient Type.
 * @param <P> Class of the Stack Type.
 */
public abstract class IngredientStack<T extends Predicate<P>, P> {
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
        return new Fluid(stack);
    }

    /**
     * Fluid ingredient factory. Creates an ingredient from a {@link FluidIngredient} and an amount.
     * @param ingredient The ingredient to convert.
     * @param amount The amount of the ingredient.
     * @return The converted ingredient.
     */
    public static Fluid of(FluidIngredient ingredient, int amount) {
        return new Fluid(ingredient, amount);
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
     * Tests the input stack for a match with the ingredient.
     * @param stack The stack to test.
     */
    @SuppressWarnings("unchecked")
    @Contract("null -> false")
    public boolean test(Object stack) {
        if (stack == null) {
            return false;
        }
        return this.ingredient.test((P) stack);
    }

    /**
     * Tests the input stack for a match with the ingredient.
     * @param stack The stack to test.
     */
    public abstract boolean test(GenericStack stack);

    /**
     * Consumes the amount based on the input stack.
     * @param stack The stack to consume.
     */
    @SuppressWarnings("unchecked")
    public void consume(Object stack) {
        if (this.amount <= 0) {
            return;
        }
        if (this.ingredient.test((P) stack)) {
            int from = getStackAmount((P) stack);
            if (from > this.amount) {
                setStackAmount((P) stack, from - this.amount);
                this.amount = 0;
            } else {
                setStackAmount((P) stack, 0);
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
    public abstract void toNetwork(RegistryFriendlyByteBuf buffer);

    /**
     * Helper class that defines an {@link IngredientStack} that handles items.
     */
    public static final class Item extends IngredientStack<Ingredient, ItemStack> {

        /**
         * The codec used to read/write this ingredient stack.
         */
        public static final Codec<Item> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(i -> i.ingredient),
                        ExtraCodecs.POSITIVE_INT.optionalFieldOf("amount", 1).forGetter(i -> i.amount))
                .apply(builder, Item::new));

        /**
         * The stream codec used to read/write this ingredient stack to a buffer.
         */
        public static final StreamCodec<RegistryFriendlyByteBuf, Item> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, f -> f.ingredient, ByteBufCodecs.INT, f -> f.amount, Item::new);

        /**
         * Constructor for an Item Ingredient Stack.
         * @param ingredient The ingredient of this ingredient stack.
         * @param amount The amount of this ingredient stack.
         */
        public Item(Ingredient ingredient, int amount) {
            super(ingredient, amount);
        }

        @Override
        public Item sample() {
            return new Item(this.ingredient, this.amount);
        }

        @Override
        public boolean checkType(Object obj) {
            return obj instanceof ItemStack || (obj instanceof GenericStack gen && gen.what() instanceof AEItemKey);
        }

        @Override
        public boolean test(GenericStack stack) {
            if (!checkType(stack)) {
                return false;
            }

            ItemStack s = ((AEItemKey) stack.what()).toStack(Ints.saturatedCast(stack.amount()));
            return this.ingredient.test(s);
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
        public void toNetwork(RegistryFriendlyByteBuf buffer) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, this.ingredient);
            buffer.writeInt(this.amount);
        }
    }

    /**
     * Helper class that defines an {@link IngredientStack} that handles fluids.
     */
    public static class Fluid extends IngredientStack<FluidIngredient, FluidStack> {

        /**
         * The codec used to read/write this ingredient stack.
         */
        public static final Codec<Fluid> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        FluidIngredient.CODEC.fieldOf("ingredient").forGetter(f -> f.ingredient),
                        ExtraCodecs.POSITIVE_INT.optionalFieldOf("amount", 1).forGetter(f -> f.amount))
                .apply(builder, Fluid::new));

        /**
         * The stream codec used to read/write this ingredient stack to a buffer.
         */
        public static final StreamCodec<RegistryFriendlyByteBuf, Fluid> STREAM_CODEC = StreamCodec.composite(
                FluidIngredient.STREAM_CODEC, f -> f.ingredient, ByteBufCodecs.INT, f -> f.amount, Fluid::new);

        /**
         * Constructor for a Fluid Ingredient Stack from a {@link FluidStack}.
         * @param ingredient The fluid ingredient of this stack.
         * @param amount The amount of the ingredient.
         */
        public Fluid(FluidIngredient ingredient, int amount) {
            super(ingredient, amount);
        }

        /**
         * Constructor for a Fluid Ingredient Stack from a {@link FluidStack}.
         * @param stack The stack of this ingredient stack.
         */
        public Fluid(FluidStack stack) {
            super(FluidIngredient.of(stack.getFluid()), stack.getAmount());
        }

        @Override
        public Fluid sample() {
            return new Fluid(this.ingredient, this.amount);
        }

        @Override
        public boolean checkType(Object obj) {
            return obj instanceof FluidStack || (obj instanceof GenericStack gen && gen.what() instanceof AEFluidKey);
        }

        @Override
        public boolean test(GenericStack stack) {
            if (!checkType(stack)) {
                return false;
            }

            FluidStack f = ((AEFluidKey) stack.what()).toStack(Ints.saturatedCast(stack.amount()));
            return this.ingredient.test(f);
        }

        @Override
        public int getStackAmount(FluidStack stack) {
            return stack.getAmount();
        }

        @Override
        public void setStackAmount(FluidStack stack, int amount) {
            stack.setAmount(amount);
        }

        @Override
        public void toNetwork(RegistryFriendlyByteBuf buffer) {
            buffer.writeJsonWithCodec(CODEC, this);
        }
    }
}

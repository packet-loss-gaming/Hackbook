/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gg.packetloss.hackbook;

import gg.packetloss.hackbook.exceptions.UnsupportedFeatureException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ModifierBook {
    public static class BaseModifier {
        private final UUID modifierID;
        private final String modifierName;

        private BaseModifier(UUID modifierID, String modifierName) {
            this.modifierID = modifierID;
            this.modifierName = modifierName;
        }

        public UUID getModifierID() {
            return modifierID;
        }

        public String getModifierName() {
            return modifierName;
        }


        public Modifier get(double value, ModifierOperation operation, @Nullable Slot slot) {
            return new Modifier(this, value, operation, slot);
        }
    }

    public enum ModifierOperation {
        ADDITIVE(0);

        private final int opCode;

        ModifierOperation(int opCode) {
            this.opCode = opCode;
        }

        protected int getOpCode() {
            return opCode;
        }
    }

    public enum Slot {
        MAIN_HAND("mainhand"),
        OFF_HAND("offhand"),
        HEAD("head"),
        CHEST("chest"),
        LEGS("legs"),
        FEET("feet");

        private final String name;

        Slot(String name) {
            this.name = name;
        }

        protected String getName() {
            return name;
        }
    }

    public static class Modifier {
        private final BaseModifier modifier;
        private final double value;
        private final ModifierOperation operation;
        private final Slot slot;

        private Modifier(BaseModifier modifier, double value, ModifierOperation operation, @Nullable Slot slot) {
            this.modifier = modifier;
            this.value = value;
            this.operation = operation;
            this.slot = slot;
        }

        public UUID getModifierID() {
            return modifier.getModifierID();
        }

        public String getModifierName() {
            return modifier.getModifierName();
        }

        public double getValue() {
            return value;
        }

        public ModifierOperation getOperation() {
            return operation;
        }

        public Slot getSlot() {
            return slot;
        }
    }

    public static final BaseModifier ITEM_ATTACK_DAMAGE = new BaseModifier(
            UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"), "generic.attackDamage"
    );
    public static final BaseModifier ITEM_ATTACK_SPEED = new BaseModifier(
            UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3"), "generic.attackSpeed"
    );

    private static CompoundTag buildModifierTag(Modifier modifier) {
        CompoundTag modifierTag = new CompoundTag();

        Slot slot = modifier.getSlot();
        if (slot != null) {
            modifierTag.putString("Slot", slot.getName());
        }

        modifierTag.putString("AttributeName", modifier.getModifierName());
        modifierTag.putString("Name", modifier.getModifierName());
        modifierTag.putDouble("Amount", modifier.getValue());
        modifierTag.putInt("Operation", modifier.getOperation().getOpCode());
        modifierTag.putInt("UUIDLeast", (int) modifier.getModifierID().getLeastSignificantBits());
        modifierTag.putInt("UUIDMost", (int) modifier.getModifierID().getMostSignificantBits());

        return modifierTag;
    }

    public static ItemStack cloneWithSpecifiedModifiers(ItemStack stack, List<Modifier> modifierList) throws UnsupportedFeatureException {
        try {
            net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

            CompoundTag compound = nmsStack.getTag();
            if (compound == null) {
                nmsStack.setTag(new CompoundTag());
                compound = nmsStack.getTag();
            }

            ListTag modifiers = new ListTag();

            for (Modifier modifier : modifierList) {
                modifiers.add(buildModifierTag(modifier));
            }

            compound.put("AttributeModifiers", modifiers);

            return CraftItemStack.asBukkitCopy(nmsStack);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new UnsupportedFeatureException();
        }
    }

}

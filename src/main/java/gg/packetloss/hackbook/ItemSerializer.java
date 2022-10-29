/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gg.packetloss.hackbook;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemSerializer {
    private static CompoundTag toTag(ItemStack stack) {
        CompoundTag compound = new CompoundTag();
        CraftItemStack.asNMSCopy(stack).save(compound);
        return compound;
    }

    public static String toJSON(ItemStack stack) {
        return toTag(stack).toString();
    }

    public static void writeToOutputStream(ItemStack stack, OutputStream stream) throws IOException {
        NbtIo.writeCompressed(toTag(stack), stream);
    }

    public static void writeToOutputStream(Collection<ItemStack> stacks, OutputStream stream) throws IOException {
        CompoundTag compoundTag = new CompoundTag();

        ListTag tag = new ListTag();
        for (ItemStack stack : stacks) {
            tag.add(toTag(stack));
        }

        compoundTag.put("elements", tag);
        compoundTag.putInt("DataVersion", DataMigrator.getCurrentVersion());

        NbtIo.writeCompressed(compoundTag, stream);
    }

    public static List<ItemStack> fromInputStream(InputStream stream, boolean migrate) throws IOException {
        CompoundTag compoundTag = NbtIo.readCompressed(stream);

        ListTag tag = (ListTag) compoundTag.get("elements");

        Validate.isTrue(compoundTag.contains("DataVersion"));
        int prevVersion = compoundTag.getInt("DataVersion") ;

        List<ItemStack> stacks = new ArrayList<>(tag.size());

        for (int i = 0; i < tag.size(); ++i) {
            CompoundTag itemTag = tag.getCompound(i);

            if (migrate) {
                itemTag = DataMigrator.updateItemStack(prevVersion, itemTag);
            }

            stacks.add(CraftItemStack.asCraftMirror(net.minecraft.world.item.ItemStack.of(itemTag)));
        }

        return stacks;
    }

    public static List<ItemStack> fromInputStream(InputStream stream) throws IOException {
        return fromInputStream(stream, false);
    }
}

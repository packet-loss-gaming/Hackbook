/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gg.packetloss.hackbook;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemSerializer {
    private static RegistryAccess.Frozen getRegistry() {
        return MinecraftServer.getServer().registryAccess();
    }

    private static Tag toTag(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            CompoundTag compound = new CompoundTag();
            compound.put("HACKBOOK_AIR_ITEM", IntTag.valueOf(1));
            return compound;
        }

        return CraftItemStack.asNMSCopy(stack).save(getRegistry());
    }

    public static String toJSON(ItemStack stack) {
        return toTag(stack).toString();
    }

    public static void writeToOutputStream(Collection<ItemStack> stacks, OutputStream stream) throws IOException {
        CompoundTag compoundTag = new CompoundTag();

        ListTag tag = new ListTag();
        for (ItemStack stack : stacks) {
            Tag producedTag = toTag(stack);
            tag.add(producedTag);
        }

        compoundTag.put("elements", tag);
        compoundTag.putInt("DataVersion", DataMigrator.getCurrentVersion());

        NbtIo.writeCompressed(compoundTag, stream);
    }

    public static List<ItemStack> fromInputStream(InputStream stream, boolean migrate) throws IOException {
        CompoundTag compoundTag = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());

        ListTag tag = (ListTag) compoundTag.get("elements");

        Validate.isTrue(compoundTag.contains("DataVersion"));
        int prevVersion = compoundTag.getInt("DataVersion") ;

        List<ItemStack> stacks = new ArrayList<>(tag.size());

        for (int i = 0; i < tag.size(); ++i) {
            CompoundTag itemTag = tag.getCompound(i);

            if (itemTag.contains("HACKBOOK_AIR_ITEM")) {
                stacks.add(new ItemStack(Material.AIR));
            } else {
                if (migrate) {
                    itemTag = DataMigrator.updateItemStack(prevVersion, itemTag);
                }

                var optItem = net.minecraft.world.item.ItemStack.parse(getRegistry(), itemTag);
                if (optItem.isPresent()) {
                    stacks.add(CraftItemStack.asCraftMirror(optItem.get()));
                } else {
                    stacks.add(new ItemStack(Material.AIR));
                }
            }
        }

        return stacks;
    }

    public static List<ItemStack> fromInputStream(InputStream stream) throws IOException {
        return fromInputStream(stream, false);
    }
}

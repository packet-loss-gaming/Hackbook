/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gg.packetloss.hackbook;

import net.minecraft.world.item.Item;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
import org.bukkit.inventory.ItemStack;

public class MaterialInfoUtil {
    public static float getBreakSpeed(Material tool, Block block) {
        Item item = CraftMagicNumbers.getItem(tool);
        return item.getDestroySpeed(CraftItemStack.asNMSCopy(new ItemStack(tool)), ((CraftBlock) block).getNMS());
    }
}

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gg.packetloss.hackbook;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataConverterRegistry;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class DataMigrator {
    private static NBTTagCompound runFixer(int prevVersion, DSL.TypeReference typeReference, NBTTagCompound tag) {
        DataFixer fixer = DataConverterRegistry.a();

        return (NBTTagCompound) fixer.update(
                typeReference,
                new Dynamic<>(DynamicOpsNBT.a, tag),
                prevVersion,
                getCurrentVersion()
        ).getValue();
    }

    protected static NBTTagCompound updateItemStack(int prevVersion, NBTTagCompound tag) {
        return runFixer(prevVersion, DataConverterTypes.m, tag);
    }

    public static int getCurrentVersion() {
        return SharedConstants.getGameVersion().getWorldVersion();
    }
}

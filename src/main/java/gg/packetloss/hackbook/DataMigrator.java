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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;

public class DataMigrator {
    private static CompoundTag runFixer(int prevVersion, DSL.TypeReference typeReference, CompoundTag tag) {
        DataFixer fixer = DataFixers.getDataFixer();

        return (CompoundTag) fixer.update(
                typeReference,
                new Dynamic<>(NbtOps.INSTANCE, tag),
                prevVersion,
                getCurrentVersion()
        ).getValue();
    }

    protected static CompoundTag updateItemStack(int prevVersion, CompoundTag tag) {
        return runFixer(prevVersion, References.ITEM_STACK, tag);
    }

    public static int getCurrentVersion() {
        return SharedConstants.getCurrentVersion().getDataVersion().getVersion();
    }
}

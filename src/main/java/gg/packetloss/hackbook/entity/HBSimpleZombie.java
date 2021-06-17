/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gg.packetloss.hackbook.entity;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice;
import gg.packetloss.hackbook.DataMigrator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class HBSimpleZombie {
    private static boolean registered = false;
    private static EntityType<?> registration;

    private HBSimpleZombie() { }

    private static void register() {
        if (registered) {
            return;
        }

        registered = true;

        DataFixer registry = DataFixers.getDataFixer();
        Schema currentSchema = registry.getSchema(DataFixUtils.makeKey(DataMigrator.getCurrentVersion()));
        TaggedChoice.TaggedChoiceType<?> entityNameConverter = currentSchema.findChoiceType(References.ENTITY);

        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) entityNameConverter.types();
        dataTypes.put("minecraft:hb_zombie", dataTypes.get("minecraft:zombie"));

        try {
            Method m = EntityType.class.getDeclaredMethod("register", String.class, EntityType.Builder.class);
            m.setAccessible(true);

            EntityType.Builder<Entity> b = EntityType.Builder.of(HBSimpleZombieInternal::new, MobCategory.MONSTER).sized(0.6F, 1.95F);
            registration = (EntityType<?>) m.invoke(null, "hb_zombie", b);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    public static Zombie spawn(Location loc) {
        register();

        ServerLevel world = ((CraftWorld) loc.getWorld()).getHandle();
        Entity nmsEntity = registration.create(world);
        nmsEntity.setPos(loc.getX(), loc.getY(), loc.getZ());

        ((Mob) nmsEntity).finalizeSpawn(
            world,
            world.getCurrentDifficultyAt(new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())),
            MobSpawnType.COMMAND,
            null,
            null
        );

        // Reset the zombie
        Zombie bukkitEntity = (Zombie) nmsEntity.getBukkitEntity();
        bukkitEntity.getEquipment().clear();

        // Add the zombie
        world.addEntity(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);

        return bukkitEntity;
    }

    public static boolean is(org.bukkit.entity.Entity entity) {
        return ((CraftEntity) entity).getHandle() instanceof HBSimpleZombieInternal;
    }
}

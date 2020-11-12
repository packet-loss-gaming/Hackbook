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
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class HBSimpleZombie {
    private static boolean registered = false;
    private static  EntityTypes<?> registration;

    private HBSimpleZombie() { }

    private static void register() {
        if (registered) {
            return;
        }

        registered = true;

        DataFixer registry = DataConverterRegistry.a();
        Schema currentSchema = registry.getSchema(DataFixUtils.makeKey(DataMigrator.getCurrentVersion()));
        TaggedChoice.TaggedChoiceType<?> entityNameConverter = currentSchema.findChoiceType(DataConverterTypes.ENTITY);

        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) entityNameConverter.types();
        dataTypes.put("minecraft:hb_zombie", dataTypes.get("minecraft:zombie"));

        try {
            Method m = EntityTypes.class.getDeclaredMethod("a", String.class, EntityTypes.Builder.class);
            m.setAccessible(true);

            EntityTypes.Builder<Entity> b = EntityTypes.Builder.a(HBSimpleZombieInternal::new, EnumCreatureType.MONSTER).a(0.6F, 1.95F);
            registration = (EntityTypes<?>) m.invoke(null, "hb_zombie", b);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    public static Zombie spawn(Location loc) {
        register();

        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        Entity nmsEntity = registration.a(world);
        nmsEntity.setPosition(loc.getX(), loc.getY(), loc.getZ());

        ((EntityInsentient) nmsEntity).prepare(
            world,
            world.getDamageScaler(new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())),
            EnumMobSpawn.COMMAND,
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

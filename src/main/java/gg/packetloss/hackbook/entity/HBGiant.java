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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Giant;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class HBGiant {
    private static boolean registered = false;
    private static EntityType<?> registration;

    private HBGiant() { }

    private static void register() {
        if (registered) {
            return;
        }

        registered = true;

        DataFixer registry = DataFixers.getDataFixer();
        Schema currentSchema = registry.getSchema(DataFixUtils.makeKey(DataMigrator.getCurrentVersion()));
        TaggedChoice.TaggedChoiceType<?> entityNameConverter = currentSchema.findChoiceType(References.ENTITY);

        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) entityNameConverter.types();
        dataTypes.put("minecraft:hb_giant", dataTypes.get("minecraft:giant"));

        try {
            Method m = EntityType.class.getDeclaredMethod("register", String.class, EntityType.Builder.class);
            m.setAccessible(true);

            EntityType.Builder<Entity> b = EntityType.Builder.of(HBGiantInternal::new, MobCategory.MONSTER).sized(3.6F, 12.0F);
            registration = (EntityType<?>) m.invoke(null, "hb_giant", b);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    public static Giant spawn(Location loc) {
        register();

        ServerLevel world = ((CraftWorld) loc.getWorld()).getHandle();
        net.minecraft.world.entity.Entity nmsEntity = registration.create(world);
        nmsEntity.setPos(loc.getX(), loc.getY(), loc.getZ());

        world.addEntity(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);

        return (Giant) nmsEntity.getBukkitEntity();
    }

    public static boolean is(org.bukkit.entity.Entity entity) {
        return ((CraftEntity) entity).getHandle() instanceof HBGiantInternal;
    }
}

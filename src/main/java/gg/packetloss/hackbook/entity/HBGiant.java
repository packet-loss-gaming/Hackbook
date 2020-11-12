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
import org.bukkit.entity.Giant;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class HBGiant {
    private static boolean registered = false;
    private static EntityTypes<?> registration;

    private HBGiant() { }

    private static void register() {
        if (registered) {
            return;
        }

        registered = true;

        DataFixer registry = DataConverterRegistry.a();
        Schema currentSchema = registry.getSchema(DataFixUtils.makeKey(DataMigrator.getCurrentVersion()));
        TaggedChoice.TaggedChoiceType<?> entityNameConverter = currentSchema.findChoiceType(DataConverterTypes.ENTITY);

        Map<Object, Type<?>> dataTypes = (Map<Object, Type<?>>) entityNameConverter.types();
        dataTypes.put("minecraft:hb_giant", dataTypes.get("minecraft:giant"));

        try {
            Method m = EntityTypes.class.getDeclaredMethod("a", String.class, EntityTypes.Builder.class);
            m.setAccessible(true);

            EntityTypes.Builder<Entity> b = EntityTypes.Builder.a(HBGiantInternal::new, EnumCreatureType.MONSTER).a(3.6F, 12.0F);
            registration = (EntityTypes<?>) m.invoke(null, "hb_giant", b);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }

    public static Giant spawn(Location loc) {
        register();

        World world = ((CraftWorld) loc.getWorld()).getHandle();
        net.minecraft.server.v1_16_R3.Entity nmsEntity = registration.a(world);
        nmsEntity.setPosition(loc.getX(), loc.getY(), loc.getZ());

        world.addEntity(nmsEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);

        return (Giant) nmsEntity.getBukkitEntity();
    }

    public static boolean is(org.bukkit.entity.Entity entity) {
        return ((CraftEntity) entity).getHandle() instanceof HBGiantInternal;
    }
}

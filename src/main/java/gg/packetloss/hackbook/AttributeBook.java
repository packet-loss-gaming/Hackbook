/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gg.packetloss.hackbook;

import gg.packetloss.hackbook.exceptions.UnsupportedFeatureException;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

public class AttributeBook {
    public enum Attribute {
        MAX_HEALTH(Attributes.MAX_HEALTH),
        FOLLOW_RANGE(Attributes.FOLLOW_RANGE),
        KNOCKBACK_RESISTANCE(Attributes.KNOCKBACK_RESISTANCE),
        MOVEMENT_SPEED(Attributes.MOVEMENT_SPEED),
        ATTACK_KNOCKBACK(Attributes.ATTACK_KNOCKBACK),
        ATTACK_DAMAGE(Attributes.ATTACK_DAMAGE);

        public final net.minecraft.world.entity.ai.attributes.Attribute attribute;

        Attribute(net.minecraft.world.entity.ai.attributes.Attribute attribute) {
            this.attribute = attribute;
        }
    }

    public static double getAttribute(LivingEntity entity, Attribute attribute) throws UnsupportedFeatureException {
        try {
            Mob nmsEntity = getNMSEntity(entity);

            return nmsEntity.getAttribute(attribute.attribute).getBaseValue();
        } catch (Throwable t) {
            t.printStackTrace();
            throw new UnsupportedFeatureException();
        }
    }

    public static void setAttribute(LivingEntity entity, Attribute attribute, double value) throws UnsupportedFeatureException {
        try {
            Mob nmsEntity = getNMSEntity(entity);

            nmsEntity.getAttribute(attribute.attribute).setBaseValue(value);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new UnsupportedFeatureException();
        }
    }

    private static Mob getNMSEntity(LivingEntity entity) throws UnsupportedFeatureException {
        try {
            return ((Mob) ((CraftLivingEntity) entity).getHandle());
        } catch (Throwable t) {
            t.printStackTrace();
            throw new UnsupportedFeatureException();
        }
    }
}

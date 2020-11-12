/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gg.packetloss.hackbook.entity;

import net.minecraft.server.v1_16_R3.*;

class HBGiantInternal extends EntityGiantZombie {
    public HBGiantInternal(EntityTypes<? extends EntityGiantZombie> var0, World var1) {
        super(EntityTypes.GIANT, var1); // This ensures the giant shows and saves as a giant, instead of some custom
                                        // invalid custom type. However, this also means that the this giant will
                                        // not be restored to this class. So, we have to be careful to ensure we
                                        // recreate the giant when coming from disk.
    }

    @Override
    protected void initPathfinder() {
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.23D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(12.0D);

        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }
}
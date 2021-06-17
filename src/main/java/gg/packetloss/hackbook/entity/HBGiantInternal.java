/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package gg.packetloss.hackbook.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

class HBGiantInternal extends Giant implements EntityType.EntityFactory<Giant> {
    public HBGiantInternal(EntityType<? extends Giant> var0, Level var1) {
        super(EntityType.GIANT, var1); // This ensures the giant shows and saves as a giant, instead of some custom
                                       // invalid custom type. However, this also means that the this giant will
                                       // not be restored to this class. So, we have to be careful to ensure we
                                       // recreate the giant when coming from disk.
    }

    @Override
    public Giant create(EntityType<Giant> entityType, Level level) {
        return new HBGiantInternal(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.23D);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(12.0D);

        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }
}
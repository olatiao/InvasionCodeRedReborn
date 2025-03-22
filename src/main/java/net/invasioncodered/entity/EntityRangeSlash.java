package net.invasioncodered.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EntityRangeSlash extends ThrowableProjectile implements GeoEntity {
    private static final EntityDataAccessor<Boolean> IS_BIG = SynchedEntityData.defineId(EntityRangeSlash.class,
            EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    public EntityRangeSlash(EntityType<? extends ThrowableProjectile> p_36721_, Level p_36722_) {
        super(p_36721_, p_36722_);
        this.setNoGravity(true);
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount >= 120 || this.getOwner() == null) {
            this.discard();
        }
        if (this.getOwner() != null) {
            if (this.distanceTo(this.getOwner()) >= 100) {
                this.discard();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult p_37258_) {

    }

    @Override
    protected void onHitEntity(EntityHitResult p_36757_) {
        Entity entity = p_36757_.getEntity();
        if (this.getOwner() != null && entity != this.getOwner() && this.getOwner() instanceof LivingEntity livingOwner) {
            // 检查目标是否是召唤者的同伴
            boolean isOwnersFriend = false;
            
            // 如果目标是裂刃龙且与本实体有相同拥有者，则不造成伤害
            if (entity instanceof EntityGashslitDragon dragon && dragon.getOwner() == this.getOwner()) {
                isOwnersFriend = true;
            }
            
            // 如果目标是另一个斩击并且有相同拥有者，则不造成伤害
            if (entity instanceof EntityRangeSlash slash && slash.getOwner() == this.getOwner()) {
                isOwnersFriend = true;
            }
            
            // 如果目标是裂刃帝王且是本实体的拥有者，则不造成伤害
            if (entity instanceof EntityGashslit gashslit && gashslit == this.getOwner()) {
                isOwnersFriend = true;
            }
            
            // 只有当目标不是召唤者的同伴时才造成伤害
            if (!isOwnersFriend) {
                entity.hurt(this.damageSources().mobAttack(livingOwner), this.isBig() ? 26 : 12);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        // 空实现
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(IS_BIG, false);
    }

    public void setBig(boolean value) {
        this.entityData.set(IS_BIG, value);
    }

    public boolean isBig() {
        return this.entityData.get(IS_BIG);
    }

    @Override
    public double getTick(Object o) {
        return this.tickCount;
    }
}

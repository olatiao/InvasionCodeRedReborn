package net.invasioncodered.entity;

import java.util.Iterator;
import java.util.List;
import net.invasioncodered.config.AbstractBedrockRaider;
import net.invasioncodered.network.GashslitParticlePacket;
import net.invasioncodered.InvasionCodeRedNetwork;
import net.invasioncodered.InvasionCodeRedUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.invasioncodered.register.InvasionCodeRedSounds;
import net.invasioncodered.register.InvasionCodeRedEntities;
import net.invasioncodered.register.InvasionCodeRedParticles;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class EntityGashslit extends AbstractBedrockRaider implements GeoEntity {
    protected static final RawAnimation WALK = RawAnimation.begin().then("animation.gashslit.walk",
            Animation.LoopType.LOOP);
    protected static final RawAnimation RUN = RawAnimation.begin().then("animation.gashslit.run",
            Animation.LoopType.LOOP);
    protected static final RawAnimation PREPARE = RawAnimation.begin().then("animation.gashslit.prepare",
            Animation.LoopType.PLAY_ONCE);
    protected static final RawAnimation CHARGE = RawAnimation.begin().then("animation.gashslit.charge",
            Animation.LoopType.LOOP);
    protected static final RawAnimation DASH_POSE = RawAnimation.begin().then("animation.gashslit.flew2",
            Animation.LoopType.PLAY_ONCE);
    protected static final RawAnimation SLOW_ATTACK = RawAnimation.begin()
            .then("animation.gashslit.slowattack", Animation.LoopType.PLAY_ONCE);
    protected static final RawAnimation ATTACK = RawAnimation.begin().then("animation.gashslit.attack",
            Animation.LoopType.PLAY_ONCE);
    protected static final RawAnimation SMASH = RawAnimation.begin().then("animation.gashslit.swordsmash",
            Animation.LoopType.PLAY_ONCE);
    protected static final RawAnimation BLOCK = RawAnimation.begin().then("animation.gashslit.block",
            Animation.LoopType.LOOP);
    protected static final RawAnimation STEP_BACK = RawAnimation.begin()
            .then("animation.gashslit.stepback", Animation.LoopType.LOOP);
    protected static final RawAnimation SHOOT = RawAnimation.begin().then("animation.gashslit.shoot",
            Animation.LoopType.LOOP);
    protected static final RawAnimation FLEX = RawAnimation.begin().then("animation.gashslit.flex",
            Animation.LoopType.LOOP);
    protected static final RawAnimation DEATH = RawAnimation.begin().then("animation.gashslit.death",
            Animation.LoopType.HOLD_ON_LAST_FRAME);
    protected static final RawAnimation RAGE_POSE = RawAnimation.begin()
            .then("animation.gashslit.ragepose", Animation.LoopType.LOOP);

    private static final EntityDataAccessor<Boolean> POWER = SynchedEntityData.defineId(EntityGashslit.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DEFENSE = SynchedEntityData.defineId(EntityGashslit.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> POWER2 = SynchedEntityData.defineId(EntityGashslit.class,
            EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LAST_CHANCE = SynchedEntityData.defineId(EntityGashslit.class,
            EntityDataSerializers.BOOLEAN);

    private double speed = 0.24D;
    private int deathAnimTime;

    private List<TimedEvent> events = new ObjectArrayList<>();
    private List<TimedEvent> fired = new ObjectArrayList<>();
    private List<EntityGashslitDragon> summonCap = new ObjectArrayList<>();
    private final AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);
    private final ServerBossEvent bossEvent = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS));

    public EntityGashslit(EntityType<? extends Raider> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 680)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.ATTACK_DAMAGE, 13)
                .add(Attributes.FOLLOW_RANGE, 100);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(POWER, false);
        this.entityData.define(DEFENSE, false);
        this.entityData.define(POWER2, false);
        this.entityData.define(LAST_CHANCE, false);
    }

    @Override
    protected void tickDeath() {
        ++this.deathAnimTime;
        if (this.deathAnimTime >= 95) {
            this.level().broadcastEntityEvent(this, (byte) 1);
            this.setRemoved(RemovalReason.KILLED);
        }
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
        this.playSound(SoundEvents.WARDEN_STEP);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return InvasionCodeRedSounds.GASHSLIT_HURT.get();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return InvasionCodeRedSounds.GASHSLIT_AMBIENT.get();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Monster.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 0.60));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, (living) -> {
            return !(living instanceof EntityGashslitDragon) && !(living instanceof EntityGashslit);
        }));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true));
        this.targetSelector.addGoal(3,
                new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true, (villager) -> {
                    return !villager.isBaby();
                }));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer p_31483_) {
        super.startSeenByPlayer(p_31483_);
        this.bossEvent.addPlayer(p_31483_);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer p_31488_) {
        super.stopSeenByPlayer(p_31488_);
        this.bossEvent.removePlayer(p_31488_);
    }

    @Override
    public void tick() {
        super.tick();
        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 9, true, false));
        if (this.isAlive()) {
            if (this.getTarget() != null) {
                if (this.getSkinID() != 9) {
                    if (!this.isEventFired() && this.getSkinID() == 0) {
                        if (this.tickCount % 5 == 0) {
                            if (this.getHealth() <= 300) {
                                this.phase2RandomAttack();
                                
                                if (this.tickCount % 15 == 0 && !this.isEventFired()) {
                                    this.phase2ComboAttack();
                                }
                            } else {
                                this.randomAttack();
                                
                                if (this.tickCount % 25 == 0 && !this.isEventFired()) {
                                    this.comboAttack();
                                }
                            }
                        }
                    }

                    if (this.isDelayedAttacking()) {
                        // this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 9, true,
                        // false));
                    }

                    if (this.getHealth() <= 520 && !this.entityData.get(POWER)) {
                        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 0, true, false));
                        this.entityData.set(POWER, true);
                    } else if (this.getHealth() <= 300 && !this.entityData.get(DEFENSE)) {
                        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 2, true, false));
                        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 780, 1, true, false));
                        InvasionCodeRedNetwork.sendToAll(
                                new GashslitParticlePacket(GashslitParticlePacket.ParticleType.POP_EFFECT, this));
                        this.entityData.set(DEFENSE, true);
                    } else if (this.getHealth() <= 150 && !this.entityData.get(POWER2)) {
                        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 199980, 2, true, false));
                        this.entityData.set(POWER2, true);
                    } else if (this.getHealth() <= 10 && !this.entityData.get(LAST_CHANCE)) {
                        this.removeAllEffects();
                        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 4, true, false));
                        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 2, true, false));
                        InvasionCodeRedNetwork.sendToAll(
                                new GashslitParticlePacket(GashslitParticlePacket.ParticleType.POP_EFFECT, this));
                        this.entityData.set(LAST_CHANCE, true);
                    }
                }

                this.lookControl.setLookAt(this.getTarget(), 30, 30);
                this.getNavigation().moveTo(this.getTarget(), this.getAttributeBaseValue(Attributes.MOVEMENT_SPEED));

                for (int i = 0; i < this.events.size(); i++) {
                    TimedEvent event = this.events.get(i);
                    if (event != null && event.ticks <= this.tickCount) {
                        event.callback.run();
                        this.fired.add(event);
                    }
                }
                for (Iterator<TimedEvent> itr = this.events.iterator(); itr.hasNext();) {
                    TimedEvent event = itr.next();
                    if (event != null && this.fired.contains(event)) {
                        itr.remove();
                    }
                }

                for (Iterator<EntityGashslitDragon> itr = this.summonCap.iterator(); itr.hasNext();) {
                    EntityGashslitDragon dragon = itr.next();
                    if (dragon != null && !dragon.isAlive()) {
                        itr.remove();
                    }
                }

                if (this.getSkinID() == 6) {
                    if (this.tickCount % 5 == 0) {
                        EntityRangeSlash slash = new EntityRangeSlash(InvasionCodeRedEntities.RANGE_SLASH.get(),
                                this.level());
                        slash.setOwner(this);
                        slash.setPos(this.getX(), this.getEyeY() - 0.5, this.getZ());
                        if (this.getHealth() <= 300) {
                            slash.setBig(true);
                        }
                        double d0 = this.getTarget().getX() - this.getX();
                        double d1 = this.getTarget().getY(0.5) - slash.getY();
                        double d2 = this.getTarget().getZ() - this.getZ();
                        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                        slash.shoot(d0 * 3, d1 + d3 * (double) 0.0F, d2 * 3, 1.6F, 1);
                        this.level().addFreshEntity(slash);
                    }
                }

                if (this.getSkinID() == 9) {
                    List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class,
                            this.getBoundingBox().inflate(3.5D));
                    for (int i = 0; i < list.size(); i++) {
                        LivingEntity living = list.get(i);
                        if (living != this) {
                            living.hurt(this.damageSources().mobAttack(this), 46);
                        }
                    }

                    if (!this.level().isClientSide()) {
                        InvasionCodeRedNetwork.sendToAll(
                                new GashslitParticlePacket(GashslitParticlePacket.ParticleType.SLASH_HIT, this));
                    }
                }

                if (this.getSkinID() == 8) {
                    float f14 = this.getYRot() * ((float) Math.PI / 180F);
                    float x = Mth.sin(f14);
                    float z = Mth.cos(f14);
                    this.setPos(this.getX() + (x * -6), this.getY() + 0.2, this.getZ() + (z * 6));
                    this.setYRot(-this.getYRot());
                    if (!this.level().isClientSide()) {
                        InvasionCodeRedNetwork.sendToAll(
                                new GashslitParticlePacket(GashslitParticlePacket.ParticleType.SLASH_HIT, this));
                    }
                    List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class,
                            this.getBoundingBox().inflate(2.5D));
                    for (int i = 0; i < list.size(); i++) {
                        LivingEntity living = list.get(i);
                        if (living != this) {
                            living.hurt(this.damageSources().mobAttack(this), this.getHealth() <= 300 ? 25 : 20);
                        }
                    }
                }
            }

            if (this.getHealth() <= 300) {
                for (int i = 0; i < 50; i++) {
                    double spawnRange = 30;
                    double yRange = 16;
                    double x = (double) this.getX()
                            + (this.level().getRandom().nextDouble() - this.level().getRandom().nextDouble())
                                    * (double) spawnRange
                            + 0.5D;
                    double y = (double) this.getY()
                            + (this.level().getRandom().nextDouble() - this.level().getRandom().nextDouble())
                                    * (double) yRange
                            + 0.5D;
                    double z = (double) this.getZ()
                            + (this.level().getRandom().nextDouble() - this.level().getRandom().nextDouble())
                                    * (double) spawnRange
                            + 0.5D;
                    this.level().addParticle(InvasionCodeRedParticles.RAGE_MODE.get(), x, y, z, 0, 0.2, 0);
                }
            }
        }

        if (!this.isAlive()) {
            this.removeAllEvents();
        }

        if (!this.level().isClientSide()) {
            if (this.getTarget() == null) {
                this.removeAllEvents();
            }
        }
    }

    public void randomAttack() {
        if (InvasionCodeRedUtil.percent(0.11)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 3.9F)) {
                this.walkmode1();
            }
        } else if (InvasionCodeRedUtil.percent(0.12)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 2.5F)) {
                this.walkmode2();
            }
        } else if (InvasionCodeRedUtil.percent(0.10)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 3.9F)) {
                this.smash();
            }
        } else if (InvasionCodeRedUtil.percent(0.25)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 8.0F) || this.tickCount % 30 == 0) {
                this.dash2start();
            }
        } else if (InvasionCodeRedUtil.percent(0.08)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 3.9F)) {
                this.blockmode();
            }
        } else if (InvasionCodeRedUtil.percent(0.09)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 6.9F) || this.tickCount % 40 == 0) {
                this.stepback();
            }
        } else if (InvasionCodeRedUtil.percent(0.20)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 7.0F) || this.tickCount % 35 == 0) {
                this.dash();
            }
        } else if (InvasionCodeRedUtil.percent(0.09)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 8.5F) || this.tickCount % 50 == 0) {
                if (this.summonCap.size() < 8) {
                    this.summonmode();
                }
            }
        }
    }

    public void phase2RandomAttack() {
        if (InvasionCodeRedUtil.percent(0.12)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 5.9F)) {
                this.phase2walkmode1();
            }
        } else if (InvasionCodeRedUtil.percent(0.12)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 4.5F)) {
                this.phase2walkmode2();
            }
        } else if (InvasionCodeRedUtil.percent(0.10)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 5.9F)) {
                this.phase2smash();
            }
        } else if (InvasionCodeRedUtil.percent(0.25)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 8.0F) || this.tickCount % 30 == 0) {
                this.phase2dash2start();
            }
        } else if (InvasionCodeRedUtil.percent(0.08)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 3.9F)) {
                this.phase2blockmode();
            }
        } else if (InvasionCodeRedUtil.percent(0.09)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 6.9F) || this.tickCount % 40 == 0) {
                this.phase2stepback();
            }
        } else if (InvasionCodeRedUtil.percent(0.22)) {
            if (this.isWithinMeleeAttackRange(this.getTarget(), 7.0F) || this.tickCount % 35 == 0) {
                this.phase2dash();
            }
        }
    }

    public void phase2dash() {
        this.setEventFired(true);
        this.setSkinID(8);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
            }
        }, 24);
    }

    public void phase2shootmode() {
        this.setSkinID(6);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
            }
        }, 80);
    }

    public void phase2stepback() {
        this.setEventFired(true);
        this.setSkinID(7);
        double d0 = this.getX() - this.getTarget().getX();
        double d2 = this.getZ() - this.getTarget().getZ();
        double xD = -d0 / (0.2f * this.distanceTo(this.getTarget()));
        double zD = -d2 / (0.2f * this.distanceTo(this.getTarget()));
        this.setDeltaMovement(-xD, this.getDeltaMovement().y, -zD);
        this.addEvent(() -> this.phase2shootmode(), 6);
    }

    public void phase2blockmode() {
        this.setEventFired(true);
        this.setSkinID(4);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(100);
        this.setInvulnerable(true);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setInvulnerable(false);
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
                EntityGashslit.this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(EntityGashslit.this.speed);
                EntityGashslit.this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5);
            }
        }, 80);
    }

    public void phase2dash2() {
        this.setSkinID(9);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
        double d0 = this.getX() - this.getTarget().getX();
        double d2 = this.getZ() - this.getTarget().getZ();
        double xD = -d0 / (0.06f * this.distanceTo(this.getTarget()));
        double zD = -d2 / (0.06f * this.distanceTo(this.getTarget()));
        this.setDeltaMovement(xD, this.getDeltaMovement().y, zD);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setSkinID(0);
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(EntityGashslit.this.speed);
            }
        }, 6);
        this.playSound(InvasionCodeRedSounds.SLICE_FX1.get());
        if (!this.level().isClientSide()) {
            InvasionCodeRedNetwork
                    .sendToAll(new GashslitParticlePacket(GashslitParticlePacket.ParticleType.DASH_SMOKE, this));
            InvasionCodeRedNetwork
                    .sendToAll(new GashslitParticlePacket(GashslitParticlePacket.ParticleType.DASH_TRAIL, this));
        }
    }

    public void phase2dash2start() {
        this.setEventFired(true);
        this.setSkinID(10);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1);
        this.addEvent(() -> this.phase2dash2(), 10);
        if (!this.level().isClientSide()) {
            InvasionCodeRedNetwork
                    .sendToAll(new GashslitParticlePacket(GashslitParticlePacket.ParticleType.DASH_TRAIL, this));
        }
    }

    public void phase2smash() {
        this.setEventFired(true);
        this.setSkinID(3);
        this.setDelayedAttacking(true);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.19);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.5);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                if (EntityGashslit.this.isWithinMeleeAttackRange(EntityGashslit.this.getTarget(), 5.9F)) {
                    EntityGashslit.this.getTarget()
                            .hurt(EntityGashslit.this.damageSources().mobAttack(EntityGashslit.this), 29);
                }
            }
        }, 25);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setDelayedAttacking(false);
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
                EntityGashslit.this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(EntityGashslit.this.speed);
                EntityGashslit.this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5);
            }
        }, 40);
    }

    public void phase2walkmode2() {
        this.setEventFired(true);
        this.setSkinID(1);
        this.setDelayedAttacking(true);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                if (EntityGashslit.this.isWithinMeleeAttackRange(EntityGashslit.this.getTarget(), 4.5F)) {
                    EntityGashslit.this.getTarget()
                            .hurt(EntityGashslit.this.damageSources().mobAttack(EntityGashslit.this), 13);
                }
            }
        }, 7);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setDelayedAttacking(false);
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
            }
        }, 10);
    }

    public void phase2walkmode1() {
        this.setEventFired(true);
        this.setSkinID(2);
        this.setDelayedAttacking(true);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                if (EntityGashslit.this.isWithinMeleeAttackRange(EntityGashslit.this.getTarget(), 5.9F)) {
                    EntityGashslit.this.getTarget()
                            .hurt(EntityGashslit.this.damageSources().mobAttack(EntityGashslit.this), 25);
                }
            }
        }, 20);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setDelayedAttacking(false);
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
            }
        }, 30);
    }

    public void summonmode() {
        this.setEventFired(true);
        this.setSkinID(5);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 4; i++) {
                    EntityGashslitDragon dragon = new EntityGashslitDragon(
                            InvasionCodeRedEntities.GASHSLIT_DRAGON.get(),
                            EntityGashslit.this.level());
                    dragon.setPos(
                            EntityGashslit.this.getX() + EntityGashslit.this.level().getRandom().nextGaussian() * 0.2D,
                            EntityGashslit.this.getY() + 0.5
                                    + EntityGashslit.this.level().getRandom().nextGaussian() * 0.2D,
                            EntityGashslit.this.getZ() + EntityGashslit.this.level().getRandom().nextGaussian() * 0.2D);
                    dragon.setLimitedLife(600);
                    EntityGashslit.this.level().addFreshEntity(dragon);
                    EntityGashslit.this.summonCap.add(dragon);
                }
            }
        }, 18);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
            }
        }, 35);
    }

    // slice barrage
    public void dash() {
        this.setEventFired(true);
        this.setSkinID(8);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
            }
        }, 24);
    }

    public void shootmode() {
        this.setSkinID(6);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
            }
        }, 80);
    }

    public void stepback() {
        this.setEventFired(true);
        this.setSkinID(7);
        double d0 = this.getX() - this.getTarget().getX();
        double d2 = this.getZ() - this.getTarget().getZ();
        double xD = -d0 / (0.2f * this.distanceTo(this.getTarget()));
        double zD = -d2 / (0.2f * this.distanceTo(this.getTarget()));
        this.setDeltaMovement(-xD, this.getDeltaMovement().y, -zD);
        this.addEvent(() -> this.shootmode(), 6);
    }

    public void blockmode() {
        this.setEventFired(true);
        this.setSkinID(4);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
        this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(100);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
                EntityGashslit.this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(EntityGashslit.this.speed);
                EntityGashslit.this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.5);
            }
        }, 80);
    }

    public void dash2() {
        this.setSkinID(9);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
        double d0 = this.getX() - this.getTarget().getX();
        double d2 = this.getZ() - this.getTarget().getZ();
        double xD = -d0 / (0.06f * this.distanceTo(this.getTarget()));
        double zD = -d2 / (0.06f * this.distanceTo(this.getTarget()));
        this.setDeltaMovement(xD, this.getDeltaMovement().y, zD);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setSkinID(0);
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(EntityGashslit.this.speed);
            }
        }, 6);
        this.playSound(InvasionCodeRedSounds.SLICE_FX1.get());
        if (!this.level().isClientSide()) {
            InvasionCodeRedNetwork
                    .sendToAll(new GashslitParticlePacket(GashslitParticlePacket.ParticleType.DASH_SMOKE, this));
            InvasionCodeRedNetwork
                    .sendToAll(new GashslitParticlePacket(GashslitParticlePacket.ParticleType.DASH_TRAIL, this));
        }
    }

    public void dash2start() {
        this.setEventFired(true);
        this.setSkinID(10);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.19);
        this.addEvent(() -> this.dash2(), 10);
        if (!this.level().isClientSide()) {
            InvasionCodeRedNetwork
                    .sendToAll(new GashslitParticlePacket(GashslitParticlePacket.ParticleType.DASH_TRAIL, this));
        }
    }

    public void smash() {
        this.setEventFired(true);
        this.setSkinID(3);
        this.setDelayedAttacking(true);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.19);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                if (EntityGashslit.this.isWithinMeleeAttackRange(EntityGashslit.this.getTarget(), 3.9F)) {
                    EntityGashslit.this.getTarget()
                            .hurt(EntityGashslit.this.damageSources().mobAttack(EntityGashslit.this), 29);
                }
            }
        }, 25);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setDelayedAttacking(false);
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
                EntityGashslit.this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(EntityGashslit.this.speed);
            }
        }, 40);
    }

    public void walkmode2() {
        this.setEventFired(true);
        this.setSkinID(1);
        this.setDelayedAttacking(true);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                if (EntityGashslit.this.isWithinMeleeAttackRange(EntityGashslit.this.getTarget(), 2.5F)) {
                    EntityGashslit.this.getTarget()
                            .hurt(EntityGashslit.this.damageSources().mobAttack(EntityGashslit.this), 13);
                }
            }
        }, 7);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setDelayedAttacking(false);
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
            }
        }, 10);
    }

    public void walkmode1() {
        this.setEventFired(true);
        this.setSkinID(2);
        this.setDelayedAttacking(true);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                if (EntityGashslit.this.isWithinMeleeAttackRange(EntityGashslit.this.getTarget(), 3.9F)) {
                    EntityGashslit.this.getTarget()
                            .hurt(EntityGashslit.this.damageSources().mobAttack(EntityGashslit.this), 25);
                }
            }
        }, 20);
        this.addEvent(new Runnable() {
            @Override
            public void run() {
                EntityGashslit.this.setDelayedAttacking(false);
                EntityGashslit.this.setEventFired(false);
                EntityGashslit.this.setSkinID(0);
            }
        }, 30);
    }

    public void removeAllEvents() {
        this.events.clear();
        this.fired.clear();
        this.setDelayedAttacking(false);
        this.setEventFired(false);
        this.setSkinID(0);
    }

    public void addEvent(Runnable runnable, int ticksFromNow) {
        this.events.add(new TimedEvent(runnable, this.tickCount + ticksFromNow));
    }

    private static class TimedEvent implements Comparable<TimedEvent> {
        Runnable callback;
        int ticks;

        public TimedEvent(Runnable callback, int ticks) {
            this.callback = callback;
            this.ticks = ticks;
        }

        @Override
        public int compareTo(TimedEvent event) {
            return event.ticks < ticks ? 1 : -1;
        }
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        if (this.getSkinID() == 4 && !p_21016_.isCreativePlayer()) {
            return false;
        }
        return super.hurt(p_21016_, p_21017_);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController<>(this, "attack", 1, this::attackController));
        data.add(new AnimationController<>(this, "dash", 4, this::dashController));
        data.add(new AnimationController<>(this, "block", 4, this::blockController));
        data.add(new AnimationController<>(this, "move", 6, this::moveController));
        data.add(new AnimationController<>(this, "stepback", 2, this::stepbackController));
        data.add(new AnimationController<>(this, "shoot", 1, this::shootController));
        data.add(new AnimationController<>(this, "summon", 4, this::summonController));
        data.add(new AnimationController<>(this, "death", 1, this::deathController));
        data.add(new AnimationController<>(this, "charge", 1, this::chargeController));
    }

    protected PlayState chargeController(AnimationState<EntityGashslit> event) {
        if (this.getSkinID() == 8 && this.isAlive()) {
            event.getController().setAnimation(CHARGE);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    protected PlayState deathController(AnimationState<EntityGashslit> event) {
        if (!this.isAlive()) {
            event.getController().setAnimation(DEATH);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    protected PlayState summonController(AnimationState<EntityGashslit> event) {
        if (this.getSkinID() == 5 && this.isAlive()) {
            event.getController().setAnimation(FLEX);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    protected PlayState shootController(AnimationState<EntityGashslit> event) {
        if (this.getSkinID() == 6 && this.isAlive()) {
            event.getController().setAnimation(SHOOT);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    protected PlayState stepbackController(AnimationState<EntityGashslit> event) {
        if (this.getSkinID() == 7 && this.isAlive()) {
            event.getController().setAnimation(STEP_BACK);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    protected PlayState moveController(AnimationState<EntityGashslit> event) {
        // 记录详细的动画状态信息
        boolean isOnGround = this.onGround();
        int skinID = this.getSkinID();
        boolean isAlive = this.isAlive();
        boolean isMoving = event.isMoving();
        double speed = this.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
        double moveSpeed = this.getDeltaMovement().horizontalDistance();

        if (isOnGround && skinID == 0 && isAlive) {
            if (isMoving) {
                if (this.getHealth() <= 300) {
                    event.getController().setAnimation(RAGE_POSE);
                } else {
                    if (speed >= 0.98) {
                        event.getController().setAnimation(RUN);
                    } else {
                        // 动态调整走路动画速度，根据实际移动速度
                        // 基准速度为1.5，但如果实际移动速度较高，则提高动画速度
                        double animationSpeed = 1.5;
                        if (moveSpeed > 0.1) {
                            // 根据实际移动速度调整动画速率，最高可达2.5倍速
                            animationSpeed = 1.5 + (moveSpeed * 10.0);
                            animationSpeed = Math.min(animationSpeed, 2.5);
                        }
                        event.getController().setAnimationSpeed(animationSpeed);
                        event.getController().setAnimation(WALK);
                    }
                }
            } else {
                if (this.getHealth() <= 300) {
                    event.getController().setAnimation(RAGE_POSE);
                } else {
                    // 静止时使用标准速度的走路动画作为待机姿势
                    event.getController().setAnimationSpeed(1.5);
                    event.getController().setAnimation(WALK);
                }
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    protected PlayState blockController(AnimationState<EntityGashslit> event) {
        if (this.getSkinID() == 4 && this.isAlive()) {
            event.getController().setAnimation(BLOCK);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    protected PlayState dashController(AnimationState<EntityGashslit> event) {
        if (this.getSkinID() == 10 && this.isAlive()) {
            event.getController().setAnimation(PREPARE);
            return PlayState.CONTINUE;
        } else if (this.getSkinID() == 9 && this.isAlive()) {
            event.getController().setAnimation(DASH_POSE);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    protected PlayState attackController(AnimationState<EntityGashslit> event) {
        if (this.isDelayedAttacking() && this.getSkinID() == 1 && this.isAlive()) {
            event.getController().setAnimation(ATTACK);
            return PlayState.CONTINUE;
        } else if (this.isDelayedAttacking() && this.getSkinID() == 2 && this.isAlive()) {
            event.getController().setAnimation(SLOW_ATTACK);
            return PlayState.CONTINUE;
        } else if (this.isDelayedAttacking() && this.getSkinID() == 3 && this.isAlive()) {
            event.getController().setAnimation(SMASH);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.factory;
    }

    @Override
    public double getTick(Object o) {
        return this.tickCount;
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_) {

    }

    @Override
    public SoundEvent getCelebrateSound() {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    public void comboAttack() {
        if (this.getTarget() != null && !this.isEventFired() && this.distanceTo(this.getTarget()) > 8.0F) {
            // 50%几率使用组合技
            if (InvasionCodeRedUtil.percent(0.5)) {
                // 先后退
                this.stepback();
                
                // 然后在回调中添加冲刺和召唤
                this.addEvent(new Runnable() {
                    @Override
                    public void run() {
                        if (EntityGashslit.this.isAlive() && EntityGashslit.this.getTarget() != null) {
                            EntityGashslit.this.dash2start();
                            
                            // 冲刺后有20%几率召唤小龙
                            if (InvasionCodeRedUtil.percent(0.2) && EntityGashslit.this.summonCap.size() < 6) {
                                EntityGashslit.this.addEvent(() -> EntityGashslit.this.summonmode(), 20);
                            }
                        }
                    }
                }, 90);
            }
        }
    }
    
    public void phase2ComboAttack() {
        if (this.getTarget() != null && !this.isEventFired() && this.distanceTo(this.getTarget()) > 7.0F) {
            // 65%几率使用组合技
            if (InvasionCodeRedUtil.percent(0.65)) {
                // 先冲刺
                this.phase2dash2start();
                
                // 然后在回调中添加另一个攻击
                this.addEvent(new Runnable() {
                    @Override
                    public void run() {
                        if (EntityGashslit.this.isAlive() && EntityGashslit.this.getTarget() != null) {
                            if (InvasionCodeRedUtil.percent(0.7)) {
                                EntityGashslit.this.phase2walkmode1();
                            } else {
                                EntityGashslit.this.phase2smash();
                            }
                        }
                    }
                }, 20);
            }
        }
    }
}
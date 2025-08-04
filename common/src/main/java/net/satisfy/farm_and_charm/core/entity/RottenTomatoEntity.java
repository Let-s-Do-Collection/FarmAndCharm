package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class RottenTomatoEntity extends ThrowableItemProjectile {

    public RottenTomatoEntity(Level world, LivingEntity owner) {
        super(EntityTypeRegistry.ROTTEN_TOMATO.get(), owner, world);
    }

    public RottenTomatoEntity(EntityType<? extends RottenTomatoEntity> entityType, Level world) {
        super(entityType, world);
    }

    protected @NotNull Item getDefaultItem() {
        return ObjectRegistry.ROTTEN_TOMATO.get();
    }

    private ParticleOptions getParticleParameters() {
        return new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ObjectRegistry.ROTTEN_TOMATO.get()));
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleEffect = getParticleParameters();
            for (int i = 0; i < 30; ++i) {
                double offsetX = this.random.nextGaussian() * 0.02D;
                double offsetY = this.random.nextGaussian() * 0.02D;
                double offsetZ = this.random.nextGaussian() * 0.02D;
                this.level().addParticle(particleEffect, this.getX() + offsetX, this.getY() + offsetY, this.getZ() + offsetZ, 0.0D, 0.0D, 0.0D);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 30, 0));
        }
        int damage = 1;
        entity.hurt(entity.damageSources().thrown(this, this.getOwner()), (float) damage);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.playSound(SoundEvents.SLIME_BLOCK_BREAK, 1.0F, 1.0F);
            if (this.random.nextFloat() < 0.15F) {
                this.spawnAtLocation(ObjectRegistry.TOMATO_SEEDS.get());
            }
            this.discard();
        }
    }
}
package me.andrew.gravitychanger.mixin;

import me.andrew.gravitychanger.accessor.PlayerEntityAccessor;
import me.andrew.gravitychanger.util.RotationUtil;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PillagerEntity.class)
public abstract class PillagerEntityMixin implements CrossbowUser {
    @Redirect(
            method = "shoot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/PillagerEntity;shoot(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/projectile/ProjectileEntity;FF)V",
                    ordinal = 0
            )
    )
    private void redirect_shoot_shoot_0(PillagerEntity pillagerEntity, LivingEntity entity, LivingEntity target, ProjectileEntity projectile, float multishotSpray, float speed) {
        if(!(target instanceof PlayerEntity)) {
            this.shoot(entity, target, projectile, multishotSpray, speed);
            return;
        }
        PlayerEntityAccessor playerEntityAccessor = (PlayerEntityAccessor) target;
        Direction gravityDirection = playerEntityAccessor.gravitychanger$getGravityDirection();

        Vec3d targetPos = target.getPos().add(RotationUtil.vecPlayerToWorld(0.0D, target.getHeight() * 0.3333333333333333D, 0.0D, gravityDirection));

        double d = targetPos.x - entity.getX();
        double e = targetPos.z - entity.getZ();
        double f = Math.sqrt(d * d + e * e);
        if(gravityDirection != Direction.DOWN) {
            f = Math.sqrt(f);
        }
        double g = targetPos.y - projectile.getY() + f * 0.20000000298023224D;
        Vec3f vec3f = this.getProjectileLaunchVelocity(entity, new Vec3d(d, g, e), multishotSpray);
        projectile.setVelocity((double)vec3f.getX(), (double)vec3f.getY(), (double)vec3f.getZ(), speed, (float)(14 - entity.world.getDifficulty().getId() * 4));
        entity.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (entity.getRandom().nextFloat() * 0.4F + 0.8F));
    }
}

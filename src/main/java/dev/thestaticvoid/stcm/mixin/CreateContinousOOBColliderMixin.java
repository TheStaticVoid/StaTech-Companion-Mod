package dev.thestaticvoid.stcm.mixin;

import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @see <a href="https://github.com/Creators-of-Create/Create/pull/10301">Creators-of-Create/Create#10301</a>
 */
@Mixin(targets = "com.simibubi.create.foundation.collision.ContinuousOBBCollider$ContinuousSeparationManifold", remap = false)
public class CreateContinousOOBColliderMixin {
    @Shadow(remap = false)
    private Vec3 normalAxis;

    @Shadow(remap = false)
    private double normalSeparation;

    @Shadow(remap = false)
    private Vec3 axis;

    @Shadow(remap = false)
    private double separation;

    @Shadow(remap = false)
    private double collisionX;

    @Shadow(remap = false)
    private double collisionY;

    @Shadow(remap = false)
    private double collisionZ;

    @Inject(method = "separate(Lnet/minecraft/world/phys/Vec3;DDDDZ)Z", at = @At("TAIL"), remap = false)
    private void aoa$recordZeroDistanceAxis(
            Vec3 testedAxis,
            double tl,
            double rA,
            double rB,
            double projectedMotion,
            boolean axisOfObjA,
            CallbackInfoReturnable<Boolean> callback) {
        if(!callback.getReturnValueZ() && testedAxis != null)
        {
            double distance = Math.abs(tl);
            if(distance == 0.0)
            {
                double diff = distance - (rA + rB);
                double penetration = -diff;
                if(!(penetration <= 0.0) && Double.isFinite(penetration))
                {
                    if(axisOfObjA && this.normalAxis == null && penetration <= Math.abs(this.normalSeparation))
                    {
                        this.normalAxis = testedAxis;
                        this.normalSeparation = penetration;
                    }

                    if(this.axis == null && penetration <= Math.abs(this.separation))
                    {
                        this.axis = testedAxis;
                        this.separation = penetration;
                        double collisionScale = Math.signum(tl) * (axisOfObjA ? -rA : -rB) - Math.signum(penetration) * 0.125;
                        this.collisionX = testedAxis.x * collisionScale;
                        this.collisionY = testedAxis.y * collisionScale;
                        this.collisionZ = testedAxis.z * collisionScale;
                    }
                }
            }
        }
    }
}

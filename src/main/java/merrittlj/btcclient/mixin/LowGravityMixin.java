package merrittlj.btcclient.mixin;

import merrittlj.btcclient.BTCClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class LowGravityMixin {
    @Inject(method = "getFinalGravity", at = @At("RETURN"), cancellable = true)
    private void injectTest(CallbackInfoReturnable<Double> cir) {
        if (BTCClient.lowGravityEnabled) cir.setReturnValue(0.02);
    }
}
package merrittlj.btcclient.mixin;

import merrittlj.btcclient.BTCClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class AnyElytraMixin {
    @Inject(method = "canGlideWith", at = @At("RETURN"), cancellable = true)
    private static void injectCanGlideWith(CallbackInfoReturnable<Boolean> cir) {
        if (BTCClient.anyElytraEnabled) {
            cir.setReturnValue(true);
        }
    }
}

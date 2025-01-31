package merrittlj.btcclient.mixin;

import merrittlj.btcclient.BTCClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class TestMixin {
    @Inject(method = "isGliding", at = @At("RETURN"), cancellable = true)
    private void injectTest(CallbackInfoReturnable<Boolean> cir) {
        if (BTCClient.testEnabled) cir.setReturnValue(true);
    }
}
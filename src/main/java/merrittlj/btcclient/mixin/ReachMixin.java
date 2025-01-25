package merrittlj.btcclient.mixin;

import merrittlj.btcclient.BTCClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class ReachMixin {
    @Inject(method = "getBlockInteractionRange", at = @At("RETURN"), cancellable = true)
    private void injectTest(CallbackInfoReturnable<Double> cir) {
        if ( BTCClient.reachEnabled) cir.setReturnValue(6.0);
    }
}

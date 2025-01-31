package merrittlj.btcclient.mixin;

import merrittlj.btcclient.BTCClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class AnyElytraMixin extends Entity {

    protected AnyElytraMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> levitation);

    @Inject(method = "canGlideWith", at = @At("RETURN"), cancellable = true)
    private static void injectCanGlideWith(CallbackInfoReturnable<Boolean> cir) {
        if (BTCClient.anyElytraEnabled) {
            BTCClient.enableGlide = true;
            cir.setReturnValue(true);
        }
    }
    @Inject(method = "isGliding", at = @At("RETURN"), cancellable = true)
    private void injectIsGliding(CallbackInfoReturnable<Boolean> cir) {
        if (!BTCClient.anyElytraEnabled) return;
        if (this.isOnGround() && !MinecraftClient.getInstance().player.input.playerInput.jump()) {
            BTCClient.enableGlide = false;
            return;
        }
        if (BTCClient.enableGlide && !this.hasVehicle() && !this.hasStatusEffect(StatusEffects.LEVITATION)) cir.setReturnValue(true);
    }
}

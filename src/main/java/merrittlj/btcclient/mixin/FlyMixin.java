package merrittlj.btcclient.mixin;

import com.mojang.authlib.GameProfile;
import merrittlj.btcclient.BTCClient;
import merrittlj.btcclient.SecretScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Mixin(ClientPlayerEntity.class)
public class FlyMixin extends AbstractClientPlayerEntity {
    public FlyMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @ModifyVariable(method = "tickMovement", at = @At(value = "STORE"), ordinal = 0)
    private PlayerAbilities overridePlayerAbilities(PlayerAbilities p) {
        if (BTCClient.flyEnabled) {
            p.allowFlying = true;
        }
        return p;
    }

    /*
    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;checkGliding()Z", shift = At.Shift.AFTER))
    private void onTickMovement(CallbackInfo ci) {
        if (!BTCClient.flyEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity p = (ClientPlayerEntity)(Object)this;

        if (!p.input.playerInput.jump() || p.isOnGround() || p.isGliding() || p.isTouchingWater() || p.hasStatusEffect(StatusEffects.LEVITATION)) return;

        client.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        client.player.startGliding();
    }
     */
}

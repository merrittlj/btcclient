package merrittlj.btcclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import merrittlj.btcclient.BTCClient;
import merrittlj.btcclient.BTCClientConfigModel;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Mixin(ClientPlayerEntity.class)
public class ChestplateSwapMixin extends AbstractClientPlayerEntity {
    public ChestplateSwapMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    private int chestSlot = 38;

    @Inject(method = "tickMovement", at = @At(value = "HEAD"))
    private void onTickMovementHead(CallbackInfo ci) {
        if (!BTCClient.CONFIG.moduleChestSwap()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity p = (ClientPlayerEntity)(Object)this;
        PlayerInventory inv = p.getInventory();
        TagKey<Item> chestplate = TagKey.of(Registries.ITEM.getKey(), Identifier.of("chest_armor"));

        boolean wearingElytra = inv.getStack(chestSlot).getItem() == Items.ELYTRA;
        if (!p.isOnGround() || !wearingElytra || !inv.contains(chestplate)) return;

        AtomicInteger highestSlot = new AtomicInteger();
        highestSlot.set(-1);

        IntStream.range(0, inv.size()) // Iterate through inventory indices
                .filter(slot -> {
                    ItemStack stack = inv.getStack(slot);
                    return !stack.isEmpty()
                            && stack.isIn(chestplate);
                })
                .forEach(slot -> {
                    ItemStack stack = inv.getStack(slot);
                    ItemStack highestStack = null;
                    if (highestSlot.get() != -1) highestStack = inv.getStack(highestSlot.get());

                    if (highestSlot.get() == -1 || BTCClient.getChestplateStat(stack) > BTCClient.getChestplateStat(highestStack)) {
                        highestSlot.set(slot);
                    }
                });

        BTCClient.swapChest(highestSlot.get(), client);
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;checkGliding()Z", shift = At.Shift.AFTER))
    private void onTickMovement(CallbackInfo ci) {
        if (!BTCClient.CONFIG.moduleChestSwap()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity p = (ClientPlayerEntity)(Object)this;
        PlayerInventory inv = p.getInventory();
        TagKey<Item> chestplate = TagKey.of(Registries.ITEM.getKey(), Identifier.of("chest_armor"));

        boolean hasElytra = inv.main.stream().anyMatch(itemStack -> itemStack.getItem() == Items.ELYTRA);
        boolean wearingChestplate = inv.getStack(chestSlot).isIn(chestplate);

        if (!hasElytra || !inv.contains(chestplate) || p.isGliding() || p.isTouchingWater() || p.hasStatusEffect(StatusEffects.LEVITATION)) return;
        if (!p.input.playerInput.jump() || !wearingChestplate || p.isOnGround()) return;

        AtomicInteger highestSlot = new AtomicInteger();
        highestSlot.set(-1);

        IntStream.range(0, inv.size()) // Iterate through inventory indices
                .filter(slot -> {
                    ItemStack stack = inv.getStack(slot);
                    return !stack.isEmpty()
                            && stack.getItem() == Items.ELYTRA;
                })
                .forEach(slot -> {
                    ItemStack stack = inv.getStack(slot);
                    ItemStack highestStack = null;
                    if (highestSlot.get() != -1) highestStack = inv.getStack(highestSlot.get());

                    if (highestSlot.get() == -1 || BTCClient.getElytraStat(stack) > BTCClient.getElytraStat(highestStack)) {
                        highestSlot.set(slot);
                    }
                });

        BTCClient.swapChest(highestSlot.get(), client);

        client.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
        client.player.startGliding();
    }
}

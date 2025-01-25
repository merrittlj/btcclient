package merrittlj.btcclient;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class BTCClient implements ModInitializer {
	public static final String MOD_ID = "btcclient";

	public static final merrittlj.btcclient.BTCClientConfig CONFIG = merrittlj.btcclient.BTCClientConfig.createAndLoad();
	public static boolean flyEnabled = false;
	public static boolean noFallEnabled = false;
	public static boolean anyElytraEnabled = false;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("BTCClient loading");

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitresult) -> {
			merrittlj.btcclient.BTCClientConfig.NestedModuleSwapTool_ config = CONFIG.nestedModuleSwapTool;

			if(player.isSpectator() || !world.isClient() || !config.enableModule()) return ActionResult.PASS;

			PlayerInventory inv = player.getInventory();
			TagKey<Item> weapon = TagKey.of(Registries.ITEM.getKey(), Identifier.of("c:tools/melee_weapons"));
			if (!config.swapSlotLimit()) inv.setSelectedSlot(config.swapSlot());
			if (inv.selectedSlot == config.swapSlot() && inv.contains(weapon)) {
				AtomicInteger highestSlot = new AtomicInteger();
				highestSlot.set(-1);

				IntStream.range(0, player.getInventory().main.size()) // Iterate through inventory indices
						.filter(slot -> {
							ItemStack stack = inv.getStack(slot);
							return !stack.isEmpty()
									&& stack.isIn(weapon);
						})
						.forEach(slot -> {
							ItemStack stack = inv.getStack(slot);
							ItemStack highestStack = null;
							if (highestSlot.get() != -1) highestStack = inv.getStack(highestSlot.get());

							if (config.weaponMethod() == BTCClientConfigModel.NestedModuleSwapTool.WeaponChoices.DAMAGE) {
								if (highestSlot.get() == -1 || stack.getMaxDamage() > highestStack.getMaxDamage()) {
									highestSlot.set(slot);
								}
							}
							else if (config.weaponMethod() == BTCClientConfigModel.NestedModuleSwapTool.WeaponChoices.DURABILITY) {
								// Maybe not the best solution, but item damage increases as item material value increases,
								// So netherite tool has higher damage than diamond, diamond > iron, etc.
								// Only suitable tools will be selected anyways so combat items shouldn't impact this
								if (highestSlot.get() == -1 || stack.getMaxDamage() > highestStack.getMaxDamage()) {
									highestSlot.set(slot);
								}
							}
						});

				if (highestSlot.get() != -1 && highestSlot.get() != config.swapSlot()) {
					BTCClient.swap(config.swapSlot(), highestSlot.get(), MinecraftClient.getInstance());
				}
			}

			return ActionResult.PASS;
		});

		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
			BlockState state = world.getBlockState(pos);

			merrittlj.btcclient.BTCClientConfig.NestedModuleSwapTool_ config = CONFIG.nestedModuleSwapTool;

			if (player.isSpectator() || !world.isClient() || !config.enableModule()) return ActionResult.PASS;

			PlayerInventory inv = player.getInventory();
			if (!config.swapSlotLimit()) inv.setSelectedSlot(config.swapSlot());
			// Only search tools if:
			// --REMOVED AS THIS IGNORES BETTER TOOLS--Current tool is unsuitable
			// Current tool is in tool slot
			// Inventory has a tool
			TagKey<Item> toolTag = TagKey.of(Registries.ITEM.getKey(), Identifier.of("c:tools"));
			if (inv.selectedSlot == config.swapSlot() && inv.contains(toolTag)) {

				AtomicInteger highestSlot = new AtomicInteger();
				highestSlot.set(-1);

				IntStream.range(0, player.getInventory().main.size()) // Iterate through inventory indices
						.filter(slot -> {
							ItemStack stack = inv.getStack(slot);
							return !stack.isEmpty()
									&& stack.isIn(toolTag)
									&& stack.isSuitableFor(state);
						})
						.forEach(slot -> {
							ItemStack stack = inv.getStack(slot);
							ItemStack highestStack = null;
							if (highestSlot.get() != -1) highestStack = inv.getStack(highestSlot.get());

							if (config.toolMethod() == BTCClientConfigModel.NestedModuleSwapTool.ToolChoices.MINING_SPEED) {
								if (highestSlot.get() == -1 || stack.getMiningSpeedMultiplier(state)
										> highestStack.getMiningSpeedMultiplier(state)) {
									highestSlot.set(slot);
								}
							}
							else if (config.toolMethod() == BTCClientConfigModel.NestedModuleSwapTool.ToolChoices.DURABILITY) {
								// Maybe not the best solution, but item damage increases as item material value increases,
								// So netherite tool has higher damage than diamond, diamond > iron, etc.
								// Only suitable tools will be selected anyways so combat items shouldn't impact this
								if (highestSlot.get() == -1 || stack.getMaxDamage() > highestStack.getMaxDamage()) {
									highestSlot.set(slot);
								}
							}
						});

				if (highestSlot.get() != -1 && highestSlot.get() != config.swapSlot()) {
					BTCClient.swap(config.swapSlot(), highestSlot.get(), MinecraftClient.getInstance());
				}
			}

			return ActionResult.PASS;
		});

		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (player.isGliding()) {
				Vec3d vec3d = player.getRotationVector();
				double d = 5;
				double e = 0.1;
				Vec3d vec3d2 = player.getVelocity();
				player
						.setVelocity(
								vec3d2.add(
										vec3d.x * e + (vec3d.x * d - vec3d2.x) * 0.5, vec3d.y * e + (vec3d.y * d - vec3d2.y) * 0.5, vec3d.z * e + (vec3d.z * d - vec3d2.z) * 0.5
								)
						);
			}

			return ActionResult.PASS;
		});
	}

	public static void swapChest(int slot, MinecraftClient client) {
		ClientPlayerInteractionManager in = client.interactionManager;
		ClientPlayerEntity p = client.player;

		if (slot < 9) slot += 36;

		in.clickSlot(0, slot, 0, SlotActionType.PICKUP, p);
		in.clickSlot(0, 6, 0, SlotActionType.PICKUP, p);
		in.clickSlot(0, slot, 0, SlotActionType.PICKUP, p);
	}

	public static void swap(int src, int dst, MinecraftClient client) {
		ClientPlayerInteractionManager in = client.interactionManager;
		ClientPlayerEntity p = client.player;

		if (dst < 9) dst += 36;

		try {
			in.clickSlot(
					p.playerScreenHandler.syncId,
					dst,
					src,
					SlotActionType.SWAP,
					p
			);
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
	}

	public static int getChestplateStat(ItemStack chestplateItem) {
		float score = 1;

		if(chestplateItem.getItem() instanceof ArmorItem armorItem){
			var component = armorItem.getComponents().get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
			for (AttributeModifiersComponent.Entry entry : component.modifiers()) {
				RegistryEntry<EntityAttribute> attribute = entry.attribute();
				if(attribute == EntityAttributes.ARMOR) {
					score += entry.modifier().value();
				}
				if(attribute == EntityAttributes.ARMOR_TOUGHNESS) {
					score += entry.modifier().value();
				}
			}
			score += getLevel(Enchantments.PROTECTION,chestplateItem)*2;
			score += getLevel(Enchantments.MENDING,chestplateItem)*0.5;
			score += chestplateItem.contains(DataComponentTypes.CUSTOM_NAME)?0.25:0;
			score += getLevel(Enchantments.UNBREAKING,chestplateItem)*0.24/3;
		}

		return (int) (score*1000);
	}

	private static int getLevel(RegistryKey<Enchantment> key, ItemStack stack) {
		var enchant = getEnchantmentRegistry().get(key);
		RegistryEntry<Enchantment> enchantEntry = getEnchantmentRegistry().getEntry(enchant);
		return EnchantmentHelper.getLevel(enchantEntry,stack);
	}
	public static int getElytraStat(ItemStack elytraItem) {
		var stat = (getLevel(Enchantments.MENDING,elytraItem)*3+1)+getLevel(Enchantments.UNBREAKING,elytraItem);

		return stat;
	}
	private static Registry<Enchantment> getEnchantmentRegistry() {
		return MinecraftClient.getInstance().world.getRegistryManager()
				.getOrThrow(RegistryKeys.ENCHANTMENT);
	}
}
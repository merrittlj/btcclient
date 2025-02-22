package merrittlj.btcclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class BTCClientActualClient implements ClientModInitializer
{
    private static KeyBinding superSecretBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.examplemod.spook", // The translation key of the keybinding's name
        InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
        GLFW.GLFW_KEY_R, // The keycode of the key
        "category.examplemod.test" // The translation key of the keybinding's category.
    ));

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (superSecretBind.wasPressed()) {
                if (BTCClient.CONFIG.nestedSuperSecret.password() != 1337) return;
                client.setScreen(new SecretScreen());
            }

            if (BTCClient.noFallEnabled) {
                client.player.networkHandler
                        .sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, client.player.horizontalCollision));
            }
        });
    }
}

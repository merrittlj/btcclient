package merrittlj.btcclient;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.HorizontalAlignment;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.core.VerticalAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class SecretScreen extends BaseOwoScreen<FlowLayout> {

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent
                .surface(Surface.VANILLA_TRANSLUCENT)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER);

        rootComponent.child(
                Components.button(
                        Text.literal("Fly"),
                        button -> {
                            BTCClient.flyEnabled = !BTCClient.flyEnabled;
                             }
                )
        ).child(
                Components.button(
                        Text.literal("No Fall"),
                        button -> {
                            BTCClient.noFallEnabled = !BTCClient.noFallEnabled;
                        }
                )
        ).child(
                Components.button(
                        Text.literal("Any Elytra"),
                        button -> {
                            BTCClient.anyElytraEnabled = !BTCClient.anyElytraEnabled;
                            if (!BTCClient.anyElytraEnabled) BTCClient.enableGlide = false;
                        }
                )
        ).child(
                Components.button(
                        Text.literal("Infinite Jump"),
                        button -> {
                            BTCClient.infiniteJumpEnabled = !BTCClient.infiniteJumpEnabled;
                        }
                )
        ).child(
                Components.button(
                        Text.literal("Reach"),
                        button -> {
                            BTCClient.reachEnabled = !BTCClient.reachEnabled;
                        }
                )
        ).child(
                Components.button(
                        Text.literal("Low Gravity"),
                        button -> {
                            BTCClient.lowGravityEnabled = !BTCClient.lowGravityEnabled;
                        }
                )
        ).child(
                Components.button(
                        Text.literal("Test"),
                        button -> {
                            BTCClient.testEnabled = !BTCClient.testEnabled;
                            MinecraftClient.getInstance().player.sendMessage(Text.of("YOU ARE ENABLING TEST"), false);
                        }
                )
        );
    }
}
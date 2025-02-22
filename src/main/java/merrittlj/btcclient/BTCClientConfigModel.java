package merrittlj.btcclient;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;

@Modmenu(modId = "btcclient")
@Config(name = "btcclient-config", wrapperName = "BTCClientConfig")
public class BTCClientConfigModel {
    public boolean enable = true;
    public boolean moduleNoRender = false;
    public boolean moduleChestSwap = false;
    @Nest
    public NestedModuleSwapTool nestedModuleSwapTool = new NestedModuleSwapTool();
    @Nest
    public NestedSuperSecret nestedSuperSecret = new NestedSuperSecret();

    public static class NestedModuleSwapTool {
        public boolean enableModule = false;
        public boolean swapSlotLimit = true;
        public int swapSlot = 0;
        public ToolChoices toolMethod = ToolChoices.MINING_SPEED;
        public WeaponChoices weaponMethod = WeaponChoices.DURABILITY;

        public enum ToolChoices {
            MINING_SPEED,
            DURABILITY
        }

        public enum WeaponChoices {
            DAMAGE,
            DURABILITY
        }
    }

    public static class NestedSuperSecret {
        public int password = 0;
        public double anyElytraSpeed = 1.5;
    }
}

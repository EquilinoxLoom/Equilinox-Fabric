package loom.mixins;

import gameMenu.GameMenuGui;
import guis.GuiComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = {GameMenuGui.class}, remap = false)
public abstract class MixinGameMenuGui extends GuiComponent {
	@ModifyConstant(method = "addVersion", constant = @Constant(stringValue = "Version 1.7.0b"))
	private String modifyVersion(String version) {
		return version + " (modded)";
	}
}

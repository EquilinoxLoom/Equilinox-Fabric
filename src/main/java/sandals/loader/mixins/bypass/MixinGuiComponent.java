package sandals.loader.mixins.bypass;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import guis.GuiComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GuiComponent.class, remap = false)
public class MixinGuiComponent {
    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lguis/GuiComponent;addNewChildren()V"))
    private void bypassTextVAO(GuiComponent instance, Operation<Void> original) {
        try {
            original.call(instance);
        } catch (ArithmeticException ignored) {}
    }
}

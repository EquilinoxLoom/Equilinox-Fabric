package sandals.loader.mixins;

import errors.ErrorPopUp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.swing.*;

@Mixin(value = ErrorPopUp.class, remap = false)
public class MixinErrorPopUp {
    @Redirect(method = "createFrame", at = @At(value = "INVOKE", target = "Ljavax/swing/JFrame;setSize(II)V"))
    private static void increaseFrame(JFrame instance, int width, int height) {
        instance.setSize(750, 600);
    }
}

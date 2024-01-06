package loom.mixins.clearlog;

import fontRendering.TextLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.PrintStream;

@Mixin(value = {TextLoader.class}, remap = false)
public class MixinTextLoader {
    @Redirect(method = "createStructure", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", remap = false))
    private void check(PrintStream printStream, String x) {}
}

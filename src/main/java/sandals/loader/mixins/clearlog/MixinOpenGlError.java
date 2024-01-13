package sandals.loader.mixins.clearlog;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import toolbox.OpenGlError;

import java.io.PrintStream;

@Mixin(value = {OpenGlError.class}, remap = false)
public class MixinOpenGlError {
    @Redirect(method = "check(Ljava/lang/String;)Z", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", remap = false))
    private static void clearLog(PrintStream printStream, String x) {}
}

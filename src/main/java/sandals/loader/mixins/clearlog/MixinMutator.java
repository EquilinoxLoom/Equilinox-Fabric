package sandals.loader.mixins.clearlog;

import components.Mutator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.PrintStream;

@Mixin(value = {Mutator.class}, remap = false)
public class MixinMutator {
    @Redirect(method = "load", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", remap = false))
    private static void clearLog(PrintStream instance, String x) {}
}

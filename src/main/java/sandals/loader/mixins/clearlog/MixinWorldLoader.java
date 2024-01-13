package sandals.loader.mixins.clearlog;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import world.WorldLoader;

import java.io.PrintStream;

@Mixin(value = WorldLoader.class, remap = false)
public class MixinWorldLoader {
  @Redirect(method = "generateHeights", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", remap = false))
  private static void clearLog(PrintStream printStream, String x) {}
}

package sandals.loader.mixins.clearlog;

import audio.SoundLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.PrintStream;

@Mixin(value = SoundLoader.class, remap = false)
public class MixinSoundLoader {
  @Redirect(method = "doInitialSoundLoad", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", remap = false))
  private static void clearLog_0(PrintStream printStream, String x) {}

  @Redirect(method = "cleanUp", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", remap = false))
  private static void clearLog_1(PrintStream printStream, String x) {}
  
  @Redirect(method = "loadSoundDataIntoBuffer", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", remap = false))
  private static void clearLog_2(PrintStream printStream, String x) {}
}

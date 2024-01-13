package sandals.loader.mixins.clearlog;

import audio.Streamer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.PrintStream;

@Mixin(value = Streamer.class, remap = false)
public class MixinStreamer {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", remap = false))
    private void clearLog(PrintStream printStream, String x) {}
}

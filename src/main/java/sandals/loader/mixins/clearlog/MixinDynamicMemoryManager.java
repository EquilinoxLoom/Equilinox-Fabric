package sandals.loader.mixins.clearlog;

import batches.DynamicMemoryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.PrintStream;

@Mixin(value = DynamicMemoryManager.class, remap = false)
public class MixinDynamicMemoryManager {
    @Redirect(method = "allocateMemory", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", remap = false))
    private void clearLog(PrintStream printStream, String x) {}
}

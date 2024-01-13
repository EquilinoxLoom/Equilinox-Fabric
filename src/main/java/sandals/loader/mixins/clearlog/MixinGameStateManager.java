package sandals.loader.mixins.clearlog;

import gameManaging.GameStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.PrintStream;

@Mixin(value = GameStateManager.class, remap = false)
public class MixinGameStateManager {
    @Redirect(method = "changeState", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V"))
    public void clearLog(PrintStream instance, String x) {}
}

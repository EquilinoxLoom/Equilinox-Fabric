package sandals.loader.mixins.clearlog;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.PrintStream;

@Pseudo @Mixin(targets = {
        "gameManaging.GameState$1", "gameManaging.GameState$2", "gameManaging.GameState$3",
        "gameManaging.GameState$4", "gameManaging.GameState$5", "gameManaging.GameState$6",
        "gameManaging.GameState$7", "gameManaging.GameState$8", "gameManaging.GameState$9"
}, remap = false)
public class MixinGameState {
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V"))
    public void log0(PrintStream instance, String x) {}
}

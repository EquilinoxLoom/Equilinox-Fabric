package loom.mixins.clearlog;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import saves.SaveSlot;

import java.io.PrintStream;

@Mixin(value = {SaveSlot.class}, remap = false)
public class MixinSaveSlot {
    @Redirect(method = {"getReader"}, at = @At(value = "INVOKE", target = "Ljava/io/PrintStream;println(Ljava/lang/String;)V", remap = false))
    private void getReader(PrintStream printStream, String x) {}
}
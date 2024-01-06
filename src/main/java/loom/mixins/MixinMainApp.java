package loom.mixins;

import main.MainApp;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MainApp.class, remap = false)
public class MixinMainApp {
    @Inject(method = "main", at = @At(value = "HEAD"))
    private static void init(String[] args, CallbackInfo ci) {
        FabricLoaderImpl.INSTANCE.prepareModInit(FabricLoader.getInstance().getGameDir(), FabricLoaderImpl.INSTANCE.getGameInstance());
    }
}

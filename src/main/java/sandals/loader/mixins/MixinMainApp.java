package sandals.loader.mixins;

import main.MainApp;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Paths;

@Mixin(value = MainApp.class, remap = false)
public class MixinMainApp {
    @Inject(method = "main", at = @At(value = "HEAD"))
    private static void initMods(String[] args, CallbackInfo ci) {
        FabricLoaderImpl floader = FabricLoaderImpl.INSTANCE;

        floader.prepareModInit(Paths.get("."), floader.getGameInstance());

        floader.invokeEntrypoints("main", ModInitializer.class, ModInitializer::onInitialize);
        floader.invokeEntrypoints("main", ModInitializer.class, mod -> System.out.println("Loading " + mod.getClass().getName()));
        floader.invokeEntrypoints("client", ClientModInitializer.class, ClientModInitializer::onInitializeClient);
    }
}

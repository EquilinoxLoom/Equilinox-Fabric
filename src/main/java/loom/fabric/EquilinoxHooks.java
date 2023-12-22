package loom.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class EquilinoxHooks {
    public static void initMods() {
        Path runDir = Paths.get(".");
        FabricLoaderImpl.INSTANCE.prepareModInit(runDir, FabricLoaderImpl.INSTANCE.getGameInstance());
        EntrypointUtils.invoke("main", ModInitializer.class, ModInitializer::onInitialize);
        EntrypointUtils.invoke("client", ClientModInitializer.class, ClientModInitializer::onInitializeClient);
    }
}

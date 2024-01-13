package sandals.loader.fabric;

import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.game.GameProviderHelper;
import net.fabricmc.loader.impl.game.patch.GameTransformer;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata;
import net.fabricmc.loader.impl.util.Arguments;
import net.fabricmc.loader.impl.util.SystemProperties;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;

public class EquilinoxGameProvider implements GameProvider {
    private final GameTransformer transformer = new GameTransformer();

    private List<Path> classpath;

    private Arguments arguments;

    public String entry;

    private String version;

    @Override
    public String getGameId() {
        return "equilinox";
    }

    @Override
    public String getGameName() {
        return "Equilinox";
    }

    @Override
    public String getRawGameVersion() {
        return version;
    }

    @Override
    public String getNormalizedGameVersion() {
        return version;
    }

    @Override
    public Collection<BuiltinMod> getBuiltinMods() {
        return List.of(new BuiltinMod(
                classpath, new BuiltinModMetadata.Builder(getGameId(), getNormalizedGameVersion())
                        .setName(getGameName()).setDescription("A sandbox evolution game.").build()));
    }

    @Override
    public String getEntrypoint() {
        return entry;
    }

    @Override
    public Path getLaunchDirectory() {
        try {
            return Paths.get(".").toRealPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isObfuscated() {
        return false;
    }

    @Override
    public boolean requiresUrlClassLoader() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean locateGame(FabricLauncher launcher, String[] args) {
        arguments = new Arguments();
        arguments.parse(args);

        entry = "main.MainApp";
        version = "0.0.0";

        Path launchDir = getLaunchDirectory();

        try {
            GameProviderHelper.FindResult result;

            List<String> gameLocations = new ArrayList<>();
            if (System.getProperty(SystemProperties.GAME_JAR_PATH) != null) {
                gameLocations.add(System.getProperty(SystemProperties.GAME_JAR_PATH));
            }
            gameLocations.add(launchDir.resolve("input.jar").toString());
            gameLocations.add(launchDir.resolve("EquilinoxWindows.jar").toString());
            gameLocations.add(launchDir.resolve("EquilinoxLinux.jar").toString());

            List<Path> jarPaths = gameLocations.stream()
                    .map(path -> Paths.get(path).toAbsolutePath().normalize())
                    .filter(Files::exists).toList();

            result = GameProviderHelper.findFirst(jarPaths, new HashMap<>(), true, entry);
            if (result == null) return false;

            classpath = List.of(result.path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public void initialize(FabricLauncher launcher) {
        try {
            launcher.setValidParentClassPath(Collections.singletonList(Path.of(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI())));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        transformer.locateEntrypoints(launcher, classpath);
    }

    @Override
    public GameTransformer getEntrypointTransformer() {
        return transformer;
    }

    @Override
    public void unlockClassPath(FabricLauncher launcher) {
        classpath.forEach(launcher::addToClassPath);
    }

    @Override
    public void launch(ClassLoader loader) {
        MethodHandle invoker;
        try {
            Class<?> target = loader.loadClass(getEntrypoint());
            invoker = MethodHandles.lookup().findStatic(target, "main", MethodType.methodType(void.class, String[].class));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Failed to find entry point", e);
        }

        try {
            invoker.invokeExact(arguments.toArray());
        } catch(Throwable e) {
            throw new RuntimeException("Failed to launch", e);
        }
    }

    @Override
    public Arguments getArguments() {
        return arguments;
    }

    @Override
    public String[] getLaunchArguments(boolean sanitize) {
        return arguments.toArray();
    }
}

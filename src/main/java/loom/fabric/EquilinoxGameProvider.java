package loom.fabric;

import net.fabricmc.loader.impl.FormattedException;
import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.game.GameProviderHelper;
import net.fabricmc.loader.impl.game.patch.GameTransformer;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata;
import net.fabricmc.loader.impl.metadata.ContactInformationImpl;
import net.fabricmc.loader.impl.util.Arguments;
import net.fabricmc.loader.impl.util.SystemProperties;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.fabricmc.loader.impl.util.log.LogLevel;
import net.fabricmc.loader.impl.util.version.StringVersion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipFile;

public class EquilinoxGameProvider implements GameProvider {
    public static final String ENTRY_POINT = "main.MainApp", API = "loom-api-0.1.0.jar";

    private Arguments arguments;

    private Path gameJar;

    String entrypoint;

    private static final StringVersion gameVersion = new StringVersion("1.0.0");
    private static final GameTransformer TRANSFORMER = new GameTransformer(new EquilinoxEntryPointPatch());

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
        return gameVersion.getFriendlyString();
    }

    @Override
    public String getNormalizedGameVersion() {
        return gameVersion.getFriendlyString();
    }

    @Override
    public Collection<BuiltinMod> getBuiltinMods() {
        Map<String, String> contactInfo = new HashMap<>();
        contactInfo.put("github", "https://github.com/EquilinoxLoom/Loom");

        BuiltinModMetadata.Builder equilinoxMetadata =
                new BuiltinModMetadata.Builder(getGameId(), getNormalizedGameVersion())
                        .setName(getGameName())
                        .addAuthor("Sandáliaball", contactInfo)
                        .setContact(new ContactInformationImpl(contactInfo))
                        .setDescription("A sandbox evolution game.");

        List<Path> mods = new ArrayList<>();
        mods.add(gameJar); mods.add(Paths.get(".", API));

        return Collections.singletonList(new BuiltinMod(mods, equilinoxMetadata.build()));
    }

    @Override
    public String getEntrypoint() {
        return entrypoint;
    }

    @Override
    public Path getLaunchDirectory() {
        if (arguments == null) {
            return Paths.get(".");
        }
        return getLaunchDirectory(arguments);
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
        this.arguments = new Arguments();
        this.arguments.parse(args);

        Map<Path, ZipFile> zipFiles = new HashMap<>();

        try {
            GameProviderHelper.FindResult result = null;

            List<String> gameLocations = new ArrayList<>();
            if (System.getProperty(SystemProperties.GAME_JAR_PATH) != null) {
                gameLocations.add(System.getProperty(SystemProperties.GAME_JAR_PATH));
            }
            gameLocations.add("./input.jar");
            gameLocations.add("./EquilinoxWindows.jar");
            gameLocations.add("./EquilinoxLinux.jar");

            List<Path> jarPaths = gameLocations.stream()
                    .map(path -> Paths.get(path).toAbsolutePath().normalize())
                    .filter(Files::exists).toList();

            result = GameProviderHelper.findFirst(jarPaths, zipFiles, true, ENTRY_POINT);

            if (result == null) return false;

            entrypoint = result.name;
            gameJar = result.path;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String os = System.getProperty("os.name").toLowerCase();
        File natives = new File("./natives");
        if (os.contains("win")) {
            if (natives.mkdir()) Log.log(LogLevel.INFO, LogCategory.GAME_PROVIDER, "Created natives folder");
            extract(natives, "jinput-dx8.dll", "jinput-dx8_64.dll", "jinput-raw.dll", "jinput-raw_64.dll",
                    "lwjgl.dll", "lwjgl64.dll", "OpenAL32.dll", "OpenAL64.dll");
        } else if (os.contains("mac")) {
            if (natives.mkdir()) Log.log(LogLevel.INFO, LogCategory.GAME_PROVIDER, "Created natives folder");
            extract(natives, "libjinput-osx.dylib", "liblwjgl.dylib", "openal.dylib");
        }

        System.setProperty("org.lwjgl.librarypath", natives.getAbsolutePath());

        extract(natives, API);

        processArgumentMap(arguments);

        return true;
    }

    private void extract(File folder, String... names) {
        for (String name : names) {
            try {
                File file = new File(folder, name);
                if (!file.exists()) {
                    InputStream resource = getClass().getResourceAsStream(name);
                    if (resource == null) break;
                    Files.copy(resource, file.getAbsoluteFile().toPath());
                }
            } catch (IOException e) {
                Log.warn(LogCategory.GAME_PROVIDER, "Failed to extract native " + name);
            }
        }
    }

    @Override
    public void initialize(FabricLauncher launcher) {
        TRANSFORMER.locateEntrypoints(launcher, Collections.singletonList(gameJar));
    }

    @Override
    public GameTransformer getEntrypointTransformer() {
        return TRANSFORMER;
    }

    @Override
    public void unlockClassPath(FabricLauncher launcher) {
        launcher.addToClassPath(gameJar);
    }

    @Override
    public void launch(ClassLoader loader) {
        try {
            Class<?> c = loader.loadClass(entrypoint);
            Method m = c.getMethod("main", String[].class);
            m.invoke(null, (Object) arguments.toArray());
        } catch (InvocationTargetException e) {
            throw new FormattedException("Equilinox has crashed!", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new FormattedException("Failed to start Equilinox", e);
        }
    }

    @Override
    public Arguments getArguments() {
        return arguments;
    }

    @Override
    public String[] getLaunchArguments(boolean sanitize) {
        if (arguments == null) return new String[0];

        String[] ret = arguments.toArray();
        if (!sanitize) return ret;

        int writeIdx = 0;

        for (int i = 0; i < ret.length; i++) {
            ret[writeIdx++] = ret[i];
        }

        if (writeIdx < ret.length) ret = Arrays.copyOf(ret, writeIdx);

        return ret;
    }

    private void processArgumentMap(Arguments arguments) {
        if (!arguments.containsKey("gameDir")) {
            arguments.put("gameDir", getLaunchDirectory(arguments).toAbsolutePath().normalize().toString());
        }

        Log.debug(LogCategory.GAME_PROVIDER, "Launch directory is " + Paths.get(arguments.get("gameDir")));
    }

    private static Path getLaunchDirectory(Arguments arguments) {
        return Paths.get(arguments.getOrDefault("gameDir", "."));
    }
}

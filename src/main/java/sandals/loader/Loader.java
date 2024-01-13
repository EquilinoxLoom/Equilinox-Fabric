package sandals.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Loader {
    private static final Path THIS;

    static {
        try {
            THIS = Paths.get(Loader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Path GAME_DIR = Paths.get(".");

    static {
        List<String> locations = new ArrayList<>();
        locations.add("./EquilinoxWindows.jar");
        locations.add("./input.jar");
        locations.add("./EquilinoxLinux.jar");

        locations.stream().map(GAME_DIR::resolve).filter(Files::isRegularFile).findFirst().orElseThrow(() -> new RuntimeException("Could not find game in specified directory"));
    }

    private static final String ASM_VERSION = "9.6";
    private static final String MIXIN_VERSION = "0.12.5+mixin.0.8.5";
    private static final String FABRIC_VERSION = "0.15.3";

    private static final boolean WINDOWS;
    private static final boolean MAC;

    static {
        String sys = System.getProperty("os.name").toLowerCase();
        WINDOWS = sys.contains("win");
        MAC = sys.contains("mac");
    }

    private static final Path LIB = GAME_DIR.resolve("libraries");
    public static final Path NATIVES = LIB.resolve("sandals/loader");

    private static final Path[] DEPENDENCIES;

    static {
        Path fabric = Paths.get("net").resolve("fabricmc");
        Path asm = Paths.get("org").resolve("ow2").resolve("asm");
        DEPENDENCIES = new Path[] {
                fabric.resolve(jar("fabric-loader", FABRIC_VERSION)),
                fabric.resolve(jar("sponge-mixin",MIXIN_VERSION)),
                asm.resolve(jar("asm", ASM_VERSION)),
                asm.resolve(jar("asm-commons", ASM_VERSION)),
                asm.resolve(jar("asm-analysis", ASM_VERSION)),
                asm.resolve(jar("asm-tree", ASM_VERSION)),
                asm.resolve(jar("asm-util", ASM_VERSION))
        };
    }

    private static String jar(String name, String version) {
        return name + "-" + version + ".jar";
    }

    private static int iterations = 0;
    public static void main(String[] args) {
        if (intact()) {
            log("Running fabric equilinox");

            try {
                List<String> libraries = Arrays.stream(DEPENDENCIES).map(LIB::resolve).map(Path::normalize).map(Path::toString).toList();

                ProcessBuilder processBuilder = new ProcessBuilder(
                        "java",
                        "-Dhttps.protocols=TLSv1.2,TLSv1.1,TLSv1",
                        "-XX:+ShowCodeDetailsInExceptionMessages",
                        "-Dfabric.skipMcProvider=true",
                        "-Dfabric.side=client",
                        "-Djava.library.path=" + NATIVES.toAbsolutePath().normalize(),
                        "-Dorg.lwjgl.librarypath=" + NATIVES.toAbsolutePath().normalize(),
                        "-Dfabric.debug.disableClassPathIsolation",
                        "-Dfabric.addMods=" + THIS.toAbsolutePath().normalize(),
                        "-cp",
                        "\"" + String.join(File.pathSeparator, libraries) + File.pathSeparator + THIS.toAbsolutePath().normalize() + "\"",
                        "net.fabricmc.loader.impl.launch.knot.KnotClient"
                );

                processBuilder.redirectErrorStream(true);
                Process start = processBuilder.start();

                redirectStreams(start.getInputStream(), System.out);
                redirectStreams(start.getErrorStream(), System.err);
                redirectStreams(System.in, start.getOutputStream());

                int i = start.waitFor();
                System.exit(i);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            log("Running equilinox fabric installer");
            try {
                install();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (iterations++ > 2) throw new RuntimeException("Error on install");
            main(args);
        }
    }

    private static void redirectStreams(final InputStream input, final OutputStream output) {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private static boolean intact() {
        for (Path dep : DEPENDENCIES) {
            if (!Files.exists(LIB.resolve(dep))) {
                return false;
            }
        }
        return Files.exists(NATIVES);
    }

    private static void install() throws IOException {
        if (WINDOWS) {
            extractNatives("jinput-dx8.dll", "jinput-dx8_64.dll", "jinput-raw.dll", "jinput-raw_64.dll",
                    "lwjgl.dll", "lwjgl64.dll", "OpenAL32.dll", "OpenAL64.dll");
        } else if (MAC) {
            extractNatives("libjinput-osx.dylib", "liblwjgl.dylib", "openal.dylib");
        }

        for (Path destination : DEPENDENCIES) {
            String file = destination.getFileName().toString();
            int last = file.lastIndexOf('-');
            String url = "https://maven.fabricmc.net/" + destination.getParent()
                    .resolve(file.substring(0, last))
                    .resolve(file.substring(last + 1).replace(".jar", ""))
                    .resolve(file).toString().replace("\\", "/");

            log("Downloading dependency " + file + " from " + url);
            destination = LIB.resolve(destination);
            Files.createDirectories(destination);

            try (InputStream inputStream = new URI(url).toURL().openStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (URISyntaxException ignored) {}
        }

        log("Done!");
    }

    private static void extractNatives(String... resources) {
        for (String resource : resources) {
            if (resource == null || resource.isEmpty()) {
                log("Null resource can't be extracted");
                continue;
            }
            InputStream stream = Loader.class.getResourceAsStream("/natives/" + resource);
            try {
                Files.createDirectories(NATIVES);
            } catch (IOException e) {
                continue;
            }
            Path path = NATIVES.resolve(resource);

            if (stream == null) {
                log("File " + resource + "not found");
                continue;
            }

            try {
                Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
                stream.close();
            } catch (IOException ignored) {}
        }
    }

    private static void log(String msg) {
        System.out.printf("[%tT] [%s/%s]: %s%n", System.currentTimeMillis(), "FabricLoader", "GameProvider", msg);
    }
}

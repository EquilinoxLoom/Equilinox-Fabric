package loom.fabric;

import net.fabricmc.loader.impl.game.patch.GamePatch;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class EquilinoxEntryPointPatch extends GamePatch {
    @Override
    public void process(FabricLauncher launcher, Function<String, ClassReader> classSource, Consumer<ClassNode> classEmitter) {
        String entrypoint = launcher.getEntrypoint();

        ClassNode main = readClass(classSource.apply(entrypoint));

        MethodNode method = findMethod(main, init -> init.name.equals("main"));
        if (method == null) {
            throw new NoSuchMethodError("Could not find init method in " + entrypoint);
        }
        Log.debug(LogCategory.GAME_PATCH, "Found init method: %s -> %s", entrypoint, main.name);
        Log.debug(LogCategory.GAME_PATCH, "Patching init method %s%s", method.name, method.desc);

        ListIterator<AbstractInsnNode> it = method.instructions.iterator();
        it.add(new MethodInsnNode(Opcodes.INVOKESTATIC, EquilinoxHooks.INTERNAL_NAME, "init", "()V", false));
        classEmitter.accept(main);
    }
}

package sandals.loader.mixins.bypass;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import utils.BinaryReader;

import java.io.DataInputStream;

@Mixin(value = BinaryReader.class, remap = false)
public class MixinBinaryReader {
    @WrapOperation(method = "readBoolean", at = @At(value = "INVOKE", target = "Ljava/io/DataInputStream;readBoolean()Z"))
    private boolean clear0(DataInputStream instance, Operation<Boolean> original) {
        try {
            return original.call(instance);
        } catch (Exception e) {
            return false;
        }
    }

    @WrapOperation(method = "readFloat", at = @At(value = "INVOKE", target = "Ljava/io/DataInputStream;readFloat()F"))
    private float clear1(DataInputStream instance, Operation<Float> original) {
        try {
            return original.call(instance);
        } catch (Exception e) {
            return 0;
        }
    }

    @WrapOperation(method = "readInt", at = @At(value = "INVOKE", target = "Ljava/io/DataInputStream;readInt()I"))
    private int clear2(DataInputStream instance, Operation<Integer> original) {
        try {
            return original.call(instance);
        } catch (Exception e) {
            return 0;
        }
    }

    @WrapOperation(method = "readLong", at = @At(value = "INVOKE", target = "Ljava/io/DataInputStream;readLong()J"))
    private long clear3(DataInputStream instance, Operation<Long> original) {
        try {
            return original.call(instance);
        } catch (Exception e) {
            return 0;
        }
    }

    @WrapOperation(method = "readShort", at = @At(value = "INVOKE", target = "Ljava/io/DataInputStream;readShort()S"))
    private short clear4(DataInputStream instance, Operation<Short> original) {
        try {
            return original.call(instance);
        } catch (Exception e) {
            return 0;
        }
    }

    @WrapOperation(method = "readString", at = @At(value = "INVOKE", target = "Ljava/io/DataInputStream;readUTF()Ljava/lang/String;"))
    private String clear5(DataInputStream instance, Operation<String> original) {
        try {
            return original.call(instance);
        } catch (Exception e) {
            return null;
        }
    }
}

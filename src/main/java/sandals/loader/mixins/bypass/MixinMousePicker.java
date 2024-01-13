package sandals.loader.mixins.bypass;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.lwjgl.util.vector.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import toolbox.MousePicker;

@Mixin(value = MousePicker.class, remap = false)
public class MixinMousePicker {
    @WrapOperation(method = "toEyeCoords", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/vector/Matrix4f;invert(Lorg/lwjgl/util/vector/Matrix4f;Lorg/lwjgl/util/vector/Matrix4f;)Lorg/lwjgl/util/vector/Matrix4f;"))
    private Matrix4f bypass(Matrix4f src, Matrix4f dest, Operation<Matrix4f> original) {
        Matrix4f matrix = original.call(src, dest);
        if (matrix == null) {
            return src;
        }
        return matrix;
    }
}

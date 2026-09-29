package ru.roughcipher.spike.mixin.client.fix.window;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.render.OpenGLHelper;
import org.lwjgl.opengl.GLCapabilities;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OpenGLHelper.class)
public class LowerOpenGLVersionRequirement {

	@Shadow
	public static int[] versionPairs;

	@Inject(method = "<clinit>", at = @At("RETURN"))
	private static void spike$extendVersionPairs(CallbackInfo ci) {
		versionPairs = new int[]{
			4, 6,
			4, 3,
			4, 1,
			3, 3
		};
	}

	@ModifyExpressionValue(
		method = "testCapabilities",
		at = @At(
			value = "FIELD",
			target = "Lorg/lwjgl/opengl/GLCapabilities;OpenGL41:Z",
			opcode = Opcodes.GETFIELD
		)
	)
	private static boolean spike$require33(boolean original, GLCapabilities capabilities) {
		return capabilities.OpenGL33;
	}

	@WrapOperation(
		method = "testCapabilities",
		at = @At(
			value = "INVOKE",
			target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"
		)
	)
	private static void spike$msgMac(Logger logger, String msg, Object arg, Operation<Void> original) {
		if (msg != null && msg.contains("OpenGL4.1")) {
			msg = msg.replace("OpenGL4.1", "OpenGL3.3");
		}
		original.call(logger, msg, arg);
	}

	@WrapOperation(
		method = "testCapabilities",
		at = @At(
			value = "INVOKE",
			target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V"
		)
	)
	private static void spike$msgOther(Logger logger, String msg, Object arg, Operation<Void> original) {
		if (msg != null && msg.contains("OpenGL4.1")) {
			msg = msg.replace("OpenGL4.1", "OpenGL3.3");
		}
		original.call(logger, msg, arg);
	}
}

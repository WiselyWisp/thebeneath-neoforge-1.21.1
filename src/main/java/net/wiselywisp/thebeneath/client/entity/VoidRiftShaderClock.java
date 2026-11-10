package net.wiselywisp.thebeneath.client.entity;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.wiselywisp.thebeneath.TheBeneath;

final class VoidRiftShaderClock {
    private static final ResourceLocation PROGRAM = ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "void_rift");
    private static boolean loggedFailure;
    private VoidRiftShaderClock() { }

    static void update() {
        try {
            Class<?> api = Class.forName("foundry.veil.api.client.render.VeilRenderSystem");
            Object engine = api.getMethod("renderer").invoke(null);
            Object manager = engine.getClass().getMethod("getShaderManager").invoke(engine);
            Object shader = manager.getClass().getMethod("getShader", ResourceLocation.class).invoke(manager, PROGRAM);
            if (shader == null) return;
            Object bridge = shader.getClass().getMethod("toShaderInstance").invoke(shader);
            if (bridge instanceof ShaderInstance wrapper) {
                var uniform = wrapper.getUniform("RiftPhase");
                if (uniform != null) uniform.set((float)((System.nanoTime() % 120_000_000_000L)
                        / 120_000_000_000.0 * Math.PI * 2));
            }
        } catch (ReflectiveOperationException | RuntimeException exception) {
            if (!loggedFailure) {
                TheBeneath.LOGGER.warn("[Void Rift] Unable to update the orb shader phase.", exception);
                loggedFailure = true;
            }
        }
    }
}

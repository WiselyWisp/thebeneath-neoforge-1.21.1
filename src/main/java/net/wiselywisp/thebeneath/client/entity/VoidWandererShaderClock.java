package net.wiselywisp.thebeneath.client.entity;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.wiselywisp.thebeneath.TheBeneath;


final class VoidWandererShaderClock {
    private static final ResourceLocation[] PROGRAMS = {
            ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "void_wanderer"),
            ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "void_wanderer_echo")
    };
    private static boolean loggedFailure;

    private VoidWandererShaderClock() {}

    static void update() {
        try {
            Class<?> api = Class.forName("foundry.veil.api.client.render.VeilRenderSystem");
            Object engine = api.getMethod("renderer").invoke(null);
            Object manager = engine.getClass().getMethod("getShaderManager").invoke(engine);
            var lookup = manager.getClass().getMethod("getShader", ResourceLocation.class);
            float phase = (float) ((System.nanoTime() % 180_000_000_000L)
                    / 180_000_000_000.0 * Math.PI * 2.0);
            for (ResourceLocation name : PROGRAMS) {
                Object shader = lookup.invoke(manager, name);
                if (shader == null) continue;
                Object bridge = shader.getClass().getMethod("toShaderInstance").invoke(shader);
                if (bridge instanceof ShaderInstance wrapper) {
                    var uniform = wrapper.getUniform("VoidPhase");
                    if (uniform != null) uniform.set(phase);
                }
            }
        } catch (ReflectiveOperationException | RuntimeException error) {
            if (!loggedFailure) {
                TheBeneath.LOGGER.warn("[Void Wanderer] Could not update animation uniform.", error);
                loggedFailure = true;
            }
        }
    }
}

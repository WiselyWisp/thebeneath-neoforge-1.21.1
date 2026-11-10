#version 150
// Ghostly, grayscale VHS silhouette with a featureless head.
// Screen-space noise is confined to the entity and remains compatible with
// Veil's individual-entity shader pipeline (no fullscreen postprocessing).
// Echo variant is compiled with ECHO_PASS=1, see void_wanderer_echo.fsh.
#ifndef ECHO_PASS
#define ECHO_PASS 0
#endif
uniform float VoidPhase;
in vec2 wandererUv;
in vec3 wandererLocalPos;
in vec3 wandererViewNormal;
in vec3 wandererViewPosition;
out vec4 fragColor;
const float TAU = 6.28318530718;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}
void main() {
    vec2 px = gl_FragCoord.xy;
    float phase = VoidPhase;
    float time = phase / TAU;
    float frame = floor(time * 6300.0);

    // Pixel noise, noisy horizontal interference, and drifting monochrome snow.
    float fine = hash(floor(px * vec2(1.0, 1.2)) + vec2(frame * 0.32, frame * 0.73));
    float coarse = hash(floor(px * vec2(0.23, 0.20)) + vec2(frame * 0.17, frame * 0.39));
    float wav = 0.5 + 0.5 * sin(px.x * 0.035 + sin(px.y * 0.016 + phase * 1.3));
    float charcoal = 0.11 + 0.105 * wav + (fine - 0.5) * 0.235 + (coarse - 0.5) * 0.085;

    // Visible thin scanlines across the head and body.
    float scan = sin(px.y * 3.14159);
    charcoal *= 0.74 + 0.26 * (0.5 + 0.5 * scan);
    charcoal += 0.025 * (0.5 + 0.5 * sin(px.y * 0.23 - phase * 4.0));

    // Screen-facing edges flash silver, as in an overexposed broken CRT feed.
    vec3 viewDir = normalize(-wandererViewPosition);
    float facing = clamp(dot(normalize(wandererViewNormal), viewDir), 0.0, 1.0);
    float rim = pow(1.0 - facing, 1.5);
    float isHead = 1.0 - smoothstep(0.0, 1.5, wandererLocalPos.y);
    float headSide = smoothstep(2.5, 4.05, abs(wandererLocalPos.x)) * isHead;
    float bodySide = smoothstep(2.4, 3.95, abs(wandererLocalPos.x))
                   * smoothstep(0.0, 2.2, wandererLocalPos.y)
                   * (1.0 - smoothstep(11.0, 15.0, wandererLocalPos.y));
    float silverEdge = clamp(max(rim * 0.70, max(headSide, bodySide) * 0.48), 0.0, 1.0);
    charcoal += silverEdge * (0.12 + 0.10 * fine);

    // Tracking faults: short torn horizontal lines and drifting white static.
    float row = floor(px.y / 3.0);
    float broken = step(0.983, hash(vec2(row, floor(time * 85.0))));
    float segment = step(0.65, hash(vec2(floor(px.x / 12.0), row)));
    charcoal += broken * segment * (0.18 + 0.29 * fine);
    float trackingY = mod(time * 1950.0, 1250.0);
    float tracking = 1.0 - smoothstep(1.0, 5.0, abs(px.y - trackingY));
    charcoal += tracking * 0.15 * coarse;

    // Legs dissolve into the signal rather than looking like normal trousers.
    float bottomFade = 1.0 - smoothstep(13.0, 24.0, wandererLocalPos.y);
    charcoal = clamp(charcoal, 0.018, 0.75);

    // Ordered alpha stippling still looks ghostlike when shaders or pipeline
    // modifications don't consistently blend translucent custom RenderTypes.
    ivec2 cell = ivec2(mod(floor(px), 4.0));
    const float bayer[16] = float[16](
         0.0,  8.0,  2.0, 10.0,
        12.0,  4.0, 14.0,  6.0,
         3.0, 11.0,  1.0,  9.0,
        15.0,  7.0, 13.0,  5.0
    );
    float threshold = (bayer[cell.y * 4 + cell.x] + 0.5) / 16.0;

#if ECHO_PASS == 1
    // Secondary offset silhouettes: vague, translucent gray static.
    float keep = 0.55 * bottomFade;
    if (threshold > keep) discard;
    float echoTone = clamp(charcoal * 0.62 + silverEdge * 0.065, 0.014, 0.50);
    fragColor = vec4(vec3(echoTone), 0.24 * bottomFade);
#else
    // Same noisy translucent charcoal surface across the entire model.
    // No emissive eye strips or face-specific highlights.
    if (threshold > (0.76 * bottomFade)) discard;
    fragColor = vec4(vec3(charcoal), 0.49 * bottomFade);
#endif
}

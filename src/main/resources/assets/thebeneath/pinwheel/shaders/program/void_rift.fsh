#version 150
// A cinematic, camera-facing gravitational lens: black event horizon,
// ice-blue/white warped accretion arcs, and quiet flowing filament bands.
// No scene colour buffer is sampled: this is NOT true framebuffer refraction.
uniform float RiftPhase;
in vec2 lensUv;
out vec4 fragColor;

float gauss(float x, float width) {
    float q = x / width;
    return exp(-q * q);
}

void main() {
    // x/y in [-1, 1].  The outer ring is an ellipse; the centre is circular
    // in world-space because the billboard scales to 5.5 by 3.6 blocks.
    vec2 p = (lensUv - 0.5) * 2.0;
    float phase = RiftPhase;
    float theta = atan(p.y / 0.57, p.x);
    float r = length(vec2(p.x, p.y / 0.57));

    // Small shears bend concentric, gently moving arcs around the horizon.
    float shear = 0.017 * sin(6.0 * theta - phase * 0.55)
                + 0.009 * sin(13.0 * theta + phase * 0.8)
                + 0.007 * sin(24.0 * theta - phase * 1.15);
    float rr = r + shear;

    // Layered rings. Faint outer halo and compact, brighter inner lensing.
    float broad = gauss(rr - 0.60, 0.265);
    float inner = gauss(rr - 0.385, 0.085);
    float outer = gauss(rr - 0.82, 0.185);
    float strands = 0.5 + 0.5 * sin(105.0 * rr + 3.0 * sin(4.0 * theta)
                                      + 1.5 * sin(11.0 * theta - phase * 0.4)
                                      - phase * 0.8);
    strands = pow(strands, 2.0);
    float fine = 0.5 + 0.5 * sin(210.0 * rr + 5.0 * sin(theta * 7.0 + phase * 0.3));

    float ring = 0.19 * broad + 0.46 * inner + 0.14 * outer;
    ring += 0.27 * broad * strands + 0.08 * broad * fine;

    // The lower-forward side is brighter, resembling gravitationally lensed light.
    float lower = 1.0 - smoothstep(-0.46, 0.14, p.y);
    float flare = gauss(rr - 0.38, 0.054) * lower;
    ring += 0.64 * flare;

    // Dark slate, silver and cold-white highlights; subtle violet at the edge.
    vec3 dim = vec3(0.065, 0.097, 0.135);
    vec3 ice = vec3(0.49, 0.64, 0.72);
    vec3 bright = vec3(0.84, 0.92, 0.97);
    float shine = clamp(0.32 * strands + 0.94 * flare, 0.0, 1.0);
    vec3 tint = mix(dim, ice, 0.55 + 0.30 * strands);
    tint = mix(tint, bright, shine);
    vec3 rgb = tint * ring * 1.65;
    rgb += vec3(0.055, 0.025, 0.095) * outer * 0.24;

    // A genuine black event-horizon silhouette, round in screen/world space.
    float core = length(vec2(p.x / 0.275, p.y / 0.420));
    float silhouette = 1.0 - smoothstep(0.99, 1.015, core);
    // Subtle narrow rim without replacing the black centre.
    float rim = gauss(core - 1.014, 0.032) * 0.19;
    rgb = mix(rgb + rim * vec3(0.46, 0.69, 0.77), vec3(0.0006, 0.001, 0.003), silhouette);

    // Transparent outside the disk. The centre remains fully opaque.
    float coverage = (1.0 - smoothstep(0.89, 1.095, rr));
    coverage *= smoothstep(0.26, 0.41, rr);
    // Keep the opaque black horizon in the guaranteed-visible texture below.
    // This optional Veil pass only adds subtle moving blue-white filaments.
    float alpha = coverage * clamp((strands * 0.21 + fine * 0.07) * ring, 0.0, 0.23);
    alpha *= (1.0 - silhouette);
    if (alpha < 0.006) discard;
    fragColor = vec4(min(vec3(1.0), rgb * 1.4 + vec3(0.11, 0.20, 0.25)), alpha);
}

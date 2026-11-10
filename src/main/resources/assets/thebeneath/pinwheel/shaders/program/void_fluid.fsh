#version 150
// Dedicated Veil fluid-pass shader. Only Essence of the Void geometry is
// submitted to this program, so no atlas colour-marker test is needed.
// A uniform animated every rendered frame avoids stepped texture clocks.
uniform float VoidPhase;
in vec2 beneathTexCoord;
in vec4 beneathVertexColor;
out vec4 fragColor;
const float TAU = 6.283185307179586;
float hash21(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}
float stars(vec2 uv, float density, float phase) {
    vec2 grid = uv * density;
    vec2 cell = floor(grid);
    vec2 local = fract(grid) - .5;
    vec2 offset = vec2(hash21(cell + 9.3), hash21(cell + 27.5)) - .5;
    float star = 1.0 - smoothstep(.018, .080, length(local - offset * .6));
    float pulse = .72 + .22 * sin(phase + hash21(cell + 11.0) * TAU);
    return star * step(.91, hash21(cell + 3.0)) * pulse;
}
void main() {
    float phase = VoidPhase;
    float p2 = phase * 2.0;
    float p3 = phase * 3.0;
    float p5 = phase * 5.0;
    vec2 uv = gl_FragCoord.xy * .005;
    vec2 driftA = .080 * vec2(cos(phase), sin(phase))
                + .019 * vec2(sin(p3 + 1.1), cos(p2 + .3));
    vec2 driftB = .062 * vec2(cos(p2 + 1.7), sin(p2 + 1.7))
                + .015 * vec2(cos(p5), sin(p3));
    float haze = sin(uv.x * 2.1 + .25 * sin(phase) + .12 * cos(p3))
               * cos(uv.y * 2.0 + .23 * cos(p2) + .09 * sin(p5));
    float detail = sin((uv.x + uv.y) * 1.25 + .16 * sin(p3 + .7));
    float nebula = clamp(.50 + .34 * haze + .10 * detail, 0.0, 1.0);
    vec3 color = mix(vec3(.008,.003,.022), vec3(.10,.014,.17), nebula);
    float light = stars(uv + driftA, 19.0, phase)
                + .65 * stars(uv * 1.4 + driftB, 27.0, p2 + 1.25);
    fragColor = vec4(color + light * vec3(.65,.48,.95), 1.0);
}

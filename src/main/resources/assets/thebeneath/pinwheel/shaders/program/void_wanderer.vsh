#version 150
// A player-sized ghost; slight horizontal instability in selected scan slices.
in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float VoidPhase;
out vec2 wandererUv;
out vec3 wandererLocalPos;
out vec3 wandererViewNormal;
out vec3 wandererViewPosition;
void main() {
    vec3 displaced = Position;
    float stripe = floor(Position.y * 1.75);
    float period = floor(VoidPhase * 13.0);
    float seed = fract(sin(dot(vec2(stripe, period), vec2(37.82, 61.71))) * 14375.31);
    if (seed > 0.975) {
        displaced.x += (seed - 0.975) * 5.0 * sin(stripe * 11.0 + VoidPhase * 8.0);
    }
    vec4 eyeSpace = ModelViewMat * vec4(displaced, 1.0);
    wandererViewPosition = eyeSpace.xyz;
    wandererViewNormal = normalize(mat3(ModelViewMat) * Normal);
    gl_Position = ProjMat * eyeSpace;
    wandererUv = UV0;
    wandererLocalPos = Position;
}

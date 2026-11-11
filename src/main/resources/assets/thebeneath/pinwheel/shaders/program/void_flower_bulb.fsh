
#version 150

uniform sampler2D Sampler0;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 mask = texture(Sampler0, texCoord);

    float maskBrightness = max(
        max(mask.r, mask.g), mask.b
    );

    float emission = maskBrightness * mask.a;

    if (emission < 0.02) {
        discard;
    }


    vec3 glowColor = vec3(0.52, 0.86, 1.0);

    float bloomIntensity = 0.2;

    fragColor = vec4(
    glowColor * bloomIntensity * emission,
    1.0
    );
}

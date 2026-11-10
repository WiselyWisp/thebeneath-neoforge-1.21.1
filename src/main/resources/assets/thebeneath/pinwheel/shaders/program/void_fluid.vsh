#version 150
// Standalone Veil program prototype. This does NOT replace a global Minecraft shader.
// Vertex layout follows Minecraft's translucent chunk vertex inputs.
in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
out vec2 beneathTexCoord;
out vec4 beneathVertexColor;
void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position + ChunkOffset, 1.0);
    beneathTexCoord = UV0;
    beneathVertexColor = Color;
}

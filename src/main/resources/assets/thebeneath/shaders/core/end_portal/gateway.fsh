#version 150

#moj_import <matrix.glsl>

#moj_import <eg_purple_gateways_remastered/end_gateway_settings.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

uniform float GameTime;
uniform int EndPortalLayers;
uniform vec2 ScreenSize;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 fragColor;

#moj_import <eg_purple_gateways_remastered/logic/portal_variables.glsl>

void main() {
    #moj_import <eg_purple_gateways_remastered/logic/portal_main.glsl>
}

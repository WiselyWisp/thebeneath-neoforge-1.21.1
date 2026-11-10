vec2 texCoord = (gl_FragCoord.xy / ScreenSize) * vec2(aspect, 1);

vec4 stars = vec4(0.);

for (int i = 0; i < STAR_LAYERS; i++) {
    vec4 star = texture(Sampler1, ( vec4(texCoord, 1, 1) * end_portal_layer(i + 1) ).xy);

    #if STARS_BLENDING_MODE == 0
        stars = mix(stars, star, star.a);
    #elif STARS_BLENDING_MODE == 1
        stars += star * star.aaaa;
    #endif
}

vec3 gradient = COLORS[0];
for(int i = 0; i < COLORS.length(); i++){
    float len_f = COLORS.length();

    float _step = (float(i)-0.5)/len_f;
    float _step2 = (float(i)+0.5)/len_f;

    gradient = mix(
        gradient,
        COLORS[i],
        smoothstep(_step, _step2, clamp(stars.r,0.,1.) )
    );
}

fragColor = vec4(gradient,1);
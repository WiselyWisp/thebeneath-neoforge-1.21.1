// Veil 4.2+ diagnostic only; included with -PveilProbe=true.
// This intentionally tints vanilla solid terrain and translucent blocks.
// If the terrain becomes MAGENTA, shader injection is confirmed active.
void tail() {
    fragColor.rgb = mix(fragColor.rgb, vec3(1.0, 0.0, 0.85), 0.80);
}

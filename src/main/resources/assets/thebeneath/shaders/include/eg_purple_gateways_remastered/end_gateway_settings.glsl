#define MIN_STAR_SIZE 6
#define MAX_STAR_SIZE 0.7

// Controls how the star layers are blended together
//   0 - Alpha blending
//     | Colour value of stars overlays previous ones based on transparency (similar to blocks like stained glass)
//   1 - Additive blending
//     | Colour value of stars is added to previous ones (vanilla end portal behaviour)
#define STARS_BLENDING_MODE 1

// How many layers of stars to draw
#define STAR_LAYERS 12

// The colours applied to the image
//   Stars "furthest" from (and the background) you will use colours near the start of the list
//   Stars "closest" to you will use colours near the bottom of the list
const vec3[] COLORS = vec3[](
  vec3(0.02, 0.008, 0.039),
  vec3(0.133, 0.082, 0.173),
  vec3(0.282, 0.161, 0.318),
  vec3(0.482, 0.251, 0.494),
  vec3(0.733, 0.471, 0.647),
  vec3(0.882, 0.71, 0.788),
  vec3(0.98, 0.914, 0.925)
);
#version 330 core

in vec4 fColor;
in vec2 fTexCoord;

out vec4 color;

uniform sampler2D uTex;
uniform bool uHasTexture;
uniform bool uUseLights;

uniform vec2 uLightPos;       // light pos
uniform vec3 uLightColor;     // light color
uniform float uLightRadius;
uniform float uAmbient;       // ambiant light

void main() {

     color = uHasTexture ? texture(uTex, fTexCoord) * fColor : fColor;

     if (uUseLights) {
          float dist = distance(fTexCoord, uLightPos);
          float intensity = 1.0 - clamp(dist / uLightRadius, 0.0, 1.0);

          vec3 clr = color.rgb * (uAmbient + uLightColor * intensity);
          color = vec4(clr, color.a);
     }
}

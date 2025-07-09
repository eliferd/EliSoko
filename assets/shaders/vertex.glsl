#version 330

layout(location=0) in vec2 aPos;
layout(location=1) in vec4 aColor;
layout(location=2) in vec2 aTexCoord;

out vec4 fColor;
out vec2 fTexCoord;

uniform mat4 uViewProj;

void main() {
    fColor = aColor;
    fTexCoord = aTexCoord;
    gl_Position = uViewProj * vec4(aPos, 0.0f, 1.0f);
}

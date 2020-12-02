#version 450

in vec4 positionOut;
in vec2 texCoord;

layout (location=0) out vec4 outColor0;



uniform mat4 view;
uniform float temp;

void main() {
        outColor0 = positionOut;
}
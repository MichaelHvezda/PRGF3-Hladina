#version 450

in vec4 positionOut;
in vec2 texCoord;

layout (location=0) out vec4 outColor0;
layout (location=1) out vec4 outColor1;



uniform mat4 view;
uniform float temp;

void main() {
        outColor0 = positionOut;
        outColor1 = vec4(0,0,0,1);
}
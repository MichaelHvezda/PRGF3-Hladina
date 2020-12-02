#version 450


in vec2 texCoord;
in vec4 positionOut;

layout (binding=0) uniform sampler2D texture1;

layout (location=0) out vec4 outColor0;
layout (location=1) out vec4 outColor1;
layout (location=2) out vec4 outColor2;

void main() {
    outColor0 = vec4(positionOut.xyz,1);
    outColor1 = texture(texture1,texCoord);
    outColor2 = vec4(0,1,0,1);
}
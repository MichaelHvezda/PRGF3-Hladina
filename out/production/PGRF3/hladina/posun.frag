#version 450

in vec4 positionOut;
in vec2 texCoord;
layout (location=0) out vec4 outColor0;
//layout (location=1) out vec4 outColor1;
//layout (location=2) out vec2 outColor2;




void main() {
    outColor0 = positionOut;
    //outColor1 = vec4(1,0,0,1);
    //outColor2 = texCoord;
}
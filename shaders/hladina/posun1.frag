#version 450

//in vec4 positionOut;
in vec2 texCoord;
layout (location=0) out vec4 outColor0;
layout (location=1) out vec4 outColor1;
in vec3 newMove;
in vec3 position3d;

void main() {


    vec4 positionOut = vec4(position3d,1);
    outColor0 = positionOut;
    vec4 move= vec4(newMove,1);;
    outColor1 = move;
}
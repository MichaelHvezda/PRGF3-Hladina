#version 450


in vec2 texCoord;
layout (location=0) out vec4 outColor0;
layout (location=1) out vec4 outColor1;


in vec3 move3d;
in vec3 positionOut;
void main() {
    vec4 poss = vec4(positionOut,1);
    outColor0 =poss;
    vec4 move= vec4(move3d,1);
    outColor1 = move;
}
#version 450
in vec2 inPosition; // input from the vertex buffer
in vec2 inTexCoord; // input from the vertex buffer

out vec4 positionOut; // output from this shader to the next pipleline stage
out vec2 texCoord;


void main() {

    texCoord = inPosition;//* 2 - 1;

    gl_Position = vec4(inPosition* 2 - 1,0,1.0);
}
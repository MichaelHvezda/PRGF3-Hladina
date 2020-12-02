#version 450
in vec2 inPosition; // input from the vertex buffer
in vec2 inTexCoord; // input from the vertex buffer

out vec4 positionOut; // output from this shader to the next pipleline stage
//out vec2 texCoord;


uniform mat4 view;
uniform mat4 projection;


void main() {
    //texCoord = inPosition;
    vec2 position = inPosition * 2 - 1;

    vec3 position3d = vec3(position.x,position.y,0);
    positionOut = vec4(position3d,1);
    gl_Position = vec4(position3d, 1.0);

}
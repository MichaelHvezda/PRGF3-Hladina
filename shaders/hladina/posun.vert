#version 450
in vec2 inPosition;

out vec2 texCoord;

uniform mat4 view;
uniform mat4 projection;

void main() {

    texCoord = inPosition;//* 2 - 1;

    gl_Position = vec4(inPosition* 2 - 1,0,1.0);
}

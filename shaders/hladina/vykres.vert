#version 450
in vec2 inPosition;

//out vec2 texCoord;
uniform mat4 view;

uniform mat4 projection;
layout (binding=0) uniform sampler2D positionTexture;
//layout (binding=2) uniform sampler2D texCord;

void main() {

    vec2 texCoord = inPosition;
    vec4 sss= texture(positionTexture,texCoord);
    gl_Position = projection * view * vec4(sss.x,sss.y,sss.z, 1);
    //gl_Position = sss;
}
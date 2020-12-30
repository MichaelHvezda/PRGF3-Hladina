#version 450
in vec2 inPosition;

//out vec2 texCoord;
uniform mat4 view;

uniform mat4 projection;
layout (binding=0) uniform sampler2D positionTexture;
const float scale = 10;
out vec3 position;
void main() {

    vec2 texCoord = inPosition;
    vec4 sss= texture(positionTexture,texCoord);
    vec2 position2 = inPosition*2-1;
    vec4 pos = vec4(position2.x*scale,position2.y*scale,sss.z,1);
    position = pos.xyz;
    gl_Position = projection * view * vec4(pos.x,pos.y,pos.z, 1);

}
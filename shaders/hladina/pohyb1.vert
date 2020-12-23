#version 450
in vec2 inPosition; // input from the vertex buffer
in vec2 inTexCoord; // input from the vertex buffer

out vec2 texCoord;
layout (binding=0) uniform sampler2D positionTexture;
layout (binding=1) uniform sampler2D moveTexture;
out vec3 move3d;
out vec3 position3d;
void main() {

    texCoord = inPosition;//* 2 - 1;
    vec3 position3dPom = texture(positionTexture, inPosition).xyz;
    move3d = texture(moveTexture, inPosition).xyz;
    position3d = vec3(position3dPom.z+move3d.z);
    //position3d = vec3(position3dPom.z);

    gl_Position = vec4(inPosition* 2 - 1,0,1.0);
}
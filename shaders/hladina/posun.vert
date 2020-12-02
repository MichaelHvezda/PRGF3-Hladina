#version 450
in vec2 inPosition;
out vec4 positionOut;
out vec2 texCoord;
layout (binding=0) uniform sampler2D positionTexture;
layout (location=0) out vec4 outColor0;
uniform mat4 view;
uniform mat4 projection;

void main() {

    texCoord = inPosition;//* 2 - 1;
    vec3 position3d = texture(positionTexture, texCoord).xyz;
    position3d = vec3(position3d.x,position3d.y,position3d.z+0.001);
    positionOut = vec4(position3d,1);
    gl_Position = vec4(position3d,1.0);
}

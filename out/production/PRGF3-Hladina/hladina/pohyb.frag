#version 450

//in vec4 positionOut;
in vec2 texCoord;
layout (location=0) out vec4 outColor0;
layout (location=1) out vec4 outColor1;
//layout (location=1) out vec4 outColor1;
//layout (location=2) out vec2 outColor2;
layout (binding=0) uniform sampler2D positionTexture;
layout (binding=1) uniform sampler2D moveTexture;

void main() {
    vec3 position3d = texture(positionTexture, texCoord).xyz;
    vec4 move3d = texture(moveTexture, texCoord);

    outColor0 = vec4(position3d.x,position3d.y,position3d.z+move3d.z,1);
    outColor1 = move3d;
}
#version 450


in vec2 texCoord;
in vec4 positionOut;

layout (binding=0) uniform sampler2D texture1;

layout (location=0) out vec4 outColor0;
layout (location=1) out vec4 outColor1;
layout (location=2) out vec4 outColor2;
layout (location=3) out vec4 outColor3;

uniform mat4 view;
uniform float temp;

void main() {
        outColor0 = vec4(positionOut.xyz,1);
        outColor1 = texture(texture1,texCoord);
        //zapsani barvy nebo textury
        if(temp==7 ||temp==8 ){
                outColor2 = texture(texture1,texCoord);
        }else{
                outColor2 = vec4(1,0,0,1);
        }
        outColor3 = view * vec4(positionOut.xyz,1);
}
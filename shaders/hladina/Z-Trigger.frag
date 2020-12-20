#version 450

//in vec4 positionOut;
in vec2 texCoord;
layout (location=0) out vec4 outColor0;
layout (location=1) out vec4 outColor1;
//layout (location=1) out vec4 outColor1;
//layout (location=2) out vec2 outColor2;
layout (binding=0) uniform sampler2D positionTexture;
layout (binding=1) uniform sampler2D moveTexture;

uniform vec2 poss;

const float Vzdalenost = 0.75;
const float Sila = 1;
const float PI = 3.1415;

float getMove(vec3 pos){
    float x0 = pos.x;
    float y0 = pos.y;
    float x = poss.x;
    float y = poss.y;

    float mv = pos.z;

    float vysledek = sqrt(
    ((x0-x)*(x0-x))+((y0-y)*(y0-y))
    );

    if (vysledek <= Vzdalenost){

        vysledek = vysledek/Vzdalenost;
        vysledek = PI * vysledek;
        vysledek = (cos(vysledek)+1)/2;
        mv = mv+vysledek*Sila;
    }else {
        mv = mv+0.0;
    }

    return mv;
}

void main() {
    vec3 position3d = texture(positionTexture, texCoord).xyz;
    vec4 move3d = texture(moveTexture, texCoord);

    vec4 positionOut = vec4(position3d,1);
    outColor0 = vec4(position3d.x,position3d.y,getMove(position3d),1);
    outColor1 = move3d;
}
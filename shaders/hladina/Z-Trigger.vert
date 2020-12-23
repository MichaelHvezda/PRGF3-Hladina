#version 450
in vec2 inPosition; // input from the vertex buffer

layout (binding=0) uniform sampler2D positionTexture;
layout (binding=1) uniform sampler2D moveTexture;
out vec2 texCoord;

uniform vec2 poss;
out vec3 move3d;
out vec3 positionOut;
const float Vzdalenost = 0.75;
const float Sila = 1;
const float PI = 3.1415;

float getMove(vec3 pos){
    float x0 = inPosition.x* 2 - 1;
    float y0 = inPosition.y* 2 - 1;
    float x = poss.x;
    float y = poss.y;

    float mv = pos.z;
    float test = 0;
    float vysledek = sqrt(
    ((x0-x)*(x0-x))+((y0-y)*(y0-y))
    );

    if (vysledek <= Vzdalenost){

        vysledek = vysledek/Vzdalenost;
        vysledek = PI * vysledek;
        vysledek = (cos(vysledek)+1)/2;
        mv = mv+vysledek*Sila;
        test =mv+1;
    }else {
        mv = mv+0.0;
    }

    return mv;
}

void main() {

    texCoord = inPosition;//* 2 - 1;
    vec3 position3d = texture(positionTexture, texCoord).xyz;
    move3d = texture(moveTexture, texCoord).xyz;
    vec3 positionPom = vec3(position3d);
    positionOut = vec3(getMove(positionPom));


    gl_Position = vec4(inPosition* 2 - 1,0,1.0);
}
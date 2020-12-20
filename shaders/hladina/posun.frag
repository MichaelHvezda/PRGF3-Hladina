#version 450

//in vec4 positionOut;
in vec2 texCoord;
layout (location=0) out vec4 outColor0;
layout (location=1) out vec4 outColor1;
//layout (location=1) out vec4 outColor1;
//layout (location=2) out vec2 outColor2;
layout (binding=0) uniform sampler2D positionTexture;
layout (binding=1) uniform sampler2D moveTexture;

const float utlum = 0.99;
const float posunZZZ = 1/1440.0f; //heigh

float getUp(){
    vec2 texCoord2D = vec2(texCoord.x+posunZZZ,texCoord.y);
    float positionZ = texture(positionTexture, texCoord2D+vec2(posunZZZ,0)).z;

    float diffZ = positionZ;


    return diffZ;

}

float getDown(){
    vec2 texCoord2D = vec2(texCoord.x-posunZZZ,texCoord.y);
    float positionZ = texture(positionTexture, texCoord2D+vec2(posunZZZ,0)).z;

    float diffZ = positionZ;


    return diffZ;
}

float getLeft(){
    vec2 texCoord2D = vec2(texCoord.x,texCoord.y-posunZZZ);
    float positionZ = texture(positionTexture, texCoord2D-vec2(0,posunZZZ)).z;

    float diffZ = positionZ;


    return diffZ;
}

float getRight(){
    vec2 texCoord2D = vec2(texCoord.x,texCoord.y+posunZZZ);
    float positionZ = texture(positionTexture, texCoord2D+vec2(0,posunZZZ)).z;

    float diffZ = positionZ;


    return diffZ;
}
vec4 getMove(vec3 pos, float move){
    float sousedi = 0;

    float mv= 0;
    float up = 0;
    float down = 0;
    float left = 0;
    float right = 0;
    if(texCoord.x!=0){
        float up = getUp();
        sousedi++;
    }

    if(texCoord.x!=1){
        float down = getDown();
        sousedi++;
    }

    if(texCoord.y!=0){
        float left = getLeft();
        sousedi++;
    }

    if(texCoord.y!=1){
        float right = getRight();
        sousedi++;
    }

    mv = move*utlum + ((up+down+left+right)/sousedi-pos.z);

    return vec4(mv,mv,mv,1);
}

void main() {
    vec3 position3d = texture(positionTexture, texCoord).xyz;
    vec3 move3d = texture(moveTexture, texCoord).xyz;

    vec4 newMove = getMove(position3d,move3d.z);

    position3d = vec3(position3d.x,position3d.y,position3d.z+newMove.z);
    vec4 positionOut = vec4(position3d,1);
    outColor0 = positionOut;
    outColor1 = newMove;
}
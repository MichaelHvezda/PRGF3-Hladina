#version 450
in vec2 inPosition;

out vec2 texCoord;

uniform mat4 view;
uniform mat4 projection;

layout (binding=0) uniform sampler2D positionTexture;
layout (binding=1) uniform sampler2D moveTexture;

const float utlum = 0.99;
const float posunZZZ = 1/500.0f; //heigh
out vec3 newMove;
out vec3 position3d;
float getUp(){
    vec2 texCoord2D = vec2(texCoord.x+posunZZZ,texCoord.y);
    //float positionZ = textureOffset(positionTexture, inPosition,ivec2(1,0)).z;
    float positionZ = texture(positionTexture, texCoord2D).z;
    float diffZ = positionZ;


    return diffZ;

}

float getDown(){
    vec2 texCoord2D = vec2(texCoord.x-posunZZZ,texCoord.y);
    //float positionZ = textureOffset(positionTexture, inPosition,ivec2(-1,0)).z;
    float positionZ = texture(positionTexture, texCoord2D).z;
    float diffZ = positionZ;


    return diffZ;
}

float getLeft(){
    vec2 texCoord2D = vec2(texCoord.x,texCoord.y-posunZZZ);
    //float positionZ = textureOffset(positionTexture, inPosition,ivec2(0,1)).z;
    float positionZ = texture(positionTexture, texCoord2D).z;
    float diffZ = positionZ;


    return diffZ;
}

float getRight(){
    vec2 texCoord2D = vec2(texCoord.x,texCoord.y+posunZZZ);
    //float positionZ = textureOffset(positionTexture, inPosition,ivec2(0,-1)).z;
    float positionZ = texture(positionTexture, texCoord2D).z;

    float diffZ = positionZ;


    return diffZ;
}
vec4 getMove(float pos, float move){
    float sousedi = 0;

    float mv= 0;
    float up = 0;
    float down = 0;
    float left = 0;
    float right = 0;
    //if(texCoord.x>0.01){
    //    up = getUp();
    //    sousedi++;
    //}
    ////
    //if(texCoord.x<0.99){
    //    down = getDown();
    //    sousedi++;
    //}
    ////
    //if(texCoord.y>0.01){
    //    left = getLeft();
    //    sousedi++;
    //}
    ////
    //if(texCoord.y<0.99){
    //    right = getRight();
    //    sousedi++;
    //}
//

    up = getUp();
    down = getDown();
    left = getLeft();
    right = getRight();

    mv = move*utlum + ((down+up+right+left)/4-pos);

    return vec4(mv,mv,mv,1);
}

vec4 getMoveDerivation(vec3 pos, float move){
    float sousedi = 0;

    float mv= 0;
    float count = 0;
    float up = 0;
    float down = 0;
    float left = 0;
    float right = 0;
    if(texCoord.x!=0){
        // float up = getUp();
        count = count + getUp();
        sousedi++;
    }

    if(texCoord.x!=1){
        // float down = getDown();
        count = count + getDown();
        sousedi++;
    }

    if(texCoord.y!=0){
        // float left = getLeft();
        count = count + getLeft();
        sousedi++;
    }

    if(texCoord.y!=1){
        //float right = getRight();
        count = count + getRight();
        sousedi++;
    }

    //mv = move*utlum + ((up+down+left+right)/sousedi-pos.z);
    //mv = move*utlum + (count/sousedi-pos.z);
    mv = move*utlum + (0-pos.z);

    return vec4(mv,mv,mv,1);
}

void main() {
    texCoord = inPosition;//* 2 - 1;
    position3d = texture(positionTexture, texCoord).xyz;
    vec3 move3d = texture(moveTexture, texCoord).xyz;
    newMove = getMove(position3d.z,move3d.z).xyz;
    //newMove = vec3(move3d.z*utlum - position3d.z);
    //float mov =move3d.z*utlum - position3d.z;
    //newMove = vec3(mov,mov,mov);



    gl_Position = vec4(inPosition* 2 - 1,0,1.0);
}

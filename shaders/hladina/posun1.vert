#version 450
in vec2 inPosition;

out vec2 texCoord;

uniform mat4 view;
uniform mat4 projection;

layout (binding=0) uniform sampler2D positionTexture;
layout (binding=1) uniform sampler2D moveTexture;

const float utlum = 0.99;
uniform float posunZZZ;
out vec3 newMove;
out vec3 position3d;
float getUp(){
    vec2 texCoord2D = vec2(texCoord.x+posunZZZ,texCoord.y);
    float positionZ = texture(positionTexture, texCoord2D).z;
    float diffZ = positionZ;

    return diffZ;
}

float getDown(){
    vec2 texCoord2D = vec2(texCoord.x-posunZZZ,texCoord.y);
    float positionZ = texture(positionTexture, texCoord2D).z;
    float diffZ = positionZ;

    return diffZ;
}

float getLeft(){
    vec2 texCoord2D = vec2(texCoord.x,texCoord.y-posunZZZ);
    float positionZ = texture(positionTexture, texCoord2D).z;
    float diffZ = positionZ;

    return diffZ;
}

float getRight(){
    vec2 texCoord2D = vec2(texCoord.x,texCoord.y+posunZZZ);
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

    up = getUp();
    down = getDown();
    left = getLeft();
    right = getRight();

    mv = move*utlum + ((down+up+right+left)/4-pos);
    if(posunZZZ<0){
        mv =1;
    }

    return vec4(mv,mv,mv,1);
}

void main() {
    texCoord = inPosition;
    position3d = texture(positionTexture, texCoord).xyz;
    vec3 move3d = texture(moveTexture, texCoord).xyz;
    newMove = getMove(position3d.z,move3d.z).xyz;

    gl_Position = vec4(inPosition* 2 - 1,0,1.0);
}

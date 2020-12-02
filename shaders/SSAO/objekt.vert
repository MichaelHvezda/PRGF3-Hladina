#version 450
in vec3 inPosition; // input from the vertex buffer
in vec2 inTexCoord; // input from the vertex buffer
//in vec3 inNormal; // input from the vertex buffer

out vec4 positionOut; // output from this shader to the next pipleline stage
out vec2 texCoord;

uniform float temp;
uniform mat4 view;
uniform mat4 projection;

const float otoceni = 3.1415;

vec3 rotateXY(vec3 pos,float oto){
    return vec3(
    pos.x*cos(oto)-pos.y*sin(oto),
    pos.x*sin(oto)+pos.y*cos(oto),
    pos.z);
}

vec3 rotateYZ(vec3 pos,float oto){
    return vec3(
    pos.x,
    pos.y*cos(oto)-pos.z*sin(oto),
    pos.y*sin(oto)+pos.z*cos(oto));
}

vec3 rotateXZ(vec3 pos,float oto){
    return vec3(
    pos.x*cos(oto)-pos.z*sin(oto),
    pos.y,
    pos.x*sin(oto)+pos.z*cos(oto));
}

void main() {
    if(temp==0){
        vec3 position = inPosition*0.0015;
        position = rotateYZ(position,otoceni);
        position = rotateXY(position,otoceni);
        positionOut = vec4(position,1);
        texCoord = inTexCoord;
        gl_Position = projection * view * positionOut;
    }
    if(temp==1){
        vec3 position = inPosition*0.005;
        position = rotateYZ(position,otoceni/2);
        position = vec3(position.x + 5,position.y,position.z);
        positionOut = vec4(position,1);
        texCoord = inTexCoord;
        gl_Position = projection * view * positionOut;
    }
}
#version 450
in vec2 inPosition; // input from the vertex buffer
in vec3 inPosition3D; // input from the vertex buffer
in vec2 inTexCoord; // input from the vertex buffer

out vec4 positionOut; // output from this shader to the next pipleline stage
out vec2 texCoord;

uniform float temp;
uniform float deformVar;
uniform mat4 view;
uniform mat4 projection;

const float PI = 3.1415;

vec3 getKoule(vec3 pos) {
    float az = pos.x +1;// souřadnice z gridu je v <-1;1> a chceme v rozsahu <-PI;PI>
    float ze = pos.y * PI+ PI;// souřadnice z gridu je v <-1;1> a chceme v rozsahu <-PI/2;PI/2>
    float r = pos.z;

    float x = r * cos(az) * cos(ze);
    float y = r * sin(az) * cos(ze);
    float z = r * sin(ze);

    return vec3(x, y, z);
}
vec3 getKruh(vec3 pos) {
    float ze = pos.y * PI+ PI;
    float r = pos.x;

    float x = r * cos(ze);
    float y = r * sin(ze);
    float z = pos.z ;

    return vec3(x, y, z);
}

vec3 getValec(vec3 pos) {
    float az = pos.x +1;
    float ze = pos.y * PI+ PI;
    float r = pos.z;

    float x = r * cos(ze);
    float y = r * sin(ze);
    float z = az ;

    return vec3(x, y, z);
}

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
    texCoord = inPosition;
    vec2 position = inPosition * 2 - 1;
    //vykresleni objektu do sceny
    if(temp==0){
        vec3 position3d = vec3(position.x,position.y,1)*vec3(sin(position.y)+cos(position.x),sin(position.y),cos(position.x));
        position3d = getKruh(position3d);
        position3d = rotateYZ(position3d,-PI/2)/4;
        position3d = position3d + vec3(0,1,0);
        positionOut = vec4(position3d,1);
        gl_Position = projection * view * vec4(position3d, 1.0);
    }
    if(temp==1){
        vec3 position3d = vec3(position.x,position.y,1);
        position3d = getKruh(position3d);
        position3d = getKoule(position3d);
        position3d = rotateYZ(position3d,-PI/2)/4;
        position3d = position3d + vec3(-1,0,0);
        positionOut = vec4(position3d,1);
        gl_Position = projection * view * vec4(position3d, 1.0);
    }
    if(temp==2){
        vec3 position3d = vec3(position.x,position.y,1)*vec3(sin(position.y)+cos(position.x),sin(position.y),cos(position.x));
        position3d = getValec(position3d);
        position3d = rotateYZ(position3d,-PI/2)/4;
        positionOut = vec4(position3d,1);
        gl_Position = projection * view * vec4(position3d, 1.0);
    }
    if(temp==3){
        vec3 position3d = vec3(position.x,position.y,1);
        position3d = getKruh(position3d);
        position3d = getValec(position3d)/4;
        position3d = rotateYZ(position3d,-PI/2);
        position3d = position3d + vec3(1,0,0);
        positionOut = vec4(position3d,1);
        gl_Position = projection * view * vec4(position3d, 1.0);
    }
    if(temp==4){
        vec3 position3d = vec3(position.x,position.y,1)*vec3(sin(position.y)+cos(position.x),sin(position.y),cos(position.x));
        position3d = rotateYZ(position3d,-PI/2)/4;
        position3d = position3d + vec3(0,-1,0);
        positionOut = vec4(position3d,1);
        gl_Position = projection * view * vec4(position3d, 1.0);
    }
    if(temp==5){
        position = (position +1 )/2;
        vec3 position3d = vec3(pow(position.x,deformVar),pow(position.y,deformVar),position.x-position.y)/4;
        position3d = rotateYZ(position3d,-PI/2);
        position3d = position3d + vec3(-1,-1,0);
        positionOut = vec4(position3d,1);
        gl_Position = projection * view * vec4(position3d, 1.0);
    }
    if(temp==6){
        vec3 position3d = vec3(position,0) ;
        position3d = position3d +vec3(0,0,-1);
        positionOut = vec4(position3d,1);
        gl_Position = projection * view * vec4(position3d, 1.0);
    }
    if(temp==7){
        vec3 position = inPosition3D*0.005;
        position = rotateYZ(position,PI);
        position = rotateXY(position,PI);
        position = position + vec3(1,1,0);
        positionOut = vec4(position,1);
        texCoord = inTexCoord;
        gl_Position = projection * view * positionOut;
    }
    if(temp==8){
        vec3 position = inPosition3D*0.0015;
        position = rotateYZ(position,PI/2);
        position = rotateXY(position,deformVar);
        position = vec3(position.x + 2,position.y,position.z);
        positionOut = vec4(position,1);
        texCoord = inTexCoord;
        gl_Position = projection * view * positionOut;
    }
}
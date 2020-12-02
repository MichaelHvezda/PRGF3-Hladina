#version 150

in vec2 inPosition; // input from the vertex buffer

uniform vec3 lightPos;
uniform mat4 view;
uniform mat4 projection;
out vec2 texCoord;
out vec3 lightPosOut;
const float PI = 3.1415;
out vec3 spotDirection;

vec3 getSphere(vec2 pos,vec3 lightPosi) {
    float az = pos.x * PI;// souřadnice z gridu je v <-1;1> a chceme v rozsahu <-PI;PI>
    float ze = pos.y * PI / 2;// souřadnice z gridu je v <-1;1> a chceme v rozsahu <-PI/2;PI/2>
    float r = 0.2;

    float x = r * cos(az) * cos(ze)+lightPosi.x;
    float y = r * sin(az) * cos(ze)+lightPosi.y;
    float z = r * sin(ze)+lightPosi.z;

    return vec3(x, y, z);
}

void main() {
    /*texCoord = inPosition;
    vec2 position = inPosition * 2 - 1;
    vec3 pos3;
        pos3 = vec3(position,0);
        normal = vec3(position,0);

    gl_Position = projection * view * vec4(pos3, 1.0);

    vec4 pos4 = vec4(pos3, 1.0);
    light = lightPos - (view * pos4).xyz;
    //    light = lightPos - (mat3(view) * pos3);

    viewDirection = -(view * pos4).xyz;
*/
    //gl_Position = vec4(inPosition, 0.0, 1.0);
    //texCoord = inPosition;
    //texCoord = inPosition;
    //vec2 position = inPosition * 2 - 1;
    //gl_Position =  vec4(inPosition, 0.0, 1.0);

    texCoord = inPosition;
    vec2 position = inPosition * 2 - 1;
    spotDirection = lightPos - getSphere(position,lightPos);
    gl_Position = projection * view * vec4(getSphere(position,lightPos), 1.0);
}
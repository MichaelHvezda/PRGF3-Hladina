#version 150
in vec2 texCoord;
out vec4 outColor; // output from the fragment shader
uniform sampler2D texture1;
uniform vec3 lightDir;
uniform float lightSpotCutOff;
uniform float lightType;
in vec3 lightPosOut;
in vec3 spotDirection;

void main() {
        outColor = vec4(1,1,1.0,1.0);
}
#version 450
in vec2 texCoord;
out vec4 outColor;

layout (binding=0) uniform sampler2D positionTexture;
layout (binding=1) uniform sampler2D ssaoTexture;
layout (binding=2) uniform sampler2D imageTexture;
layout (binding=3) uniform sampler2D imageColor;
uniform mat4 view;

//uniform vec3 light;
uniform vec3 lightPosition ;
uniform vec3 cameraPosition ;

uniform vec3 lightDir;
uniform float lightSpotCutOff;
uniform float lightType;

uniform vec3 svetloADS;


void main() {
    //normalni svetle nebo refrektor
    if(lightType==0){
        //vypocet pozice
        vec3 position = texture(positionTexture, texCoord).xyz;
        vec3 spotDirection = lightPosition - position;
        float spotEffect = max(dot(normalize(-lightDir),normalize(spotDirection)),0);
        if(spotEffect>lightSpotCutOff){

            //vypocet slozek svetla
            vec4 color = texture(imageColor,texCoord);
            vec3 normal = cross(dFdxFine(position),dFdyFine( position));
            float AO = texture(ssaoTexture, texCoord).x;

            vec4 ambient = vec4(vec3(0.3) * AO, 1.0);
            vec3 light = normalize(lightPosition - position);
            float NdotL = max(0, dot(normalize(normal), normalize(light)));
            vec4 diffuse = vec4(NdotL * vec3(0.3), 1.0);

            vec3 mirrLight = reflect(-light,normalize(normal));
            vec3 camera = normalize(cameraPosition-position);
            float cosB = max(0, dot(camera, normalize(mirrLight)));
            float cosBPow = pow(cosB,32);
            vec4 specular = vec4(cosBPow*vec3(1), 1.0);

            float ligthLenght = length(lightPosition - position)/5;
            //vypocet utlumu
            float utlum=1.0/(1+ligthLenght+ligthLenght*ligthLenght);

            //moznost vypnuti a zapnuti jednotlivych slozek svetla
            if(svetloADS.x==1){
                ambient = vec4(0);
            }
            if(svetloADS.y==1){
                diffuse = vec4(0);
            }
            if(svetloADS.z==1){
                specular = vec4(0);
            }

            vec4 finalColor = ambient + (diffuse + specular)*utlum;
            vec4 textureColor = color;
            //rozmazani svetla
            float blend = clamp((spotEffect-lightSpotCutOff)/(1-lightSpotCutOff),0.0,1.0);
            outColor = mix(ambient,finalColor,blend) * textureColor;
        }else{
            vec4 ambient = vec4(vec3(0.2), 1.0);
            if(svetloADS.x==1){
                ambient = vec4(0);
            }
            vec4 textureColor = vec4(0.1,0.1,0.1, 1.0);
            outColor = ambient * textureColor;
        }
    }else{
        //vypocet slozek svetla
        vec3 position = texture(positionTexture, texCoord).xyz;
        vec4 color = texture(imageColor,texCoord);
        vec3 normal = cross(dFdxFine(position),dFdyFine( position));
        float AO = texture(ssaoTexture, texCoord).x;

        vec4 ambient = vec4(vec3(0.3) * AO, 1.0);

        vec3 light = normalize(lightPosition - position);
        float NdotL = max(0, dot(normalize(normal), normalize(light)));
        vec4 diffuse = vec4(NdotL * vec3(0.3), 1.0);

        vec3 mirrLight = reflect(-light,normalize(normal));
        vec3 camera = normalize(cameraPosition-position);
        float cosB = max(0, dot(camera, normalize(mirrLight)));
        float cosBPow = pow(cosB,32);
        vec4 specular = vec4(cosBPow*vec3(1), 1.0);

        //moznost vypnuti a zapnuti jednotlivych slozek svetla
        if(svetloADS.x==1){
            ambient = vec4(0);
        }
        if(svetloADS.y==1){
            diffuse = vec4(0);
        }
        if(svetloADS.z==1){
            specular = vec4(0);
        }

        vec4 finalColor = ambient + diffuse + specular;

        outColor = finalColor * color;
    }

}

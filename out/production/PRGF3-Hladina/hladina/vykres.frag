#version 450
in vec2 texCoord;
in vec3 position;
out vec4 outColor;

uniform mat4 view;

uniform vec3 lightPosition ;
uniform vec3 cameraPosition ;

uniform vec3 lightDir;

uniform vec3 svetloADS;

void main() {

        vec3 normal = cross(dFdxFine(position),dFdyFine( position));
        vec4 ambient = vec4(vec3(0,0.05,0.3) , 1.0);

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
                ambient = vec4(0,0,0,1);
        }
        if(svetloADS.y==1){
                diffuse = vec4(0,0,0,1);
        }
        if(svetloADS.z==1){
                specular = vec4(0,0,0,1);
        }

        vec4 finalColor = ambient + diffuse + specular;

        outColor = finalColor;


}

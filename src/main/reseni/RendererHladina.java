package main.reseni;

import lwjglutils.*;
import main.AbstractRenderer;
import main.GridFactory;
import main.LwjglWindow;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import transforms.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class RendererHladina extends AbstractRenderer {

    private OGLBuffers buffersDuck,buffersElephant ,buffersSun,quad,buffers,buffersStrip;
    private double otoceni =0.01;

    private int locViewSun, locProjectionSun, shaderProgramSun, locLightPosSun,locLightDirSun, locLightSpotCutOffSun,locLightTypeSun;
    private int shaderProgramPredani,shadeProgramVykres, shaderProgramNahrani,locShadeLightType,locShadeLightDir,locShadeLightSpotCutOff;
    private int locTempObjekt, locViewNahrani, locProjectionNahrani, locDeformObjekt,shaderProgramPosun;
    private int locSsaoProjection,locSsaoView;
    private int locShadeView,locShadeLightPos,locShadeCameraPos,locShadeSvetloADS,locShadeProjection;
    private Camera camera;
    private Camera cameraLight;
    private Mat4 projection;
    private boolean per,debug;
    OGLModelOBJ modelElephant,modelDuck;
    private OGLTexture2D texture1,randomTexture;
    private OGLTexture2D.Viewer viewer;
    private OGLRenderTarget prvniRT, druhyRT;
    private Vec3D lightDir = new Vec3D(1,0,0);
    private float lightSpotCutOff = 0.95f;
    private float deformVar = 0f;
    private int switchInt =0;
    private float lightType = 0;
    private int svetloAmbient=0,svetloDiffuse=0,svetloSpecular=0;

    @Override
    public void init() {
        OGLUtils.printOGLparameters();
        OGLUtils.printLWJLparameters();
        OGLUtils.printJAVAparameters();
        OGLUtils.shaderCheck();

        textRenderer = new OGLTextRenderer(LwjglWindow.WIDTH, LwjglWindow.HEIGHT);

        //definice lokaci shaderu
        shaderProgramNahrani = ShaderUtils.loadProgram("/hladina/nahrani");
        shaderProgramPredani = ShaderUtils.loadProgram("/hladina/predani");
        shaderProgramPosun = ShaderUtils.loadProgram("/hladina/posun");
        //shaderProgramSun = ShaderUtils.loadProgram("/hladina/objSun");
        shadeProgramVykres = ShaderUtils.loadProgram("/hladina/vykres");

        //locTempObjekt = glGetUniformLocation(shaderProgramObjekt, "temp");
        //locViewNahrani = glGetUniformLocation(shaderProgramNahrani, "view");
        //locProjectionNahrani = glGetUniformLocation(shaderProgramNahrani, "projection");
        //locDeformObjekt = glGetUniformLocation(shaderProgramObjekt, "deformVar");

        //locSsaoProjection = glGetUniformLocation(shaderProgramPredani, "projection");
        //locSsaoView = glGetUniformLocation(shaderProgramPredani, "view");

        locShadeView = glGetUniformLocation(shadeProgramVykres, "view");
        locShadeProjection = glGetUniformLocation(shadeProgramVykres, "projection");
        //locShadeLightPos = glGetUniformLocation(shadeProgram, "lightPosition");
        //locShadeCameraPos = glGetUniformLocation(shadeProgram, "cameraPosition");
        //locShadeLightType = glGetUniformLocation(shadeProgram, "lightType");
        //locShadeLightSpotCutOff = glGetUniformLocation(shadeProgram, "lightSpotCutOff");
        //locShadeLightDir = glGetUniformLocation(shadeProgram, "lightDir");
        //locShadeSvetloADS = glGetUniformLocation(shadeProgram, "svetloADS");

       //locViewSun = glGetUniformLocation(shaderProgramSun, "view");
       //locProjectionSun = glGetUniformLocation(shaderProgramSun, "projection");
       //locLightPosSun = glGetUniformLocation(shaderProgramSun, "lightPos");
       //locLightDirSun = glGetUniformLocation(shaderProgramSun, "lightDir");
       //locLightSpotCutOffSun = glGetUniformLocation(shaderProgramSun, "lightSpotCutOff");
       //locLightTypeSun = glGetUniformLocation(shaderProgramSun, "lightType");

       ////modely a buffery
       //modelElephant = new OGLModelOBJ("/obj/ElephantBody.obj");
       //buffersElephant = modelElephant.getBuffers();

       //modelDuck= new OGLModelOBJ("/obj/ducky.obj");
       //buffersDuck = modelDuck.getBuffers();
       quad = QuadFactory.getQuad();

        buffers = GridFactory.generateGrid(100,100);
        //buffersStrip = GridFactory.generateGrid(100,100);
        //buffersSun = GridFactory.generateGridStrip(100, 100);

        //kamera a projektion matice
        camera = new Camera()
                .withPosition(new Vec3D(2, 2, 3)) // pozice pozorovatele
                .withAzimuth(5 / 4f * Math.PI) // otočení do strany o (180+45) stupňů v radiánech
                .withZenith(-1 / 5f * Math.PI); // otočení (90/5) stupňů dolů

        cameraLight = new Camera().withPosition(new Vec3D(-2, -2, 2));

        projection = new Mat4PerspRH(Math.PI / 3, LwjglWindow.HEIGHT / (float)LwjglWindow.WIDTH, 1, 20);

        //textra
       // try {
       //     texture1 = new OGLTexture2D("./textures/mramor2.jpg");
       // } catch (IOException e) {
       //     e.printStackTrace();
       // }

        viewer = new OGLTexture2D.Viewer();
        //nastaveni render target na velikost obrazovky aby se kvalita udrzela aj pri vzcetseni okna na celou obrazovku
        prvniRT = new OGLRenderTarget(800, 400);
        druhyRT = new OGLRenderTarget(400, 400);
       //druhyRT = new OGLRenderTarget(2560, 1440);
       //randomTexture = RandomTextureGenerator.getTexture();
        renderNahrani();
    }

    @Override
    public void display() {
        glEnable(GL_DEPTH_TEST);
        //nastaveni prerspektivy a vycisteni obrazovky
        perspective();
        //clearAndViewPort();

        //viewer.view(prvniRT.getColorTexture(0),-1,-0.5,0.5);
        // provedeni obrazovych operaci
        //renderPredani();
        renderPosun();
        renderVykres();
        //renderSunPos();
        deformVar++;
        //nastaveni nové pozice světla
        /*cameraLight = cameraLight.withPosition(new Vec3D(
                cameraLight.getPosition().getX()*Math.cos(otoceni)-cameraLight.getPosition().getY()*Math.sin(otoceni),
                cameraLight.getPosition().getX()*Math.sin(otoceni)+cameraLight.getPosition().getY()*Math.cos(otoceni),
                cameraLight.getPosition().getZ()
        ));*/
        //nastaveni vektoru světla aby mířil do pozice 0,0,0
        //lightDir = new Vec3D(cameraLight.getPosition().mul(-1));
        //zmena velikosti na velikos okna
        textRenderer.resize(LwjglWindow.WIDTH,LwjglWindow.HEIGHT);
        //viewer.view(prvniRT.getColorTexture(0),-1,-1,0.5);
        viewer.view(druhyRT.getColorTexture(0),-1,-0.5,0.5);
        //viewer.view(prvniRT.getDepthTexture(),-1,0,0.5);

        //System.out.println(new Col(prvniRT.getColorTexture().toBufferedImage().getRGB(400,10)));
        //System.out.println(new Col(druhyRT.getColorTexture().toBufferedImage().getRGB(700,350)));
        System.out.println(deformVar);
        //vykresleni a popis scen ktere vzniknou behem obrazovych operaci
        /*if(!debug){
            viewer.view(prvniRT.getColorTexture(0),-1,0,0.5);
            viewer.view(prvniRT.getColorTexture(1),-1, -0.5, 0.5);
            viewer.view(prvniRT.getColorTexture(2),-0.5, -0.5, 0.5);
            viewer.view(prvniRT.getColorTexture(3),-0.5, 0, 0.5);
            viewer.view(prvniRT.getDepthTexture(), -0.5, -1, 0.5);
            viewer.view(druhyRT.getColorTexture(), -1, -1, 0.5);

            textRenderer.addStr2D( (int)(LwjglWindow.WIDTH*(0f/4f)), (int)(LwjglWindow.HEIGHT*(2f/4f)), "Pozice");
            textRenderer.addStr2D( (int)(LwjglWindow.WIDTH*(1f/4f)), (int)(LwjglWindow.HEIGHT*(2f/4f)), "View Pozice");
            textRenderer.addStr2D( (int)(LwjglWindow.WIDTH*(0f/4f)), (int)(LwjglWindow.HEIGHT*(3f/4f)), "Textura");
            textRenderer.addStr2D( (int)(LwjglWindow.WIDTH*(1f/4f)), (int)(LwjglWindow.HEIGHT*(3f/4f)), "Barva");
            textRenderer.addStr2D( (int)(LwjglWindow.WIDTH*(0f/4f)), (int)(LwjglWindow.HEIGHT*(4f/4f)), "SSAO");
            textRenderer.addStr2D( (int)(LwjglWindow.WIDTH*(1f/4f)), (int)(LwjglWindow.HEIGHT*(4f/4f)), "Hloubka");

        }*/

        //text popisu a promenych
        textRenderer.addStr2D(LwjglWindow.WIDTH - 500, LwjglWindow.HEIGHT - 3, camera.getPosition().toString() + " azimut: "+ camera.getAzimuth() + " zenit: "+ camera.getZenith());
        //textRenderer.addStr2D(LwjglWindow.WIDTH - 500, LwjglWindow.HEIGHT - 15, "refrektor " + ((1-lightSpotCutOff)*100)+" %");
        textRenderer.addStr2D(10, 25, "Camera [WASD QE], Perspektive [P], Mod gridu [M] , Typ světla - Reflektor/Vsestrane [L], Uhel svetla [B- V+], Debug [G], Ambient [Y], Diffuse [X], Spekular [C]");
    }

    //zakresleni objektu do sceny
    private void renderNahrani(){
        glUseProgram(shaderProgramNahrani);

        druhyRT.bind();
        //glClearColor(0.0f, 0.1f, 0.0f, 1);
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(locViewNahrani, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjectionNahrani, false, projection.floatArray());
        glUniform1f(locDeformObjekt, deformVar);

        buffers.draw(GL_TRIANGLES, shaderProgramNahrani);
    }

    private void renderPredani(){
        glUseProgram(shaderProgramPredani);
        prvniRT.bind();
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        druhyRT.bindColorTexture(shaderProgramPredani, "positionTexture", 0, 0);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        //randomTexture.bind(shaderProgramSSAO, "randomTexture", 1);

        //glUniformMatrix4fv(locSsaoView, false, camera.getViewMatrix().floatArray());
        //glUniformMatrix4fv(locSsaoProjection, false, projection.floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgramPredani);
    }

    //vypocet ssao
    private void renderPosun(){
        glUseProgram(shaderProgramPosun);
        druhyRT.bind();
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        druhyRT.bindColorTexture(shaderProgramPosun, "positionTexture", 0, 0);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        //randomTexture.bind(shaderProgramSSAO, "randomTexture", 1);

        //glUniformMatrix4fv(locSsaoView, false, camera.getViewMatrix().floatArray());
        //glUniformMatrix4fv(locSsaoProjection, false, projection.floatArray());
        buffers.draw(GL_TRIANGLES, shaderProgramPosun);
    }
    //vypocet ssao


    //finalni vykresleni sceny
    private void renderVykres() {
        glUseProgram(shadeProgramVykres);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, LwjglWindow.WIDTH, LwjglWindow.HEIGHT);
        glClearColor(0.0f, 0.0f, 0.1f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        druhyRT.bindColorTexture(shadeProgramVykres, "positionTexture", 0, 0);

        glUniformMatrix4fv(locShadeView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locShadeProjection, false, projection.floatArray());

        buffers.draw(GL_TRIANGLES, shadeProgramVykres);
    }

    //vykresleni pozice svetla
    //private void renderSunPos(){
    //    glUseProgram(shaderProgramSun);
//
    //    glUniform3fv(locLightPosSun, ToFloatArray.convert(cameraLight.getPosition()));
    //    glUniform3fv(locLightDirSun, ToFloatArray.convert(lightDir));
    //    glUniform1f(locLightSpotCutOffSun, lightSpotCutOff);
    //    glUniform1f(locLightTypeSun, lightType);
    //    glUniformMatrix4fv(locViewSun, false, camera.getViewMatrix().floatArray());
    //    glUniformMatrix4fv(locProjectionSun, false, projection.floatArray());
//
    //    buffersSun.draw(GL_TRIANGLE_STRIP, shaderProgramSun);
    //}

    //vycisteni sceny
    public void clearAndViewPort(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glClearColor(0, .1f, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glViewport(0, 0, LwjglWindow.WIDTH, LwjglWindow.HEIGHT);
    }
    public void perspective(){
        if(!per){
            projection = new Mat4PerspRH(Math.PI / 3, LwjglWindow.HEIGHT / (float)LwjglWindow.WIDTH, 1, 20);
        }else {
            projection = new Mat4OrthoRH(
                    20*LwjglWindow.HEIGHT / (float)LwjglWindow.WIDTH,
                    20*LwjglWindow.HEIGHT / (float)LwjglWindow.WIDTH,
                    1,20);
        }
    }

    @Override
    public GLFWWindowSizeCallback getWsCallback() {
        return windowResCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cursorPosCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mouseButtonCallback;
    }

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    private double oldMx, oldMy;
    private boolean mousePressed;

    private final GLFWWindowSizeCallback windowResCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int width, int height) {
            LwjglWindow.HEIGHT = height;
            LwjglWindow.WIDTH = width;

        }
    };

    private final GLFWCursorPosCallback cursorPosCallback = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mousePressed) {
                camera = camera.addAzimuth(Math.PI * (oldMx - x) / LwjglWindow.WIDTH);
                camera = camera.addZenith(Math.PI * (y - oldMy) / LwjglWindow.HEIGHT);
                oldMx = x;
                oldMy = y;
            }
        }
    };

    private final GLFWMouseButtonCallback mouseButtonCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                double[] xPos = new double[1];
                double[] yPos = new double[1];
                glfwGetCursorPos(window, xPos, yPos);
                oldMx = xPos[0];
                oldMy = yPos[0];
                mousePressed = action == GLFW_PRESS;
            }
        }
    };

    private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    case GLFW_KEY_A :
                        camera = camera.left(0.1);
                        break;
                    case GLFW_KEY_D :
                        camera = camera.right(0.1);
                        break;
                    case GLFW_KEY_W :
                        camera = camera.forward(0.1);
                        break;
                    case GLFW_KEY_S :
                        camera = camera.backward(0.1);
                        break;
                    case GLFW_KEY_Q :
                        camera = camera.up(0.1);
                        break;
                    case GLFW_KEY_E :
                        camera = camera.down(0.1);
                        break;
                    case GLFW_KEY_P :
                        per=!per;
                        break;
                    case GLFW_KEY_M :
                        switchInt = (switchInt+1)%6;
                        break;
                    case GLFW_KEY_L :
                        lightType = (lightType+1)%2;
                        break;
                    case GLFW_KEY_B :
                        lightSpotCutOff =((lightSpotCutOff+ 0.002f)+1)%1;
                        break;
                    case GLFW_KEY_V :
                        lightSpotCutOff =((lightSpotCutOff- 0.002f)+1)%1;
                        break;
                    case GLFW_KEY_C :
                        svetloSpecular=(svetloSpecular+1)%2;
                        break;
                    case GLFW_KEY_X:
                        svetloDiffuse=(svetloDiffuse+1)%2;
                        break;
                    case GLFW_KEY_Z :
                        svetloAmbient=(svetloAmbient+1)%2;
                        break;
                    case GLFW_KEY_G :
                        debug=!debug;
                        break;
                }
            }
        }
    };
    public static void main(String[] args) {
        new LwjglWindow(new RendererHladina());
    }

}

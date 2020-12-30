package main.reseni;

import lwjglutils.*;
import main.AbstractRenderer;
import main.GridFactory;
import main.LwjglWindow;
import org.lwjgl.glfw.*;
import transforms.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class RendererHladina extends AbstractRenderer {

    private OGLBuffers buffers,buffersSun;
    private double otoceni =0.01;

    private int locViewSun, locProjectionSun, shaderProgramSun, locLightPosSun;
    private int shaderProgramZTriger,shadeProgramVykres, shaderProgramNahrani,shaderProgramPohyb;
    private int shaderProgramPosun;
    private int locShadeView,locShadeProjection, locZTrigerPoss,locZTrigerStrange,locPosunPosunZZZ,locPohybHrana;
    private int locShadeLightType,locShadeLightDir,locShadeLightSpotCutOff;
    private int locShadeLightPos,locShadeCameraPos,locShadeSvetloADS;
    private Camera camera;
    private Camera cameraLight;
    private Mat4 projection;
    private boolean per,debug,trig,trigMouse,trigRess;
    private OGLTexture2D.Viewer viewer;
    private OGLRenderTarget prvniRT, druhyRT;
    private Vec3D lightDir = new Vec3D(1,0,0);
    private Vec2D cursorPos = new Vec2D(0);
    private float lightSpotCutOff = 0.95f;
    private int hustota = 200;
    private int switchInt =0;
    private float lightType = 0;
    private int svetloAmbient=0,svetloDiffuse=0,svetloSpecular=0;

    private int hranaUp=0,hranaLeft=0,hranaDown=0,hranaRight=0;

    //uni hustota //
    //odemikani hran //
    //osvětleni //
    //popis ovlani  //
    //komenty
    @Override
    public void init() {
        OGLUtils.printOGLparameters();
        OGLUtils.printLWJLparameters();
        OGLUtils.printJAVAparameters();
        OGLUtils.shaderCheck();

        textRenderer = new OGLTextRenderer(LwjglWindow.WIDTH, LwjglWindow.HEIGHT);

        //definice lokaci shaderu
        shaderProgramNahrani = ShaderUtils.loadProgram("/hladina/nahrani");

        shaderProgramPosun = ShaderUtils.loadProgram("/hladina/posun1");
        shaderProgramPohyb = ShaderUtils.loadProgram("/hladina/pohyb1");

        shadeProgramVykres = ShaderUtils.loadProgram("/hladina/vykres");

        shaderProgramZTriger = ShaderUtils.loadProgram("/hladina/Z-Trigger");

        shaderProgramSun = ShaderUtils.loadProgram("/hladina/objSun");

        locShadeView = glGetUniformLocation(shadeProgramVykres, "view");
        locShadeProjection = glGetUniformLocation(shadeProgramVykres, "projection");
        locShadeLightPos = glGetUniformLocation(shadeProgramVykres, "lightPosition");
        locShadeCameraPos = glGetUniformLocation(shadeProgramVykres, "cameraPosition");
        locShadeLightType = glGetUniformLocation(shadeProgramVykres, "lightType");
        locShadeLightSpotCutOff = glGetUniformLocation(shadeProgramVykres, "lightSpotCutOff");
        locShadeLightDir = glGetUniformLocation(shadeProgramVykres, "lightDir");
        locShadeSvetloADS = glGetUniformLocation(shadeProgramVykres, "svetloADS");

        locZTrigerPoss = glGetUniformLocation(shaderProgramZTriger, "poss");
        locZTrigerStrange = glGetUniformLocation(shaderProgramZTriger, "strange");

        locPosunPosunZZZ = glGetUniformLocation(shaderProgramPosun, "posunZZZ");

        locPohybHrana = glGetUniformLocation(shaderProgramPohyb, "hrana");

        locViewSun = glGetUniformLocation(shaderProgramSun, "view");
        locProjectionSun = glGetUniformLocation(shaderProgramSun, "projection");
        locLightPosSun = glGetUniformLocation(shaderProgramSun, "lightPos");

        buffers = GridFactory.generateGrid(100,100);
        buffersSun = GridFactory.generateGrid(100,100);

        //kamera a projektion matice
        camera = new Camera()
                .withPosition(new Vec3D(8, 8, 5)) // pozice pozorovatele
                .withAzimuth(5 / 4f * Math.PI) // otočení do strany o (180+45) stupňů v radiánech
                .withZenith(-1 / 5f * Math.PI); // otočení (90/5) stupňů dolů

        cameraLight = new Camera().withPosition(new Vec3D(-8, -8, 5));

        projection = new Mat4PerspRH(Math.PI / 3, LwjglWindow.HEIGHT / (float)LwjglWindow.WIDTH, 1, 20);


        viewer = new OGLTexture2D.Viewer();
        //nastaveni render target na velikost obrazovky aby se kvalita udrzela aj pri vzcetseni okna na celou obrazovku
        prvniRT = new OGLRenderTarget(2560, 1440,2);
        druhyRT = new OGLRenderTarget(2560, 1440,2);

        //nahrani hladiny
        renderNahrani();
    }

    @Override
    public void display() {
        glEnable(GL_DEPTH_TEST);

        perspective();
        //vypocet posunu a posun
        renderPosun();
        renderPohyb();

        //triger hladiny
        if(trig){
            renderZTrigger(new Vec2D(0,0),-1,0.5f);
        }
        if(trigMouse){
            renderZTrigger(cursorPos,-0.25f,0.25f);
        }
        //reset hladiny
        if(trigRess){
            renderNahrani();
        }
        //vykresleni hladiny
        renderVykres();
        //vykresleni svetla
        renderSunPos();
        trig =false;
        trigMouse =false;
        trigRess =false;

        textRenderer.resize(LwjglWindow.WIDTH,LwjglWindow.HEIGHT);

        //debug a vypis textu
        if(!debug){
            viewer.view(druhyRT.getColorTexture(0),-1,-1,0.5);
            viewer.view(druhyRT.getColorTexture(1),-1,-0.5,0.5);
            viewer.view(prvniRT.getColorTexture(0),-0.5,-1,0.5);
            viewer.view(prvniRT.getColorTexture(1),-0.5,-0.5,0.5);

            textRenderer.addStr2D( (int)(LwjglWindow.WIDTH*(0f/4f)), (int)(LwjglWindow.HEIGHT*(3f/4f)), "Druhy posun");
            textRenderer.addStr2D( (int)(LwjglWindow.WIDTH*(1f/4f)), (int)(LwjglWindow.HEIGHT*(3f/4f)), "Prvni posun");
            textRenderer.addStr2D( (int)(LwjglWindow.WIDTH*(0f/4f)), (int)(LwjglWindow.HEIGHT*(4f/4f)), "Druhy pozice");
            textRenderer.addStr2D( (int)(LwjglWindow.WIDTH*(1f/4f)), (int)(LwjglWindow.HEIGHT*(4f/4f)), "Prvni pozice");
        }
        textRenderer.addStr2D(LwjglWindow.WIDTH - 500, LwjglWindow.HEIGHT - 3, camera.getPosition().toString() + " azimut: "+ camera.getAzimuth() + " zenit: "+ camera.getZenith());
        textRenderer.addStr2D(LwjglWindow.WIDTH - 500, LwjglWindow.HEIGHT - 15, "hustota " + hustota);
        textRenderer.addStr2D(10, 25, "Camera [WASD QE], Perspektive [P], Mod gridu [M], Debug [G], Ambient [Y], Diffuse [X], Spekular [C], Odemceni hran [8 6 2 4], Zamčení hran [0] - Odemceni [5]");
        textRenderer.addStr2D(10, 40, "Trignutí v bode (0,0) [H], Zmena hustoty [Kolecko mysi + -], Trignuti hladiny [RMB], Vyhlazeni hladiny [SPACE]");

    }

    //nahrani hladiny
    private void renderNahrani(){
        glUseProgram(shaderProgramNahrani);

        druhyRT.bind();

        buffers.draw(GL_TRIANGLES, shaderProgramNahrani);
    }

    //vypocet posunu
    private void renderPosun(){
        glUseProgram(shaderProgramPosun);
        prvniRT.bind();
        glDisable(GL_DEPTH_TEST);

        druhyRT.bindColorTexture(shaderProgramPosun, "positionTexture", 0, 0);
        druhyRT.bindColorTexture(shaderProgramPosun, "moveTexture", 1, 1);

        glUniform1f(locPosunPosunZZZ,1/(float)hustota);
        buffers.draw(GL_TRIANGLES, shaderProgramPosun);
    }

    //posun hladiny
    private void renderPohyb(){
        glUseProgram(shaderProgramPohyb);
        druhyRT.bind();
        glDisable(GL_DEPTH_TEST);

        prvniRT.bindColorTexture(shaderProgramPohyb, "positionTexture", 0, 0);
        prvniRT.bindColorTexture(shaderProgramPohyb, "moveTexture", 1, 1);

        glUniform4f(locPohybHrana,hranaUp,hranaRight,hranaDown,hranaLeft);
        buffers.draw(GL_TRIANGLES, shaderProgramPohyb);
    }

    //vykresleni hladiny
    private void renderVykres() {
        glUseProgram(shadeProgramVykres);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, LwjglWindow.WIDTH, LwjglWindow.HEIGHT);
        glClearColor(0.0f, 0.0f, 0.1f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        druhyRT.bindColorTexture(shadeProgramVykres, "positionTexture", 0, 0);

        glUniformMatrix4fv(locShadeView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locShadeProjection, false, projection.floatArray());
        glUniform3fv(locShadeLightDir, ToFloatArray.convert(lightDir));
        glUniform3fv(locShadeCameraPos, ToFloatArray.convert(camera.getPosition()));
        glUniform3fv(locShadeLightPos, ToFloatArray.convert(cameraLight.getPosition()));
        Point3D svetlo = new Point3D(svetloAmbient,svetloDiffuse,svetloSpecular);
        glUniform3fv(locShadeSvetloADS,ToFloatArray.convert(svetlo));

        switch (switchInt){
            case 0:
                buffers.draw(GL_TRIANGLES, shadeProgramVykres);
                break;
            case 1:
                buffers.draw(GL_LINES, shadeProgramVykres);
                break;
            case 2:
                buffers.draw(GL_POINTS, shadeProgramVykres);
                break;
        }
    }

    //triger hladiny
    private void renderZTrigger(Vec2D poss, float sila, float vzdalenost) {
        glUseProgram(shaderProgramZTriger);
        druhyRT.bind();
        glDisable(GL_DEPTH_TEST);

        druhyRT.bindColorTexture(shaderProgramZTriger, "positionTexture", 0, 0);
        druhyRT.bindColorTexture(shaderProgramZTriger, "moveTexture", 1, 1);

        glUniform2f(locZTrigerPoss, (float) poss.getX(),(float) poss.getY());
        glUniform2f(locZTrigerStrange, (float) vzdalenost,(float) sila);

        buffers.draw(GL_TRIANGLES, shaderProgramZTriger);
    }

    //vykresleni pozice svetla
    private void renderSunPos(){
        glUseProgram(shaderProgramSun);

        glUniform3fv(locLightPosSun, ToFloatArray.convert(cameraLight.getPosition()));
        glUniformMatrix4fv(locViewSun, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjectionSun, false, projection.floatArray());

        buffersSun.draw(GL_TRIANGLE_STRIP, shaderProgramSun);
    }

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

    @Override
    public GLFWScrollCallback getScrollCallback(){
      return scrollCallback;
    };

    private double oldMx, oldMy;
    private double oldMxRight, oldMyRight;
    private boolean mousePressed;
    private boolean mousePressedRight;

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
            if (mousePressedRight) {
                cursorPos = new Vec2D((x/LwjglWindow.WIDTH)*2-1,(y/LwjglWindow.HEIGHT)*2-1);
                oldMxRight = x;
                oldMyRight = y;
                trigMouse = true;
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
            if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                mousePressedRight = action == GLFW_PRESS;
            }

        }
    };
    private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double xoffset, double yoffset) {
            if(yoffset<0){
                hustota=(2000+hustota-10)%2000;
                if(hustota==0){
                    hustota=2000;
                }
            }
            if(yoffset>0){
                hustota=(2000+hustota+10)%2000;
                if(hustota==0){
                    hustota=10;
                }
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
                        switchInt = (switchInt+1)%3;
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
                    case GLFW_KEY_H :
                        trig =true;
                        break;
                    case GLFW_KEY_SPACE:
                        trigRess =true;
                        break;
                    case GLFW_KEY_KP_8:
                        hranaUp=(hranaUp+1)%2;
                        break;
                    case GLFW_KEY_KP_6:
                        hranaRight=(hranaRight+1)%2;
                        break;
                    case GLFW_KEY_KP_2:
                        hranaDown=(hranaDown+1)%2;
                        break;
                    case GLFW_KEY_KP_4:
                        hranaLeft=(hranaLeft+1)%2;
                        break;
                    case GLFW_KEY_KP_5:
                        hranaLeft=1;
                        hranaDown=1;
                        hranaRight=1;
                        hranaUp=1;
                        break;
                    case GLFW_KEY_KP_0:
                        hranaLeft=0;
                        hranaDown=0;
                        hranaRight=0;
                        hranaUp=0;
                        break;

                }
            }
        }
    };
    public static void main(String[] args) {
        new LwjglWindow(new RendererHladina());
    }

}

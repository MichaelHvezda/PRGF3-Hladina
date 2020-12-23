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

    private OGLBuffers buffers;
    private double otoceni =0.01;


    private int shaderProgramZTriger,shadeProgramVykres, shaderProgramNahrani,shaderProgramPohyb;
    private int shaderProgramPosun;
    private int locShadeView,locShadeProjection,locShadePoss;
    private Camera camera;
    private Camera cameraLight;
    private Mat4 projection;
    private boolean per,debug,trig;
    private OGLTexture2D.Viewer viewer;
    private OGLRenderTarget prvniRT, druhyRT;
    private Vec3D lightDir = new Vec3D(1,0,0);
    private float lightSpotCutOff = 0.95f;
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

        shaderProgramPosun = ShaderUtils.loadProgram("/hladina/posun1");
        shaderProgramPohyb = ShaderUtils.loadProgram("/hladina/pohyb1");

        shadeProgramVykres = ShaderUtils.loadProgram("/hladina/vykres");

        shaderProgramZTriger = ShaderUtils.loadProgram("/hladina/Z-Trigger");

        locShadeView = glGetUniformLocation(shadeProgramVykres, "view");
        locShadeProjection = glGetUniformLocation(shadeProgramVykres, "projection");
        locShadePoss = glGetUniformLocation(shaderProgramZTriger, "poss");



        buffers = GridFactory.generateGrid(500,500);

        //kamera a projektion matice
        camera = new Camera()
                .withPosition(new Vec3D(2, 2, 3)) // pozice pozorovatele
                .withAzimuth(5 / 4f * Math.PI) // otočení do strany o (180+45) stupňů v radiánech
                .withZenith(-1 / 5f * Math.PI); // otočení (90/5) stupňů dolů

        cameraLight = new Camera().withPosition(new Vec3D(-2, -2, 2));

        projection = new Mat4PerspRH(Math.PI / 3, LwjglWindow.HEIGHT / (float)LwjglWindow.WIDTH, 1, 20);


        viewer = new OGLTexture2D.Viewer();
        //nastaveni render target na velikost obrazovky aby se kvalita udrzela aj pri vzcetseni okna na celou obrazovku
        prvniRT = new OGLRenderTarget(2560, 1440,2);
        druhyRT = new OGLRenderTarget(2560, 1440,2);

        renderNahrani();
    }

    @Override
    public void display() {
        glEnable(GL_DEPTH_TEST);

        perspective();

        renderPosun();
        renderPohyb();
        if(trig){
            renderZTrigger(new Vec2D(0,0));
        }
        renderVykres();

        trig =false;
        textRenderer.resize(LwjglWindow.WIDTH,LwjglWindow.HEIGHT);
        viewer.view(druhyRT.getColorTexture(0),-1,-1,0.5);
        viewer.view(druhyRT.getColorTexture(1),-1,-0.5,0.5);
        viewer.view(prvniRT.getColorTexture(0),-0.5,-1,0.5);
        viewer.view(prvniRT.getColorTexture(1),-0.5,-0.5,0.5);
        textRenderer.addStr2D(50,50,camera.getPosition().toString());
    }

    //zakresleni objektu do sceny
    private void renderNahrani(){
        glUseProgram(shaderProgramNahrani);

        druhyRT.bind();

        buffers.draw(GL_TRIANGLES, shaderProgramNahrani);
    }


    private void renderPosun(){
        glUseProgram(shaderProgramPosun);
        prvniRT.bind();
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        druhyRT.bindColorTexture(shaderProgramPosun, "positionTexture", 0, 0);
        druhyRT.bindColorTexture(shaderProgramPosun, "moveTexture", 1, 1);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        buffers.draw(GL_TRIANGLES, shaderProgramPosun);
    }

    private void renderPohyb(){
        glUseProgram(shaderProgramPohyb);
        druhyRT.bind();
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        prvniRT.bindColorTexture(shaderProgramPohyb, "positionTexture", 0, 0);
        prvniRT.bindColorTexture(shaderProgramPohyb, "moveTexture", 1, 1);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        buffers.draw(GL_TRIANGLES, shaderProgramPohyb);
    }




    private void renderVykres() {
        glUseProgram(shadeProgramVykres);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, LwjglWindow.WIDTH, LwjglWindow.HEIGHT);
        glClearColor(0.0f, 0.0f, 0.1f, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        druhyRT.bindColorTexture(shadeProgramVykres, "positionTexture", 0, 0);

        glUniformMatrix4fv(locShadeView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locShadeProjection, false, projection.floatArray());

        buffers.draw(GL_LINES, shadeProgramVykres);
    }

    private void renderZTrigger(Vec2D poss) {
        glUseProgram(shaderProgramZTriger);
        druhyRT.bind();
        //glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        druhyRT.bindColorTexture(shaderProgramZTriger, "positionTexture", 0, 0);
        druhyRT.bindColorTexture(shaderProgramZTriger, "moveTexture", 1, 1);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glUniform2f(locShadePoss, (float) poss.getX(),(float) poss.getY());

        buffers.draw(GL_TRIANGLES, shaderProgramZTriger);
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
                    case GLFW_KEY_H :
                        trig =true;
                        break;

                }
            }
        }
    };
    public static void main(String[] args) {
        new LwjglWindow(new RendererHladina());
    }

}

package no.olj.joglLottery.gui;

import no.olj.joglLottery.primitives.Point3D;
import no.olj.joglLottery.primitives.LotteryColor;
import no.olj.joglLottery.primitives.Rotation;
import no.olj.joglLottery.primitives.drawable.Drawable;
import no.olj.joglLottery.primitives.drawable.DrawableLotteryTickets;
import no.olj.joglLottery.primitives.drawable.DrawableSphere;
import no.olj.joglLottery.util.Util;
import no.olj.joglLottery.lottery.Participant;

import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GL;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import com.sun.opengl.util.FPSAnimator;
import org.jouvieje.renderer.jsr231.GLRenderer_jsr231;
import org.jouvieje.model.Model;
import org.jouvieje.model.material.ModelTextureList;
import org.jouvieje.model.light.Lighting;
import org.jouvieje.model.renderer.pipeline.DirectModeRenderer;
import org.jouvieje.model.reader.obj.OBJReader;
import org.jouvieje.model.reader.ModelReaderSettings;
import org.jouvieje.model.ressources.MediaManager;
import org.jouvieje.visibility.IBoundingVolume;
import org.jouvieje.texture.TextureLoader;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 14.okt.2008
 */
public class LotteryPainter implements GLEventListener {

    private GLCanvas canvas;

    private GLU glu = new GLU();

    private double currentSpinSpeed = 0;
    private double currentAngle = 0;
    private final Point3D centre = new Point3D(0, 0, 0);

    private LotteryCanvasListener listener;
    private List<Participant> participants;

    private List<Drawable> drawableObjects = new ArrayList<Drawable>();
    private List<Drawable> drawableRotatingObects = new ArrayList<Drawable>();

    private int roundsBeforeStop;
    private double winnerAngle;
    private DrawableLotteryTickets drawableTickets;
    private boolean stopAtWinnersAngle;

    private boolean isDrawing;
    private double cameraAnimation = 0;                      // brukes ved animering av kameraposisjon
    private int millisBeforeFadeDown;
    private final static double initialSpinSpeed = 4;        // hastigheten under trekning
    private final int maxRoundsBeforeStop = 0;               // maks antall runder f�r man setter ned hastigheten
    private final static int baseMillisBeforeFadeDown = 2000;// antall millis f�r man setter ned hastigeten
    private final static double fadeDownVar = 0.5;           // variasjon p� tid f�r man setter ned hastigheten
    private final static double spinStopSpeed = 0.3;         // hastigheten man setter ned til
    private final static double spinStopFadeDown = 0.02;     // verdien farten minker med
    private final static long spinFadeTimer = 50;            // millis mellom hver gang farten minkes
    private final double cameraAnimationSpeed = 0.002;       // hastigheten kameraanimasjonen
//        private final double cameraAnimationSpeed = 0.03;

    private LoadingFrame loadingFrame;
    private FPSAnimator animator;
    private Model modelStaticObjects;
    private Model modelRotatingObjects;
    private String modelsPath = "resources\\";
    private String rotatingObjectsFileName = "rotatingObjects.obj";
    private String staticObjectsFileName = "staticObjects.obj";
    private GLRenderer_jsr231 gl_Renderer;

    public LotteryPainter(LotteryCanvasListener listener, List<Participant> participants) {
        this.listener = listener;
        this.participants = participants;

        GLCapabilities caps = new GLCapabilities();
        caps.setDoubleBuffered(true);
        caps.setHardwareAccelerated(true);

        canvas = new GLCanvas(caps);
        canvas.addGLEventListener(this);

        create3DObjects();
        animator = new FPSAnimator(canvas, 50);
        animator.start();

        loadingFrame = new LoadingFrame("Laster 3d-objekter...");
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        GL gl = glAutoDrawable.getGL();

        gl.glClearColor(0.2f, 0.4f, 1, 0);

        GLUquadric quadric = glu.gluNewQuadric();
        glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
        glu.gluQuadricTexture(quadric, true); // Create Texture Coords

        setupLights(gl);

//        gl.glEnable(GL.GL_COLOR_MATERIAL);
//        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glDepthFunc(GL.GL_LEQUAL);    // The Type Of Depth Testing
        gl.glEnable(GL.GL_DEPTH_TEST);    // Enable Depth Testing

        gl_Renderer = new GLRenderer_jsr231(null);

        try {
            loadModels();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void display(GLAutoDrawable glAutoDrawable) {
        checkSpinAndNotifyListenerIfFoundWinner();
        setRotationOnLotteryBoard();

        GL gl = glAutoDrawable.getGL();

        addCamera(glAutoDrawable, gl);
        drawDrawableObjects(gl);
        drawModels(glAutoDrawable, gl);

        gl.glFlush();
        currentAngle = Util.getTransformedAngle(currentAngle, currentSpinSpeed);
    }

    double testAngle = 0;

    public void displayX(GLAutoDrawable glAutoDrawable) {
        GL gl = glAutoDrawable.getGL();
        GLU glu = new GLU();
        GLUquadric quadric = glu.gluNewQuadric();
        int slices = 20;
        int stacks = 10;

        addCamera(glAutoDrawable, gl);

        testAngle = Util.getTransformedAngle(testAngle, 5);
        gl.glPushMatrix();
        gl.glTranslated(1, 0, 0);
        gl.glRotated(testAngle, 0, 1, 0);
        gl.glBegin(GL.GL_POLYGON);
        Util.setGlColor(gl, new LotteryColor(1, 0, 0));
        glu.gluSphere(quadric, 0.1, slices, stacks);
        gl.glPopMatrix();
        gl.glEnd();

        gl.glPushMatrix();
        gl.glTranslated(0, 0, 0);
        gl.glBegin(GL.GL_POLYGON);
        Util.setGlColor(gl, new LotteryColor(0, 1, 0));
        glu.gluSphere(quadric, 0.1, slices, stacks);
        gl.glPopMatrix();
        gl.glEnd();

        gl.glFlush();

        currentAngle = Util.getTransformedAngle(currentAngle, currentSpinSpeed);
    }

    public void startLottery(Participant winner) {
        if (isDrawing) {
            return;
        }
        isDrawing = true;
        stopAtWinnersAngle = false;
        currentSpinSpeed = initialSpinSpeed;
        roundsBeforeStop = (int) (Math.random() * (maxRoundsBeforeStop + 1));
        millisBeforeFadeDown = (int) (baseMillisBeforeFadeDown * (1 - fadeDownVar / 2 + Math.random() * fadeDownVar));
        winnerAngle = Util.getTransformedAngle(drawableTickets.getAngle(winner), 90);

        startSpinSpeedController();
    }

    public void updateGui() {
        animator.stop();
        drawableObjects.clear();
        drawableRotatingObects.clear();
        create3DObjects();
        animator.start();
    }

    public boolean isDrawing() {
        return isDrawing;
    }

    public GLCanvas getCanvas() {
        return canvas;
    }

    private void addCamera(GLAutoDrawable glAutoDrawable, GL gl) {
        int width = glAutoDrawable.getWidth();
        int height = glAutoDrawable.getHeight();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        double cameraX = 1.01 * Math.sin(cameraAnimation);
        double cameraY = -1.5 + 0.5 * Math.cos(cameraAnimation);
        double cameraZ = 2.8 + 0.3 * Math.cos(cameraAnimation/2);
        cameraAnimation += cameraAnimationSpeed;

        /* viewport transformation */
        gl.glViewport(0, 0, width, height);

        /* projection transformation */
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45, (double)width/(double)height, 0.1, 100);

        /* viewing and modeling transformation */
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
//        glu.gluLookAt(0.1, 0, 3, // eye
        glu.gluLookAt(cameraX, cameraY, cameraZ, // eye
                0, -0.1, 0,       // center
                0, 1, 0);      // up
    }

    private void drawDrawableObjects(GL gl) {
        for (Drawable drawable : drawableObjects) {
            drawable.draw(gl);
        }
    }

    private void drawModels(GLAutoDrawable glAutoDrawable, GL gl) {
        //Update the render with the current glAutoDrawable object
        gl_Renderer.update(glAutoDrawable);
        modelStaticObjects.medias.textures.loadTextures(modelStaticObjects);
//        modelStaticObjects.renderer.render();

        Util.rotate(gl, new Rotation(currentAngle, 0, 0, 1));
        modelStaticObjects.medias.textures.loadTextures(modelRotatingObjects);
//        modelRotatingObjects.renderer.render();
    }

    private void setupLights(GL gl) {
//        gl.glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);

        float light_ambient0[] = {0.3f, 0.3f, 0.35f, 1.0f};
//        float light_diffuse0[] = {1.5f, 1.5f, 1.3f, 1.0f};
        float light_diffuse0[] = {1.0f, 1.0f, 0.9f, 1.0f};
        float light_specular0[] = {2f, 2f, 2f, 1.0f};
        float light_position0[] = new float[]{0.9f, 0.6f, 1.5f, 1.0f};

        float light_ambient1[] = {0f, 0f, 0f, 1.0f};
        float light_diffuse1[] = {0.8f, 0.6f, 0.7f, 1.0f};
        float light_specular1[] = {1f, 1f, 1.5f, 1.0f};
        float light_position1[] = new float[]{-0.5f, 0.4f, 1.5f, 1.0f};

        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, light_ambient0, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, light_diffuse0, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, light_specular0, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position0, 0);

        gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, light_ambient1, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, light_diffuse1, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, light_specular1, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, light_position1, 0);

        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_LIGHT1);
    }

    private void addToRotatableLottery(Drawable drawable) {
        drawable.setRotation(new Rotation(0, 0, 0, 1));
        drawableRotatingObects.add(drawable);
    }

    private void checkSpinAndNotifyListenerIfFoundWinner() {
        if (!stopAtWinnersAngle || currentSpinSpeed == 0) {
            return;
        }

        double nextAngle = Util.getTransformedAngle(currentAngle, currentSpinSpeed);
//        System.out.println("Vinkelen er: " + currentAngle + ", neste vinkel : " + nextAngle+ ". Stoppe p�: " + winnerAngle);
        boolean steppedOver = currentAngle <= winnerAngle && nextAngle >= winnerAngle;
        boolean steppedOverAtBeginning = currentAngle > winnerAngle && nextAngle < currentAngle && nextAngle >= winnerAngle;

        if (steppedOver || steppedOverAtBeginning) {
            if (roundsBeforeStop > 0) {
                roundsBeforeStop--;
            } else {
                currentSpinSpeed = 0;
                finishedDrawing();
            }
        }
    }

    private void startSpinSpeedController() {
        Thread fadeDownThread = new Thread() {
            public void run() {
                try {
                    sleep(millisBeforeFadeDown);

                    while (currentSpinSpeed >= spinStopSpeed) {
                        currentSpinSpeed -= spinStopFadeDown;
                        if (currentSpinSpeed < spinStopFadeDown) {
                            currentSpinSpeed = spinStopFadeDown;
                        }

                        sleep(spinFadeTimer);
                    }
                    stopAtWinnersAngle = true;

                } catch (InterruptedException e) {/**/}
            }
        };
        fadeDownThread.start();
    }

    private void finishedDrawing() {
        listener.stoppedOnLotteryWinner();
        isDrawing = false;
    }

    private void setRotationOnLotteryBoard() {
        for (Drawable drawable : drawableRotatingObects) {
            drawable.setRotationAngle(currentAngle);
        }
    }

    /* -------------------- START Metoder for � lage 3d-objektene -------------------- */

    private void loadModels() throws IOException {
        modelStaticObjects = new Model(new MediaManager());
        modelRotatingObjects = new Model(new MediaManager());

        ModelReaderSettings settingsRotatingObjects = new ModelReaderSettings();
        ModelReaderSettings settingsStaticObjects = new ModelReaderSettings();

        settingsStaticObjects.modelFolder = modelsPath;
        settingsStaticObjects.modelName = staticObjectsFileName;
//        settingsStaticObjects.lighting = Lighting.LightingMode.PER_PIXEL;

        settingsRotatingObjects.modelFolder = modelsPath;
        settingsRotatingObjects.modelName = rotatingObjectsFileName;
//        settingsRotatingObjects.lighting = Lighting.LightingMode.PER_PIXEL;

        OBJReader objReader = new OBJReader();
        objReader.read(modelStaticObjects, settingsStaticObjects);
        objReader.read(modelRotatingObjects, settingsRotatingObjects);

//        int mode = IBoundingVolume.BOUNDINGVOLUME_INSIDE;
        modelStaticObjects.medias.textures = new ModelTextureList(new TextureLoader(gl_Renderer));
//        modelStaticObjects.renderer = new DirectModeRenderer (gl_Renderer, modelStaticObjects, mode);
        modelStaticObjects.setUseLight(true);

        modelRotatingObjects.medias.textures = new ModelTextureList(new TextureLoader(gl_Renderer));
//        modelRotatingObjects.renderer = new DirectModeRenderer (gl_Renderer, modelRotatingObjects, mode);
        modelRotatingObjects.setUseLight(true);

        loadingFrame.setVisible(false);
        loadingFrame = null;
    }

    private void create3DObjects() {
        createDrawableTickets();
//        createSphereAtLightPosition();
    }

    private void createDrawableTickets() {
        double radius = 1.0;
        Point3D position = new Point3D(centre.getX(), centre.getY(), 0.05);
        drawableTickets = new DrawableLotteryTickets(participants, position, radius);
        drawableObjects.add(drawableTickets);
        addToRotatableLottery(drawableTickets);
    }

    private void createSphereAtLightPosition() {
        double radius = 0.1;
        LotteryColor color = new LotteryColor(1, 1, 0);
        Point3D position = new Point3D(-1.0, 0.6, 2.0);
        DrawableSphere sphere = new DrawableSphere(radius, position, color);
        sphere.setSlices(32);
        drawableObjects.add(sphere);
    }

    /* -------------------- SLUTT Metoder for � lage 3d-objektene -------------------- */

    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {/**/}

    public void displayChanged(GLAutoDrawable glAutoDrawable, boolean b, boolean b1) {/**/}
}

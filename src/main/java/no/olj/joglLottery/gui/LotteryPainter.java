package no.olj.joglLottery.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import org.jouvieje.model.Model;
import org.jouvieje.model.light.Lighting;
import org.jouvieje.model.material.ModelTextureList;
import org.jouvieje.model.reader.ModelReaderSettings;
import org.jouvieje.model.reader.obj.OBJReader;
import org.jouvieje.model.renderer.IModelRenderer;
import org.jouvieje.model.renderer.pipeline.DirectModeRenderer;
import org.jouvieje.model.ressources.MediaManager;
import org.jouvieje.renderer.jsr231.GLRenderer_jsr231;
import org.jouvieje.texture.TextureLoader;
import org.jouvieje.visibility.IBoundingVolume;
import org.jouvieje.world.ModelInstance;

import com.sun.opengl.util.FPSAnimator;

import no.olj.joglLottery.lottery.DrawController;
import no.olj.joglLottery.lottery.DrawControllerListener;
import no.olj.joglLottery.lottery.Participant;
import no.olj.joglLottery.primitives.LotteryColor;
import no.olj.joglLottery.primitives.Point3D;
import no.olj.joglLottery.primitives.Rotation;
import no.olj.joglLottery.primitives.drawable.Drawable;
import no.olj.joglLottery.primitives.drawable.DrawableLotteryTickets;
import no.olj.joglLottery.primitives.drawable.DrawableSphere;
import no.olj.joglLottery.util.Util;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 14.okt.2008
 */
public class LotteryPainter implements GLEventListener, DrawControllerListener {

	private final LotteryCanvasListener listener;

	private final Point3D centre = new Point3D(0, 0, 0);

	private List<Drawable> drawableObjects = new ArrayList<Drawable>();
	private List<Drawable> drawableRotatingObects = new ArrayList<Drawable>();

	private DrawableLotteryTickets drawableTickets;
	private DrawController drawController;
	private List<Participant> participants;

	private double cameraAnimation = 0;                      // brukes ved animering av kameraposisjon
	private final double cameraAnimationSpeed = 0.002;       // hastigheten kameraanimasjonen

	private GLCanvas canvas;
	private GLU glu;
	private LoadingFrame loadingFrame;
    private FPSAnimator animator;
    private Model modelStaticObjects;
    private Model modelRotatingObjects;
    private String modelsPath = "resources/";
    private String rotatingObjectsFileName = "rotatingObjects.obj";
    private String staticObjectsFileName = "staticObjects.obj";
    private GLRenderer_jsr231 gl_Renderer;
	private IModelRenderer modelRenderer;

	public LotteryPainter(LotteryCanvasListener listener, List<Participant> participants) {
        this.listener = listener;
        this.participants = participants;

		drawController = new DrawController(this);

		glu = new GLU();
		
        GLCapabilities caps = new GLCapabilities();
        caps.setDoubleBuffered(true);
        caps.setHardwareAccelerated(true);

        canvas = new GLCanvas(caps);
        canvas.addGLEventListener(this);

        create3DObjects();
        animator = new FPSAnimator(canvas, 50);
        animator.start();

        loadingFrame = new LoadingFrame("Laster...");
    }

    @Override
	public void init(GLAutoDrawable glAutoDrawable) {
        GL gl = glAutoDrawable.getGL();

        gl.glClearColor(0.2f, 0.4f, 1, 0);

        GLUquadric quadric = glu.gluNewQuadric();
        glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
        glu.gluQuadricTexture(quadric, true); // Create Texture Coords

        setupLights(gl);

        gl.glEnable(GL.GL_COLOR_MATERIAL);
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glDepthFunc(GL.GL_LEQUAL);    // The Type Of Depth Testing
        gl.glEnable(GL.GL_DEPTH_TEST);    // Enable Depth Testing

        gl_Renderer = new GLRenderer_jsr231(null);

        try {
            loadModels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
	public void display(GLAutoDrawable glAutoDrawable) {
		drawController.updateAngle();
        setRotationOnLotteryBoard();

        GL gl = glAutoDrawable.getGL();

        addCamera(glAutoDrawable, gl);
        drawDrawableObjects(gl);
        drawModels(glAutoDrawable, gl);

        gl.glFlush();
    }

    double testAngle = 0;

    public void displayX(GLAutoDrawable glAutoDrawable) {
		drawController.updateAngle();
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
    }

    public void startLottery() {
		drawController.start();
    }

    public void initializeGui() {
        animator.stop();
        drawableObjects.clear();
        drawableRotatingObects.clear();
        create3DObjects();
        animator.start();
    }

    public boolean isDrawing() {
        return drawController.isDrawing();
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
		modelRenderer.render(modelStaticObjects);

		Util.rotate(gl, new Rotation(drawController.getAngle(), 0, 0, 1));
		modelRotatingObjects.medias.textures.loadTextures(modelRotatingObjects);
		modelRenderer.render(modelRotatingObjects);
	}

	private void setupLights(GL gl) {
        gl.glLightModeli(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);

        float light_ambient0[] = {0.3f, 0.3f, 0.35f, 1.0f};
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


	@Override
	public void drawCompleted() {
		Participant winner = drawableTickets.getWinnerAndAnimate(drawController.getAngle());
		listener.gotWinner(winner);
	}

	private void setRotationOnLotteryBoard() {
        for (Drawable drawable : drawableRotatingObects) {
            drawable.setRotationAngle(drawController.getAngle());
        }
    }

    /* -------------------- START Metoder for å lage 3d-objektene -------------------- */

    private void loadModels() throws IOException {
		int mode = IBoundingVolume.BoundingVolumeOptions.BoundingVolume_Box;
		modelRenderer = new DirectModeRenderer(gl_Renderer, mode, true);

		modelStaticObjects = loadModel(staticObjectsFileName);
		modelRotatingObjects = loadModel(rotatingObjectsFileName);

        loadingFrame.setVisible(false);
        loadingFrame = null;
    }

	private Model loadModel(String fileName) throws IOException {
		Model model = new Model(new MediaManager());

		ModelReaderSettings settings = new ModelReaderSettings();
		settings.modelFolder = modelsPath;
		settings.modelName = fileName;
		settings.postProcess.lighting = Lighting.LightingMode.PerPixel;
		settings.postProcess.acurateLighting = true;

		OBJReader objReader = new OBJReader();
		objReader.read(model, settings);

		model.medias.textures = new ModelTextureList(new TextureLoader(gl_Renderer));
//        model.setUseLight(true);

		ModelInstance instance = new ModelInstance(fileName, model);

		instance.renderer = modelRenderer;
		
		return model;
	}

	private void create3DObjects() {
        createDrawableTickets();
        createSphereAtLightPosition();
    }

    private void createDrawableTickets() {
        double radius = 1.0;
        Point3D position = new Point3D(centre.getX(), centre.getY(), 0.05);
        drawableTickets = new DrawableLotteryTickets(participants, position, radius, -90);
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

    /* -------------------- SLUTT Metoder for å lage 3d-objektene -------------------- */

    @Override
	public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {/**/}

    @Override
	public void displayChanged(GLAutoDrawable glAutoDrawable, boolean b, boolean b1) {/**/}
}

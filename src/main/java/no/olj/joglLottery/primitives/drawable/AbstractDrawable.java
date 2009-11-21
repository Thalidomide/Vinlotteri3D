package no.olj.joglLottery.primitives.drawable;

import no.olj.joglLottery.primitives.LotteryColor;
import no.olj.joglLottery.primitives.Point3D;
import no.olj.joglLottery.primitives.Rotation;
import no.olj.joglLottery.util.Util;

import javax.media.opengl.GL;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 15.okt.2008
 */
public abstract class AbstractDrawable implements Drawable {

    protected LotteryColor color;
    protected Rotation rotation = new Rotation(0, 0, 0, 0);
    protected Point3D position;

    private float[] mat_ambient = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] mat_diffuse = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] mat_specular = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] mat_emission = {0.0f, 0.0f, 0.0f, 1.0f};
    protected float shininess = 0.0f;
    private float specularFactor = 1;


    public AbstractDrawable(Point3D centre, LotteryColor color) {
        this.position = centre;
        this.color = color;
        setupMaterial();
    }

    public void draw(GL gl) {
        gl.glPushMatrix();
        Util.rotate(gl, rotation);
        Util.translate(gl, position);

        gl.glBegin(getGlBeginMode());

        Util.setGlColor(gl, color);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, mat_emission, 0);
        gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, shininess);

        doDraw(gl);

        gl.glEnd();
        gl.glPopMatrix();
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public void setRotationAngle(double angle) {
        rotation.setRotationAngle(angle);
    }

    public void setShininess(float shininess) {
        this.shininess = shininess;
    }

    protected int getGlBeginMode() {
        return GL.GL_POLYGON;
    }

    private void setupMaterial() {
        float ambientFactor = 0.4f;
        float red = (float) color.getRed();
        float green = (float) color.getGreen();
        float blue = (float) color.getBlue();

        mat_diffuse[0] = red;
        mat_diffuse[1] = green;
        mat_diffuse[2] = blue;

        mat_ambient[0] = red * ambientFactor;
        mat_ambient[1] = green * ambientFactor;
        mat_ambient[2] = blue * ambientFactor;

        setupSpecular();
    }

    protected void setSpecularFactor(float specularFactor) {
        this.specularFactor = specularFactor;
        setupSpecular();
    }

    private void setupSpecular() {
        double specularBrightenFactor = 0.2;

        mat_specular[0] = (float) ((color.getRed() + (1-mat_specular[0]) * specularBrightenFactor) * specularFactor);
        mat_specular[1] = (float) ((color.getGreen() + (1-mat_specular[1]) * specularBrightenFactor) * specularFactor);
        mat_specular[2] = (float) ((color.getBlue() + (1-mat_specular[2]) * specularBrightenFactor) * specularFactor);
    }

    protected abstract void doDraw(GL gl);
}

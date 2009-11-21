package no.olj.joglLottery.primitives.drawable;

import no.olj.joglLottery.primitives.LotteryColor;
import no.olj.joglLottery.primitives.Point3D;
import no.olj.joglLottery.primitives.Rotation;
import no.olj.joglLottery.util.Util;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 11.okt.2008
 */
public class DrawableCylinder extends AbstractDrawable {

    private double height;
    private double radius;
    private GLU glu;
    private GLUquadric quadric;
    private int slices = 40;
    private int stacks = 40;

    public DrawableCylinder(double height, double radius, Point3D position, LotteryColor color) {
        super(position, color);
        this.height = height;
        this.radius = radius;

        glu = new GLU();
        quadric = glu.gluNewQuadric();
    }

    protected void doDraw(GL gl) {
        Util.setGlColor(gl, color);
        glu.gluCylinder(quadric, radius, radius, height, slices, stacks);
    }

    public void setSlices(int slices) {
        this.slices = slices;
    }

    public void setStacks(int stacks) {
        this.stacks = stacks;
    }
}

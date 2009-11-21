package no.olj.joglLottery.primitives.drawable;

import no.olj.joglLottery.util.Util;
import no.olj.joglLottery.primitives.Point3D;
import no.olj.joglLottery.primitives.LotteryColor;
import no.olj.joglLottery.primitives.Rotation;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 11.okt.2008
 */
public class DrawableSphere extends AbstractDrawable {

    private double radius;
    private GLU glu;
    private GLUquadric quadric;
    private int slices = 10;
    private int stacks = 6;

    public DrawableSphere(double radius, Point3D position, LotteryColor color) {
        super(position, color);
        this.radius = radius;

        setSpecularFactor(0f);
        setShininess(50);
        glu = new GLU();
        quadric = glu.gluNewQuadric();
    }

    protected void doDraw(GL gl) {
        glu.gluSphere(quadric, radius, slices, stacks);
    }

    public void setSlices(int slices) {
        this.slices = slices;
    }

    public void setStacks(int stacks) {
        this.stacks = stacks;
    }
}

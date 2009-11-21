package no.olj.joglLottery.primitives;

import no.olj.joglLottery.util.Util;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 12.okt.2008
 */
public class Rotation {

    private double angle;
    private double x, y, z;

    public Rotation(Rotation rotation) {
        this.angle = rotation.angle;
        this.x = rotation.x;
        this.y = rotation.y;
        this.z = rotation.z;
    }

    public Rotation(double angle, double x, double y, double z) {
        this.angle = angle;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRotationAngle(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}

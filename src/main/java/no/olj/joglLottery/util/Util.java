package no.olj.joglLottery.util;

import no.olj.joglLottery.primitives.LotteryColor;
import no.olj.joglLottery.primitives.Rotation;
import no.olj.joglLottery.primitives.Point3D;

import javax.media.opengl.GL;
import java.awt.Color;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 11.okt.2008
 */
public class Util {

    public static void setGlColor(GL gl, LotteryColor color) {
        gl.glColor3d(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static double getRandom(double min, double max) {
        double interval = max - min;
        return (Math.random() * interval + min);
    }

    public static double getTransformedAngle(double angle, double rotate) {
        return (angle + rotate) % 360;
    }

    public static void rotate(GL gl, Rotation rotation) {
        gl.glRotated(rotation.getAngle(), rotation.getX(), rotation.getY(), rotation.getZ());
    }

    public static void translate(GL gl, Point3D translation) {
        gl.glTranslated(translation.getX(), translation.getY(), translation.getZ());
    }

    public static LotteryColor convertColor(Color color) {
        double red = (double)color.getRed()/255.0;
        double green = (double)color.getGreen()/255.0;
        double blue = (double)color.getBlue()/255.0;
        return new LotteryColor(red, green, blue);
    }
}

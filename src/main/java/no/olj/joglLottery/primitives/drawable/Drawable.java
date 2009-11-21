package no.olj.joglLottery.primitives.drawable;

import no.olj.joglLottery.primitives.Rotation;

import javax.media.opengl.GL;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 11.okt.2008
 */
public interface Drawable {

    void draw(GL gl);

//    int getGLMode();

    void setRotation(Rotation rotation);
    void setRotationAngle(double angle);
}

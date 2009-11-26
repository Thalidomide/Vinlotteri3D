package no.olj.joglLottery.primitives.drawable;

import no.olj.joglLottery.primitives.LotteryColor;
import no.olj.joglLottery.primitives.Point3D;
import no.olj.joglLottery.util.Util;

import javax.media.opengl.GL;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 11.okt.2008
 */
public class DrawableArc extends AbstractDrawable {

    private List<Point3D> pointPositions;
    private double createNewPointEveryNDegree = 5;

    public DrawableArc(Point3D centre, double startAngle, double angle, double radius, LotteryColor color) {
        super(centre, color);

        setShininess(90);

        createPoints(startAngle, angle, radius);
    }

    @Override
	protected void doDraw(GL gl) {
		color.updateColor();
		Util.setGlColor(gl, color);
        for (Point3D point : pointPositions) {
            gl.glNormal3d(point.getX()/10, point.getY()/10, 1.2);
            gl.glVertex3d(point.getX(), point.getY(), point.getZ());
        }
    }

    private void createPoints(double startAngle, double angle, double radius) {
        pointPositions = new ArrayList<Point3D>();

//		System.out.println("Startangle, angle: " + startAngle + ", " + angle);
        int inBetweens = (int)((angle / (float)createNewPointEveryNDegree) - 1);
//        System.out.println("Antall punkter som skal lages: " + inBetweens);

        pointPositions.add(position);
        pointPositions.add(getPointPosition(position, startAngle, radius));

        if (inBetweens > 0) {
            for (int i = 0; i < inBetweens; i++) {
                double currentAngle = startAngle + (i + 1) * createNewPointEveryNDegree;
                pointPositions.add(getPointPosition(position, currentAngle, radius));
            }
        }

        //Last
        pointPositions.add(getPointPosition(position, startAngle + angle, radius));
    }

    private Point3D getPointPosition(Point3D centre, double angle, double radius) {
//        System.out.println("Lage nytt punkt i vinkelen: " + angle);
        double x = centre.getX() + radius * Math.cos(Math.toRadians(-angle));
        double y = centre.getY() + radius * Math.sin(Math.toRadians(-angle));

        return new Point3D(x, y, centre.getZ());
    }
}

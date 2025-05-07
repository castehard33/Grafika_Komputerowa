package gk.lab3;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.ArrayList;

/**
 * A panel that displays a two-dimensional animation that is drawn
 * using subroutines to implement hierarchical modeling.  There is a
 * checkbox that turns the animation on and off.
 */
public class SubroutineHierarchy extends JPanel {

	public static void main(String[] args) {
		JFrame window = new JFrame("Subroutine Hierarchy");
		window.setContentPane( new SubroutineHierarchy() );
		window.pack();
		window.setLocation(100,60);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	//-------------------------- Create the world and implement the animation --------------

	private final static int WIDTH = 800;   // The preferred size for the drawing area.
	private final static int HEIGHT = 600;

	private final static double X_LEFT = -4;    // The xy limits for the coordinate system.
	private final static double X_RIGHT = 4;
	private final static double Y_BOTTOM = -3;
	private final static double Y_TOP = 3;

	private final static Color BACKGROUND = Color.WHITE; // Initial background color for drawing.

	private float pixelSize;  // The size of a pixel in drawing coordinates.

	private int frameNumber = 0;  // Current frame number, goes up by one in each frame.

	/**
	 *  Responsible for drawing the entire scene.  The display is filled with the background
	 *  color before this method is called.
	 */
	private void drawWorld(Graphics2D g2) {

		double wheelRotationAngle = frameNumber * 2.0; // degrees

		// Seesaw 1 parameters
		double s1_fulcrumX = -2.0, s1_fulcrumBaseY = 0.2;
		double s1_fulcrumW = 0.5, s1_fulcrumH = 0.8;
		double s1_beamL = 3.0, s1_beamW = 0.3;
		double s1_wheelD = 1.0;
		double s1_tilt = 15;
		Color s1_fulcrumColor = new Color(128,0,128); // Purple

		drawSeesaw(g2, s1_fulcrumColor, s1_fulcrumX, s1_fulcrumBaseY, s1_fulcrumW, s1_fulcrumH,
				   Color.RED, s1_beamL, s1_beamW,
				   Color.BLACK, s1_wheelD, // wheelColor is black, s1_wheelD is diameter
				   s1_tilt, wheelRotationAngle);

		// Seesaw 2 parameters
		double s2_fulcrumX = 2.0, s2_fulcrumBaseY = 1.0;
		double s2_fulcrumW = 0.4, s2_fulcrumH = 0.5;
		double s2_beamL = 2.0, s2_beamW = 0.25;
		double s2_wheelD = 0.7;
		double s2_tilt = 20;
		Color s2_fulcrumColor = Color.GREEN;

		drawSeesaw(g2, s2_fulcrumColor, s2_fulcrumX, s2_fulcrumBaseY, s2_fulcrumW, s2_fulcrumH,
				   Color.RED, s2_beamL, s2_beamW,
				   Color.BLACK, s2_wheelD,
				   s2_tilt, wheelRotationAngle);

		// Seesaw 3 parameters
		double s3_fulcrumX = 0.0, s3_fulcrumBaseY = -2.0;
		double s3_fulcrumW = 0.6, s3_fulcrumH = 0.5;
		double s3_beamL = 4.0, s3_beamW = 0.4;
		double s3_wheelD = 1.2;
		double s3_tilt = 10;
		Color s3_fulcrumColor = Color.BLUE;

		drawSeesaw(g2, s3_fulcrumColor, s3_fulcrumX, s3_fulcrumBaseY, s3_fulcrumW, s3_fulcrumH,
				   Color.RED, s3_beamL, s3_beamW,
				   Color.BLACK, s3_wheelD,
				   s3_tilt, wheelRotationAngle);

	} // end drawWorld()


	/**
	 * This method is called before each frame is drawn.
	 */
	private void updateFrame() {
		frameNumber++;
	}


	// Method to draw a complete seesaw
	private void drawSeesaw(Graphics2D g2,
							Color fulcrumColor, double fulcrumBaseX, double fulcrumBaseY, double fulcrumWidth, double fulcrumHeight,
							Color beamColor, double beamLength, double beamWidth,
							Color wheelColor, double wheelDiameter,
							double tiltAngle, double wheelRotationAngle) {

		AffineTransform worldTransform = g2.getTransform(); // Save state before this seesaw
		Color originalColor = g2.getColor();

		// 1. Draw Fulcrum
		// Transform for fulcrum is relative to worldTransform
		g2.translate(fulcrumBaseX, fulcrumBaseY);
		g2.scale(fulcrumWidth, fulcrumHeight);
		g2.setColor(fulcrumColor);
		filledTriangle(g2); // Base at (0,0), tip at (0,1) in its own scaled coords
		g2.setTransform(worldTransform); // Restore to worldTransform

		// 2. Group: Beam and Wheels (they tilt together relative to fulcrum tip)
		// Transform for this group is relative to worldTransform
		g2.translate(fulcrumBaseX, fulcrumBaseY + fulcrumHeight); // Move to fulcrum tip
		g2.rotate(Math.toRadians(tiltAngle)); // Rotate group around fulcrum tip
		// Current transform is now the "group transform" (pivot point, tilted)

		AffineTransform groupTransform = g2.getTransform(); // Save this group transform

		// Draw Beam (centered at the pivot point, relative to groupTransform)
		g2.scale(beamLength, beamWidth);
		g2.setColor(beamColor);
		filledRect(g2); // filledRect is drawn centered at (0,0)
		g2.setTransform(groupTransform); // Restore to groupTransform (undoes beam's scale)

		// Draw Left Wheel (relative to groupTransform)
		g2.translate(-beamLength / 2, 0); // Move to left end of beam along the tilted beam's x-axis
		g2.rotate(Math.toRadians(wheelRotationAngle));
		g2.scale(wheelDiameter, wheelDiameter); // wheelPolygon draws diameter 1
		g2.setColor(wheelColor);
		wheelPolygon(g2, 12); // 12 sides for the wheel
		g2.setTransform(groupTransform); // Restore to groupTransform (undoes left wheel's transforms)

		// Draw Right Wheel (relative to groupTransform)
		g2.translate(beamLength / 2, 0); // Move to right end of beam
		g2.rotate(Math.toRadians(wheelRotationAngle));
		g2.scale(wheelDiameter, wheelDiameter);
		g2.setColor(wheelColor);
		wheelPolygon(g2, 12); // 12 sides for the wheel
		// g2.setTransform(groupTransform); // No need to restore here, as we restore worldTransform next

		// Restore original transform and color from before this seesaw was drawn
		g2.setTransform(worldTransform);
		g2.setColor(originalColor);
	}


	//------------------- Some methods for drawing basic shapes. ----------------

	private static void line(Graphics2D g2) { // Draws a line from (-0.5,0) to (0.5,0)
		g2.draw( new Line2D.Double( -0.5,0, 0.5,0) );
	}

	private static void rect(Graphics2D g2) { // Strokes a square, size = 1, center = (0,0)
		g2.draw(new Rectangle2D.Double(-0.5,-0.5,1,1));
	}

	private static void filledRect(Graphics2D g2) { // Fills a square, size = 1, center = (0,0)
		g2.fill(new Rectangle2D.Double(-0.5,-0.5,1,1));
	}

	private static void circle(Graphics2D g2) { // Strokes a circle, diameter = 1, center = (0,0)
		g2.draw(new Ellipse2D.Double(-0.5,-0.5,1,1));
	}

	private static void filledCircle(Graphics2D g2) { // Fills a circle, diameter = 1, center = (0,0)
		g2.draw(new Ellipse2D.Double(-0.5,-0.5,1,1)); // Note: original was g2.draw, should be g2.fill for filledCircle
	}

	private static void filledTriangle(Graphics2D g2) { // width = 1, height = 1, center of base is at (0,0), tip at (0,1)
		Path2D path = new Path2D.Double();
		path.moveTo(-0.5,0); // Base vertex 1
		path.lineTo(0.5,0);  // Base vertex 2
		path.lineTo(0,1);    // Tip vertex
		path.closePath();
		g2.fill(path);
	}

	// Draws a polygon wheel of diameter 1, centered at (0,0), with specified number of sides/spokes
	private static void wheelPolygon(Graphics2D g2, int sides) {
	    Path2D path = new Path2D.Double();
	    // Spokes
	    for (int i = 0; i < sides; i++) {
	        double angle = 2 * Math.PI * i / sides;
	        path.moveTo(0,0); // center
	        path.lineTo(0.5 * Math.cos(angle), 0.5 * Math.sin(angle)); // to vertex (radius 0.5)
	    }
	    // Polygon Rim
	    path.moveTo(0.5 * Math.cos(0), 0.5 * Math.sin(0)); // Start at first vertex
	    for (int i = 1; i <= sides; i++) { // Loop through sides + 1 to include the closing segment
	        double angle = 2 * Math.PI * i / sides;
	        path.lineTo(0.5 * Math.cos(angle), 0.5 * Math.sin(angle));
	    }
	    g2.draw(path); // Draw the combined path of spokes and rim
	}


	//--------------------------------- Implementation ------------------------------------

	private JPanel display;  // The JPanel in which the scene is drawn.

	/**
	 * Constructor creates the scene graph data structure that represents the
	 * scene that is to be drawn in this panel, by calling createWorld().
	 * It also sets the preferred size of the panel to the constants WIDTH and HEIGHT.
	 * And it creates a timer to drive the animation.
	 */
	public SubroutineHierarchy() {
		display = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D)g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				applyLimits(g2, X_LEFT, X_RIGHT, Y_TOP, Y_BOTTOM, false);
				g2.setStroke( new BasicStroke(pixelSize) ); // set default line width to one pixel.
				drawWorld(g2);  // draw the world
			}
		};
		display.setPreferredSize( new Dimension(WIDTH,HEIGHT));
		display.setBackground( BACKGROUND );
		final Timer timer = new Timer(17,new ActionListener() { // about 60 frames per second
			public void actionPerformed(ActionEvent evt) {
				updateFrame();
				repaint();
			}
		});
		final JCheckBox animationCheck = new JCheckBox("Run Animation");
		animationCheck.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (animationCheck.isSelected()) {
					if ( ! timer.isRunning() )
						timer.start();
				}
				else {
					if ( timer.isRunning() )
						timer.stop();
				}
			}
		});
		JPanel top = new JPanel();
		top.add(animationCheck);
		setLayout(new BorderLayout(5,5));
		setBackground(Color.DARK_GRAY);
		setBorder( BorderFactory.createLineBorder(Color.DARK_GRAY,4) );
		add(top,BorderLayout.NORTH);
		add(display,BorderLayout.CENTER);
	}



	/**
	 * Applies a coordinate transform to a Graphics2D graphics context.  The upper left corner of
	 * the viewport where the graphics context draws is assumed to be (0,0).  The coordinate
	 * transform will make a requested rectangle visible in the drawing area.  The requested
	 * limits might be adjusted to preserve the aspect ratio.  (This method sets the global variable
	 * pixelSize to be equal to the size of one pixel in the transformed coordinate system.)
	 * @param g2 The drawing context whose transform will be set.
	 * @param xleft requested x-value at left of drawing area.
	 * @param xright requested x-value at right of drawing area.
	 * @param ytop requested y-value at top of drawing area.
	 * @param ybottom requested y-value at bottom of drawing area; can be less than ytop, which will
	 *     reverse the orientation of the y-axis to make the positive direction point upwards.
	 * @param preserveAspect if preserveAspect is false, then the requested rectangle will exactly fill
	 * the viewport; if it is true, then the limits will be expanded in one direction, horizontally or
	 * vertically, to make the aspect ratio of the displayed rectangle match the aspect ratio of the
	 * viewport.  Note that when preserveAspect is false, the units of measure in the horizontal and
	 * vertical directions will be different.
	 */
	private void applyLimits(Graphics2D g2, double xleft, double xright,
			double ytop, double ybottom, boolean preserveAspect) {
		int width = display.getWidth();   // The width of the drawing area, in pixels.
		int height = display.getHeight(); // The height of the drawing area, in pixels.
		if (preserveAspect) {
			// Adjust the limits to match the aspect ratio of the drawing area.
			double displayAspect = Math.abs((double)height / width);
			double requestedAspect = Math.abs(( ybottom-ytop ) / ( xright-xleft ));
			if (displayAspect > requestedAspect) {
				double excess = (ybottom-ytop) * (displayAspect/requestedAspect - 1);
				ybottom += excess/2;
				ytop -= excess/2;
			}
			else if (displayAspect < requestedAspect) {
				double excess = (xright-xleft) * (requestedAspect/displayAspect - 1);
				xright += excess/2;
				xleft -= excess/2;
			}
		}
		double pixelWidth = Math.abs(( xright - xleft ) / width);
		double pixelHeight = Math.abs(( ybottom - ytop ) / height);
		pixelSize = (float)Math.min(pixelWidth,pixelHeight);
		g2.scale( width / (xright-xleft), height / (ybottom-ytop) );
		g2.translate( -xleft, -ytop );
	}

}
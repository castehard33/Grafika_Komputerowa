package gk.lab3;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.ArrayList;

/**
 * A panel that displays a two-dimensional animation that is constructed
 * using a scene graph to implement hierarchical modeling.  There is a
 * checkbox that turns the animation on and off.
 */
public class SceneGraph extends JPanel {

	public static void main(String[] args) {
		JFrame window = new JFrame("Scene Graph 2D");
		window.setContentPane( new SceneGraph() );
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

	private CompoundObject world; // SceneGraphNode representing the entire scene.

	// Global variables for animated wheel objects
	private TransformedObject wheel1_L, wheel1_R;
	private TransformedObject wheel2_L, wheel2_R;
	private TransformedObject wheel3_L, wheel3_R;

	/**
	 *  Builds the data structure that represents the entire picture.
	 */
	private void createWorld() {

		world = new CompoundObject();  // Root node for the scene graph.

		// Define parameters for each seesaw
		// Seesaw 1 (Purple Fulcrum, Top-Left)
		double s1_fulcrumX = -2.0, s1_fulcrumBaseY = 0.2;
		double s1_fulcrumW = 0.5, s1_fulcrumH = 0.8; // Fulcrum tip at (s1_fulcrumX, s1_fulcrumBaseY + s1_fulcrumH)
		double s1_beamL = 3.0, s1_beamW = 0.3;
		double s1_wheelD = 1.0;
		double s1_tilt = 15; // degrees
		Color s1_fulcrumColor = new Color(128,0,128); // Purple

		// Seesaw 2 (Green Fulcrum, Top-Right)
		double s2_fulcrumX = 2.0, s2_fulcrumBaseY = 1.0;
		double s2_fulcrumW = 0.4, s2_fulcrumH = 0.5; // Tip at (2.0, 1.5)
		double s2_beamL = 2.0, s2_beamW = 0.25;
		double s2_wheelD = 0.7;
		double s2_tilt = 20; // degrees
		Color s2_fulcrumColor = Color.GREEN;

		// Seesaw 3 (Blue Fulcrum, Bottom-Center)
		double s3_fulcrumX = 0.0, s3_fulcrumBaseY = -2.0;
		double s3_fulcrumW = 0.6, s3_fulcrumH = 0.5; // Tip at (0.0, -1.5)
		double s3_beamL = 4.0, s3_beamW = 0.4;
		double s3_wheelD = 1.2;
		double s3_tilt = 10; // degrees
		Color s3_fulcrumColor = Color.BLUE;


		// Create Seesaw 1
		TransformedObject fulcrum1 = new TransformedObject(filledTriangle)
			.setColor(s1_fulcrumColor)
			.setScale(s1_fulcrumW, s1_fulcrumH)
			.setTranslation(s1_fulcrumX, s1_fulcrumBaseY);
		world.add(fulcrum1);

		CompoundObject beamAndWheels1 = new CompoundObject();
		TransformedObject beam1 = new TransformedObject(filledRect)
			.setColor(Color.RED)
			.setScale(s1_beamL, s1_beamW);
		beamAndWheels1.add(beam1);

		wheel1_L = new TransformedObject(wheelNode) // wheelNode draws a diameter 1 wheel
			.setScale(s1_wheelD, s1_wheelD)
			.setTranslation(-s1_beamL / 2, 0);
		beamAndWheels1.add(wheel1_L);

		wheel1_R = new TransformedObject(wheelNode)
			.setScale(s1_wheelD, s1_wheelD)
			.setTranslation(s1_beamL / 2, 0);
		beamAndWheels1.add(wheel1_R);

		TransformedObject tiltedAssembly1 = new TransformedObject(beamAndWheels1)
			.setTranslation(s1_fulcrumX, s1_fulcrumBaseY + s1_fulcrumH) // Pivot at fulcrum tip
			.setRotation(s1_tilt);
		world.add(tiltedAssembly1);


		// Create Seesaw 2
		TransformedObject fulcrum2 = new TransformedObject(filledTriangle)
			.setColor(s2_fulcrumColor)
			.setScale(s2_fulcrumW, s2_fulcrumH)
			.setTranslation(s2_fulcrumX, s2_fulcrumBaseY);
		world.add(fulcrum2);

		CompoundObject beamAndWheels2 = new CompoundObject();
		TransformedObject beam2 = new TransformedObject(filledRect)
			.setColor(Color.RED)
			.setScale(s2_beamL, s2_beamW);
		beamAndWheels2.add(beam2);

		wheel2_L = new TransformedObject(wheelNode)
			.setScale(s2_wheelD, s2_wheelD)
			.setTranslation(-s2_beamL / 2, 0);
		beamAndWheels2.add(wheel2_L);

		wheel2_R = new TransformedObject(wheelNode)
			.setScale(s2_wheelD, s2_wheelD)
			.setTranslation(s2_beamL / 2, 0);
		beamAndWheels2.add(wheel2_R);

		TransformedObject tiltedAssembly2 = new TransformedObject(beamAndWheels2)
			.setTranslation(s2_fulcrumX, s2_fulcrumBaseY + s2_fulcrumH)
			.setRotation(s2_tilt);
		world.add(tiltedAssembly2);


		// Create Seesaw 3
		TransformedObject fulcrum3 = new TransformedObject(filledTriangle)
			.setColor(s3_fulcrumColor)
			.setScale(s3_fulcrumW, s3_fulcrumH)
			.setTranslation(s3_fulcrumX, s3_fulcrumBaseY);
		world.add(fulcrum3);

		CompoundObject beamAndWheels3 = new CompoundObject();
		TransformedObject beam3 = new TransformedObject(filledRect)
			.setColor(Color.RED)
			.setScale(s3_beamL, s3_beamW);
		beamAndWheels3.add(beam3);

		wheel3_L = new TransformedObject(wheelNode)
			.setScale(s3_wheelD, s3_wheelD)
			.setTranslation(-s3_beamL / 2, 0);
		beamAndWheels3.add(wheel3_L);

		wheel3_R = new TransformedObject(wheelNode)
			.setScale(s3_wheelD, s3_wheelD)
			.setTranslation(s3_beamL / 2, 0);
		beamAndWheels3.add(wheel3_R);

		TransformedObject tiltedAssembly3 = new TransformedObject(beamAndWheels3)
			.setTranslation(s3_fulcrumX, s3_fulcrumBaseY + s3_fulcrumH)
			.setRotation(s3_tilt);
		world.add(tiltedAssembly3);

	} // end createWorld()


	/**
	 * This method is called just before each frame is drawn.  It updates the modeling
	 * transformations of the objects in the scene that are animated.
	 */
	public void updateFrame() {
		frameNumber++;
		double rotationSpeed = 2.0; // degrees per frame
		double currentRotation = frameNumber * rotationSpeed;

		if (wheel1_L != null) wheel1_L.setRotation(currentRotation);
		if (wheel1_R != null) wheel1_R.setRotation(currentRotation);
		if (wheel2_L != null) wheel2_L.setRotation(currentRotation);
		if (wheel2_R != null) wheel2_R.setRotation(currentRotation);
		if (wheel3_L != null) wheel3_L.setRotation(currentRotation);
		if (wheel3_R != null) wheel3_R.setRotation(currentRotation);
	}



	//------------------- A Simple Scene Object-Oriented Scene Graph API ----------------

	private static abstract class SceneGraphNode {
		Color color;  // If not null, the default color for this node and its children.
		              // If null, the default color is inherited.
		SceneGraphNode setColor(Color c) {
			this.color = c;
			return this;
		}
		final void draw(Graphics2D g) {
			Color saveColor = null;
			if (color != null) {
				saveColor = g.getColor();
				g.setColor(color);
			}
			doDraw(g);
			if (saveColor != null) {
				g.setColor(saveColor);
			}
		}
		abstract void doDraw(Graphics2D g);
	}

	/**
	 *  Defines a subclass, CompoundObject, of SceneGraphNode to represent
	 *  an object that is made up of sub-objects.  Initially, there are no
	 *  sub-objects.  Objects are added with the add() method.
	 */
	private static class CompoundObject extends SceneGraphNode {
		ArrayList<SceneGraphNode> subobjects = new ArrayList<SceneGraphNode>();
		CompoundObject add(SceneGraphNode node) {
			subobjects.add(node);
			return this;
		}
		void doDraw(Graphics2D g) {
			for (SceneGraphNode node : subobjects)
				node.draw(g);
		}
	}

	/**
	 *  TransformedObject is a subclass of SceneGraphNode that
	 *  represents an object along with a modeling transformation to
	 *  be applied to that object.  The object must be specified in
	 *  the constructor.  The transformation is specified by calling
	 *  the setScale(), setRotate() and setTranslate() methods. Note that
	 *  each of these methods returns a reference to the TransformedObject
	 *  as its return value, to allow for chaining of method calls.
	 *  The modeling transformations are always applied to the object
	 *  in the order scale, then rotate, then translate.
	 */
	private static class TransformedObject extends SceneGraphNode {
		SceneGraphNode object;
		double rotationInDegrees = 0;
		double scaleX = 1, scaleY = 1;
		double translateX = 0, translateY = 0;
		TransformedObject(SceneGraphNode object) {
			this.object = object;
		}
		TransformedObject setRotation(double degrees) {
			rotationInDegrees = degrees;
			return this;
		}
		TransformedObject setTranslation(double dx, double dy) {
			translateX = dx;
			translateY = dy;
			return this;
		}
		TransformedObject setScale(double sx, double sy) {
			scaleX = sx;
			scaleY = sy;
			return this;
		}
		void doDraw(Graphics2D g) {
			AffineTransform savedTransform = g.getTransform();
			if (translateX != 0 || translateY != 0)
				g.translate(translateX,translateY);
			if (rotationInDegrees != 0)
				g.rotate( rotationInDegrees/180.0 * Math.PI);
			if (scaleX != 1 || scaleY != 1)
				g.scale(scaleX,scaleY);
			object.draw(g);
			g.setTransform(savedTransform);
		}
	}

	       // Create some basic objects as custom SceneGraphNodes.

	private static SceneGraphNode line = new SceneGraphNode() {
		void doDraw(Graphics2D g) {  g.draw( new Line2D.Double( -0.5,0, 0.5,0) ); }
	};

	private static SceneGraphNode rect = new SceneGraphNode() {
		void doDraw(Graphics2D g) {  g.draw(new Rectangle2D.Double(-0.5,-0.5,1,1)); }
	};

	private static SceneGraphNode filledRect = new SceneGraphNode() {
		void doDraw(Graphics2D g) {  g.fill(new Rectangle2D.Double(-0.5,-0.5,1,1)); }
	};

	private static SceneGraphNode circle = new SceneGraphNode() {
		void doDraw(Graphics2D g) {  g.draw(new Ellipse2D.Double(-0.5,-0.5,1,1)); }
	};

	private static SceneGraphNode filledCircle = new SceneGraphNode() {
		void doDraw(Graphics2D g) {  g.fill(new Ellipse2D.Double(-0.5,-0.5,1,1)); }
	};

	private static SceneGraphNode filledTriangle = new SceneGraphNode() {
		void doDraw(Graphics2D g) {  // width = 1, height = 1, center of base is at (0,0), tip at (0,1)
			Path2D path = new Path2D.Double();
			path.moveTo(-0.5,0); // Base vertex 1
			path.lineTo(0.5,0);  // Base vertex 2
			path.lineTo(0,1);    // Tip vertex
			path.closePath();
			g.fill(path);
		}
	};

	// Custom SceneGraphNode for a wheel (polygon with spokes)
	// Draws a wheel of diameter 1, centered at (0,0)
	private static SceneGraphNode wheelNode = new SceneGraphNode() {
	    void doDraw(Graphics2D g) {
	        int sides = 12; // Number of sides for the polygon rim and number of spokes
	        Color prevColor = g.getColor();
	        if (this.color == null) { // If node color isn't set, use black for wheel
	            g.setColor(Color.BLACK);
	        }
	        // else use this.color which would have been set by g.setColor(this.color)

	        Path2D path = new Path2D.Double();
	        // Spokes
	        for (int i = 0; i < sides; i++) {
	            double angle = 2 * Math.PI * i / sides;
	            path.moveTo(0,0); // center
	            path.lineTo(0.5 * Math.cos(angle), 0.5 * Math.sin(angle)); // Radius 0.5
	        }
	        // Polygon Rim
	        path.moveTo(0.5 * Math.cos(0), 0.5 * Math.sin(0)); // Start at first vertex
	        for (int i = 1; i <= sides; i++) { // Loop through sides + 1 to include the closing segment
	            double angle = 2 * Math.PI * i / sides;
	            path.lineTo(0.5 * Math.cos(angle), 0.5 * Math.sin(angle));
	        }
	        g.draw(path);

	        if (this.color == null) { // Restore previous color if we changed it
	            g.setColor(prevColor);
	        }
	        // If this.color was set, the outer draw() method will restore it.
	    }
	};


	//--------------------------------- Implementation ------------------------------------

	private JPanel display;  // The JPanel in which the scene is drawn.

	/**
	 * Constructor creates the scene graph data structure that represents the
	 * scene that is to be drawn in this panel, by calling createWorld().
	 * It also sets the preferred size of the panel to the constants WIDTH and HEIGHT.
	 * And it creates a timer to drive the animation.
	 */
	public SceneGraph() {
		display = new JPanel() {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D)g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				applyLimits(g2, X_LEFT, X_RIGHT, Y_TOP, Y_BOTTOM, false);
				g2.setStroke( new BasicStroke(pixelSize) ); // set default line width to one pixel.
				if (world != null) world.draw(g2); // Check if world is initialized
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
		createWorld(); // createWorld is called here
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
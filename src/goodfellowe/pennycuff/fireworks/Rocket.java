/**
 * 
 */
package goodfellowe.pennycuff.fireworks;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author Corey
 *
 */
public class Rocket {
	// Constants
	final public boolean ALIVE = true;
	final public boolean DEAD = false;
	final public int GRAVITY = 3;
	
	// State variables
	private boolean state;
	private int lastX, lastY;
	private int screenWidth, screenHeight;
	
	// Mathematical variables for computing position
	private float y0, x2, x2a, y2, x1, y1; // position variables
	private long started; // Timestamp of when the animation started;
	private long lastUpdate; // Timestamp of last update in milliseconds
	private long duration; // Desirable length of the animation
	private long cutoff; // Timestamp of animation end
	private float coefA, coefB;
	private double quadA, quadB, quadC, quadTag;
	
	public Paint paint;

	/**
	 * 
	 */
	public Rocket() {
		state = DEAD;
		paint = null;
	}
	
	public boolean defineCriticalPoints(int y0, int y2, int x1, int y1, long duration, int screenWidth, int screenHeight) {
		lastX = 0;
		lastY = (int)y0;
		
		this.y0 = y0;
		this.y2 = y2;
		this.x1 = x1;
		this.y1 = y1;
		
		this.duration = duration;
		
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		
		// Original equation of y = Ax^2 + Bx + C
		// Values of A and B are achieved algebraically using a starting
		// point (0, y0), ending at point (x1, y1), and having a maximum
		// at the unknown value (x2, y2).  5 variables are known, with x2 being
		// the only unknown.  Algebraic manipulation reveals the necessary
		// values and coefficients.
		quadA = (y1 - y0);
		quadB = (-2 * x1 * y2) + (2 * x1 * y0);
		quadC = (x1 * x1 * y2) - (x1 * y0);
		double first = quadB * quadB;
		double second = 4 * quadA * quadC;
		double third = first - second;
		quadTag = Math.sqrt(third);
		x2 = (float)((-quadB - quadTag) / (2 * quadA));
		x2a = (float)((-quadB + quadTag) / (2 * quadA));
		if (x2a < x2) {
			//x2 = x2a;
		}
		coefA = (y1 - y0) / ((x1 * x1) - (2 * x2 * x1));
		coefB = -2 * coefA * x2;
		if (third < 0) {
			return false;
		}
		return true;
	}
	
	private double position(float x) {
		return (x * ((coefA * x) + coefB)) + y0;
	}
	
	/**
	 * Return the value that should be used for x at the specified time.
	 * @param time
	 * @return
	 */
	private float xval(long time) {
		/**
		 * Uses the cross-multiplication equivalence
		 * x1             ??
		 * -------- = ----------
		 * duration   time - started
		 */
		return (x1) * (time - started) / duration;
	}
	
	public void makeAlive(long lastUpdate) {
		state = ALIVE;
		this.lastUpdate = lastUpdate;
		cutoff = lastUpdate + duration;
		started = lastUpdate;
	}
	
	public boolean isAlive() {
		return state;
	}
	
	public void draw(Canvas canvas, long time) {
		if (state == ALIVE) {
			float tempX = xval(time);
			if (tempX > x1) {
				tempX = x1;
			}
			int newX = (int) tempX;
			int newY = (int) position(tempX);
			for (int x = lastX; x <= newX; x++) {
				//canvas.drawLine(lastX, screenHeight - (int) position(lastX), x, screenHeight - (int) position(x), paint);
				lastX = x;
			}
			lastX = newX;
			lastY = newY;
			if (time > cutoff) {
				state = DEAD;
			}
			//canvas.drawCircle(x1, screenHeight - y1, 5, paint);
		}
	}
	
	public int getCurrentX() {
		return lastX;
	}
	
	public int getCurrentY() {
		return lastY;
	}

}
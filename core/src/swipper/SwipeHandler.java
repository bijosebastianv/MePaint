package swipper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import simplify.ResolverRadialChaikin;


public class SwipeHandler extends InputAdapter {
	
	private FixedList<Vector2> inputPoints;
	
	/** The pointer associated with this swipe event. */
	private int inputPointer = 0;
	
	/** The minimum distance between the first and second point in a drawn line. */
	public int initialDistance = 10;
	
	/** The minimum distance between two points in a drawn line (starting at the second point). */
	public int minDistance = 20;
	
	private Vector2 lastPoint = new Vector2();
    private Vector2 lastPoint2 = new Vector2();
	
	private boolean isDrawing = false;
	
	private SwipeResolver simplifier = (SwipeResolver) new ResolverRadialChaikin();
	private Array<Vector2> simplified;
	
	public SwipeHandler(int maxInputPoints) {
		this.inputPoints = new FixedList<Vector2>(maxInputPoints, Vector2.class);
		simplified = new Array<Vector2>(true, maxInputPoints, Vector2.class);
		resolve(); //copy initial empty list
	}

	/**
	 * Returns the fixed list of input points (not simplified).
	 * @return the list of input points
	 */
	public Array<Vector2> input() {
		return this.inputPoints;
	}
	
	/**
	 * Returns the simplified list of points representing this swipe.
	 * @return
	 */
	public Array<Vector2> path() {
		return simplified;
	}
	
	/**
	 * If the points are dirty, the line is simplified.
	 */
	public void resolve() {
		simplifier.resolve(inputPoints, simplified);
	}
	
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {


		if (pointer!=inputPointer)
			return false;
		isDrawing = true;

	//	System.out.print("xx");

		//clear points
		inputPoints.clear();
		
		//starting point
		lastPoint = new Vector2(screenX, Gdx.graphics.getHeight()-screenY);
		inputPoints.insert(lastPoint);
		
		resolve();
		return true;
	}
	
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//on release, the line is simplified
		resolve();
		isDrawing = false;
		return false;
	}
	public void clrIn()
	{

		//simplified.clear();
		//clear points
        if(isDrawing)
        {

            inputPoints.clear();
            inputPoints.insert(lastPoint2);
            inputPoints.insert(lastPoint);
            simplified.clear();
        }else
        {
            simplified.clear();
            inputPoints.clear();
        }
	}
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		//System.out.print("DD");
		if (pointer!=inputPointer)
			return false;
		isDrawing = true;
		
		Vector2 v = new Vector2(screenX, Gdx.graphics.getHeight()-screenY);
		
		//calc length
		float dx = v.x - lastPoint.x;
		float dy = v.y - lastPoint.y;
		float len = (float)Math.sqrt(dx*dx + dy*dy);
		//TODO: use minDistanceSq
		
		//if we are under required distance
		if (len < minDistance && (inputPoints.size>1 || len<initialDistance)) {


			return false;
		}

        if(inputPoints.size>0)
        {
            lastPoint2=inputPoints.get(0);
        }
		//add new point
		inputPoints.insert(v);
		
		lastPoint = v;
		
		//simplify our new line
		resolve();
		return true;
	}
}

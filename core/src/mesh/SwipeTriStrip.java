package mesh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SwipeTriStrip {

	public static FrameBuffer buffer;
	Array<Vector2> texcoord = new Array<Vector2>();
	Array<Vector2> tristrip = new Array<Vector2>();
	int batchSize;
	Vector2 perp = new Vector2();
	public float thickness = 10f;
	public float endcap =  0f;
	public Color color = new Color(Color.WHITE);
	ImmediateModeRenderer20 gl20;
	
	public SwipeTriStrip() {
		gl20 = new ImmediateModeRenderer20(false, true, 1);
		buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),true);

	}
	
	public void draw(Camera cam) {
		if (tristrip.size<=0)
			return;

        // Gdx.gl.glEnable(GL20.GL_BLEND);
       // Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        buffer.begin();


       // Gdx.gl.glEnable(GL20.GL_BLEND);
       //  Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);


        // Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        gl20.begin(cam.combined, GL20.GL_TRIANGLE_STRIP);
		for (int i=0; i<tristrip.size; i++) {
            if (i == batchSize) {
                gl20.end();
                buffer.end();
                buffer.begin();
                gl20.begin(cam.combined, GL20.GL_TRIANGLE_STRIP);

               /* Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
*/
			}
			Vector2 point = tristrip.get(i);
			Vector2 tc = texcoord.get(i);
			gl20.color(color.r, color.g, color.b, color.a);
			gl20.texCoord(tc.x, 0f);
			gl20.vertex(point.x, point.y, 0f);

		}
		//tristrip.clear();
		gl20.end();
		   buffer.end();
	}
	
	private int generate(Array<Vector2> input, int mult) {
		int c = tristrip.size;
		if (endcap<=0) {
			tristrip.add(input.get(0));
		} else {
			Vector2 p = input.get(0);
			Vector2 p2 = input.get(1);
			perp.set(p).sub(p2).scl(endcap);
			tristrip.add(new Vector2(p.x+perp.x, p.y+perp.y));
		}
		texcoord.add(new Vector2(0f, 0f));
		
		for (int i=1; i<input.size-1; i++) {
			Vector2 p = input.get(i);
			Vector2 p2 = input.get(i+1);
			
			//get direction and normalize it
			perp.set(p).sub(p2).nor();
			
			//get perpendicular
			perp.set(-perp.y, perp.x);
			
		//	 float thick = thickness * (1f-((i)/(float)(input.size)));
			//float thick = thickness;
			//move outward by thickness

			// perp.scl(thick/2f);
			 perp.scl(10f);
			
			//decide on which side we are using
			perp.scl(mult);
			
			//add the tip of perpendicular
			tristrip.add(new Vector2(p.x+perp.x, p.y+perp.y));
			//0.0 -> end, transparent
			texcoord.add(new Vector2(0f, 0f));
			
			//add the center point
			tristrip.add(new Vector2(p.x, p.y));
			//1.0 -> center, opaque
			texcoord.add(new Vector2(1f, 0f));
		}
		
		//final point
		if (endcap<=0) {
			tristrip.add(input.get(input.size-1));
		} else {
			Vector2 p = input.get(input.size-2);
			Vector2 p2 = input.get(input.size-1);
			perp.set(p2).sub(p).scl(endcap);
			tristrip.add(new Vector2(p2.x+perp.x, p2.y+perp.y));
		}
		//end cap is transparent
		 texcoord.add(new Vector2(1f, 0f));
		return tristrip.size-c;
	}
	
	public void update(Array<Vector2> input) {
		tristrip.clear();
		texcoord.clear();
		
		if (input.size<2)
			return;
		batchSize = generate(input, 1);
		int b = generate(input, -1);
	}
	
}

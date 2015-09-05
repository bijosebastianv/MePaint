package plane;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class PointPool  {
	
	private List<Vector2> _pool;
	private int _poolIndex = -1;
	
	public PointPool () {
		_pool = new ArrayList<Vector2>(500);
		for (int i = 0; i < 200; i++) {
			_pool.add(new Vector2(0,0));
		}
	}
	
	public Vector2 getObject () {
		_poolIndex++;
		
		if (_poolIndex >= _pool.size()) {
			_poolIndex = 0;
		}
		
		return _pool.get(_poolIndex);
	}
	
	public void dispose () {
		_pool.clear();
	}
}

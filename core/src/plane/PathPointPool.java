package plane;

import java.util.ArrayList;
import java.util.List;

public class PathPointPool  {
	
	private List<PathPoint> _pool;
	private int _poolIndex = -1;
	
	public PathPointPool () {
		_pool = new ArrayList<PathPoint>(500);
		for (int i = 0; i < 200; i++) {
			_pool.add(new PathPoint(0, 0));
		}
	}
	
	public PathPoint getObject () {
		_poolIndex++;
		
		if (_poolIndex >= _pool.size()) {
			_poolIndex = 0;
		}
		
		return _pool.get(_poolIndex);
	}
	
	public void dispose () {
       // _poolIndex=-1;
		_pool.clear();
	}

}

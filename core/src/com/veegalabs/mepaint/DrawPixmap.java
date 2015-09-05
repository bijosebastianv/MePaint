package com.veegalabs.mepaint;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import plane.LinePath2D;
import plane.PointPool;


public class DrawPixmap extends ApplicationAdapter implements Screen,InputProcessor {
	SpriteBatch batch;
	Texture img;
Pixmap pixmap;
	private FileHandle file ;

ScreenViewport viewport;
	Stage stage;
ScreenshotFactory factory;
Skin skin;

	InputMultiplexer multiplexer = new InputMultiplexer();

	ColorPicker colorPicker;

	boolean spicker=false;








	private List<Vector2> _points;
	private int _maxDistance =1;
	private LinePath2D _linePath;

	//space between dashes or dots
	private float _gap = .1f;
	private PointPool _vectorPool;
    float progressIncrement=1f;
    private Vector2 lastPoint;
    private boolean _lastPoint=false;
    private int radius=1;
    private MyActor myactor;




	private  class MyActor extends Actor {

		@Override
		public void draw(Batch batch, float parentAlpha) {
			batch.draw(img,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		}
	}
	@Override
	public void create () {

        Pixmap pm = new Pixmap(Gdx.files.internal("cursor.png"));
        int xHotSpot = 0;
        int yHotSpot =60;

        Gdx.input.setCursorImage(pm, xHotSpot, yHotSpot);
        pm.dispose();





		_linePath = new LinePath2D();





		_points = new ArrayList<Vector2>();

		_vectorPool = new PointPool();










		batch = new SpriteBatch();
		//pixmap=new Pixmap(Gdx.files.internal("v.png"));
		//pixmap=new Pixmap(Gdx.files.internal("badlogic.jpg"));
		pixmap=new Pixmap(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), Pixmap.Format.RGB565);
		pixmap.setColor(Color.BROWN);
		pixmap.fill();
		img = new Texture(pixmap);

        myactor=new MyActor();
        myactor.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());



 
		file = Gdx.files.local("bin/img/pixmap.cim");
		factory=new ScreenshotFactory();

		viewport=new ScreenViewport();
		stage=new Stage(viewport);
		//Scale s=new Scale(viewport.getWorldWidth(),viewport.getWorldHeight());
		//img.

		skin=new Skin(Gdx.files.internal("data/uiskin.json"));
		//Window window=new Window("chooseer",skin);
		//EditorColorPicker editorColorPicker=new EditorColorPicker(100,500,window,skin,"color");
		//editorColorPicker.setPosition(0, 0);
  // stage.addActor(window);
		 colorPicker=new ColorPicker(skin);
		colorPicker.setPosition(0, 0);
        ColorPicker.selectedColor = Color.BLUE;
//stage.addActor(new ColorPicker(skin));
//stage.setDebugAll(true);
		stage.addActor(myactor);
		stage.act();
		//Gdx.input.setInputProcessor(stage);
		Gdx.input.setOnscreenKeyboardVisible(true);
		multiplexer.addProcessor(stage);
		multiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//batch.begin();
		stage.draw();
		//batch.draw(img, 0, 0);
	//	batch.end();
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {

	}

	@Override
	public void hide() {

	}


	@Override
	public boolean keyDown(int keycode) {


		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		switch(character) {


            case ']': // restart screen
                if(radius<30)
                    radius++;
                return true;
            case '[': // restart screen
                if(radius>1)
                    radius--;
                return true;

			case 'r': // restart screen
				Gdx.app.log("PixmapEditor", "restarted screen");
				return true;
			case 's': // save
				PixmapIO.writeCIM(file, pixmap);
				Gdx.app.log("PixmapEditor", "saved to " + file);
				ScreenshotFactory.saveScreenshot();
				return true;
			case 'l': // load
				if(file.exists()) {
					pixmap.dispose();
					pixmap = PixmapIO.readCIM(file);
					//img.draw(pixmap, 0, 0);
					// create a new texture if you want it to be the size of the pixmap (drawing will not be offset for pixmaps smaller than the screen size)
					 img.dispose();
					 img = new Texture(pixmap);
					Gdx.app.log("PixmapEditor", "loaded from " + file);
				} else
					Gdx.app.log("PixmapEditor", file + " doesn't exist");
				return true;
			case 'c'://color chooser

			{
				if (!spicker) {
					stage.addActor(colorPicker);


				} else {
					colorPicker.remove();


				}
				spicker = !spicker;
				return true;
			}
			default:
				return false;
		}
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        _linePath.points.clear();
        _linePath.totalLength=0;
        _points.clear();
_lastPoint=false;

        pixmap.setColor(ColorPicker.selectedColor);
        pixmap.fillCircle(screenX, screenY - (Gdx.graphics.getHeight() - pixmap.getHeight()), radius);
        img.draw(pixmap, 0, 0);

        return false;
	}



	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        _linePath.points.clear();
        _linePath.totalLength=0;
        _points.clear();

       // drawLine(pixmap);
       // img.draw(pixmap, 0, 0);

		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {


        pixmap.setColor(ColorPicker.selectedColor);
		//pixmap.fillCircle(screenX, screenY - (Gdx.graphics.getHeight() - pixmap.getHeight()), 5);
if(_lastPoint)
{
    addClick(lastPoint.x,lastPoint.y,true);
    _lastPoint=false;
}
addClick(screenX, screenY, true);

        float points  = _linePath.totalLength / _gap;

        System.out.println(points + "");


        progressIncrement = 1/points;


		drawLine(pixmap);
		img.draw(pixmap, 0, 0);
		return true;
	}

	private void drawLine(Pixmap pixmap) {
        float points  = _linePath.totalLength / _gap;

		if (_linePath.totalLength >1 &&  _linePath.points.size() != 0 && _points.size() != 0)  {

			/*float points  = _linePath.totalLength / _gap;

            System.out.println(points + "");


			 progressIncrement = 1/points;*/

			//spread the points evenly
            float p = 0f;
			//float p = progressIncrement;
			Vector2 point;




			while (p < 1 ) {
				//in order to draw the path, traverse it with a fixed progress and grab the points from that
				point = _linePath.getPointAtProgress(p);

                if (point != null && p > _linePath.progress + 0.01)
				{


					pixmap.fillCircle((int) point.x, (int) (point.y - (Gdx.graphics.getHeight() - pixmap.getHeight())),radius);

                 /*   _points.remove(point);

                    if (_points.size() == 0) {
                        _linePath.points.clear();
                    }*/

                  //  if(_linePath.totalLength>0&&points>100)
                  //  _linePath.removePoint(point);

lastPoint=point;
				}
				p += progressIncrement;
			}
if(points>=2000) {
    _linePath.points.clear();
    _points.clear();
_lastPoint=true;
    _linePath.totalLength = 0;

    progressIncrement = 1;
}
		}


	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}


    public  void addClick(float x, float y, boolean dragging)
    {


		if (dragging) {

			Vector2 point = _vectorPool.getObject();
			point.set(x,y);

			if (_points.size() == 0) {
				_linePath.points.clear();
				_points.add(point);
				_linePath.insertMultiplePoints(_points, 0);

                System.out.println("PPP");
			} else {

				Vector2 lastPoint = _points.get(_points.size() - 1);
				//only add a point if the distance to the previous point is long enough
				//(you don't want to add the same point over and over again!)
				if (point.dst(lastPoint) > _maxDistance)
				{
					_points.add(point);
					_linePath.appendPoint(point);


				}
			}
		}




    }



}

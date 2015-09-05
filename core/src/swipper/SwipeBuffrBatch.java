package swipper;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import mesh.SwipeTriStrip;
import mesh.SwipeTriStripBuffrBatch;


public class SwipeBuffrBatch extends ApplicationAdapter implements InputProcessor {



    OrthographicCamera cam;
    SpriteBatch batch,batch2;

    public static SwipeHandler swipe;

    Texture tex;
    ShapeRenderer shapes;

    SwipeTriStripBuffrBatch tris;
    private TextureRegion tr;
    InputMultiplexer multiplexer;
    Stage stage;


    @Override
    public void create() {
        //the triangle strip renderer
        tris = new SwipeTriStripBuffrBatch();

        //a swipe handler with max # of input points to be kept alive
        swipe = new SwipeHandler(100);

        //minimum distance between two points
        swipe.minDistance = 50;

        //minimum distance between first and second point
        swipe.initialDistance = 50;

        //we will use a texture for the smooth edge, and also for stroke effects
        tex = new Texture("data/gradient.png");
       tex = new Texture("data/cgn.png");
        tex = new Texture("data/grdnt.png");
       // tex = new Texture("data/cg.png");
        Pixmap pixmap=new Pixmap(1,1, Pixmap.Format.RGB565);
        pixmap.setColor(Color.BROWN);
        pixmap.fill();
        //  tex = new Texture(pixmap);
        tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        shapes = new ShapeRenderer();
        batch = new SpriteBatch();
        batch2=new SpriteBatch();

        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //handle swipe input
        //Gdx.input.setInputProcessor(swipe);

        stage=new Stage(new ScreenViewport());

        multiplexer = new InputMultiplexer();

        multiplexer.addProcessor(swipe);
        // multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);

        Gdx.input.setInputProcessor(multiplexer);
        // stage.addActor(new Sprite(tex));
        stage.act();

        tr=  new TextureRegion(SwipeTriStripBuffrBatch.buffer.getColorBufferTexture());
       // tr.flip(false, true);
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }



    @Override
    public void render() {

        Gdx.gl.glClearColor(1, 1,0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

         // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));


        //Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        cam.update();
        batch.setProjectionMatrix(cam.combined);


        tex.bind();

        //the endcap scale
        //tris.endcap = 50f;

        //the thickness of the line
        //tris.thickness = 10f;

        //generate the triangle strip from our path
        tris.update(swipe.path());

        //the vertex color for tinting, i.e. for opacity
       // tris.color = Color.WHITE;

        //render the triangles to the screen
        tris.draw(cam, batch);

        //  stage.draw();

   //cam.update();



         // batch.setProjectionMatrix(cam.combined);
            batch.setBlendFunction(GL20.GL_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

           batch.setColor(Color.WHITE);
         batch.begin();

        //batch.draw(SwipeTriStrip.buffer.getColorBufferTexture(), 0, 0, 600, 600);

        batch.draw(tr, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // stage.draw();
        batch.end();

batch2.begin();

        batch2.draw(SwipeTriStripBuffrBatch.buffer2.getColorBufferTexture(),  Gdx.graphics.getWidth()-200,  Gdx.graphics.getHeight()-200, 200,200);

        batch2.end();
        //uncomment to see debug lines
         // drawDebug();
    }

    //optional debug drawing..
    void drawDebug() {
        Array<Vector2> input = swipe.input();

        //draw the raw input
        shapes.begin(ShapeType.Line);
        shapes.setColor(Color.GRAY);
        for (int i=0; i<input.size-1; i++) {
            Vector2 p = input.get(i);
            Vector2 p2 = input.get(i+1);
            // shapes.line(p.x, p.y, p2.x, p2.y);
            //shapes.rectLine(p.x, p.y, p2.x, p2.y,30);
        }
        shapes.end();

        //draw the smoothed and simplified path
        shapes.begin(ShapeType.Line);
        shapes.setColor(Color.RED);
        Array<Vector2> out = swipe.path();
        for (int i=0; i<out.size-1; i++) {
            Vector2 p = out.get(i);
            Vector2 p2 = out.get(i+1);
            // shapes.line(p.x, p.y, p2.x, p2.y);
            shapes.rectLine(p.x, p.y, p2.x, p2.y, 20);
        }
        shapes.end();


        //render our perpendiculars
        shapes.begin(ShapeType.Line);
        Vector2 perp = new Vector2();

        for (int i=1; i<input.size-1; i++) {
            Vector2 p = input.get(i);
            Vector2 p2 = input.get(i+1);

            shapes.setColor(Color.LIGHT_GRAY);
            perp.set(p).sub(p2).nor();
            perp.set(perp.y, -perp.x);
            perp.scl(10f);
            //	  shapes.line(p.x, p.y, p.x + perp.x, p.y + perp.y);
            perp.scl(-1f);
            shapes.setColor(Color.BLUE);
            // shapes.line(p.x, p.y, p.x + perp.x, p.y + perp.y);
        }
        shapes.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        shapes.dispose();
        tex.dispose();
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
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
//generate the triangle strip from our path
        //tris.update(swipe.path());
        //  System.out.print("bbb");
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

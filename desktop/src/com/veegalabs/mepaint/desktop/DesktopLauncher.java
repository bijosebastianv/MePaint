package com.veegalabs.mepaint.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.veegalabs.mepaint.DrawPixmap;
import com.veegalabs.mepaint.MePaint;

import swipper.SwipeBuffrBatch;
import swipper.SwiperImproved;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//new LwjglApplication(new DrawPixmap(), config);

		  // new LwjglApplication(new SwiperImproved(), config);
		  new LwjglApplication(new SwipeBuffrBatch(), config);
	}
}

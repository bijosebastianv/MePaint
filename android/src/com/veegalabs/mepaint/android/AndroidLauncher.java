package com.veegalabs.mepaint.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.veegalabs.mepaint.DrawPixmap;
import com.veegalabs.mepaint.MePaint;

import swipper.SwiperImproved;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		// initialize(new DrawPixmap(), config);
		  initialize(new SwiperImproved(), config);


	}
}

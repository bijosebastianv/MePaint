package com.veegalabs.mepaint;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Scaling;

public class ColorPicker extends Table {
 
        private static final int COLORS_WIDTH = 256;
        private static final int COLORS_HEIGHT = 256;
        private static final int BRIGHTNESS_WIDTH = 32;
        private static final int BRIGHTNESS_HEIGHT = 256;
 
        public static Color selectedColor= Color.BLUE;
 
        private Color colorPicked = new Color(1, 1, 1, 1);
        private float luminancePicked = 1.0f;
 
        private Image imgColor;
 
        private Pixmap pmColors;
        private Texture texColors;
 
        private Pixmap pmBrightness;
        private Texture texBrightness;
 
        private TextField tfHue;
        private TextField tfSaturation;
        private TextField tfLuminance;
        private TextField tfRed;
        private TextField tfGreen;
        private TextField tfBlue;
 
        private boolean textFilterRGBenabled = true;
        private boolean textFilterHSLenabled = true;
 
        public ColorPicker(Skin skin) {
                // colors
                pmColors = new Pixmap(256, 256, Format.RGB888);
                for (int x = 0; x < COLORS_WIDTH; x++) {
                        for (int y = 0; y < COLORS_HEIGHT; y++) {
                                float h = x / (float) COLORS_WIDTH;
                                float s = (COLORS_HEIGHT - y) / (float) COLORS_HEIGHT;
                                float l = 0.5f;
                                Color color = HSLtoRGB(h, s, l);
 
                                pmColors.setColor(color);
                                pmColors.drawPixel(x, y);
                        }
                }
 
                texColors = new Texture(pmColors);
                TextureRegion imageColors = new TextureRegion(texColors, COLORS_WIDTH, COLORS_HEIGHT);
 
                final Image colors = new Image(imageColors);
                colors.setScaling(Scaling.stretch);
                colors.addListener(new DragListener() {
                        @Override
                        public void drag(InputEvent event, float x, float y, int pointer) {
                                y = COLORS_HEIGHT - y;
 
                                Color.rgba8888ToColor(colorPicked, pmColors.getPixel((int) x, (int) y));
                                updateSelectedColor((int) x, (int) y);
                                updateBrightness(luminancePicked);
                                updateTextFields();
 
                                super.drag(event, x, y, pointer);
                        }
                });
                colors.addListener(new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                y = COLORS_HEIGHT - y;
 
                                Color.rgba8888ToColor(colorPicked, pmColors.getPixel((int) x, (int) y));
                                updateSelectedColor((int) x, (int) y);
                                updateBrightness(luminancePicked);
                                updateTextFields();
 
                                return super.touchDown(event, x, y, pointer, button);
                        }
                });
 
                // brightness
                pmBrightness = new Pixmap(32, 256, Format.RGB888);
                texBrightness = new Texture(pmBrightness);
 
                TextureRegion imageBrightness = new TextureRegion(texBrightness, BRIGHTNESS_WIDTH, BRIGHTNESS_HEIGHT);
                final Image brightness = new Image(imageBrightness);
 
                brightness.addListener(new DragListener() {
                        @Override
                        public void drag(InputEvent event, float x, float y, int pointer) {
                                y = BRIGHTNESS_HEIGHT - y;
 
                                luminancePicked = (BRIGHTNESS_HEIGHT - y) / (float) BRIGHTNESS_HEIGHT;
 
                                updateSelectedColor();
                                updateBrightness(luminancePicked);
                                updateTextFields();
 
                                super.drag(event, x, y, pointer);
                        }
                });
 
                float[] hsb = RGBtoHSL(Color.RED);
                hsb[2] = 1;
 
                brightness.addListener(new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                luminancePicked = y / (float) BRIGHTNESS_HEIGHT;
 
                                updateSelectedColor();
                                updateBrightness(luminancePicked);
                                updateTextFields();
 
                                return super.touchDown(event, x, y, pointer, button);
                        }
                });
 
                // color selected
//              imgColor = new Image(new TextureRegionDrawable(TextureManager.get("data/pixel.png")));
                Pixmap pixPixel = new Pixmap(1, 1, Format.RGBA8888);
                pixPixel.setColor(Color.WHITE);
                pixPixel.fill();
                Texture texPixel = new Texture(pixPixel);
                imgColor = new Image(texPixel);
 
                add(colors).size(COLORS_WIDTH, COLORS_HEIGHT).top().left().spaceBottom(10);
                add(brightness).size(BRIGHTNESS_WIDTH, BRIGHTNESS_HEIGHT).top().right();
 
                Table tableTextFields = new Table(skin);
 
                tfHue = new TextField("", skin);
                tfHue.setMaxLength(100);
                tfHue.setTextFieldFilter(new TextFieldFilter() {
                        @Override
                        public boolean acceptChar(TextField field, char c) {
                                if (textFilterHSLenabled) {
                                        String fieldValue = field.getText() + c;
                                        if (Character.isDigit(c) && Integer.valueOf(fieldValue) <= field.getMaxLength()) {
                                                updateColorFromFieldsHSL(tfHue.getText() + c, tfSaturation.getText(), tfLuminance.getText());
                                                return true;
                                        }
                                        return false;
                                }
 
                                return true;
                        }
                });
 
                tfSaturation = new TextField("", skin);
                tfSaturation.setMaxLength(100);
                tfSaturation.setTextFieldFilter(new TextFieldFilter() {
                        @Override
                        public boolean acceptChar(TextField field, char c) {
                                if (textFilterHSLenabled) {
                                        String fieldValue = field.getText() + c;
                                        if (Character.isDigit(c) && Integer.valueOf(fieldValue) <= field.getMaxLength()) {
                                                updateColorFromFieldsHSL(tfHue.getText(), tfSaturation.getText() + c, tfLuminance.getText());
                                                return true;
                                        }
                                        return false;
                                }
 
                                return true;
                        }
                });
 
                tfLuminance = new TextField("", skin);
                tfLuminance.setMaxLength(360);
                tfLuminance.setTextFieldFilter(new TextFieldFilter() {
                        @Override
                        public boolean acceptChar(TextField field, char c) {
                                if (textFilterHSLenabled) {
                                        String fieldValue = field.getText() + c;
                                        if (Character.isDigit(c) && Integer.valueOf(fieldValue) <= field.getMaxLength()) {
                                                updateColorFromFieldsHSL(tfHue.getText(), tfSaturation.getText(), tfLuminance.getText() + c);
                                                return true;
                                        }
                                        return false;
                                }
 
                                return true;
                        }
                });
 
                tfRed = new TextField("", skin);
                tfRed.setMaxLength(255);
                tfRed.setTextFieldFilter(new TextFieldFilter() {
                        @Override
                        public boolean acceptChar(TextField field, char c) {
                                if (textFilterRGBenabled) {
                                        String fieldValue = field.getText() + c;
                                        if (Character.isDigit(c) && Integer.valueOf(fieldValue) <= field.getMaxLength()) {
                                                updateColorFromFieldsRGB(tfRed.getText() + c, tfGreen.getText(), tfBlue.getText());
                                                return true;
                                        }
                                        return false;
                                }
 
                                return true;
                        }
                });
 
                tfGreen = new TextField("", skin);
                tfGreen.setMaxLength(255);
                tfGreen.setTextFieldFilter(new TextFieldFilter() {
                        @Override
                        public boolean acceptChar(TextField field, char c) {
                                if (textFilterRGBenabled) {
                                        String fieldValue = field.getText() + c;
                                        if (Character.isDigit(c) && Integer.valueOf(fieldValue) <= field.getMaxLength()) {
                                                updateColorFromFieldsRGB(tfRed.getText(), tfGreen.getText() + c, tfBlue.getText());
                                                return true;
                                        }
                                        return false;
                                }
 
                                return true;
                        }
                });
 
                tfBlue = new TextField("", skin);
                tfBlue.setMaxLength(255);
                tfBlue.setTextFieldFilter(new TextFieldFilter() {
                        @Override
                        public boolean acceptChar(TextField field, char c) {
                                if (textFilterRGBenabled) {
                                        String fieldValue = field.getText() + c;
                                        if (Character.isDigit(c) && Integer.valueOf(fieldValue) <= field.getMaxLength()) {
                                                updateColorFromFieldsRGB(tfRed.getText(), tfGreen.getText(), tfBlue.getText() + c);
                                                return true;
                                        }
                                        return false;
                                }
 
                                return true;
                        }
                });
 
                tableTextFields.row();
                tableTextFields.add("hue").right();
                tableTextFields.add(tfHue).left().width(50).spaceRight(20);
                tableTextFields.add("red").right();
                tableTextFields.add(tfRed).left().width(50);
 
                tableTextFields.row();
                tableTextFields.add("saturation").right();
                tableTextFields.add(tfSaturation).left().width(50);
                tableTextFields.add("green").right();
                tableTextFields.add(tfGreen).left().width(50);
 
                tableTextFields.row();
                tableTextFields.add("luminance").right();
                tableTextFields.add(tfLuminance).left().width(50);
                tableTextFields.add("blue").right();
                tableTextFields.add(tfBlue).left().width(50);
 
                Table t2 = new Table(skin);
                t2.add(imgColor).size(50, 50).top().left();
                t2.add(tableTextFields);
 
                row();
                add(t2).colspan(2);
 
                pack();
 
                updateBrightness(0.5f);
                updateSelectedColor();
                updateTextFields();
        }
 
        private void updateBrightness(float luminance) {
                int y = BRIGHTNESS_HEIGHT - (int) (luminance * BRIGHTNESS_HEIGHT);
 
                for (int i = 0; i < BRIGHTNESS_HEIGHT; i++) {
                        float[] hsl = RGBtoHSL(colorPicked);
                        hsl[2] = (BRIGHTNESS_HEIGHT - i) / (float) BRIGHTNESS_HEIGHT;
 
                        pmBrightness.setColor(HSLtoRGB(hsl[0], hsl[1], hsl[2]));
 
                        if (i != y) {
                                pmBrightness.drawLine(0, i, BRIGHTNESS_WIDTH, i);
                        } else {
                                for (int j = 0; j < BRIGHTNESS_WIDTH; j++) {
                                        if (j % 2 == 0) {
                                                pmBrightness.drawPixel(j, y, 0);
                                        } else {
                                                pmBrightness.drawPixel(j, y, 0xffffffff);
                                        }
                                }
                        }
                }
 
                texBrightness.draw(pmBrightness, 0, 0);
        }
 
        private void updateSelectedColor() {
                float[] hsl = RGBtoHSL(colorPicked);
 
                selectedColor = HSLtoRGB(hsl[0], hsl[1], luminancePicked);
 
                imgColor.setColor(selectedColor);
        }
 
        private void updateSelectedColor(int a, int b) {
                for (int x = 0; x < COLORS_WIDTH; x++) {
                        for (int y = 0; y < COLORS_HEIGHT; y++) {
                                float h = x / (float) COLORS_WIDTH;
                                float s = (COLORS_HEIGHT - y) / (float) COLORS_HEIGHT;
                                float l = 0.5f;
                                Color color = HSLtoRGB(h, s, l);
 
                                pmColors.setColor(color);
                                pmColors.drawPixel(x, y);
                        }
                }
 
                pmColors.setColor(Color.WHITE);
                pmColors.drawPixel(a, b);
 
                texColors.draw(pmColors, 0, 0);
 
                updateSelectedColor();
        }
 
        private void updateTextFields() {
                float[] hsl = RGBtoHSL(selectedColor);
 
                textFilterRGBenabled = false;
                textFilterHSLenabled = false;
 
                tfHue.setText(String.valueOf((int) (hsl[0] * 100.0f)));
                tfSaturation.setText(String.valueOf((int) (hsl[1] * 100.0f)));
                tfLuminance.setText(String.valueOf((int) (hsl[2] * 360.0f)));
 
                tfRed.setText(String.valueOf((int) (selectedColor.r * 255.0f)));
                tfGreen.setText(String.valueOf((int) (selectedColor.g * 255.0f)));
                tfBlue.setText(String.valueOf((int) (selectedColor.b * 255.0f)));
 
                textFilterRGBenabled = true;
                textFilterHSLenabled = true;
        }
 
        private void updateColorFromFieldsHSL(String hue, String saturation, String luminance) {
                Color color;
 
                try {
                        luminancePicked = Integer.valueOf(luminance) / 360f;
 
                        color = HSLtoRGB(Integer.valueOf(hue) / 100f, Integer.valueOf(saturation) / 100f, luminancePicked);
                } catch (Exception e) {
                        return;
                }
 
                selectedColor = color;
                colorPicked = color;
 
                updateSelectedColor();
                updateBrightness(luminancePicked);
 
                textFilterRGBenabled = false;
                tfRed.setText(String.valueOf((int) (selectedColor.r * 255.0f)));
                tfGreen.setText(String.valueOf((int) (selectedColor.g * 255.0f)));
                tfBlue.setText(String.valueOf((int) (selectedColor.b * 255.0f)));
                textFilterRGBenabled = true;
        }
 
        private void updateColorFromFieldsRGB(String red, String green, String blue) {
                Color color;
                try {
                        color = new Color(Integer.valueOf(red) / 255.0f, Integer.valueOf(green) / 255.0f, Integer.valueOf(blue) / 255.0f, 1);
                } catch (Exception e) {
                        return;
                }
 
                float[] hsl = RGBtoHSL(color);
 
                selectedColor = color;
 
                colorPicked = color;
                luminancePicked = hsl[2];
 
                // updateSelectedColor();
                selectedColor = HSLtoRGB(hsl[0], hsl[1], luminancePicked);
                imgColor.setColor(selectedColor);
 
                updateBrightness(luminancePicked);
 
                textFilterHSLenabled = false;
                tfHue.setText(String.valueOf((int) (hsl[0] * 100.0f)));
                tfSaturation.setText(String.valueOf((int) (hsl[1] * 100.0f)));
                tfLuminance.setText(String.valueOf((int) (hsl[2] * 360.0f)));
                textFilterHSLenabled = true;
        }
 
        public Color getSelectedColor() {
                return selectedColor;
        }
 
        public static Color HSLtoRGB(float h, float s, float l) {
                float q = 0;
 
                if (l < 0.5)
                        q = l * (1 + s);
                else
                        q = (l + s) - (s * l);
 
                float p = 2 * l - q;
 
                float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
                float g = Math.max(0, HueToRGB(p, q, h));
                float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));
 
                return new Color(r, g, b, 1);
        }
 
        private static float HueToRGB(float p, float q, float h) {
                if (h < 0)
                        h += 1;
 
                if (h > 1)
                        h -= 1;
 
                if (6 * h < 1) {
                        return p + ((q - p) * 6 * h);
                }
 
                if (2 * h < 1) {
                        return q;
                }
 
                if (3 * h < 2) {
                        return p + ((q - p) * 6 * ((2.0f / 3.0f) - h));
                }
 
                return p;
        }
       
        public static float[] RGBtoHSL(Color color) {
                // Get RGB values in the range 0 - 1
                float r = color.r;
                float g = color.g;
                float b = color.b;
 
                // Minimum and Maximum RGB values are used in the HSL calculations
                float min = Math.min(r, Math.min(g, b));
                float max = Math.max(r, Math.max(g, b));
 
                // Calculate the Hue
                float h = 0;
 
                if (max == min)
                        h = 0;
                else if (max == r)
                        h = ((60 * (g - b) / (max - min)) + 360) % 360;
                else if (max == g)
                        h = (60 * (b - r) / (max - min)) + 120;
                else if (max == b)
                        h = (60 * (r - g) / (max - min)) + 240;
 
                // Calculate the Luminance
                float l = (max + min) / 2;
 
                // Calculate the Saturation
                float s = 0;
 
                if (max == min)
                        s = 0;
                else if (l <= .5f)
                        s = (max - min) / (max + min);
                else
                        s = (max - min) / (2 - max - min);
 
                return new float[] { h / 360.0f, s, l };
        }
       
}
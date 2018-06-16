package org.frendo.game2048;

import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;

class TileDesign {
    private final static String GLOW_COLOR = "#f3d774";

    //static final private TileDesign unknown = new TileDesign("#3c3a32", "#f9f6f2", null, null, 5);
    static final private TileDesign[] tileDesigns = {
            new TileDesign("#D0EEEE", "#776e65", null, null, 2),
            new TileDesign("#BAEFEF", "#776e65", null, null, 2),
            new TileDesign("#25D5D5", "#f9f6f2", null, null, 2),
            new TileDesign("#1898AC", "#f9f6f2", null, null, 2),
            new TileDesign("#0E617A", "#f9f6f2", null, null, 2),
            new TileDesign("#15517E", "#f9f6f2", null, null, 2),
            new TileDesign("#10295F", "#f9f6f2", 0.3, 0.1, 2.5),
            new TileDesign("#7D1ACF", "#f9f6f2", 0.4, 0.2, 2.5),
            new TileDesign("#ED3962", "#f9f6f2", 0.5, 0.3, 2.5),
            new TileDesign("#F52F32", "#f9f6f2", 0.6, 0.4, 3),
            new TileDesign("#edc22e", "#f9f6f2", 0.7, 0.5, 3),
    };

    final Color backColor;
    final Color foreColor;
    final double sizeFraction;
    Effect glow;

    private TileDesign(String backColor, String foreColor, Double outerGlowValue, Double innerGlowValue, double sizeFraction) {
        this.backColor = Color.web(backColor);
        this.foreColor = Color.web(foreColor);

        if (outerGlowValue != null) {
            final DropShadow outerGlow = new DropShadow();
            outerGlow.setColor(Color.web(TileDesign.GLOW_COLOR, 0.3));
            outerGlow.setOffsetX(0);
            outerGlow.setOffsetY(0);
            outerGlow.setSpread(outerGlowValue);
            outerGlow.setRadius(30);

            final InnerShadow innerGlow = new InnerShadow();
            innerGlow.setColor(Color.web("#fff", innerGlowValue));
            innerGlow.setOffsetX(0);
            innerGlow.setOffsetY(0);
            innerGlow.setRadius(5);

            innerGlow.setInput(outerGlow);
            glow = innerGlow;
        }

        this.sizeFraction = sizeFraction;
    }

    static TileDesign forValue(int i) {
        //if (i < tileDesigns.length)
            return tileDesigns[i];
        //else
            //return unknown;
    }
}

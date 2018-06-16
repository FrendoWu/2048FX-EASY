package org.frendo.game2048;

import javafx.animation.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.awt.*;

class TileView {

    Pane pane;
    private Tile tile;

    TileView(Tile tile) {
        this.tile = tile;
        final TileDesign design = TileDesign.forValue(tile.value);

        Rectangle rectangle = new Rectangle(Config.TILE_PIXEL_LENGTH, Config.TILE_PIXEL_LENGTH);
        rectangle.setArcWidth(Config.TILE_PIXEL_RADIUS);
        rectangle.setArcHeight(Config.TILE_PIXEL_RADIUS);
        rectangle.setFill(design.backColor);
        rectangle.setEffect(design.glow);
        rectangle.setCache(true);

        Text text = new Text(String.valueOf(2 << tile.value));
        text.setFont(Font.font(text.getFont().getName(), FontWeight.BOLD, Config.TILE_PIXEL_LENGTH / design.sizeFraction));
        text.setFill(design.foreColor);
        text.setStroke(Color.TRANSPARENT);
        text.setCache(true);

        Point p = getPixelPoint(tile.spot);
        pane = new StackPane(rectangle, text);
        pane.setTranslateX(p.x);
        pane.setTranslateY(p.y);
        pane.setOpacity(0);
    }

    static Point getPixelPoint(Grid.Coordinate spot) {
        int px = (Config.TILE_PIXEL_LENGTH * spot.x) + ((spot.x + 1) * Config.BOARD_PIXEL_PADDING);
        int py = (Config.TILE_PIXEL_LENGTH * spot.y) + ((spot.y + 1) * Config.BOARD_PIXEL_PADDING);
        return new Point(px, py);
    }

    Transition moveTransition() {
        TranslateTransition tt = new TranslateTransition(Config.ANIMATION_DURATION_FIRST_PART, pane);
        Point p = getPixelPoint(tile.spot);
        tt.setToX(p.x);
        tt.setToY(p.y);
        tt.setInterpolator(Interpolator.SPLINE(Config.ANIMATION_MOVE_EASING, 1, Config.ANIMATION_MOVE_EASING, 1));
        return tt;
    }

    Transition mergeTransition() {
        FadeTransition fadeTransition = new FadeTransition(Config.ANIMATION_DURATION_SECOND_PART, pane);
        fadeTransition.setFromValue(0.95);
        fadeTransition.setToValue(1);

        ScaleTransition scaleTransition1 = new ScaleTransition(Config.ANIMATION_DURATION_SECOND_PART.divide(2), pane);
        scaleTransition1.setFromX(0.75);
        scaleTransition1.setFromY(0.75);
        scaleTransition1.setToX(1.25);
        scaleTransition1.setToY(1.25);

        ScaleTransition scaleTransition2 = new ScaleTransition(Config.ANIMATION_DURATION_SECOND_PART.divide(2), pane);
        scaleTransition2.setFromX(1.25);
        scaleTransition2.setFromY(1.25);
        scaleTransition2.setToX(1);
        scaleTransition2.setToY(1);

        SequentialTransition bothScaleTransitions = new SequentialTransition(scaleTransition1, scaleTransition2);

        return new ParallelTransition(fadeTransition, bothScaleTransitions);
    }

    Transition creationTransition() {
        FadeTransition fadeTransition = new FadeTransition(Config.ANIMATION_DURATION_SECOND_PART, pane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);

        ScaleTransition scaleTransition = new ScaleTransition(Config.ANIMATION_DURATION_SECOND_PART, pane);
        scaleTransition.setFromX(0.1);
        scaleTransition.setFromY(0.1);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);

        return new ParallelTransition(fadeTransition, scaleTransition);
    }
}

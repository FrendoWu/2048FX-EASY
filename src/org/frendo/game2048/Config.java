package org.frendo.game2048;

import javafx.scene.paint.Color;
import javafx.util.Duration;

class Config {

    static final boolean QUICK_MODE = false;//关闭快速模式
    
    static final int GRID_SIZE = 5;//格子大小
    static final int STARTING_TILES = 2;//起始方块
    static final int WINNING_VALUE = 10;//胜负判准

    //static final Color BACKGROUND_COLOR = Color.web("#BBADA0");//背景颜色
    static final Color BACKGROUND_COLOR = Color.web("#2AE0F9");//背景颜色
    static final Color EMPTY_TILE_COLOR = Color.web("#B2E0EE");//空位置颜色

    static final int TILE_PIXEL_LENGTH = 100;//像素长宽
    static final int TILE_PIXEL_RADIUS = TILE_PIXEL_LENGTH / 12;//像素半径
    static final int BOARD_PIXEL_PADDING = TILE_PIXEL_LENGTH / 7;
    static final int BOARD_PIXEL_LENGTH = (TILE_PIXEL_LENGTH * GRID_SIZE) + ((GRID_SIZE + 1) * BOARD_PIXEL_PADDING);

    final static Duration ANIMATION_DURATION_FIRST_PART = Duration.millis(125);
    final static Duration ANIMATION_DURATION_SECOND_PART = Duration.millis(125);
    final static Duration ANIMATION_PAUSE_BEFORE_SECOND_PART = ANIMATION_DURATION_FIRST_PART.multiply(0.85);
    final static double ANIMATION_MOVE_EASING = 0.9;

    static final int BUTTON_FONT_SIZE = 18;//提示字体大小
}

package org.frendo.game2048;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

class GameController {
    private final HashMap<Tile, TileView> visibleTileViews = new HashMap<>();
    private final Pane board = new Pane();
    private Stage primaryStage;
    private Game game = new Game();
    private ParallelTransition activeTransition;
    private boolean sawEndScreen = false;
    private final EventHandler<KeyEvent> gameEventHandler = (keyEvent) -> {
        switch (keyEvent.getCode()) {
            case UP:
                runMove(Game.Move.Up);
                break;
            case DOWN:
                runMove(Game.Move.Down);
                break;
            case LEFT:
                runMove(Game.Move.Left);
                break;
            case RIGHT:
                runMove(Game.Move.Right);
                break;
        }
    };

    GameController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    void startGame() {
        final ArrayList<Tile> initialTiles = game.addInitialTiles();
        ArrayList<Transition> creationTransitions = new ArrayList<>();

        for (Tile tile : initialTiles) {
            final TileView tileView = new TileView(tile);
            board.getChildren().add(tileView.pane);
            visibleTileViews.put(tile, tileView);

            final Transition transition = tileView.creationTransition();
            creationTransitions.add(transition);
        }

        ParallelTransition creationTransition = new ParallelTransition(creationTransitions.toArray(new Transition[0]));
        creationTransition.play();

        StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundFill(Config.BACKGROUND_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        root.getChildren().add(buildBackground());
        root.getChildren().add(board);

        Scene scene = new Scene(root, Config.BOARD_PIXEL_LENGTH, Config.BOARD_PIXEL_LENGTH);
        primaryStage.setTitle("2048");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, gameEventHandler);
        primaryStage.show();
    }

    private void runMove(Game.Move move) {
        if (!game.Movable())
            return;

        if (activeTransition != null) {
            final EventHandler<ActionEvent> oldHandler = activeTransition.getOnFinished();
            activeTransition.setOnFinished(event -> {
                oldHandler.handle(null);
                runMove(move);
            });
            activeTransition.setRate(10);
            return;
        }

        final Game.MoveResult moveResult = game.runMove(move);

        if (!moveResult.mergeResult.didChange)
            return;

        ArrayList<Transition> moveTransitions = new ArrayList<>();
        for (Tile movedTile : visibleTileViews.keySet()) {
            final TileView tileView = visibleTileViews.get(movedTile);
            Transition moveTransition = tileView.moveTransition();
            moveTransitions.add(moveTransition);
        }
        ParallelTransition parallelMoveTransition = new ParallelTransition(moveTransitions.toArray(new Animation[0]));

        ArrayList<Transition> popUpTransitions = new ArrayList<>();

        TileView newTileView = new TileView(moveResult.newTile);
        board.getChildren().add(newTileView.pane);
        visibleTileViews.put(moveResult.newTile, newTileView);
        Transition creationTransition = newTileView.creationTransition();
        popUpTransitions.add(creationTransition);

        for (Tile createdTile : moveResult.mergeResult.newTilesFromMerge.keySet()) {
            final TileView tileView = new TileView(createdTile);
            board.getChildren().add(tileView.pane);
            visibleTileViews.put(createdTile, tileView);
            Transition mergeTransition = tileView.mergeTransition();
            popUpTransitions.add(mergeTransition);
        }
        ParallelTransition parallelPopUpTransition = new ParallelTransition(popUpTransitions.toArray(new Animation[0]));
        PauseTransition waitBeforePoppingUpTransition = new PauseTransition(Config.ANIMATION_PAUSE_BEFORE_SECOND_PART);
        SequentialTransition overallPopUpTransition = new SequentialTransition(waitBeforePoppingUpTransition, parallelPopUpTransition);

        activeTransition = new ParallelTransition(parallelMoveTransition, overallPopUpTransition);

        activeTransition.setOnFinished(actionEvent -> {
            for (Tile createdTile : moveResult.mergeResult.newTilesFromMerge.keySet()) {
                for (Tile goneTile : moveResult.mergeResult.newTilesFromMerge.get(createdTile)) {
                    final TileView tileView = visibleTileViews.get(goneTile);
                    board.getChildren().remove(tileView.pane);
                    visibleTileViews.remove(goneTile);
                }
            }

            activeTransition = null;

            int highestNewMergeValue = 0;
            for (Tile createdTile : moveResult.mergeResult.newTilesFromMerge.keySet()) {
                if (createdTile.value > highestNewMergeValue)
                    highestNewMergeValue = createdTile.value;
            }

            ifGameover(highestNewMergeValue);
        });

        activeTransition.play();
    }

    private void ifGameover(int newestValue) {
        if (game.Movable() && (newestValue != Config.WINNING_VALUE || sawEndScreen))
            return;

        sawEndScreen = true;
        boolean didWin = newestValue == Config.WINNING_VALUE;

        primaryStage.removeEventHandler(KeyEvent.KEY_PRESSED, gameEventHandler);

        GridPane gridPane = new GridPane();
        gridPane.setPrefSize(board.getWidth(), board.getHeight());
        gridPane.setBackground(new Background(new BackgroundFill(Color.web("#000", 0.35), CornerRadii.EMPTY, Insets.EMPTY)));
        gridPane.setOpacity(0);
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setAlignment(Pos.CENTER);
        board.getChildren().add(gridPane);

        final Text text = new Text(didWin ? "YOU WON!!! Great job!" : "Oh rats, the game is over.\nGood try though :)");
        text.setFont(new Font(30));
        text.setFill(Color.WHITE);
        gridPane.add(text, 0, 0, 3, 1);

        int nextCol = 0;

        if (didWin) {
            final Button keepPlayingButton = new Button("Keep playing");
            keepPlayingButton.setFont(new Font(Config.BUTTON_FONT_SIZE));
            keepPlayingButton.setOnAction((e) -> {
                primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, gameEventHandler);
                board.getChildren().remove(gridPane);
            });
            gridPane.add(keepPlayingButton, nextCol++, 1);
        }

        final Button continueButton = new Button("New game");
        continueButton.setFont(new Font(Config.BUTTON_FONT_SIZE));
        continueButton.setOnAction((e) -> new GameController(primaryStage).startGame());
        gridPane.add(continueButton, nextCol++, 1);

        final Button quitButton = new Button("Quit");
        quitButton.setFont(new Font(Config.BUTTON_FONT_SIZE));
        quitButton.setOnAction((e) -> System.exit(0));
        gridPane.add(quitButton, nextCol, 1);

        FadeTransition transition = new FadeTransition(Duration.seconds(1), gridPane);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();
    }

    private Pane buildBackground() {
        Pane backgroundPane = new Pane();
        for (int y = 0; y < Config.GRID_SIZE; y++) {
            for (int x = 0; x < Config.GRID_SIZE; x++) {
                Rectangle r = new Rectangle(Config.TILE_PIXEL_LENGTH, Config.TILE_PIXEL_LENGTH);
                r.setArcWidth(Config.TILE_PIXEL_RADIUS);
                r.setArcHeight(Config.TILE_PIXEL_RADIUS);
                r.setFill(Config.EMPTY_TILE_COLOR);
                Point point = TileView.getPixelPoint(new Grid.Coordinate(x, y));
                r.setTranslateX(point.x);
                r.setTranslateY(point.y);
                backgroundPane.getChildren().add(r);
            }
        }
        return backgroundPane;
    }

}

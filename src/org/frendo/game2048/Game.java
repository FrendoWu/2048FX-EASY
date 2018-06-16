package org.frendo.game2048;

import java.util.ArrayList;

class Game {

    private Grid grid = new Grid();

    ArrayList<Tile> addInitialTiles() {
        ArrayList<Tile> initialTiles = new ArrayList<>();

        for (int i = 0; i < Config.STARTING_TILES; i++)
            initialTiles.add(grid.addRandomTile());

        return initialTiles;
    }

    MoveResult runMove(Move move) {
        Grid.MergeResult mergeResult = null;
        for (int i = 0; i < 4; i++) {
            if (i == move.rotations) {
                mergeResult = grid.mergeLeft();
            }
            grid.rotateClockwise();
        }
        assert mergeResult != null;
        grid.reassignCoordinates();

        for (Tile newTileFromMerge : mergeResult.newTilesFromMerge.keySet()) {
            for (Tile goneTile : mergeResult.newTilesFromMerge.get(newTileFromMerge)) {
                goneTile.spot = newTileFromMerge.spot;
            }
        }

        MoveResult moveResult = new MoveResult(mergeResult);

        if (mergeResult.didChange && !grid.openSpots().isEmpty())
            moveResult.newTile = grid.addRandomTile();

        return moveResult;
    }

    boolean Movable() {
        // if the board has open spots, you can move
        if (!grid.openSpots().isEmpty())
            return true;

        // if there's no open spots, but you can merge, then you can move
        boolean Movable = false;
        for (int i = 0; i < 4; i++) {
            if (grid.canMergeLeft()) {
                Movable = true;
            }
            grid.rotateClockwise();
        }
        return Movable;
    }

    enum Move {
        Left(0),
        Down(1),
        Right(2),
        Up(3);

        private int rotations;

        Move(int rotations) {
            this.rotations = rotations;
        }
    }

    class MoveResult {
        Tile newTile;
        Grid.MergeResult mergeResult;

        MoveResult(Grid.MergeResult mergeResult) {
            this.mergeResult = mergeResult;
        }
    }

}

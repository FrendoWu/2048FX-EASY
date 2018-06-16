package org.frendo.game2048;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

class Grid {

    private final Random randomizer = new Random();
    private Tile[][] tiles = new Tile[Config.GRID_SIZE][Config.GRID_SIZE];

    void reassignCoordinates() {
        for (int y = 0; y < Config.GRID_SIZE; y++) {
            for (int x = 0; x < Config.GRID_SIZE; x++) {
                final Tile tile = tiles[y][x];
                if (tile != null)
                    tile.spot = new Coordinate(x, y);
            }
        }
    }

    void rotateClockwise() {
        Tile[][] newTiles = new Tile[Config.GRID_SIZE][Config.GRID_SIZE];
        for (int y = 0; y < Config.GRID_SIZE; y++) {
            for (int x = 0; x < Config.GRID_SIZE; x++) {
                newTiles[y][x] = tiles[(Config.GRID_SIZE - 1) - x][y];
            }
        }
        tiles = newTiles;
    }

    MergeResult mergeLeft() {
        MergeResult mergeResult = new MergeResult();
        mergeResult.didChange = false;

        for (int y = 0; y < Config.GRID_SIZE; y++) {

            Tile lastUnmergedTile = null;
            ArrayList<Tile> newRow = new ArrayList<>();

            for (int x = 0; x < Config.GRID_SIZE; x++) {
                Tile currentTile = tiles[y][x];

                if (currentTile == null)//空的，跳过
                    continue;

                if (lastUnmergedTile == null) {
                    lastUnmergedTile = currentTile;

                } else if (currentTile.value != lastUnmergedTile.value) {

                    newRow.add(lastUnmergedTile);
                    lastUnmergedTile = currentTile;

                } else {
                    Tile newTile = new Tile(currentTile.value + 1);
                    newRow.add(newTile);

                    ArrayList<Tile> mergedTiles = new ArrayList<>();
                    mergedTiles.add(lastUnmergedTile);
                    mergedTiles.add(currentTile);

                    mergeResult.newTilesFromMerge.put(newTile, mergedTiles);

                    lastUnmergedTile = null;

                }
            }

            if (lastUnmergedTile != null)
                newRow.add(lastUnmergedTile);

            final int emptyCellsToFill = Config.GRID_SIZE - newRow.size();
            for (int i = 0; i < emptyCellsToFill; i++) {
                newRow.add(null);
            }

            for (int i = 0; i < Config.GRID_SIZE; i++) {
                Tile a = newRow.get(i);
                Tile b = tiles[y][i];

                if (a != b) {
                    mergeResult.didChange = true;
                    break;
                }
            }

            tiles[y] = newRow.toArray(new Tile[0]);
        }

        return mergeResult;
    }

    Tile addRandomTile() {
        Coordinate spot = findOpenSpot();
        assert spot != null;

        int value = randomizer.nextDouble() < 0.9 ? 0 : 1;

        if (Config.QUICK_MODE) {
            for (int y = 0; y < Config.GRID_SIZE; y++) {
                for (int x = 0; x < Config.GRID_SIZE; x++) {
                    final Tile tile = tiles[y][x];
                    if (tile == null)
                        continue;

                    int maybe = tile.value;
                    if (maybe > value)
                        value = maybe;
                }
            }
        }

        final Tile tile = new Tile(value, spot);
        tiles[spot.y][spot.x] = tile;
        return tile;
    }

    private Coordinate findOpenSpot() {
        final ArrayList<Coordinate> coordinates = openSpots();
        if (openSpots().isEmpty())
            return null;

        int i = randomizer.nextInt(coordinates.size());
        return coordinates.get(i);
    }

    ArrayList<Coordinate> openSpots() {
        ArrayList<Coordinate> spots = new ArrayList<>();
        for (int y = 0; y < Config.GRID_SIZE; y++) {
            for (int x = 0; x < Config.GRID_SIZE; x++) {
                if (tiles[y][x] == null)
                    spots.add(new Coordinate(x, y));
            }
        }
        return spots;
    }

    boolean canMergeLeft() {
        for (int y = 0; y < Config.GRID_SIZE; y++) {

            Tile lastTile = null;
            for (int x = 0; x < Config.GRID_SIZE; x++) {

                Tile thisTile = tiles[y][x];

                if (thisTile == null)
                    continue;

                if (lastTile == null) {
                    lastTile = thisTile;
                } else if (lastTile.value != thisTile.value) {
                    lastTile = thisTile;
                } else {
                    return true;
                }
            }
        }

        return false;
    }

    static class Coordinate {
        final int x;
        final int y;

        Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    class MergeResult {
        boolean didChange;
        HashMap<Tile, ArrayList<Tile>> newTilesFromMerge = new HashMap<>();
    }

}

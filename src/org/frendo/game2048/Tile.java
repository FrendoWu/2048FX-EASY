package org.frendo.game2048;

class Tile {

    int value;
    Grid.Coordinate spot;

    Tile(int value) {
        this.value = value;
    }

    Tile(int value, Grid.Coordinate spot) {
        this.value = value;
        this.spot = spot;
    }
}

/*
package model;

import java.util.HashSet;
import java.util.Set;

public class Cell {
    private static final int SAME = 0;
    private static final int NORTH = -1;
    private static final int SOUTH = 1;
    private static final int WEST = -1;
    private static final int EAST = 1;

    private final int x;
    private final int y;

    private final Automat a;

    public Cell(int x, int y, Automat a){
        this.x = x;
        this.y = y;
        this.a = a;
    }

    */
/**
     * Create Cell from String
     *//*

    public Cell(String s){
        this.x = x;
        this.y = y;
        this.a = a;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int[] getCoordinates() {
        int[] coordinates = {x,y};
        return coordinates;
    }

    public Set<Cell> neighborCell() {
        HashSet<Cell> neighbors = new HashSet<Cell>();
        neighbors.add(getNorthWest());
        neighbors.add(getNorth());
        neighbors.add(getNorthEast());
        neighbors.add(getWest());
        neighbors.add(getEast());
        neighbors.add(getSouthEast());
        neighbors.add(getSouth());
        neighbors.add(getSouthWest());
        return neighbors;
    }

    private Cell getNorthWest() {
        return neighbor(WEST, NORTH);
    }

    private Cell getNorth() {
        return neighbor(SAME, NORTH);
    }

    private Cell getNorthEast() {
        return neighbor(EAST, NORTH);
    }

    private Cell getEast() {
        return neighbor(EAST, SAME);
    }

    private Cell getWest() {
        return neighbor(WEST, SAME);
    }

    private Cell getSouthWest() {
        return neighbor(WEST, SOUTH);
    }

    private Cell getSouth() {
        return neighbor(SAME, SOUTH);
    }

    private Cell getSouthEast() {
        return neighbor(EAST, SOUTH);
    }

    private Cell neighbor(int xDiff, int yDiff) {
        int fw = a.getFw();
        int fh = a.getFh();
        return new Cell((fw + x + xDiff) % fw, (fh + y + yDiff) % fh, a);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Cell other = (Cell) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
}
*/

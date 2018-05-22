package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Observable;
import java.util.*;

public class Automat extends Observable implements Runnable {

    // Observers

    // Feldgröße
    private final int fw;
    private final int fh;

    // Spielregeln
    private int survival[] = {2,3};
    private int birth[] = {3};

    private long generation;

    // Speed
    private int speed;
    private int sleep;

    private boolean isRunning;

    private Set<Cell> livingCells;

    public byte viewCounter; // TODO Counter oder direkt Liste mit referenzierten views?

    public Automat(int fw, int fh) {
        isRunning = false;
        this.fw = fw;
        this.fh = fh;
        init();
    }

    public void init() {
        setSpeed(50);
        generation = 0;
        viewCounter = 0;
        livingCells = new HashSet<>();
    }

    public void setSpeed(int speed) {
        if (speed > 101 || speed < 1) {
            throw new IllegalArgumentException( "Geschwindigkeit muss zwischen 1 und 100 liegen!" );
        } else {
            this.speed = speed;
            sleep = (int)(Math.pow(0.96, speed)*1000);
        }
        setChanged();
        notifyObservers(2);

    }

    public int getSpeed() {
        return speed;
    }

    public void addLivingCellAt(int x, int y) {
        livingCells.add(new Cell(x,y));
        setChanged();
        notifyObservers(1);
    }

    public void toggleLivingCellAt(int x, int y) {
        Cell cell = new Cell(x,y);
        if (livingCells.contains(cell)) livingCells.remove(cell);
        else livingCells.add(cell);
        setChanged();
        notifyObservers(1);
    }

    public void removeLivingCellAt(int x, int y) {
        livingCells.remove(new Cell(x,y));
        setChanged();
        notifyObservers(1);
    }

    public void clearLivingCells() {
        livingCells.clear();
        setChanged();
        notifyObservers(1);
    }

    public void reset() {
        generation = 0;
        clearLivingCells();
    }

    private void calculateNextGen() {
        // Nur Zellen in unmittelbarer Nachbarschaft zu Lebenden Zellen müssen neu berechnet werden
        // Step 1: Vorigen Zwischenspeicher löschen. Lebende und dessen Nachbarn zur Überprüfung zwischenspeichern.
        Set<Cell> ngLivingCells = new HashSet<>(livingCells);       // TODO Exception in thread "main" java.util.ConcurrentModificationException
        Set<Cell> cloneLivingCells = new HashSet<>(livingCells);    // TODO Concurrency!!!
        Set<Cell> evolvingCells = new HashSet<>();

        for (Cell c : cloneLivingCells) {
            evolvingCells.add(c);
            evolvingCells.addAll(c.getNeighborCells());
        }
        // Step 2: Auf Lebende Zellen in Nachbarschaft prüfen
        Set<Cell> currentNeighbors;
        for (Cell c : evolvingCells) {
            currentNeighbors = new HashSet<>(c.getNeighborCells());

            // Step 2.1: Lebende Nachbarn zählen
            int neighborCount = 0;
            for (Cell cn : currentNeighbors) {
                if (cloneLivingCells.contains(cn))
                    neighborCount++;
            }

            // Step 2.2: Spielregeln anwenden
            final int temp = neighborCount;
            if (Arrays.stream(survival).noneMatch(x -> x == temp)) ngLivingCells.remove(c);
            if (Arrays.stream(birth).anyMatch(x -> x == temp)) ngLivingCells.add(c);

        }

        // Step 3: Generation übernehmen und ++
        livingCells.clear();
        livingCells.addAll(ngLivingCells);
        generation++;
        setChanged();
        notifyObservers(1);
    }

    public long getGeneration() {
        return generation;
    }

    public Set<Cell> getLivingCells() {
        return livingCells;
    }

    public int getFw() {

        return fw;
    }

    public int getFh() {
        return fh;
    }

    @Override
    public void run() {
        isRunning = true;
        //init();
        while (isRunning) {
            try {
                calculateNextGen();
                Thread.sleep(sleep);
                //System.out.println("Thread läuft");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        isRunning = false;
    }

    /**
     * Klasse für einzelne Zellen mit Zugriff auf Feldgröße
     */
    public class Cell {
        private static final int SAME = 0;
        private static final int NORTH = -1;
        private static final int SOUTH = 1;
        private static final int WEST = -1;
        private static final int EAST = 1;

        private final int x;
        private final int y;
        
        public Cell(int x, int y){
            this.x = (fw + x) % fw;
            this.y = (fh + y) % fh;
        }

        public int[] getCoordinates() {
            int[] coordinates = {x,y};
            return coordinates;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Set<Cell> getNeighborCells() {
            HashSet<Cell> neighbors = new HashSet<>();
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
            return new Cell((fw + x + xDiff) % fw, (fh + y + yDiff) % fh);
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

}

package GameOfLife.model;

import GameOfLife.controller.Mode;
import GameOfLife.view.GameFrame;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Observable;
import java.util.*;

public class GameOfLifeModel extends Observable implements Runnable, Cloneable, Serializable {

    // Instanz Counter
    private static int instanceCounter = 0;
    private int instanceNumber;

    // aktive Views
    private ArrayList<GameFrame> activeViews;
    public int viewCounter;


    // Feldgröße
    private final int fw;
    private final int fh;

    // Spielregeln
    private int[] survival = {2,3};
    private int[] birth = {3};

    private long generation;

    // Speed
    private int speed;
    private int sleep;

    // Mode
    private Mode mode;
    private boolean isRunning;

    // Daten
    private Set<Cell> livingCells;


    public GameOfLifeModel(int fw, int fh) {
        instanceCounter++;
        instanceNumber = instanceCounter;
        activeViews = new ArrayList<>();
        setMode(Mode.PAINT);
        isRunning = false;
        this.fw = fw;
        this.fh = fh;
        setSpeed(50);
        generation = 0;
        viewCounter = 0;
        livingCells = Collections.synchronizedSet(new HashSet<>());
    }

    public GameOfLifeModel clone() {
        GameOfLifeModel clone = new GameOfLifeModel(fw,fh);
        clone.livingCells = new HashSet<>(this.livingCells);
        return clone;
    }

    public void setRules (String s) {
        String[] parts = s.split("/");
        this.survival = new int[parts[0].length()];
        this.birth = new int[parts[1].length()];
        for (int i = 0; i < parts[0].length(); i++) {
            survival[i] = Character.getNumericValue(parts[0].charAt(i));
        }
        for (int i = 0; i < parts[1].length(); i++) {
            birth[i] = Character.getNumericValue(parts[1].charAt(i));
        }
    }

    public ArrayList<GameFrame> getActiveViews() {
        return activeViews;
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    public int getViewCount() {
        return viewCounter;
    }

    public void addActiveView(GameFrame activeView) {
        activeViews.add(activeView);
        viewCounter++;
    }

    public void removeActiveView(GameFrame activeView) {
        activeViews.remove(activeView);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        setChanged();
        notifyObservers(2);
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
        //push();
        setChanged();
        notifyObservers(1);
    }

    public void toggleLivingCellAt(int x, int y) {
        Cell cell = new Cell(x,y);
        if (livingCells.contains(cell)) livingCells.remove(cell);
        else livingCells.add(cell);
        //push();
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
        // Lebende und dessen Nachbarn zur Überprüfung zwischenspeichern.
        Set<Cell> evolvingCells = Collections.synchronizedSet(new HashSet<>(livingCells));

        evolvingCells.addAll(
            livingCells.parallelStream().collect(
                HashSet::new,
                (c, e) -> c.addAll(e.getNeighborCells()),
                AbstractCollection::addAll
            )
        );

        // Dieser Block ist nur notwendig wenn neue Zellen bei 0 Nachbarn geboren werden.
        if (Arrays.stream(birth).anyMatch(x -> x == 0)) {
            for (int i = 0; i < fh; i++) {
                for (int j = 0; j < fw; j++) {
                    evolvingCells.add(new Cell(j,i));
                }
            }
        }

        // Für jede zu überprüfende Zelle Nachbarn zählen und neuen Status
        livingCells = evolvingCells.parallelStream().collect(HashSet::new, (cellSet, cell) -> {
            int neighborCount = 0;

            for (Cell c : cell.getNeighborCells()) {
                if (livingCells.contains(c))
                    neighborCount++;
            }

            final int finalNeighborCount = neighborCount;
            if (Arrays.stream(birth).anyMatch(x -> x == finalNeighborCount)) {
                cellSet.add(cell);
            } else if (livingCells.contains(cell)) {
                if (Arrays.stream(survival).anyMatch(x -> x == finalNeighborCount)) cellSet.add(cell);
            }

            },
            AbstractCollection::addAll
        );
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
        while (isRunning && !activeViews.isEmpty()) {
            try {
                calculateNextGen();
                Thread.sleep(sleep);
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

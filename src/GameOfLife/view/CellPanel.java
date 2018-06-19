package GameOfLife.view;

import GameOfLife.model.GameOfLifeModel.Cell;
import GameOfLife.model.GameOfLifeModel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.*;

public class CellPanel extends JPanel implements Observer {

    final GameOfLifeModel model;

    private int zoom;
    private Shape selectedShape;
    private Set<Cell> cloneLivingCells;

    // Orientierung
    public AffineTransform transform;
    public AffineTransform mouseTransform;
    public Orientation orientation;

    // Farben
    public Color colorAlive;
    public Color colorDead;
    public boolean gridOn;

    public CellPanel(GameOfLifeModel model) {
        this.model = model;
        colorAlive = Color.CYAN;
        colorDead = Color.DARK_GRAY;
        gridOn = true;
        orientation = Orientation.NORTH;
        setZoom(8);
        selectedShape = null;
        setBackground(colorDead);
        spawnCloneLivingCells();
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
        setPreferredSize(new Dimension(zoom * model.getFw(), zoom * model.getFh()));
        setMaximumSize(new Dimension(zoom * model.getFw(), zoom * model.getFh()));
        setMinimumSize(new Dimension(zoom * model.getFw(), zoom * model.getFh()));
        setSize(new Dimension(zoom * model.getFw(), zoom * model.getFh()));
        setTransformation();
    }

    public void setSelectedShape(Shape selectedShape) {
        this.selectedShape = selectedShape;
    }

    public void spawnCloneLivingCells() {
        cloneLivingCells = new HashSet<>(model.getLivingCells());
        repaint();
    }

    public void setTransformation() {
        AffineTransform at = new AffineTransform();
        switch (orientation) {
            case NORTH: {
                transform = null;
                mouseTransform = null;
                break;
            }
            case EAST: {
                at.rotate(Math.PI/2, getWidth()/2, getHeight()/2);
                transform = at;
                try {
                    mouseTransform = (AffineTransform) transform.clone();
                    mouseTransform.invert();
                } catch (NoninvertibleTransformException e1) {
                    e1.printStackTrace();
                }
                break;
            }
            case SOUTH: {
                at.rotate(Math.PI, getWidth()/2, getHeight()/2);
                transform = at;
                mouseTransform = (AffineTransform) transform.clone();
                break;
            }
            case WEST: {
                at.rotate(-Math.PI/2, getWidth()/2, getHeight()/2);
                transform = at;
                try {
                    mouseTransform = (AffineTransform) transform.clone();
                    mouseTransform.invert();
                } catch (NoninvertibleTransformException e1) {
                    e1.printStackTrace();
                }
                break;
            }
            default: {
                transform = null;
                mouseTransform = null;
            }
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Orientierung anwenden
        if (transform != null) {
            g2d.transform(transform);
        }

        // Zellen zeichnen
        for (Cell c : cloneLivingCells) {
            g2d.setColor(colorAlive);
            g2d.fillRect(c.getX() * zoom, c.getY() * zoom, zoom, zoom);
        }

        // Cursor zeichnen
        if (selectedShape != null) {
            g2d.setColor(Color.YELLOW);
            g2d.fill(selectedShape);
        }

        // Gitterlinien zeichnen
        if (gridOn) {
            g2d.setColor(Color.black);
            for (int i = 0; i < model.getFh(); i++) {
                g2d.drawLine(0,i * zoom,model.getFw() * zoom,i * zoom);
            }
            for (int i = 0; i < model.getFw(); i++) {
                g2d.drawLine(i * zoom,0,i * zoom,model.getFh() * zoom);
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if ((int)arg == 1) {
            spawnCloneLivingCells();
        }
    }
}

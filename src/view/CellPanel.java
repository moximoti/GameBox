package view;

import model.Automat.Cell;
import model.Automat;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CellPanel extends JPanel {

    Automat linkedGame;
    private int zoom;
    private Shape selectedShape;

    public CellPanel(Automat linkedGame) {
        this.linkedGame = linkedGame;
        zoom = 8;
        selectedShape = null;
    }

    public Shape getSelectedShape() {
        return selectedShape;
    }

    public int getZoom() {
        return zoom;
    }

    public void setSelectedShape(Shape selectedShape) {
        this.selectedShape = selectedShape;
    }




    //public void paintComponent(Graphics g) {
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        //cellElements.clear();
        Set<Cell> cloneLivingCells = new HashSet<>(linkedGame.getLivingCells());

/*
        AffineTransform at = new AffineTransform();
        //at.setToScale(2, 2);
        //at.setToRotation(Math.PI/2, getWidth()/2, getHeight()/2);
        //at.scale(2, 2);
        at.rotate(Math.PI/2, getWidth()/2, getHeight()/2);
        g2d.transform(at);
*/


        for (int i = 0; i < linkedGame.getFh(); i++) {
            for (int k = 0; k < linkedGame.getFw(); k++) {
                Rectangle rect = new Rectangle(i * zoom, k * zoom, zoom, zoom);
                g2d.setColor(Color.darkGray);
                g2d.fill(rect);
                //cellElements.add(rect);

                //Rahmen
                g2d.setColor(Color.black);
                g2d.draw(rect);
            }
        }

        for (Cell c : cloneLivingCells) {
            g2d.setColor(Color.green);
            g2d.fillRect(c.getX() * zoom, c.getY() * zoom, zoom, zoom);
            g2d.setColor(Color.black);
            g2d.drawRect(c.getX() * zoom, c.getY() * zoom, zoom, zoom);

        }

        if (selectedShape != null) {
            //Graphics2D newG2 = (Graphics2D) g2d.create();
            g2d.setColor(Color.YELLOW);
            g2d.fill(selectedShape);
            g2d.setColor(Color.black);
            g2d.draw(selectedShape);
            //newG2.dispose(); // because this is a created Graphics object
        }

    }
}

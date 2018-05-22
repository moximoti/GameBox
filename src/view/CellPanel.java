package view;

import controller.GameController;
import model.Automat.Cell;
import model.Automat;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class CellPanel extends JPanel implements Observer {

    Automat model;
    private int zoom;
    private Shape selectedShape;

    public CellPanel(Automat model) {
        this.model = model;
        zoom = 8;
        selectedShape = null;
        setPreferredSize(new Dimension(zoom * model.getFw(), zoom * model.getFh()));
        setMaximumSize(new Dimension(zoom * model.getFw(), zoom * model.getFh()));
        setMinimumSize(new Dimension(zoom * model.getFw(), zoom * model.getFh()));
        setBackground(Color.YELLOW);
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
        Set<Cell> cloneLivingCells = new HashSet<>(model.getLivingCells());

/*
        AffineTransform at = new AffineTransform();
        //at.setToScale(2, 2);
        //at.setToRotation(Math.PI/2, getWidth()/2, getHeight()/2);
        //at.scale(2, 2);
        at.rotate(Math.PI/2, getWidth()/2, getHeight()/2);
        g2d.transform(at);
*/


        for (int i = 0; i < model.getFh(); i++) {
            for (int k = 0; k < model.getFw(); k++) {
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

    @Override
    public void update(Observable o, Object arg) {
        if ((int)arg == 1) repaint();
    }
}

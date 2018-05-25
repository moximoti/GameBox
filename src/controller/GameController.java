package controller;

import model.Automat;
import view.CellPanel;
import view.GameFrame;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;


public class GameController {

    // View Elements
    private GameFrame gameFrame;
    private CellPanel cellPanel;

    // Model
    private Automat model;


    public GameController(Automat model) {
        this.model = model;
        this.gameFrame = new GameFrame(model, this);
        this.cellPanel = gameFrame.getCellPanel();

        MyMouseAdapter ma = new MyMouseAdapter();
        cellPanel.addMouseListener(ma);
        cellPanel.addMouseMotionListener(ma);
        gameFrame.cellPanelWrapper.addMouseWheelListener(ma);
        model.addObserver(cellPanel);
        model.addObserver(gameFrame);

    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }

    private class MyMouseAdapter extends MouseAdapter {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            super.mouseWheelMoved(e);
            int newValue = cellPanel.getZoom()+e.getWheelRotation();
            if (newValue > 1 && newValue <= 100) {
                cellPanel.setSelectedShape(null);
                cellPanel.setZoom(newValue);
                gameFrame.setSize(gameFrame.getSize());
                gameFrame.repaint();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (model.getMode() == Mode.TOGGLE) {
                if (cellPanel.mouseTransform != null) {
                    Point2D p = cellPanel.mouseTransform.transform(new Point2D.Double(e.getX(), e.getY()), null);
                    model.addLivingCellAt((int)p.getX() / cellPanel.getZoom(), (int)p.getY() / cellPanel.getZoom());
                } else {
                    super.mousePressed(e);
                    model.toggleLivingCellAt(e.getX() / cellPanel.getZoom(), e.getY() / cellPanel.getZoom());
                    cellPanel.setSelectedShape(null);
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
            cellPanel.setSelectedShape(null);
            cellPanel.repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (model.getMode() != Mode.RUN) {
                super.mouseMoved(e);
                if (cellPanel.mouseTransform != null) {
                    Point2D p = cellPanel.mouseTransform.transform(new Point2D.Double(e.getX(), e.getY()), null);
                    Shape s = new Rectangle((int)p.getX()/cellPanel.getZoom()*cellPanel.getZoom(),(int)p.getY()/cellPanel.getZoom()*cellPanel.getZoom(),cellPanel.getZoom(),cellPanel.getZoom());
                    cellPanel.setSelectedShape(s);
                } else {
                    Shape s = new Rectangle(e.getX()/cellPanel.getZoom()*cellPanel.getZoom(),e.getY()/cellPanel.getZoom()*cellPanel.getZoom(),cellPanel.getZoom(),cellPanel.getZoom());
                    cellPanel.setSelectedShape(s);
                }
                cellPanel.repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (model.getMode() == Mode.PAINT) {
                super.mouseDragged(e);
                if (cellPanel.mouseTransform != null) {
                    Point2D p = cellPanel.mouseTransform.transform(new Point2D.Double(e.getX(), e.getY()), null);
                    model.addLivingCellAt((int)p.getX() / cellPanel.getZoom(), (int)p.getY() / cellPanel.getZoom());
                    Shape s = new Rectangle((int)p.getX()/cellPanel.getZoom()*cellPanel.getZoom(),(int)p.getY()/cellPanel.getZoom()*cellPanel.getZoom(),cellPanel.getZoom(),cellPanel.getZoom());                    cellPanel.setSelectedShape(s);
                    cellPanel.setSelectedShape(s);
                } else {
                    model.addLivingCellAt(e.getX() / cellPanel.getZoom(), e.getY() / cellPanel.getZoom());
                    Shape s = new Rectangle(e.getX()/cellPanel.getZoom()*cellPanel.getZoom(),e.getY()/cellPanel.getZoom()*cellPanel.getZoom(),cellPanel.getZoom(),cellPanel.getZoom());
                    cellPanel.setSelectedShape(s);
                }
                //System.out.println("Mouse dragging detected! Actual mouse position is: " + e.getX()+ "," + e.getY() + ".");

            }
        }
    }

}

package controller;

import model.Automat;
import view.CellPanel;
import view.GameFrame;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class GameController {

    // View Elements
    private GameFrame gameFrame;
    private CellPanel cellPanel;

    // Parent View

    // Model
    private Automat model;

    // Control Logic
    private Mode mode;


    public GameController(Automat model) {
        this.model = model;
        this.gameFrame = new GameFrame(model, this);
        this.cellPanel = gameFrame.getCellPanel();
        this.mode = Mode.PAINT;

        MyMouseAdapter ma = new MyMouseAdapter();
        cellPanel.addMouseListener(ma);
        cellPanel.addMouseMotionListener(ma);
        model.addObserver(cellPanel);
        model.addObserver(gameFrame);

    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }


    public GameFrame getGameFrame() {
        return gameFrame;
    }

    private class MyMouseAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (mode == Mode.TOGGLE) {
                super.mousePressed(e);
                model.toggleLivingCellAt(e.getX()/cellPanel.getZoom(),e.getY()/cellPanel.getZoom());
                cellPanel.setSelectedShape(null);
                //cellPanel.repaint();
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
            if (mode != Mode.RUN) {
                super.mouseMoved(e);
                Shape s = new Rectangle(e.getX()/cellPanel.getZoom()*cellPanel.getZoom(),e.getY()/cellPanel.getZoom()*cellPanel.getZoom(),cellPanel.getZoom(),cellPanel.getZoom());
                cellPanel.setSelectedShape(s);
                cellPanel.repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (mode == Mode.PAINT) {
                //System.out.println("Mouse dragging detected! Actual mouse position is: " + e.getX()+ "," + e.getY() + ".");
                model.addLivingCellAt(e.getX() / cellPanel.getZoom(), e.getY() / cellPanel.getZoom());

                Shape s = new Rectangle(e.getX()/cellPanel.getZoom()*cellPanel.getZoom(),e.getY()/cellPanel.getZoom()*cellPanel.getZoom(),cellPanel.getZoom(),cellPanel.getZoom());
                cellPanel.setSelectedShape(s);
            }
        }
    }

}

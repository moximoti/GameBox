package controller;

import model.Automat;
import view.CellPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.util.stream.Collectors;


public class CellPanelController {

    volatile CellPanel cellPanel;
    volatile Automat linkedGame;
    Mode mode;


    public CellPanelController(CellPanel cellPanel, Automat linkedGame) {
        this.linkedGame = linkedGame;
        this.cellPanel = cellPanel;
        this.mode = Mode.PAINT;

        cellPanel.addMouseListener(new MyMouseAdapter());
        cellPanel.addMouseMotionListener(new MyMouseAdapter());

    }

    private class MyMouseAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (mode == Mode.TOGGLE) {
                super.mousePressed(e);
                linkedGame.toggleLivingCellAt(e.getX()/cellPanel.getZoom(),e.getY()/cellPanel.getZoom());
                cellPanel.setSelectedShape(null);
                cellPanel.repaint();
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
//                for (Shape s : cellPanel.getCellElements()) {
//                    if (s.contains(e.getPoint())) {
//                        super.mouseMoved(e);
//                        cellPanel.setSelectedShape(s);
//                        cellPanel.repaint();
//                    }
//                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (mode == Mode.PAINT) {
                //System.out.println("Mouse dragging detected! Actual mouse position is: " + e.getX()+ "," + e.getY() + ".");
                linkedGame.addLivingCellAt(e.getX() / cellPanel.getZoom(), e.getY() / cellPanel.getZoom());

                Shape s = new Rectangle(e.getX()/cellPanel.getZoom()*cellPanel.getZoom(),e.getY()/cellPanel.getZoom()*cellPanel.getZoom(),cellPanel.getZoom(),cellPanel.getZoom());
                cellPanel.setSelectedShape(s);
                cellPanel.repaint();

//                for (Shape s : cellPanel.getCellElements()) {
//                    if (s.contains(e.getPoint())) {
//                        super.mouseDragged(e);
//                        cellPanel.setSelectedShape(s);
//                        cellPanel.repaint();
//                    }
//                }
            }
        }
    }

}

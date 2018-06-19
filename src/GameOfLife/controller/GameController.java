package GameOfLife.controller;

import GameOfLife.model.GameOfLifeModel;
import GameOfLife.view.CellPanel;
import GameOfLife.view.GameFrame;
import MainWindow.view.MainFrame;
import GameOfLife.view.Orientation;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.io.*;


public class GameController {

    // View Elements
    private GameFrame gameFrame;
    private CellPanel cellPanel;

    // Model
    private GameOfLifeModel model;


    public GameController(GameOfLifeModel model) {
        this.model = model;
        this.gameFrame = new GameFrame(model, this);
        this.cellPanel = gameFrame.getCellPanel();

        MyMouseAdapter ma = new MyMouseAdapter();
        cellPanel.addMouseListener(ma);
        cellPanel.addMouseMotionListener(ma);
        gameFrame.cellPanelWrapper.addMouseWheelListener(ma);
        model.addObserver(cellPanel);
        model.addObserver(gameFrame);

        addListeners();
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }

    private void addListeners() {
        // Menu Bar
        gameFrame.rules.addActionListener(event -> {
            String[] options = {
                    "23/3",
                    "236/3",
                    "135/35",
                    "1357/1357",
                    "12345/3",
                    "0123/01234",
                    "01234678/0123478"
            };
            String choice = (String) JOptionPane.showInputDialog(
                    gameFrame,
                    "Regelwerk wählen:",
                    "Regelwerk",
                    JOptionPane.QUESTION_MESSAGE,
                    null, options,
                    options[0] );
            if (choice != null) {
                System.out.println(choice);
                model.setRules(choice);
            }
        });
        gameFrame.loadPreset.addActionListener(event -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new MyFilter(".txt"));
            chooser.setAcceptAllFileFilterUsed(false);
            int result = chooser.showDialog(gameFrame, "Preset auswählen");
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();

                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(selectedFile));
                    String line;
                    if (!(line = br.readLine()).equals("#Life 1.06")) {
                        System.out.println("Wrong Filetype! Make sure to use Life 1.06 pattern files.");
                        return;
                    }
                    model.clearLivingCells();
                    while((line = br.readLine()) != null) {
                        String[] parts = line.split(" ");
                        int x = Integer.valueOf(parts[0]);
                        int y = Integer.valueOf(parts[1]);
                        model.addLivingCellAt(x+model.getFw()/2,y+model.getFh()/2);

                    }
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                } catch(IOException e) {
                    e.printStackTrace();
                } finally {
                    if(br != null) {
                        try {
                            br.close();
                        } catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        gameFrame.quit.addActionListener(event -> {
            for (GameFrame f : model.getActiveViews()) {
                f.dispose();
            }
        });
        gameFrame.colors.addActionListener(event -> {
            Color alive = JColorChooser.showDialog(gameFrame, "Lebende Zellen:", cellPanel.colorAlive);
            if (alive != null)
                cellPanel.colorAlive = alive;
            Color dead = JColorChooser.showDialog(gameFrame, "Tote Zellen:", cellPanel.colorDead);
            if (dead != null)
                cellPanel.colorDead = dead;
            cellPanel.setBackground(dead);
            String[] options = {"An",
                    "Aus"};
            int n = JOptionPane.showOptionDialog(gameFrame,
                    "Gitterlinien anzeigen?",
                    "Gitter",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            System.out.println(n);
            if (n == 0) cellPanel.gridOn = true;
            if (n == 1) cellPanel.gridOn = false;

        });
        gameFrame.newView.addActionListener(event -> {
            // View und Controller erzeugen
            GameController gameController = new GameController(model);
            ((MainFrame)gameFrame.getTopLevelAncestor()).addChild(gameController.getGameFrame());
            gameController.getGameFrame().toFront();
            gameController.getGameFrame().requestFocus();
            gameController.getGameFrame().requestFocusInWindow();
        });
        gameFrame.clone.addActionListener(event -> {
            // View und Controller erzeugen
            GameOfLifeModel modelClone = null;
            try {
                modelClone = (GameOfLifeModel) model.clone();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            GameController gameController = new GameController(modelClone);
            ((MainFrame)gameFrame.getTopLevelAncestor()).addChild(gameController.getGameFrame());
            gameController.getGameFrame().toFront();
            gameController.getGameFrame().requestFocus();
            gameController.getGameFrame().requestFocusInWindow();
        });
        gameFrame.rotate.addActionListener( e -> {
            // Neues Model erzeugen
            String[] options = {"Nord","Ost","Süd","West"};
            String orientation = (String) JOptionPane.showInputDialog(
                    gameFrame,
                    "Bitte Orientierung der Ansicht auswählen:",
                    "Orientierung",
                    JOptionPane.QUESTION_MESSAGE,
                    null, options,
                    options[0] );
            if (orientation != null) {
                switch (orientation) {
                    case "Nord": {
                        cellPanel.orientation = Orientation.NORTH;
                        break;
                    }
                    case "Ost": {
                        cellPanel.orientation = Orientation.EAST;
                        break;
                    }
                    case "Süd": {
                        cellPanel.orientation = Orientation.SOUTH;
                        break;
                    }
                    case "West": {
                        cellPanel.orientation = Orientation.WEST;
                        break;
                    }
                }
                cellPanel.setTransformation();
            }
        });
        // Controls
        gameFrame.speedSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider)changeEvent.getSource();
            //if ( !source.getValueIsAdjusting() )
            model.setSpeed(source.getValue());
        });
        gameFrame.playButton.addActionListener(event -> {
            if (event.getActionCommand().equals("Start")) {
                gameFrame.playButton.setText("Stop");
                new Thread(model).start();
                model.setMode(Mode.RUN);
                gameFrame.drawCellButton.setEnabled(false);
                gameFrame.setCellButton.setEnabled(false);
            } else if (event.getActionCommand().equals("Stop")) {
                model.stop();
                gameFrame.playButton.setText("Start");
                gameFrame.drawCellButton.setEnabled(true);
                gameFrame.setCellButton.setEnabled(true);
                if (gameFrame.drawCellButton.isSelected()) {
                    model.setMode(Mode.PAINT);
                    gameFrame.drawCellButton.setEnabled(false);
                } else {
                    model.setMode(Mode.TOGGLE);
                    gameFrame.setCellButton.setEnabled(false);
                }
            }

        });

        gameFrame.drawCellButton.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                model.setMode(Mode.PAINT);
                gameFrame.setCellButton.setSelected(false);
                gameFrame.drawCellButton.setEnabled(false);
            }
        });

        gameFrame.setCellButton.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                model.setMode(Mode.TOGGLE);
                gameFrame.drawCellButton.setSelected(false);
                gameFrame.setCellButton.setEnabled(false);
            }
        });
        gameFrame.resetButton.addActionListener(event -> model.reset());

        gameFrame.addInternalFrameListener(new InternalFrameAdapter(){
            public void internalFrameClosing(InternalFrameEvent e) {
                model.removeActiveView(gameFrame);
            }
        });
/*


        loadPreset;

*/

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
    class MyFilter extends FileFilter {
        private String endung;

        public MyFilter(String endung) {
            this.endung = endung;
        }

        @Override
        public boolean accept(File f) {
            if(f == null) return false;

            // Ordner anzeigen
            if(f.isDirectory()) return true;

            // true, wenn File gewuenschte Endung besitzt
            return f.getName().toLowerCase().endsWith(endung);
        }

        @Override
        public String getDescription() {
            return endung;
        }
    }
}

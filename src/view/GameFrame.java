package view;

import controller.GameController;
import controller.Mode;
import model.Automat;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.*;
import java.util.Observable;
import java.util.Observer;

public class GameFrame extends JInternalFrame implements Observer {

    // Game view
    public CellPanel cellPanel;
    public JPanel cellPanelWrapper;

    // Controller
    GameController controller;

    // Model
    Automat model;

    // Menüleiste
    public JMenuBar menuBar;

    public JMenu menuGame;
    public JMenuItem clone;
    public JMenuItem newView;
    public JMenuItem quit;
    public JMenuItem rules;

    public JMenu menuView;
    public JMenuItem rotate;
    public JMenuItem colors;

    // Controls
    public JPanel controlPanel;

    public JPanel speedPanel;
    public JPanel modePanel;
    public JPanel generationPanel;
    public JPanel presetPanel;

    public JSlider speedSlider;
    public JToggleButton playButton;
    public JToggleButton drawCellButton;
    public JToggleButton setCellButton;
    public JLabel generationCounter;
    public JButton loadPreset;
    public JButton resetButton;


    public GameFrame(Automat model, GameController controller) {
        super(" ", true,true,true,true);
        this.controller = controller;
        this.model = model;
        model.addActiveView(this);

        setTitle("Spiel "+ this.model.getInstanceNumber() +" – Fenster "+ this.model.getViewCount());
        setLocation(0,0);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);


        cellPanel = new CellPanel(model);
        JPanel jPanel = new JPanel();

        cellPanelWrapper = new JPanel();
        cellPanelWrapper.setLayout(new BoxLayout(cellPanelWrapper,BoxLayout.X_AXIS));
        cellPanelWrapper.add(Box.createHorizontalGlue());
        cellPanelWrapper.add(cellPanel);
        cellPanelWrapper.add(Box.createHorizontalGlue());
        cellPanelWrapper.setBackground(Color.BLACK);
        //System.out.println(cellPanel.getSize());

        JScrollPane jsp = new JScrollPane(cellPanelWrapper);


        jPanel.setLayout(new BorderLayout());
        jPanel.add(jsp,BorderLayout.CENTER);
        initMenuBar();
        initControls();
        addListeners();
        updateControlState();
        jPanel.add(controlPanel, BorderLayout.PAGE_END);

        setMinimumSize(new Dimension(600,400));
        //setSize(new Dimension(cellPanel.getZoom()* cellPanel.model.getFw(), cellPanel.getZoom()* cellPanel.model.getFh()+80));

        setContentPane(jPanel);

        pack();
        setVisible(true);

    }

    private void initMenuBar() {
        menuBar = new JMenuBar();

        menuGame = new JMenu("Spiel");
        menuView = new JMenu("Ansicht");

        clone = new JMenuItem("Spiel klonen");
        quit = new JMenuItem("Instanz beenden");
        newView = new JMenuItem("Neues Fenster");
        rules = new JMenuItem("Regeln...");

        rotate = new JMenuItem("Orientierung...");
        colors = new JMenuItem("Aussehen...");

        menuGame.add(newView);
        menuGame.add(new JSeparator());
        menuGame.add(rules);
        menuGame.add(new JSeparator());
        menuGame.add(clone);
        menuGame.add(quit);

        menuView.add(rotate);
        menuView.add(colors);

        menuBar.add(menuGame);
        menuBar.add(menuView);

        setJMenuBar(menuBar);
    }

    private void initControls() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.X_AXIS));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        speedPanel = new JPanel();
        modePanel = new JPanel();
        generationPanel = new JPanel();
        presetPanel = new JPanel();

        speedPanel.setBorder(BorderFactory.createTitledBorder("Geschwindigkeit"));
        modePanel.setBorder(BorderFactory.createTitledBorder("Modus"));
        generationPanel.setBorder(BorderFactory.createTitledBorder("Generation"));
        presetPanel.setBorder(BorderFactory.createTitledBorder("Presets"));

        speedSlider = new JSlider();
        speedSlider.setMinimum(1);
        speedSlider.setMaximum(101);
        speedSlider.setPaintTicks(true);
        speedSlider.setSnapToTicks(true);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setMajorTickSpacing(10);
        speedSlider.createStandardLabels(1);
        speedSlider.setValue(model.getSpeed());
        speedPanel.add(speedSlider);

        playButton = new JToggleButton("Start");
        drawCellButton = new JToggleButton("Zeichnen");
        drawCellButton.setSelected(true);
        setCellButton = new JToggleButton("Setzen");
        modePanel.add(playButton);
        modePanel.add(new JSeparator());
        modePanel.add(drawCellButton);
        modePanel.add(setCellButton);

        generationCounter = new JLabel("0");
        generationPanel.setPreferredSize(new Dimension(140,40));
        generationPanel.add(generationCounter);

        loadPreset = new JButton();
        loadPreset.setText("Preset laden");
        resetButton = new JButton("Reset");
        presetPanel.add(loadPreset);
        presetPanel.add(resetButton);

        controlPanel.add(speedPanel);
        controlPanel.add(modePanel);
        controlPanel.add(generationPanel);
        controlPanel.add(presetPanel);

    }

    private void updateControlState() {
        // update Control Buttons
        if (model.getMode() == Mode.RUN) {
            playButton.setText("Stop");
            playButton.setSelected(true);
            drawCellButton.setEnabled(false);
            setCellButton.setEnabled(false);
        } else if (model.getMode() != Mode.RUN) {
            playButton.setText("Start");
            playButton.setSelected(false);
            drawCellButton.setEnabled(true);
            setCellButton.setEnabled(true);
            if (model.getMode() == Mode.PAINT) {
                drawCellButton.setSelected(true);
                setCellButton.setSelected(false);
            } else {
                drawCellButton.setSelected(false);
                setCellButton.setSelected(true);
            }
        }
        speedSlider.setValue(model.getSpeed());
        generationCounter.setText(String.valueOf(model.getGeneration()));
    }
    
    private void addListeners() {
        // Menu Bar
        rules.addActionListener(event -> {
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
                    this,
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
        loadPreset.addActionListener(event -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new MyFilter(".txt"));
            chooser.setAcceptAllFileFilterUsed(false);
            int result = chooser.showDialog(this, "Preset auswählen");
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
        quit.addActionListener(event -> {
            for (GameFrame f : model.getActiveViews()) {
                f.dispose();
            }
        });
        colors.addActionListener(event -> {
            Color alive = JColorChooser.showDialog(this, "Lebende Zellen:", cellPanel.colorAlive);
            if (alive != null)
                cellPanel.colorAlive = alive;
            Color dead = JColorChooser.showDialog(this, "Tote Zellen:", cellPanel.colorDead);
            if (dead != null)
                cellPanel.colorDead = dead;
            cellPanel.setBackground(dead);
            String[] options = {"An",
                    "Aus"};
            int n = JOptionPane.showOptionDialog(this,
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
        newView.addActionListener(event -> {
            // View und Controller erzeugen
            GameController gameController = new GameController(model);
            ((MainFrame)getTopLevelAncestor()).addChild(gameController.getGameFrame());
            gameController.getGameFrame().toFront();
            gameController.getGameFrame().requestFocus();
            gameController.getGameFrame().requestFocusInWindow();
        });
        clone.addActionListener(event -> {
            // View und Controller erzeugen
            Automat modelClone = null;
            try {
                modelClone = (Automat) model.clone();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            GameController gameController = new GameController(modelClone);
            ((MainFrame)getTopLevelAncestor()).addChild(gameController.getGameFrame());
            gameController.getGameFrame().toFront();
            gameController.getGameFrame().requestFocus();
            gameController.getGameFrame().requestFocusInWindow();
        });
        rotate.addActionListener( e -> {
            // Neues Model erzeugen
            String[] options = {"Nord","Ost","Süd","West"};
            String orientation = (String) JOptionPane.showInputDialog(
                    this,
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
        speedSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider)changeEvent.getSource();
            //if ( !source.getValueIsAdjusting() )
                model.setSpeed(source.getValue());
        });
        playButton.addActionListener(event -> {
            if (event.getActionCommand().equals("Start")) {
                playButton.setText("Stop");
                new Thread(model).start();
                model.setMode(Mode.RUN);
                drawCellButton.setEnabled(false);
                setCellButton.setEnabled(false);
            } else if (event.getActionCommand().equals("Stop")) {
                model.stop();
                playButton.setText("Start");
                if (drawCellButton.isSelected())
                    model.setMode(Mode.PAINT);
                else
                    model.setMode(Mode.TOGGLE);
                drawCellButton.setEnabled(true);
                setCellButton.setEnabled(true);
            }

        });

        drawCellButton.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                model.setMode(Mode.PAINT);
                setCellButton.setSelected(false);
            }
        });

        setCellButton.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                model.setMode(Mode.TOGGLE);
                drawCellButton.setSelected(false);
            }
        });
        resetButton.addActionListener(event -> model.reset());

        addInternalFrameListener(new InternalFrameAdapter(){
            public void internalFrameClosing(InternalFrameEvent e) {
                model.removeActiveView(GameFrame.this);
            }
        });
/*


        loadPreset;

*/

    }

    public CellPanel getCellPanel() {
        return cellPanel;
    }


    @Override
    public void update(Observable o, Object arg) {
        if ((int)arg == 2) {
            updateControlState();
            repaint();
        }
        if ((int)arg == 1) generationCounter.setText(String.valueOf(model.getGeneration()));
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

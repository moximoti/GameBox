package GameOfLife.view;

import GameOfLife.controller.GameController;
import GameOfLife.controller.Mode;
import GameOfLife.model.GameOfLifeModel;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class GameFrame extends JInternalFrame implements Observer {

    // Game GameOfLife.view
    public CellPanel cellPanel;
    public JPanel cellPanelWrapper;

    // Controller
    GameController controller;

    // Model
    GameOfLifeModel model;

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


    public GameFrame(GameOfLifeModel model, GameController controller) {
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
        updateControlState();
        jPanel.add(controlPanel, BorderLayout.PAGE_END);

        setMinimumSize(new Dimension(600,400));
        //setSize(new Dimension(cellPanel.getZoom()* cellPanel.GameOfLife.model.getFw(), cellPanel.getZoom()* cellPanel.GameOfLife.model.getFh()+80));

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
            if (model.getMode() == Mode.PAINT) {
                drawCellButton.setSelected(true);
                setCellButton.setEnabled(true);
                drawCellButton.setEnabled(false);
                setCellButton.setSelected(false);
            } else {
                drawCellButton.setSelected(false);
                setCellButton.setSelected(true);
                drawCellButton.setEnabled(true);
                setCellButton.setEnabled(false);
            }
        }
        speedSlider.setValue(model.getSpeed());
        generationCounter.setText(String.valueOf(model.getGeneration()));
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

}

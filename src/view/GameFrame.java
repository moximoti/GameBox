package view;

import controller.GameController;
import controller.Mode;
import model.Automat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Observable;
import java.util.Observer;

public class GameFrame extends JInternalFrame implements Observer {

    // Game view
    CellPanel cellPanel;

    // Controller
    GameController controller;

    // Model
    Automat model;

    // Menüleiste
    public JMenuBar menuBar;

    public JMenu menuGame;
    public JMenuItem clone;
    public JMenuItem newView;
    public JMenuItem closeView;
    public JMenuItem quit;

    public JMenu menuView;
    public JMenuItem rotate;
    public JMenuItem zoom;
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
    public JComboBox presetChooser;
    public JButton resetButton;


    public GameFrame(Automat model, GameController controller) {
        super("Spiel 1 – Fenster 1", true,true,true,true);
        setLocation(0,0);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        this.controller = controller;
        this.model = model;

        cellPanel = new CellPanel(model);
        JPanel jPanel = new JPanel();

        JPanel cellPanelWrapper = new JPanel();
        cellPanelWrapper.setLayout(new BoxLayout(cellPanelWrapper,BoxLayout.X_AXIS));
        cellPanelWrapper.add(Box.createHorizontalGlue());
        cellPanelWrapper.add(cellPanel);
        cellPanelWrapper.add(Box.createHorizontalGlue());
        cellPanelWrapper.setBackground(Color.BLACK);

        JScrollPane jsp = new JScrollPane(cellPanelWrapper);


        jPanel.setLayout(new BorderLayout());
        jPanel.add(jsp,BorderLayout.CENTER);
        initMenuBar();
        initControls();
        addListeners();
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
        quit = new JMenuItem("Spiel beenden");
        newView = new JMenuItem("Neues Fenster");
        closeView = new JMenuItem("Fenster schließen");

        rotate = new JMenuItem("Drehen...");
        zoom = new JMenuItem("Zoom...");
        colors = new JMenuItem("Farben...");

        menuGame.add(newView);
        menuGame.add(closeView);
        menuGame.add(new JSeparator());
        menuGame.add(clone);
        menuGame.add(quit);

        menuView.add(rotate);
        menuView.add(zoom);
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

        presetChooser = new JComboBox();
        presetChooser.addItem("Default");
        resetButton = new JButton("Reset");
        presetPanel.add(presetChooser);
        presetPanel.add(resetButton);

        controlPanel.add(speedPanel);
        controlPanel.add(modePanel);
        controlPanel.add(generationPanel);
        controlPanel.add(presetPanel);
    }
    
    private void addListeners() {
/*
        // Menu Bar
        clone;
        newView;
        closeView;
        quit;

        rotate;
        zoom;
        colors;
*/

        // Controls
        speedSlider.addChangeListener(changeEvent -> {
            JSlider source = (JSlider)changeEvent.getSource();
            //if ( !source.getValueIsAdjusting() )
                model.setSpeed(source.getValue());
        });
        playButton.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                playButton.setText("Stop");
                new Thread(model).start();
                controller.setMode(Mode.RUN);
                drawCellButton.setEnabled(false);
                setCellButton.setEnabled(false);
            } else if (itemEvent.getStateChange() == ItemEvent.DESELECTED) {
                model.stop();
                playButton.setText("Start");
                if (drawCellButton.isSelected())
                    controller.setMode(Mode.PAINT);
                else
                    controller.setMode(Mode.TOGGLE);
                drawCellButton.setEnabled(true);
                setCellButton.setEnabled(true);
            }
        });

        drawCellButton.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                controller.setMode(Mode.PAINT);
                setCellButton.setSelected(false);
            }
        });

        setCellButton.addItemListener(itemEvent -> {
            if (itemEvent.getStateChange() == ItemEvent.SELECTED) {
                controller.setMode(Mode.TOGGLE);
                drawCellButton.setSelected(false);
            }
        });
        resetButton.addActionListener(event -> model.reset());
/*


        presetChooser;

*/

    }

    public CellPanel getCellPanel() {
        return cellPanel;
    }


    @Override
    public void update(Observable o, Object arg) {
        if ((int)arg == 2) repaint();
        generationCounter.setText(String.valueOf(model.getGeneration()));
    }
}

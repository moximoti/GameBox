package view;

import controller.MainFrameController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class MainFrame extends JFrame {

    private MainFrameController controller;

    public JDesktopPane desktopPane;
    public JMenuBar menuBar;
    // Menu Items
    public JMenu menuGame;
    public JMenuItem newGame;
    public JMenuItem quit;
    //JMenu[] menuItems;
    //JMenuItem[] menuEntriesDatei;
    //ArrayList<JComponent> menuEntriesSpiel;

    public MainFrame() {
        desktopPane = new JDesktopPane();
        desktopPane.setDesktopManager(new DefaultDesktopManager());
        desktopPane.setBackground(Color.LIGHT_GRAY);
        setContentPane(desktopPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Game of Life – MVC/MDL Version");
        initMenus();
        controller = new MainFrameController(this);
        setVisible(true);
    }

    public void addChild(JInternalFrame childFrame) {
        childFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        desktopPane.add(childFrame);
        childFrame.setVisible(true);
    }

    private void initMenus() {
        menuBar = new JMenuBar();

        menuGame = new JMenu("Spiel");
        newGame = new JMenuItem("Neues Spiel starten...");
        quit = new JMenuItem("Beenden");

        menuGame.add(newGame);
        menuGame.add(new JSeparator());
        menuGame.add(quit);

        menuBar.add(menuGame);

        setJMenuBar(menuBar);
    }
}

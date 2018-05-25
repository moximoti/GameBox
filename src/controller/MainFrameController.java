package controller;

import model.Automat;
import view.GameFrame;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class MainFrameController {

    private MainFrame view;

    public MainFrameController(MainFrame mainFrame) {
        this.view = mainFrame;
        mainFrame.quit.addActionListener( e -> {
            mainFrame.dispose();
            System.exit(0);
        });
        mainFrame.newGame.addActionListener( e -> {
            // Neues Model erzeugen
            Integer[] sizeOptions = {40,80,160,320};
            int size = (int) JOptionPane.showInputDialog(
                    mainFrame,
                    "Feldgröße",
                    "Bitte Feldgröße wählen:",
                    JOptionPane.QUESTION_MESSAGE,
                    null, sizeOptions,
                    sizeOptions[1] );
            Automat a = new Automat(size,size);

            // View und Controller erzeugen
            GameController gameController = new GameController(a);
            mainFrame.addChild(gameController.getGameFrame());
            gameController.getGameFrame().toFront();
            gameController.getGameFrame().requestFocus();
            gameController.getGameFrame().requestFocusInWindow();
        } );
    }


}

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

    public MainFrameController(MainFrame view) {
        this.view = view;
        view.newGame.addActionListener( e -> {
            // Neues Model erzeugen
            Automat a = new Automat(50,50);
/*
            a.addLivingCellAt(3,4);
            a.addLivingCellAt(12,4);
            a.addLivingCellAt(3,5);
            a.addLivingCellAt(3,6);
*/
            //new Thread(a).start();

            // View und Controller erzeugen
            GameController gameController = new GameController(a);
            view.addChild(gameController.getGameFrame());
        } );
    }


}

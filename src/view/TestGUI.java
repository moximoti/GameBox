package view;

import controller.CellPanelController;
import model.Automat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TestGUI extends JFrame {

    CellPanel cellPanel;
    CellPanelController cellPanelController;

    public TestGUI() throws InterruptedException {
        //super(title, gc);
        this.cellPanel = new CellPanel(new Automat(100,100));
        this.cellPanelController = new CellPanelController(cellPanel, cellPanel.linkedGame);
        initGUI();
        cellPanel.linkedGame.addLivingCellAt(3,4);
        cellPanel.linkedGame.addLivingCellAt(12,4);
        cellPanel.linkedGame.addLivingCellAt(3,5);
        cellPanel.linkedGame.addLivingCellAt(3,6);
        Thread.sleep(5000);

        for (int i = 0; i < 100000; i++) {
            Thread.sleep(50);
            cellPanel.linkedGame.calculateNextGen();
            cellPanel.repaint();
        }
    }

    void initGUI() {

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("Game of Life");
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        jPanel.add(cellPanel,BorderLayout.CENTER);
        setSize(new Dimension(cellPanel.getZoom()* cellPanel.linkedGame.getFw()+20, cellPanel.getZoom()* cellPanel.linkedGame.getFh()+42));

        this.setContentPane(jPanel);
        this.setLocationRelativeTo(null);
        this.setVisible(true);


    }

}


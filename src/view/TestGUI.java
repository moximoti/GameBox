//package view;
//
//import controller.GameController;
//import model.Automat;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import java.awt.*;
//
//public class TestGUI extends JFrame {
//
//    CellPanel cellPanel;
//    GameController gameController;
//
//    public TestGUI(Automat model, String title) throws InterruptedException {
//        super(title);
//        //Automat a = new Automat(100,100);
//        this.cellPanel = new CellPanel(model);
//        this.gameController = new GameController(cellPanel, cellPanel.model);
//        initGUI();
//
//        cellPanel.model.addLivingCellAt(3,4);
//        cellPanel.model.addLivingCellAt(12,4);
//        cellPanel.model.addLivingCellAt(3,5);
//        cellPanel.model.addLivingCellAt(3,6);
//        Thread.sleep(5000);
//
//        for (int i = 0; i < 100000; i++) {
//            Thread.sleep(50);
//            cellPanel.model.calculateNextGen();
//            cellPanel.repaint();
//        }
//    }
//
//    void initGUI() {
//
//        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        //this.setTitle("Game of Life");
//        JPanel jPanel = new JPanel();
//        jPanel.setLayout(new BorderLayout());
//        jPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
//        jPanel.add(cellPanel,BorderLayout.CENTER);
//        setSize(new Dimension(cellPanel.getZoom()* cellPanel.model.getFw()+20, cellPanel.getZoom()* cellPanel.model.getFh()+42));
//
//        this.setContentPane(jPanel);
//        //this.setLocationRelativeTo(null);
//        this.setVisible(true);
//
//
//    }
//
//}
//

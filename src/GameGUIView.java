import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameGUIView extends JFrame{
    // private static GameView instance;
    // private JTextField playerCountField;
    // private JButton startButton;
    // private GameController controller;

    // private GameView(GameController controller) {
    //     this.controller = controller;
    //     setupUI();
    // }

    // public static synchronized GameView getInstance(GameController controller) {
    //     if (instance == null) {
    //         instance = new GameView(controller);
    //     }
    //     return instance;
    // }

    // private void setupUI() {
    //     setTitle("Deadwood Game Setup");
    //     setSize(300, 200);
    //     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //     setLocationRelativeTo(null);

    //     playerCountField = new JTextField(10);
    //     startButton = new JButton("Start Game");

    //     startButton.addActionListener(new ActionListener() {
    //         @Override
    //         public void actionPerformed(ActionEvent e) {
    //             String input = playerCountField.getText();
    //             controller.handlePlayerCountInput(input);
    //         }
    //     });

    //     JPanel panel = new JPanel();
    //     panel.add(new JLabel("Enter number of players:"));
    //     panel.add(playerCountField);
    //     panel.add(startButton);
    //     add(panel);
    // }

    // public void displayError(String message) {
    //     JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    // }
}


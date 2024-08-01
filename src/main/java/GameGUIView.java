import java.util.concurrent.CountDownLatch;
import java.awt.*;
import javax.swing.*;

// import javax.imageio.ImageIO;
import java.awt.event.*;
import java.util.InputMismatchException;

public class GameGUIView extends JFrame implements GameView {

    // JLabels
    JLabel boardlabel;
    JLabel cardlabel;
    JLabel playerlabel;
    JLabel mLabel;
  
    //JButtons
    JButton bAct;
    JButton bRehearse;
    JButton bMove;

    // JLayered Pane
    JLayeredPane bPane;

    // Singleton instance
    private static GameGUIView instance;

    // User Input
    private String input;

    // CountDownLatch
    private CountDownLatch latch;

    // Constructor
    public GameGUIView() {
        
        // Set the title of the JFrame
        super("Deadwood");
        // Set the exit option for the JFrame
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Create the JLayeredPane to hold the display, cards, dice and buttons
        bPane = getLayeredPane();

        // Create the deadwood board
        boardlabel = new JLabel();
        ImageIcon icon =  new ImageIcon(getClass().getClassLoader().getResource("board.jpg"));
        boardlabel.setIcon(icon); 
        boardlabel.setBounds(0,0,icon.getIconWidth(),icon.getIconHeight());
        
        // Add the board to the lowest layer
        bPane.add(boardlabel, new Integer(0));
        
        // Set the size of the GUI
        setSize(icon.getIconWidth()+400,icon.getIconHeight()+28);
        
        // Add a scene card to this room
        cardlabel = new JLabel();
        ImageIcon cIcon =  new ImageIcon("01.png");
        cardlabel.setIcon(cIcon); 
        cardlabel.setBounds(20,65,cIcon.getIconWidth()+2,cIcon.getIconHeight());
        cardlabel.setOpaque(true);
        
        // Add the card to the lower layer
        bPane.add(cardlabel, new Integer(1));
        
        


        // Add a dice to represent a player. 
        // Role for Crusty the prospector. The x and y co-ordiantes are taken from Board.xml file
        playerlabel = new JLabel();
        ImageIcon pIcon = new ImageIcon("r2.png");
        playerlabel.setIcon(pIcon);
        //playerlabel.setBounds(114,227,pIcon.getIconWidth(),pIcon.getIconHeight());  
        playerlabel.setBounds(114,227,46,46);
        playerlabel.setVisible(false);
        bPane.add(playerlabel,new Integer(3));
        
        // Create the Menu for action buttons
        mLabel = new JLabel("MENU");
        mLabel.setBounds(icon.getIconWidth()+40,0,100,20);
        bPane.add(mLabel,new Integer(2));

        // Create Action buttons
        bAct = new JButton("ACT");
        bAct.setBackground(Color.white);
        bAct.setBounds(icon.getIconWidth()+10, 30,100, 20);
        bAct.addMouseListener(new boardMouseListener());
        
        bRehearse = new JButton("REHEARSE");
        bRehearse.setBackground(Color.white);
        bRehearse.setBounds(icon.getIconWidth()+10,60,100, 20);
        bRehearse.addMouseListener(new boardMouseListener());
        
        bMove = new JButton("MOVE");
        bMove.setBackground(Color.white);
        bMove.setBounds(icon.getIconWidth()+10,90,100, 20);
        bMove.addMouseListener(new boardMouseListener());

        // Place the action buttons in the top layer
        bPane.add(bAct, new Integer(2));
        bPane.add(bRehearse, new Integer(2));
        bPane.add(bMove, new Integer(2));
    }

    /**
     * Get the singleton instance of the GameGUIView.
     * @return the singleton instance
     */
    public static GameGUIView getInstance() {
        if (instance == null) {
            instance = new GameGUIView();
        }
        return instance;
    }

    // This class implements Mouse Events

    class boardMouseListener implements MouseListener{

        // Code for the different button clicks
        @Override
        public void mouseClicked(MouseEvent e) {
            
            if (e.getSource()== bAct){
                playerlabel.setVisible(true);
                System.out.println("Acting is Selected\n");
            } else if (e.getSource()== bRehearse){
                System.out.println("Rehearse is Selected\n");
            } else if (e.getSource()== bMove){
                System.out.println("Move is Selected\n");
            }         
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }


/************************************************************
 * View
 ************************************************************/

    /**
     * Get the player's input.
     * 
     * @return The player's input
     */
    @Override
    public String getPlayerInput() {
        latch = new CountDownLatch(1);
        try {
            latch.await(); // Wait for the button click
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return input;
    }

    // /**
    //  *  Get the number of players.
    //  * 
    //  * @return The number of players
    //  */
    // @Override
    // public int getNumPlayers() {
    //     while (true) {
    //         System.out.println("Enter the number of players (between 2 and 8):");
    //         try {
    //             int numPlayers = this.scanner.nextInt();
    //             this.scanner.nextLine(); // consume the newline
    //             if (numPlayers >= 2 && numPlayers <= 8) {
    //                 return numPlayers;
    //             } else {
    //                 System.out.println("Invalid number of players.");
    //             }
    //         } catch (InputMismatchException e) {
    //             System.out.println("Invalid input. Please enter an integer.");
    //             this.scanner.nextLine(); // discard the invalid input
    //         }
    //     }
    // }

    /**
     * Display a message.
     * 
     * @param message The message to display
     */
    public void showMessage(String message) {
        // System.out.println(message);
    }


/************************************************************
 * Observer Pattern
 ************************************************************/

    /**
     * Updates the view.
     * 
     * @param arg The argument to update the view
     */
    public void update(Object arg) {
        // TODO: complete this stub for update method
    }

    /**
     * Main method
     * 
     * @param args
     */
    public int getNumPlayers() {

        GameGUIView board = getInstance();
        board.setVisible(true);

        // Take input from the user about number of players
        while(true) {
            try {
                String input = JOptionPane.showInputDialog(board, "How many players? (between 2 and 8):");
                int numPlayers = Integer.parseInt(input);
                if (numPlayers >= 2 && numPlayers <= 8) {
                    return numPlayers;
                } else {
                    JOptionPane.showMessageDialog(board,"Invalid number of players. Please enter a number between 2 and 8", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (InputMismatchException e) {
                JOptionPane.showMessageDialog(board, "Invalid input. Please enter an integer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
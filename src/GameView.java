import javax.swing.*;
class GameView{
    public static void main(String args[]){
    //    Game game = new Game();
    //    game.start();
       JFrame frame = new JFrame(title:"My First GUI");
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setSize(width:300,height:300);
       JButton button = new JButton(text:"Press");
       frame.getContentPane().add(button); // Adds Button to content pane of frame
       frame.setVisible(true);
    }
}


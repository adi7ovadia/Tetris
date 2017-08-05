package Tetris;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;


@SuppressWarnings("serial")

public class Tetris extends JFrame 
{
    JLabel scoreLabel;

    public Tetris() 
    {

    	scoreLabel = new JLabel("Score: 0");
        add(scoreLabel, BorderLayout.SOUTH);
        Board gameBoard = new Board(this);
        add(gameBoard);
        gameBoard.start();

        setSize(200, 400);
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
   }

   public JLabel getScoreLabel() 
   {
       return scoreLabel;
   }
   
   public static void main(String[] args) 
   {
/*   	Menu menu=new Menu();
   	menu.setLocationRelativeTo(null);
   	menu.setVisible(true);
*/
       Tetris game = new Tetris();
       game.setLocationRelativeTo(null);
       game.setVisible(true);

   }


    
}
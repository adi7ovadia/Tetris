package Tetris;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Menu extends JFrame{
	
	JButton btnNewGame=new JButton("New Game");
	JButton btnWinnerTable=new JButton("Winner Table");

	public Menu()
	{
		this.setTitle("Menu");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(200, 150);
		this.setAlwaysOnTop(true);
		this.setVisible(true);    
		
		JPanel btnPanel=new JPanel();
		btnPanel.setLayout(new FlowLayout() );
		btnPanel.add (btnNewGame);
		btnPanel.add(btnWinnerTable);
		this.add(btnPanel);
		
		btnNewGame.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
 

}

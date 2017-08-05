package Tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import Tetris.Shape.ShapeKind;


@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener 
{
	public final int WIDTH = 10;
	public final int HEIGHT = 22;
	public Timer timer;
	public boolean isFinished = false;
	public boolean isStarted = false;
	public boolean isPaused = false;
	public int numLinesRemoved = 0;
	public int curX = 0;
	public int curY = 0;
	public JLabel scoreLabel;
	public Shape curShape;
	public ShapeKind[] board;



	public Board(Tetris tetris) 
	{
		setFocusable(true);
		curShape = new Shape();
		timer = new Timer(400, this);
		timer.start(); 

		scoreLabel =  tetris.getScoreLabel();
		board = new ShapeKind[WIDTH * HEIGHT];
		addKeyListener(new TAdapter());
		clearBoard();  
	}

	public void actionPerformed(ActionEvent e) 
	{
		if (isFinished) 
		{
			isFinished = false;
			newShape();
		} 
		else 
			getDown();
	}

	public void start()
	{
		if (isPaused) return;

		isStarted = true;
		isFinished = false;
		numLinesRemoved = 0;
		clearBoard();

		newShape();
		timer.start();
	}

	private void pause()
	{
		if (!isStarted)
			return;

		isPaused = !isPaused;
		if (isPaused) 
		{
			timer.stop();
			scoreLabel.setText("Paused");
		} else 
		{
			timer.start();
			scoreLabel.setText("Score: "+String.valueOf(numLinesRemoved));
		}
		repaint();
	}

	public void paint(Graphics g)
	{ 
		super.paint(g);

		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - HEIGHT * squareHeight();


		for (int i = 0; i < HEIGHT; ++i) 
		{
			for (int j = 0; j < WIDTH; ++j) 
			{
				ShapeKind shape = shapeAt(j, HEIGHT - i - 1);
				if (shape != ShapeKind.NoShape)
					drawSquare(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), shape);
			}
		}

		if (curShape.getShape() != ShapeKind.NoShape) 
		{
			for (int i = 0; i < 4; ++i) 
			{
				int x = curX + curShape.x(i);
				int y = curY - curShape.y(i);
				drawSquare(g, 0 + x * squareWidth(), boardTop + (HEIGHT - y - 1) * squareHeight(),
						curShape.getShape());
			}
		}
	}


	private void getDown()
	{
		if (!shapeMove(curShape, curX, curY - 1))
			shapeDropped();
	}


	private void clearBoard()
	{
		for (int i = 0; i < HEIGHT * WIDTH; ++i)
			board[i] = ShapeKind.NoShape;
	}

	private void shapeDropped()
	{
		for (int i = 0; i < 4; ++i) 
		{
			int x = curX + curShape.x(i);
			int y = curY - curShape.y(i);
			board[(y * WIDTH) + x] = curShape.getShape();
		}

		removeFullLines();

		if (!isFinished)
			newShape();
	}

	private void newShape()
	{
		curShape.setRandomShape();
		curX = WIDTH / 2 + 1;
		curY = HEIGHT - 1 + curShape.minY();

		if (!shapeMove(curShape, curX, curY)) 
		{
			curShape.setShape(ShapeKind.NoShape);
			timer.stop();
			isStarted = false;
			scoreLabel.setText("GAME OVER");
		}
	}

	private boolean shapeMove(Shape newShape, int newX, int newY)
	{
		for (int i = 0; i < 4; ++i) 
		{
			int x = newX + newShape.x(i);
			int y = newY - newShape.y(i);
			if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
				return false;
			if (shapeAt(x, y) != ShapeKind.NoShape)
				return false;
		}

		curShape = newShape;
		curX = newX;
		curY = newY;
		repaint();
		return true;
	}

	private void removeFullLines()
	{
		int numFullLines = 0;
		boolean lineIsFull;

		for (int i = HEIGHT - 1; i >= 0; --i) 
		{
			lineIsFull = true;
			for (int j = 0; j < WIDTH; ++j) 
			{
				if (shapeAt(j, i) == ShapeKind.NoShape) 
				{
					lineIsFull = false;
					break;
				}
			}

			if (lineIsFull) 
			{
				++numFullLines;
				for (int k = i; k < HEIGHT - 1; ++k) 
				{
					for (int j = 0; j < WIDTH; ++j)
						board[(k * WIDTH) + j] = shapeAt(j, k + 1);
				}
			}
		}

		if (numFullLines > 0) 
		{
			numLinesRemoved += numFullLines;
			scoreLabel.setText("Score: "+String.valueOf(numLinesRemoved));
			isFinished = true;
			curShape.setShape(ShapeKind.NoShape);
			repaint();
		}
	}

	private void drawSquare(Graphics g, int x, int y, ShapeKind shape)
	{
		Color colors[] = 
			{ 
					new Color(0, 0, 0), new Color(201, 103, 103), 
					new Color(103, 201, 103), new Color(103, 103, 201), 
					new Color(201, 201, 103), new Color(201, 103, 201), 
					new Color(103, 201, 201), new Color(217, 170, 0)
			};


		Color color = colors[shape.ordinal()];

		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

		g.setColor(color.brighter());
		g.drawLine(x, y + squareHeight() - 1, x, y);
		g.drawLine(x, y, x + squareWidth() - 1, y);

		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1,
				x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
				x + squareWidth() - 1, y + 1);
	}

	class TAdapter extends KeyAdapter 
	{
		public void keyPressed(KeyEvent e) 
		{
			int keycode;
			
			if (!isStarted || curShape.getShape() == ShapeKind.NoShape)   return;
			
			keycode = e.getKeyCode();

			if (keycode == 'p' || keycode == 'P') 
			{
				pause();
				return;
			}

			if (isPaused)
				return;

			switch (keycode) 
			{
			
			case KeyEvent.VK_LEFT:
				shapeMove(curShape, curX - 1, curY);
				break;
			
			case KeyEvent.VK_RIGHT:
				shapeMove(curShape, curX + 1, curY);
				break;
			
			case KeyEvent.VK_DOWN:
				shapeMove(curShape.rotateRight(), curX, curY);
				break;
			
			case KeyEvent.VK_UP:
				shapeMove(curShape.rotateLeft(), curX, curY);
				break;
			
			case KeyEvent.VK_SPACE:
				getDown();
				break;

			}

		}
	}
	int squareWidth() { return (int) getSize().getWidth() / WIDTH ; }
	int squareHeight() { return (int) getSize().getHeight() / HEIGHT; }
	ShapeKind shapeAt(int x, int y) { return board[(y * WIDTH) + x]; }
}
package Tetris;

import java.util.Random;
import java.lang.Math;


public class Shape 
{

	enum ShapeKind { NoShape, ZShape, SShape, LineShape, 
		TShape, SquareShape, LShape, MirroredLShape };

		private ShapeKind pieceShape;
		private int coords[][];
		private int[][][] shapeCoords;
		private final int TETRA=4;

		public Shape() 
		{
			coords = new int[TETRA][2];
			setShape(ShapeKind.NoShape);

		}

		public void setShape(ShapeKind shape) 
		{

			shapeCoords = new int[][][] 
					{
				{ { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } },
				{ { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } },
				{ { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } },
				{ { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } },
				{ { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } },
				{ { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } },
				{ { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } },
				{ { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } }
					};

					for (int i = 0; i < TETRA ; i++) 
					{
						for (int j = 0; j < 2; ++j) 
							coords[i][j] = shapeCoords[shape.ordinal()][i][j];

					}
					pieceShape = shape;

		}


		public void setRandomShape()
		{
			Random r = new Random();
			int x = Math.abs(r.nextInt()) % 7 + 1;
			ShapeKind[] values = ShapeKind.values(); 
			setShape(values[x]);
		}

		public int minX()
		{
			int m = coords[0][0];
			for (int i=0; i < TETRA; i++) 
				m = Math.min(m, coords[i][0]);
			return m;
		}


		public int minY() 
		{
			int m = coords[0][1];
			for (int i=0; i < TETRA; i++) 
			{
				m = Math.min(m, coords[i][1]);
			}
			return m;
		}

		public Shape rotateLeft() 
		{
			if (pieceShape == ShapeKind.SquareShape)
				return this;

			Shape result = new Shape();
			result.pieceShape = pieceShape;

			for (int i = 0; i < TETRA; ++i) 
			{
				result.setX(i, y(i));
				result.setY(i, -x(i));
			}
			return result;
		}

		public Shape rotateRight()
		{
			if (pieceShape == ShapeKind.SquareShape)
				return this;

			Shape result = new Shape();
			result.pieceShape = pieceShape;

			for (int i = 0; i < TETRA; ++i) 
			{
				result.setX(i, -y(i));
				result.setY(i, x(i));
			}
			return result;
		}

		private void setX(int index, int x) { coords[index][0] = x; }
		private void setY(int index, int y) { coords[index][1] = y; }
		public int x(int index) { return coords[index][0]; }
		public int y(int index) { return coords[index][1]; }
		public ShapeKind getShape()  { return pieceShape; }
}
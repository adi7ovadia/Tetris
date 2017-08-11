package Tetris;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

@SuppressWarnings("serial")
public class Server extends JFrame 
{
	private ServerSocket serverSocket;
	private Socket socket;
	private int clientNo=0;
	private ArrayList<Player> arr=new ArrayList<>();
	private static java.sql.Statement stmt=null;
	private JButton btnNewGame=new JButton("New Game");
	private JButton btnWinnerTable = new JButton("Winner Table");
	private JTextField tflPlayerName= new JTextField();
	private String playerName="";


	public Server()
	{
		initializeDB();

		this.setTitle("Server");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(600,200);
		this.setAlwaysOnTop(true);
		this.setVisible(true);    

		JPanel panel=new JPanel();
		panel.setLayout(new FlowLayout() );
		panel.add (new JLabel("Enter your player name:"));
		tflPlayerName.setPreferredSize( new Dimension( 160, 25 ) );
		panel.add (tflPlayerName);
		panel.add (btnNewGame);
		panel.add (btnWinnerTable);
		this.add(panel);

		btnWinnerTable.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getDBGames();
			}
		});

		btnNewGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(clientNo < 5)
				{
					playerName=getNamePlayer(tflPlayerName); 
					if(checkUser(playerName)){
						Tetris game;
						try {
							game = new Tetris();
							game.setLocationRelativeTo(null);
							game.setVisible(true);
						} catch (UnknownHostException e1) {e1.printStackTrace();
						} catch (IOException e1) {e1.printStackTrace();}
					}
				}
				else
				{
					btnNewGame.setEnabled(false);
				}
			}});

		new Thread( () ->{ 
			try
			{ 
				serverSocket = new ServerSocket(8000);
				while (true)
				{ 
					socket = serverSocket.accept();
					clientNo++;
					Player player=new Player(clientNo,this.playerName);
					arr.add(player);
					DataOutputStream toClient=new DataOutputStream(socket.getOutputStream());
					toClient.writeInt(clientNo);
					//toClient.writeUTF(this.playerName);
					new Thread(new HandleASession(socket,this.playerName,clientNo)).start();           
				}
			}
			catch(SocketException ex)
			{ 
				try
				{ 
					serverSocket.close();
					socket.close();
				} 
				catch (IOException e){ }}  catch(IOException ex){ }
		}).start();

	}




	/**
	 * The initializeDB method initialize the Data Base 
	 * 
	 * @exception Exception when the data base doesn't load
	 */
	private void initializeDB() 
	{
		try { // Load the JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			// Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("Driver loaded");
			// Establish a connection
			Connection conn= DriverManager.getConnection("jdbc:mysql://localhost/", "scott", "tiger");
			System.out.println("Database connected");
			stmt = conn.createStatement();


			FileInputStream fstream = new FileInputStream("createTetristables_mysql.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)
			{ 
				if (strLine != null && !strLine.equals("")) stmt.execute(strLine);          
			}

			conn= DriverManager.getConnection("jdbc:mysql://localhost/tetrisdb", "scott", "tiger");
			stmt = conn.createStatement();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * The getDBGames method get the games from the data base
	 * by the players average or the descending order games
	 */
	private void getDBGames()
	{
		TableView<?> tableView = new TableView<Object>();
		Stage stage=new Stage();
		HBox hBox = new HBox(1);
		Button btClose=new Button("Close");
		hBox.getChildren().add(btClose);
		hBox.setAlignment(Pos.CENTER);
		BorderPane pane = new BorderPane();
		pane.setCenter(tableView);
		pane.setBottom(hBox);

		showGamesDes(tableView);


		// Create a scene and place it in the stage
		Scene scene = new Scene(pane, 600, 400);
		stage.setTitle("Winner Table"); // Set the stage title
		stage.setScene(scene); // Place the scene in the stage
		stage.setAlwaysOnTop(true);
		stage.show(); // Display the stage
		stage.setOnCloseRequest(
				new EventHandler<WindowEvent>(){ 
					public void handle(WindowEvent event){ tableView.getItems().clear(); stage.close();  }});

		btClose.setOnAction(e -> {tableView.getItems().clear(); stage.close(); });
	}
	/**
	 * The showGamesDes method show games details in descending order
	 * 
	 * @exception SQLException when there is a problem to read
	 * from the data base
	 * 
	 * @param tableView - the data base table view
	 */
	@SuppressWarnings("rawtypes")
	private void showGamesDes(TableView tableView) 
	{

		ResultSet resultSet=null;
		try 
		{
			String queryString = "SELECT name, realScore "
					+ "FROM tertisdb.Games "
					+ "ORDER BY realScore DESC;";
			resultSet = stmt.executeQuery(queryString);
			populateTableView(resultSet, tableView);

		} catch (SQLException ex) {ex.printStackTrace();}

	}

	/**
	 * The populateTableView method show players by their average score
	 * 
	 * @exception Exception when there is an error by building the data
	 * 
	 * @param rs - the current row of data
	 * @param tableView - the data base table view
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void populateTableView(ResultSet rs, TableView tableView) 
	{ 
		tableView.getColumns().clear();
		tableView.setItems(FXCollections.observableArrayList());
		try { 
			int numOfColums = rs.getMetaData().getColumnCount();
			for(int i = 1 ; i <= numOfColums ; i++)
			{
				final int columNum = i - 1;
				TableColumn<String[],String> column = new TableColumn<>(rs.getMetaData().getColumnLabel(i));
				column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[columNum]));
				tableView.getColumns().add(column);
			}
			while(rs.next())
			{
				String[] cells = new String[numOfColums];
				for(int i = 1 ; i <= numOfColums ; i++)
					cells[i - 1] = rs.getString(i);
				tableView.getItems().add(cells);
			}
		} catch (Exception e) {
			System.out.println("Error on Building Data");
		}
	}

	public static boolean checkUser(String name)
	{
		ResultSet resultSet=null;
		try 
		{
			String queryString = "SELECT name "
					+ "FROM tertisdb.Games "
					+ "WHERE name='"+name+";";
			resultSet = stmt.executeQuery(queryString);
			if(!resultSet.next())
				return true;
			return false;

		} catch (SQLException ex) {ex.printStackTrace();}
		return false;

	}

	private void writeGameDB(String playerName,int realScore)
	{
		String sql=null;

		try 
		{
			sql="INSERT INTO tertisdb.Games " +
					"VALUES ('"+playerName+"', "+realScore+");";
			stmt.executeUpdate(sql);


		} catch (SQLException e) {}
	}


	public static void main(String[] args) 
	{
		@SuppressWarnings("unused")
		Server server=new Server();
	}

	/**
	 * The getNamePlayer method give the player name
	 * or null if there is'nt name inside the TextField
	 * 
	 * @param tflPlayerName2 - the string of the player name
	 * @return temp - the player name
	 */
	private String getNamePlayer(JTextField tflPlayerName2)
	{
		String temp=(tflPlayerName2.getText());
		tflPlayerName2.setText(null);
		if(temp==null || temp.equals(""))
			temp=null;
		else
			temp=(temp.trim()).toUpperCase();
		return temp;
	}


	/**
	 * The HandleASession class Define the thread class 
	 * for handling a new session for two players
	 * <br>Implements: {@link Runnable}
	 */
	class HandleASession implements Runnable
	{ 
		/**
		 *The socket of the client
		 *<br>From the type: {@link socket}.
		 */
		private Socket socket;
		/**
		 *The Client number
		 */
		private int clientNum;
		/**
		 *The player name
		 */
		private String playerName;
		/**
		 *The real score of the game by the number of hits
		 */
		private int realScore;
		// Continue to play
		/**
		 * The method HandleASession construct a thread
		 *  
		 *  @param socket - the client socket
		 *  @param name - the player name
		 *  @param clientNum - the client number
		 */
		public HandleASession(Socket socket,String name,int clientNum)
		{ 
			this.socket=socket;
			this.clientNum=clientNum;
			this.playerName=name;
		}

		/**
		 * Implement the run() method for the thread 
		 * 
		 * @exception SocketException when there is an error 
		 * creating or accessing a Socket
		 */
		public void run()
		{ 
			try 
			{ // Create data input and output streams
				DataInputStream inputStream = new DataInputStream(
						socket.getInputStream());
				while (true)
				{ 

					realScore=inputStream.readInt();

					writeGameDB(playerName,realScore);

					deleteIndex(clientNum);

				}
			}
			catch(SocketException ex)
			{ 
				try
				{ 
					serverSocket.close();
					socket.close();
				} 
				catch (IOException e){ }} 
			catch(IOException ex){ }
			/** Determine if the player with the specified token wins */
		}
		/**
		 * The deleteIndex method delete player by his number
		 *  
		 *  @param clientNum - the client number
		 */
		public void deleteIndex(int clientNum)
		{
			for(int i=0;i<arr.size();i++)
			{
				if(arr.get(i).getNumberPlayer()==clientNum)
					arr.remove(i);
			}
		}

		/**
		 * The setScoreClient method setting the player score
		 *  
		 *  @param clientNum - the client number
		 *  @param score - the player score
		 */
		public void setScoreClient(int clientNum,int score)
		{
			for(int i=0;i<arr.size();i++)
			{
				if(arr.get(i).getNumberPlayer()==clientNum)
					arr.get(i).setScore(score);
			}		
		}
	}
}

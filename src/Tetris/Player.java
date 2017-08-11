package Tetris;


@SuppressWarnings("rawtypes")
public class Player implements Comparable
{
	/**
	 * The number of this player
	 */
	private int numberPlayer;
	/**
	 * The name of this player
	 */
	private String namePlayer;
	/**
	 * The score of this player
	 */
	private int score;

	/**
	 * This method construct player
	 * 
	 * @param numberPlayer - the player number
	 * @param playerName - the player name
	 */
	public Player(int numberPlayer,String playerName)
	{
		this.numberPlayer=numberPlayer;
		this.namePlayer=playerName;
		score=0;
	}
	
	/**
	 * Getting the number of the player
	 * 
	 * @return - the number of the player
	 */
	public int getNumberPlayer() {return numberPlayer;}
	
	/**
	 * Setting the number of the player
	 * 
	 * @param numberPlayer - the number of the player
	 */
	public void setNumberPlayer(int numberPlayer) {	this.numberPlayer = numberPlayer;}
	
	/**
	 * Getting the name of the player
	 * 
	 * @return - the name of the player
	 */
	public String getNamePlayer() {	return namePlayer;}
	
	/**
	 * Setting the name of the player
	 * 
	 * @param namePlayer - the name of the player
	 */
	public void setNamePlayer(String namePlayer) {	this.namePlayer = namePlayer;}
	
	/**
	 * Getting the score of the player
	 * 
	 * @return - the score of the player
	 */
	public int getScore() {	return score;}
	
	/**
	 * Setting the score of the player
	 * 
	 * @param score - the score of the player
	 */
	public void setScore(int score) {	this.score = score;}
	
	/**
	 * Compare two players by their score
	 * to sort the array of players
	 * from the biggest score to the lowest
	 * 
	 * @param o - the second player
	 * @return which player is bigger
	 */
	@Override
	public int compareTo(Object o) {
		return (-1)*(score-((Player)o).getScore());
	}



}

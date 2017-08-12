package tests;

import org.junit.Test;

import Tetris.Server;

public class tests 
{
	private String userName = "ADI";
	private int score = 10 ;
	
    @Test
    public void testIsExist()
    {
       Server.checkUser(userName);
    }
    
    @Test
    public void testClientBiggerThenFive()
    {
       Server.checkNumberClientBiggerThenFive();
    }
    
    @Test
    public void testInsertIntoDB()
    {
       Server.writeGameDB(userName, score);
    }
    
   
}

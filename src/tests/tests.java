package tests;

import org.junit.Test;

import Tetris.Server;

public class tests 
{
	private String userName = "ADI";
	
    @Test
    public void testIsExist(){
       Server.checkUser(userName);
    }
   
}

/**
 * @author  Sklonnie Konstantin
 * @since   2019-17-Jan 
 */
package elevatorSimulation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.swing.JFrame;
public class Main 
{
	static GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	public static int screenWidth = gd.getDisplayMode().getWidth(), ID = 0;
	public static int screenHeight = gd.getDisplayMode().getHeight();
	public static Semaphore sem = new Semaphore(1);
	public static double totalWatingTime =0, totalReqTime =0, avarageDistance = 0 , sigmaXSquare = 0;
	
	public final static int peopleNumber = 100;   /*		CHOOSE HOW MANY PEOPLE/REQUESTS		*/
	public static void main(String[] args)
	{
		Scanner s = null;
		try {	s = new Scanner(new File("input.txt"));	}
		catch (FileNotFoundException e1) {		e1.printStackTrace();	}	
		ArrayList<Integer> array = new ArrayList<Integer>();;
		for (int i = 0; i < peopleNumber ; i++)
		    array.add(s.nextInt()) ;

		
		JFrame frame = new JFrame("ElevatorBuilding");
		frame.setPreferredSize(new Dimension(screenWidth/2-150  , screenHeight));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Building building = new Building( frame ,      2      );		/*	#1 : ALWAYS RUN		; #2 : RUN ON CALL		; #3: GIVEN DESTINATION		*/
		Container cp = frame.getContentPane();							
		cp.setLayout( null );
		for(int i=0 ;i<Building.numElevetors ;i++)
			frame.add(building.getElevetor(i));
		frame.pack();
		frame.setVisible(true);
		building.run();		

		/*			TIMEOUT
		  try { 	Thread.sleep((long) (15*1000)); 
		} catch (InterruptedException e) {		e.printStackTrace();	}
		 */
		try { 	Thread.sleep((long) (2*1000));  
		} catch (InterruptedException e) {		e.printStackTrace();	}
		
		ExecutorService executor = Executors.newCachedThreadPool();
		for(int i=0 ; i<peopleNumber ; i++)						
		{
			try { 	Thread.sleep((long) (((Math.random()*1000)%1 +2)*250));  /*SLEEP UP TO ~0.5 SEC BETWEEN REQUESTS*/
			} catch (InterruptedException e) {		e.printStackTrace();	}
			
			executor.execute(new Person( (int)array.get(i) , ID++ , building , Building.maxLevel));
		}

		try { 	Thread.sleep((long) (10*1000));  /*SLEEP 60 sec */
		} catch (InterruptedException e) {		e.printStackTrace();	}
		int reqNum= peopleNumber*2;
		System.out.println("***********************************************************************");
		System.out.println("With "+reqNum +" requests ,avarage of "+(avarageDistance/(double)reqNum)+" Floors per Req. ");
		System.out.println(Building.numElevetors+" elevators ,in a "+Building.maxLevel+" Floor Building");
		System.out.println("the Avg. Waiting time is: "+( Main.totalWatingTime/(double)reqNum) +" Sec.");
		System.out.println("the Avg. Request completion time is: "+ (Main.totalReqTime/(double)reqNum) +" Sec.");

		
		System.out.println("the Wating Varience is: "+ ( (Main.sigmaXSquare/(double)reqNum) - ( (Main.totalWatingTime/(double)reqNum)*(Main.totalWatingTime/(double)reqNum) ))    );

		building.setPowerON(false);	
		/*			TIMEOUT			*/
		try { 	Thread.sleep((long) (5*1000)); 
		} catch (InterruptedException e) {		e.printStackTrace();	}
		System.exit(0);
	}
}

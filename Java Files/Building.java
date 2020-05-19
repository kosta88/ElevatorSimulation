/**
 * @author  Sklonnie Konstantin
 * @since   2019-15-Jan 
 */

package elevatorSimulation;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;

public class Building 
{
	/***************************************************************************************/
	private JFrame frame;

	public static final int numElevetors = 8;  	/*		CHOOSE HOW MANY ELEVATORS	*/
	public static final int maxLevel = 45;		    /*		CHOOSE HOW MANY FLOORS THE BUILDING HAS		*/

	private boolean powerON= true;
	private Algorithm algorithm;
	private ArrayList<Elevator> elevators;
	public LinkedList<Request> requestQ;
	/***************************************************************************************/
	public Building( JFrame mainframe ,int flag ) 
	{
		frame = mainframe;
		elevators = new ArrayList<Elevator>();
		requestQ = new LinkedList<Request>();

		for( int i=0; i<numElevetors;i++)
			try
			{    
				if (flag != 3)
					elevators.add(new SimpleElevator(maxLevel, frame , powerON));   
				else
					elevators.add(new AdvancedElevator(maxLevel, frame , powerON));   
			} 
		catch (Exception e)		
		{	e.printStackTrace();	}
		
		if (flag == 1)
			algorithm = new AlwaysRun(elevators, requestQ, powerON, maxLevel);

		else if (flag == 2)
			algorithm = new RunOnCall(elevators, requestQ, powerON, maxLevel);

		else if (flag == 3)
			algorithm = new WithGivenDestination(elevators, requestQ, powerON, maxLevel);
	}
	/***************************************************************************************/
	public void run() 
	{
		Thread t = new Thread( algorithm );
		t.start();
	}
	/***************************************************************************************/
	public DisplayPanel getElevetor(int i)
	{
		return elevators.get(i).getPanel();
	}
	/***************************************************************************************/
	public void setPowerON(boolean powerON) 
	{
		if(!powerON)
			this.algorithm.setPower(false);

		this.powerON = powerON;
	}
	/***************************************************************************************/
}

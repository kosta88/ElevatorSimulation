/**
 * @author  Sklonnie Konstantin
 * @since   2019-15-Jan 
 */

package elevatorSimulation;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class Algorithm implements Runnable
{
	/***************************************************************************************/
	public boolean power;
	public int topFloor;
	LinkedList<Request> requestQ;
	protected ArrayList<Elevator> elevators;
	/***************************************************************************************/
	public Algorithm(ArrayList<Elevator> elevators, LinkedList<Request> requestQ, boolean powerON, int maxlevel) {
		this.elevators= elevators;
		this.requestQ= requestQ;
		power = powerON;
		topFloor= maxlevel;
	}
	/***************************************************************************************/
	public void setPower(boolean power)
	{
		if(!power)
			for(Elevator e : elevators)
				e.setPowerON(false);

		this.power = power;
	}
}

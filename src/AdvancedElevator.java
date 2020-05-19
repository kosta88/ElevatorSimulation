/**
 * @author  Sklonnie Konstantin
 * @since   2019-01-May 
 */


package elevatorSimulation;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

public class AdvancedElevator extends Elevator 
{

	/***************************************************************************************/
	public AdvancedElevator(int maxFloor, JFrame mainframe, boolean power) throws IOException {
		super(maxFloor, mainframe, power);
	}

	/***************************************************************************************/
	public void run() 	{
		boolean noStopsFlag ;
		while(powerON)		
		{	
			while(!this.WORKING)
				try {		Thread.sleep(10);		}
			catch (InterruptedException e1) {  e1.printStackTrace();	}
			try {		Thread.sleep(5);		}
			catch (InterruptedException e1) {  e1.printStackTrace();	}


			adjustDir();
			this.GOING_UP = DIRECTION;
			if(startedWORKING){
				this.acceleratORslowDown();
				startedWORKING = false;	}

			noStopsFlag = true;

			/*		pickup going UP.... advance then pickup		*/
			if(GOING_UP && currentFloor < this.topFloor && pickupQ[currentFloor+1] )	{
				noStopsFlag = false;
				stopUP();
				pickUPGoingUP();
			}

			/*		stopping going UP.... advance then stop		*/
			if(GOING_UP && currentFloor < this.topFloor && stopQ[currentFloor+1] > 0) {
				noStopsFlag = false;
				this.Panel.closedDoors= false;
				stopUP();
				removeReq(currentFloor);
				if(pickupQ[currentFloor])
					pickUPGoingUP();		
				this.Panel.closedDoors= true;
				paintE();	}	

			/*		pickup going DOWN.... advance then pickup		*/
			if(!GOING_UP &&  currentFloor > 0 && pickupQ[currentFloor-1] ) {
				noStopsFlag = false;
				this.Panel.closedDoors= false;											
				stopDOWN();
				pickUPGoingDOWN();		
			}

			/*		stopping going DOWN.... advance then stop		*/
			if(!GOING_UP && currentFloor > 0 && stopQ[currentFloor-1] > 0) {
				noStopsFlag = false;
				this.Panel.closedDoors= false;								
				stopDOWN();
				removeReq(currentFloor);
				if(pickupQ[currentFloor])
					pickUPGoingDOWN();		
				this.Panel.closedDoors= true;	
				paintE(); }	

			if(isAlgoFlag()) 
				checkIfTurnOff();

			if(noStopsFlag) 
				advance();
			
			if(currentFloor == 0 || currentFloor == topFloor)
				this.acceleratORslowDown();
			
			
		}/* while loop end*/
	}	
	/***************************************************************************************/
	protected void pickUPGoingDOWN() 	{
		ArrayList<Request> requsts;
		requsts = newReqests.get(currentFloor);

		pickupQ[currentFloor]= false;			
		for( Request requst : requsts)
		{
			requst.setGotService( (int) System.currentTimeMillis());
			inProgress.get(requst.destination).add(requst);	
			this.currCapacity+= requst.people_Number;
		}
		requsts.clear();

		if(stopQ[currentFloor] != 0)	
			removeReq(currentFloor);				
	}
	/***************************************************************************************/
	protected void pickUPGoingUP() 	{
		ArrayList<Request> requsts;
		requsts = newReqests.get(currentFloor);

		pickupQ[currentFloor]= false;	
		for( Request requst : requsts)
		{
			requst.setGotService( (int) System.currentTimeMillis());
			inProgress.get(requst.destination).add(requst);	
			this.currCapacity+= requst.people_Number;
		}
		requsts.clear();

		if(stopQ[currentFloor] != 0)	
			removeReq(currentFloor);	
	}
	/***************************************************************************************/
}

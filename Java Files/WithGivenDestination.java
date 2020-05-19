/**
 * @author  Sklonnie Konstantin
 * @since   2019-01-May 
 */


package elevatorSimulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WithGivenDestination extends Algorithm
{
	/***************************************************************************************/
	public WithGivenDestination(ArrayList<Elevator> elevators, LinkedList<Request> requestQ, boolean powerON, int maxlevel) 
	{
		super(elevators, requestQ, powerON, maxlevel);
		initElevators();
	}
	/***************************************************************************************/
	private void initElevators() {
		for(int i=0 ; i< elevators.size(); i++)
		{
			elevators.get(i).setAlgoFlag(true);
			elevators.get(i).setWORKING(false);
			elevators.get(i).setCurrentFloor((int) (Math.random()*1000 %topFloor));
			elevators.get(i).getPanel().setFloor(elevators.get(i).getCurrentFloor());
		}
	}
	/***************************************************************************************/
	public void run() {				/*      ALL ELEVATORS HAVE NO BUTTONS INSIDE		*/
		Request req;
		Elevator chosenElevator = null;
		boolean flag ,NoPickUp ;

		/*		run elevators		*/
		ExecutorService executor = Executors.newCachedThreadPool();
		for( Elevator e : elevators)
			executor.execute(e);

		while(power)	{
			try {		Thread.sleep(5);		}
			catch (InterruptedException e1) {  e1.printStackTrace();	}
			try{
				req = this.requestQ.removeFirst();					
				chosenElevator = null;
				flag = false ;
				NoPickUp = false;
				/*		FIRST OR LAST FLOOR REQUEST		*/
				if(req.from == 0)
				{
					chosenElevator=firstFloorReq(flag);	
					if(flag)
						chosenElevator.setDIRECTION(req.goingUP);
					if(chosenElevator.getCurrentFloor() == 0)
					{
						req.setGotService(req.getStartingTime());
						NoPickUp = true;
					}
				}
				else if(req.from == this.topFloor)	
				{
					chosenElevator = topFloorReq(flag);
					if(flag)
						chosenElevator.setDIRECTION(req.goingUP);
					if(chosenElevator.getCurrentFloor() == this.topFloor)
					{
						req.setGotService(req.getStartingTime());
						NoPickUp = true;
					}
				}
				else 	/*	OTHER FLOORS REQUESTS	*/
				{
					for( Elevator e : elevators)		{							/*	THE FLOOR HAS A STANDING ELEVATOR		*/
						if(!e.isWORKING() && e.getCurrentFloor() == req.from)	{
							req.setGotService(req.getStartingTime());
							chosenElevator = e;	
							chosenElevator.openANDcloseDoors();
							chosenElevator.startedWORKING = true;
							chosenElevator.setWORKING(true);    
							chosenElevator.setDIRECTION(req.goingUP);
							NoPickUp = true;
							break;		}		}	

					if(chosenElevator == null)			/* choose moving or standing elevator by destination of request */
					{
						chosenElevator = chooseElevator( req);
					}

				}
				/* ******************         	UPDATE ELEVATOR QUEUE           ******************** */
				if(chosenElevator == null)
				{
					int minDistance = topFloor;
					int distance;
					for( Elevator e : elevators)			
					{
						if(req.goingUP && !e.getDIRECTION())
						{
							distance = e.getCurrentFloor();
							if(distance < minDistance )	{
								minDistance =distance;
								chosenElevator = e;				}
						}
						else if(!req.goingUP && e.getDIRECTION())	
						{
							if(e.getDIRECTION())		{
								distance = topFloor - e.getCurrentFloor();
								if(distance < minDistance)	{
									minDistance= distance;
									chosenElevator = e;			}		}		}	
					}
				}
							
					
				if(NoPickUp)				/*	 floor already has a stopped elevator		*/
				{
					chosenElevator.getInProgress().get(req.destination).add(req);
				}
				else
				{
					chosenElevator.getNewReqests().get(req.from).add(req);
					chosenElevator.getPickupQ()[req.from]= true;
				}					
				chosenElevator.getStopQ()[req.destination]+= req. people_Number;	
			}
			catch( NoSuchElementException e){ }
			checkDirections();
		}
	}
	/***************************************************************************************/
	private void checkDirections() {
		boolean flag;
		for( Elevator e : elevators)	
		{		
			if(e.isWORKING())
			{
				flag = true;
				if(e.DIRECTION == true) 
				{/* up	*/
					for(int i = e.currentFloor+1 ; i< this.topFloor+1 ; i++ )
						if(e.getPickupQ()[i] == true || e.getStopQ()[i] != 0)
							flag =false;
					if(flag)	{
						e.acceleratORslowDown();
						e.setDIRECTION(false);
						e.acceleratORslowDown();
					}
				}
				else
				{/* down	*/
					for(int i = e.currentFloor-1 ; i>=0 ; i-- )
						if(e.getPickupQ()[i] == true || e.getStopQ()[i] != 0)
							flag =false;
					if(flag)	{
						e.acceleratORslowDown();
						e.setDIRECTION(true);
						e.acceleratORslowDown();
					}
				}
			}
		}

	}
	/***************************************************************************************/
	private Elevator chooseElevator( Request req)
	{
		Elevator chosenElevatorMoving = null ,chosenElevatorStanding = null ;
		int distance , minDistance=this.topFloor , minDistanceTwo=this.topFloor;
		boolean elevatorUnder ;
		for( Elevator e : elevators)	
		{						
			if((req.from - e.currentFloor) < 0 )
				elevatorUnder = false;
			else
				elevatorUnder = true;
			/*	elevator going UP UNDER person that wants to go UP	*/
			if(e.DIRECTION && e.isWORKING() && elevatorUnder && req.goingUP)			{
				distance = req.from - e.currentFloor;
				if(distance < minDistance && distance > 1)				{
					minDistance= distance;
					chosenElevatorMoving = e;			}		}
			/*	elevator going DOWN ABOVE person that wants to go DOWN	*/
			else if(!e.DIRECTION && e.isWORKING() && !elevatorUnder && !req.goingUP)			{
				distance =  e.currentFloor - req.from ;
				if(distance < minDistance && distance > 1)				{
					minDistance= distance;
					chosenElevatorMoving = e;			}		}
			/*	elevator going UP UNDER person that wants to go DOWN	*/
			else if(e.DIRECTION && e.isWORKING() && elevatorUnder && !req.goingUP && e.noMoreUpReqThan(req.from))			{
				distance =  e.currentFloor - req.from ;
				if(distance < minDistance && distance > 1)				{
					minDistance= distance;
					chosenElevatorMoving = e;			}		}
			/*	elevator going DOWN UNDER ABOVE that wants to go UP	*/
			else if(!e.DIRECTION && e.isWORKING() && !elevatorUnder && req.goingUP && e.noMoreLowReqThan(req.from))			{
				distance =  e.currentFloor - req.from ;
				if(distance < minDistance && distance > 1)				{
					minDistance= distance;
					chosenElevatorMoving = e;			}		}
			/*	standind elevator	*/
			else if(!e.isWORKING())		{			
				distance = Math.abs(e.currentFloor - req.from );
				if(distance < minDistanceTwo )			
				{
					minDistanceTwo= distance;
					chosenElevatorStanding = e;			
				}				}
		}
		if(minDistance > minDistanceTwo )	
		{
			chosenElevatorStanding.openANDcloseDoors();
			chosenElevatorStanding.startedWORKING = true;
			chosenElevatorStanding.setWORKING(true);    
			if((req.from - chosenElevatorStanding.currentFloor) < 0 )
				chosenElevatorStanding.setDIRECTION(false);
			else
				chosenElevatorStanding.setDIRECTION(true);

			return chosenElevatorStanding;
		}
		else
			return chosenElevatorMoving;

	}
	/***************************************************************************************/
	private Elevator firstFloorReq(boolean flag) 
	{
		Elevator chosenElevatorMoving = null ,chosenElevatorStanding = null ;
		int distance , minDistance=this.topFloor , minDistanceTwo=this.topFloor;

		for( Elevator e : elevators)	
		{								
			if(!e.DIRECTION && e.isWORKING() )			{
				distance = e.getCurrentFloor();
				if(distance < minDistance && distance != 0)				{
					minDistance= distance;
					chosenElevatorMoving = e;			}		}
			else if(!e.isWORKING())		{
				distance = e.getCurrentFloor();
				if(distance < minDistanceTwo )					{
					minDistanceTwo= distance;
					chosenElevatorStanding = e;				}			}
		}
		if(minDistance > minDistanceTwo + 2)	
		{
			chosenElevatorStanding.openANDcloseDoors();
			chosenElevatorStanding.startedWORKING = true;
			chosenElevatorStanding.setWORKING(true);    
			flag = true;
			return chosenElevatorStanding;
		}
		else
			return chosenElevatorMoving;
	}
	/***************************************************************************************/
	private Elevator topFloorReq(boolean flag) 
	{
		Elevator chosenElevatorMoving = null ,chosenElevatorStanding = null ;
		int distance , minDistance=this.topFloor , minDistanceTwo=this.topFloor;

		for( Elevator e : elevators)	
		{								
			if(!e.DIRECTION && e.isWORKING() )		
			{
				distance = this.topFloor - e.getCurrentFloor();
				if(distance < minDistance && distance != 0)			{
					minDistance= distance;
					chosenElevatorMoving = e;				}			}
			else if(!e.isWORKING())		{
				distance = this.topFloor - e.getCurrentFloor();
				if(distance < minDistanceTwo )					{
					minDistanceTwo= distance;
					chosenElevatorStanding = e;			}			}
		}
		if(minDistance > minDistanceTwo + 2)
		{
			chosenElevatorStanding.openANDcloseDoors();
			chosenElevatorStanding.startedWORKING = true;
			chosenElevatorStanding.setWORKING(true);    
			flag = true;
			return chosenElevatorStanding;
		}
		else
			return chosenElevatorMoving;
	}
	
	/*
	private void turnOffInactive()
	{
		boolean stop;
		for( Elevator e : elevators)	{
			stop =true;
			for(int i=0 ; i< this.topFloor; i++)
				if(e.getStopQ()[i] != 0 || e.getPickupQ()[i])
				{
					stop= false;
					break;
				}

			if(stop)
				e.setWORKING(false);	}
	}
	*/
}

/**
 * @author  Sklonnie Konstantin
 * @since   2019-15-Jan 
 */

package elevatorSimulation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunOnCall extends Algorithm
{
	/**************************************************************************************/
	public RunOnCall(ArrayList<Elevator> elevators, LinkedList<Request> requestQ, boolean powerON, int maxlevel )
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
	public void run() {			/*		NEED TO CHECK IF ELEVATOR WORKING		*/
		Request req;
		Elevator chosenElevator = null;
		boolean elevatorGoingUP= false ,flag ,standingElevator;
		int distance, minDistance, standingDistance ;

		/*		run elevators		*/
		ExecutorService executor = Executors.newCachedThreadPool();
		for( Elevator e : elevators)
			executor.execute(e);

		while(power)		{
			try {		Thread.sleep(5);		}
			catch (InterruptedException e1) {  e1.printStackTrace();	}			
			chosenElevator = null;
			flag = false;
			standingElevator = false;
			try{	
				req = this.requestQ.removeFirst();

				if(req.from == 0 || req.from == this.topFloor)
					chosenElevator= firstORlastFloor(req);
				else
					for( Elevator e : elevators)		{							/*	THE FLOOR HAS A STANDING ELEVATOR		*/
						if(!e.isWORKING() && e.getCurrentFloor() == req.from)	{
							req.setGotService(req.getStartingTime());
							chosenElevator = e;	
							chosenElevator.openANDcloseDoors();
							standingElevator = true;
							flag = true;
							break;		}		}	

				/*	NO ELEVATOR IN THAT FLOOR>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> so......	*/
				if(chosenElevator == null)								
				{
					standingDistance = this.topFloor;
					minDistance = this.topFloor;
					for( Elevator e : elevators)
					{
						if(!e.isWORKING())								/*	CHOOSE STANDING ELEVATOR		*/
						{
							distance = Math.abs(e.getCurrentFloor() - req.from);
							if(distance< standingDistance)	{
								standingDistance = distance ;	
								chosenElevator =e;
								standingElevator = true;	}
						}
						else											/*	TRY CHOOSING A MOVING ELEVATOR		*/
						{
							elevatorGoingUP= e.getDIRECTION();
							if(req.goingUP)		
							{	
								distance =req.from - e.getCurrentFloor() ;
								if(distance > 0 && distance < minDistance && elevatorGoingUP && distance < standingDistance +2)
								{
									minDistance =distance;
									chosenElevator = e;		
									standingElevator = false;	}			}
							else		
							{
								distance = e.getCurrentFloor() - req.from;
								if(distance > 0 && distance < minDistance && !elevatorGoingUP && distance < standingDistance +2)	{
									minDistance= distance;
									chosenElevator = e;	
									standingElevator = false;		}		}		
						}
					}

				}

				/* ******************/
				/* ******** ERRRRR NO ELEVATOR		*****************************************/
				/* ******************/

				if(chosenElevator == null)
				{
					minDistance = topFloor;
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
				
				if(chosenElevator == null)					
					chosenElevator= elevators.get( (int) ( (Math.random()*100)  %8));

				/*
				System.out.println("~~~~~~~~~ REQUEST # "+req.num+ "from>>>>>  "+req.from);
				System.out.println("  elevator number: "+chosenElevator.getE_number()+" starting at "+ chosenElevator.getCurrentFloor()+" DIRECTION "+chosenElevator.getDIRECTION());
				 */

				if(standingElevator)
				{
					distance = chosenElevator.getCurrentFloor() - req.from;
					if(distance < 0)		
					{
						chosenElevator.setDIRECTION(true);
						if(!req.goingUP)
							chosenElevator.setrunToPickUp(true);
					}
					else		{
						chosenElevator.setDIRECTION(false);
						if(req.goingUP)
							chosenElevator.setrunToPickUp(true);
					}
					chosenElevator.startedWORKING = true;
					chosenElevator.setDIRECTION(req.goingUP);
					chosenElevator.setWORKING(true);    
				}

				/* ******************         	UPDATE ELEVATOR QUEUE           ******************** */
				if(flag)				/*	 floor already has a stopped elevator		*/
				{
					chosenElevator.getStopQ()[req.destination]+= req. people_Number;
					chosenElevator.getInProgress().get(req.destination).add(req);
				}
				else
				{
					chosenElevator.getPickupQ()[req.from]= true;
					chosenElevator.getNewReqests().get(req.from).add(req);
				}
				/**********************
				System.out.println(" REQUEST # "+req.num+" elevator number: "+chosenElevator.getE_number()
				+" starting at "+ chosenElevator.getCurrentFloor()+" DIRECTION "+chosenElevator.getDIRECTION()+" working "+chosenElevator.isWORKING());
				 *********************/
			}
			catch( NoSuchElementException e)	{ }	
			checkDirections();

		}		/*	 end of	while loop	*/
	}
	/**************************************************************************************/
	private Elevator firstORlastFloor(Request req)
	{					
		Elevator chosenElevator = null;
		int distance , minDistance=this.topFloor ;
		if(req.from == 0)
		{	
			for( Elevator e : elevators)		{								
				if(!e.DIRECTION && e.isWORKING() )		{
					distance = e.getCurrentFloor();
					if(distance < minDistance && distance > 0 )		{
						minDistance= distance;
						chosenElevator = e;		}		}		}
		}	
		else
		{
			for( Elevator e : elevators)		{
				if(e.DIRECTION && e.isWORKING())		{
					distance = topFloor - e.getCurrentFloor();
					if(distance < minDistance && distance > 0)		{
						minDistance= distance;
						chosenElevator = e;		}		}		}
		}

		return chosenElevator;
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
}

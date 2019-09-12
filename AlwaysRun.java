/**
 * @author  Sklonnie Konstantin
 * @since   2019-15-01 
 */

package elevatorSimulation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlwaysRun extends Algorithm
{
	/***************************************************************************************/
	public AlwaysRun(ArrayList<Elevator> elevators, LinkedList<Request> requestQ, boolean powerON, int maxlevel) 
	{
		super(elevators, requestQ, powerON, maxlevel);
		initElevators();
	}
	/***************************************************************************************/
	private void initElevators() {
		for(int i=0 ; i< elevators.size(); i++)
		{
			elevators.get(i).setAlgoFlag(false);
			elevators.get(i).setWORKING(true);
			elevators.get(i).setCurrentFloor((int) (Math.random()*1000 %topFloor));
		}
		for(int i=0 ; i< elevators.size()/2; i++)
		{
			elevators.get(i).setDIRECTION(true);
			elevators.get(elevators.size()-i         - 1  ).setDIRECTION(false);
		}
	}
	/***************************************************************************************/
	public void run() {				/*      ALL ELEVATORS ALWAYS WORKING		*/
		Request req;
		Elevator chosenElevator = null;
		boolean elevatorGoingUP= false;
		int distance , minDistance ;
		ExecutorService executor = Executors.newCachedThreadPool();
		for( Elevator e : elevators)
			executor.execute(e);
		while(power)	{
			try {		Thread.sleep(5);		}
			catch (InterruptedException e1) {  e1.printStackTrace();	}
			try{
				req = this.requestQ.removeFirst();					
				minDistance=topFloor;
				chosenElevator = null;
				
				if(req.from == 0)		
					chosenElevator=firstFloorReq();			
				else if(req.from == this.topFloor)	
					chosenElevator = topFloorReq();
				else 	
				{										/*  CHOOSE ELEVATOR IN DIRECTION			*/
					for( Elevator e : elevators)			
					{
						elevatorGoingUP= e.getDIRECTION();

						if(!elevatorGoingUP && !req.goingUP && ((e.currentFloor - req.from )> 0 ) )
						{
							distance = e.currentFloor - req.from;
							if( distance < minDistance)	{
								minDistance =distance;
								chosenElevator = e;			}
						}
						else if(elevatorGoingUP && req.goingUP && (( req.from - e.currentFloor )> 0 ) )
						{
							distance = req.from - e.currentFloor;
							if( distance < minDistance )	{
								minDistance =distance;
								chosenElevator = e;			}
						}		
					}
					
					if(chosenElevator == null)						/* NO ELEVATOR IN DIRECTION */	
					{
						minDistance=topFloor;
						for( Elevator e : elevators)			
						{
							elevatorGoingUP= e.getDIRECTION();
							if(req.goingUP && !elevatorGoingUP)
							{
								distance = e.getCurrentFloor();
								if(distance < minDistance )	{
									minDistance =distance;
									chosenElevator = e;				}
							}
							else if(!req.goingUP && elevatorGoingUP)	
							{
									distance = topFloor - e.getCurrentFloor();
									if(distance < minDistance)	{
										minDistance= distance;
										chosenElevator = e;			}		}
						}
					}
				}
				
				if(chosenElevator == null)					
					chosenElevator= elevators.get( (int) ( (Math.random()*100)  %8));
				
				/*
				System.out.println("~~~~~~~~~ REQUEST # "+req.num+ "from>>>>>  "+req.from);
				System.out.println("  elevator number: "+chosenElevator.getE_number()+" starting at "+ chosenElevator.getCurrentFloor()+" DIRECTION "+chosenElevator.getDIRECTION());
				*/


				/* ******************         	UPDATE ELEVATOR QUEUE           ******************** */
				chosenElevator.getNewReqests().get(req.from).add(req);
				chosenElevator.getPickupQ()[req.from]= true;	



			}
			catch( NoSuchElementException e){ }
		}
	}
	/***************************************************************************************/
	private Elevator firstFloorReq() 
	{
		Elevator chosenElevator = null;
		int distance , minDistance=this.topFloor ;

		for( Elevator e : elevators)		{								
			if(!e.DIRECTION)		{
				distance = e.getCurrentFloor();
				if(distance < minDistance && distance > 0)		{
					minDistance= distance;
					chosenElevator = e;		}		}		}

		if(chosenElevator == null)
			for( Elevator e : elevators)		{								
				if(e.DIRECTION)		{
					distance =this.topFloor - e.getCurrentFloor();
					if(distance < minDistance)	{
						minDistance= distance;
						chosenElevator = e;		}		}		}

		return chosenElevator;
	}
	/***************************************************************************************/
	private Elevator topFloorReq() 
	{
		Elevator chosenElevator = null;
		int distance , minDistance=this.topFloor ;
		for( Elevator e : elevators)		{
			if(e.DIRECTION)		{
				distance = topFloor - e.getCurrentFloor();
				if(distance < minDistance && minDistance != 0)		{
					minDistance= distance;
					chosenElevator = e;		}		}		}

		if(chosenElevator == null)
			for( Elevator e : elevators)		{								
				if(!e.DIRECTION)		{
					distance = e.getCurrentFloor();
					if(distance < minDistance)	{
						minDistance= distance;
						chosenElevator = e;		}		}		}
		return chosenElevator;
	}
	/***************************************************************************************/
}

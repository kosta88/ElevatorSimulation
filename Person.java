/**
 * @author  Sklonnie Konstantin
 * @since   2019-16-01 
 */

package elevatorSimulation;

public class Person implements Runnable
{
	public Request goRequest;
	public Request returnRequest;
	Building building;
	private int myID;
	private int startTime, startTimeRet;	
	public int buildingTopFloor;

	/**************************************************************************************/
	public Person(int destination, int ID , Building B, int maxlevel) {
		myID = ID;
		buildingTopFloor = maxlevel;
		building = B;
		goRequest = new Request(destination, buildingTopFloor, myID);
		returnRequest = new Request(destination, buildingTopFloor, goRequest);
	}
	/***************************************************************************************/
	public void run()
	{
		startTime = (int) System.currentTimeMillis();
		goRequest.setStartingTime(startTime);
		
		building.requestQ.add(goRequest);		
		synchronized (goRequest) {
			try {
				goRequest.wait();
			} catch (InterruptedException e) {			e.printStackTrace();		}			}
		double waitingTime=  (double)( (goRequest.getGotService()-startTime)) / 1000;
		double totalTime= ( (double)( (goRequest.getEndTime()-startTime)) / 1000);

		try {
			Main.sem.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Main.avarageDistance += goRequest.distance;

		Main.totalWatingTime += waitingTime;
		Main.totalReqTime += totalTime;
		Main.sigmaXSquare += (waitingTime*waitingTime);
		Main.sem.release();

		try { 	Thread.sleep((long) (((Math.random()*1000)%1 +2)*250));  /* SLEEP UP TO ~0.5 SEC BEFORE GOING BACK   */
		} catch (InterruptedException e) {		e.printStackTrace();	}

		startTimeRet = (int) System.currentTimeMillis();
		returnRequest.setStartingTime(startTimeRet);
		building.requestQ.add(returnRequest);				
		Main.avarageDistance += returnRequest.distance;
		synchronized (returnRequest) {
			try {
				returnRequest.wait();
			} catch (InterruptedException e) {	e.printStackTrace();	}		}
		double waitingTimeReturn=  (double)( (returnRequest.getGotService()-startTimeRet)) / 1000;
		double totalTimeReturn= ( (double)( (returnRequest.getEndTime()-startTimeRet )) / 1000);

		try {
			Main.sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		Main.totalWatingTime += waitingTimeReturn;
		Main.totalReqTime += totalTimeReturn;

		Main.sigmaXSquare += (waitingTimeReturn*waitingTimeReturn);
		Main.sem.release();

		System.out.println("Request #"+ myID +" from "+goRequest.from+"F-"+goRequest.destination +"F "+
				"  Waited for an Elevator: "+(  waitingTime)+
				" Sec., Total request time: "+totalTime+" Sec.");

		System.out.println("#"+ myID +" RETURNING from "+returnRequest.from+"F-"+returnRequest.destination +"F "+
				"  Waited for an Elevator: "+(  waitingTimeReturn)+
				" Sec., Total request time: "+totalTimeReturn+" Sec.");	

	}
	/***************************************************************************************/
}

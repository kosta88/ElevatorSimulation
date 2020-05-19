/**
 * @author  Sklonnie Konstantin
 * @since   2019-15-Jan 
 */
package elevatorSimulation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JFrame;

public class Elevator implements Runnable
{
	private JFrame frame;
	static int cnt = 0;
	final int maxCapacity = 18;
	final int topFloor;
	protected boolean GOING_UP;
	public boolean DIRECTION;			/*		TRUE MEANS UP!!! ******   */
	private boolean algoFlag;
	public boolean WORKING;
	public boolean powerON;
	public boolean startedWORKING;
	protected int currCapacity;
	protected int currentFloor;
	protected boolean runToPickUp;
	protected HashMap<Integer,ArrayList<Request>> newReqests;
	protected HashMap<Integer,ArrayList<Request>> inProgress;
	public LinkedList<Request> doneRequests;
	protected Boolean[] pickupQ ;
	protected int[] stopQ ;
	protected DisplayPanel Panel;
	private int E_number;
	/**************************************************************************************/
	public Elevator(int maxFloor, JFrame mainframe, boolean power) throws IOException 	{
		setrunToPickUp(false);
		frame = mainframe;
		DIRECTION = false;
		GOING_UP = DIRECTION;
		startedWORKING = false;
		setAlgoFlag(false);
		WORKING= false;
		powerON = power;
		setDIRECTION(false);   /* doesnt matter cause its not working */
		topFloor = maxFloor;
		currCapacity = 0;
		currentFloor = 0;
		setE_number(cnt++);

		doneRequests = new LinkedList<Request>();
		inProgress =  new HashMap<Integer,ArrayList<Request>>();
		for(int i=0 ; i< maxFloor+1 ;i++)
			inProgress.put(i, new ArrayList<Request>());
		newReqests = new HashMap<Integer,ArrayList<Request>>();
		for(int i=0 ; i< maxFloor+1 ;i++)
			newReqests.put(i, new ArrayList<Request>());

		stopQ = new int[maxFloor+1];
		Arrays.fill(stopQ, 0);
		pickupQ = new Boolean[maxFloor+1];
		Arrays.fill(pickupQ, Boolean.FALSE);
		Panel = new DisplayPanel( frame, topFloor, currentFloor, getE_number() );
	}
	/***************************************************************************************/
	protected void advance() 	{
		try {	
			if(DIRECTION && currentFloor != topFloor )		{
				this.moveOneFloor();
				currentFloor++;
				this.Panel.setFloor(currentFloor);			}
			else if(!DIRECTION && currentFloor != 0 )		{
				this.moveOneFloor();
				currentFloor--;		
				this.Panel.setFloor(currentFloor);	}	}
		catch (Exception e) {	e.printStackTrace();	}		
	}
	/***************************************************************************************/
	protected void adjustDir() 	{
		if(currentFloor == this.topFloor)
			DIRECTION = false;
		else if(currentFloor == 0)
			DIRECTION = true;		
	}
	/***************************************************************************************/
	public void removeReq(int floor)		{
		ArrayList<Request> DoneReq;
		DoneReq = this.inProgress.get(floor);

		for( Request r : DoneReq){
			currCapacity -= stopQ[currentFloor];
			stopQ[currentFloor]= 0;
			doneRequests.add(r);
			r.setEndTime( (int) System.currentTimeMillis() );

			synchronized (r) 	{
				r.notify();			}

		}
		DoneReq.clear();
	}
	/***************************************************************************************/
	public void stopUP()	
	{
		moveOneFloor();
		this.acceleratORslowDown();	
		currentFloor = currentFloor+1;
		this.openANDcloseDoors();	
		paintE();
		this.Panel.closedDoors= true;
		this.openANDcloseDoors();	
		paintE();
	}
	/***************************************************************************************/
	public void paintE()	
	{
		try {	this.Panel.setFloor(currentFloor);	} 
		catch (Exception e) {	e.printStackTrace();	}	
	}
	/***************************************************************************************/
	public void stopDOWN()	
	{
		moveOneFloor();
		this.acceleratORslowDown();	
		currentFloor = currentFloor-1;
		this.openANDcloseDoors();	
		paintE();
		this.openANDcloseDoors();
		this.Panel.closedDoors= true;
		paintE();
	}
	/***************************************************************************************/
	public void openANDcloseDoors()	
	{
		try {
			Thread.sleep(60);
		} catch (InterruptedException e) {		e.printStackTrace();		}
	}
	/***************************************************************************************/
	public void acceleratORslowDown()	{
		try {		Thread.sleep(30);
		} catch (InterruptedException e)		{		e.printStackTrace();	}
	}
	/***************************************************************************************/
	public void moveOneFloor(){
		try {
			Thread.sleep(35);
		} catch (InterruptedException e)		{			e.printStackTrace();		}
	}
	/***************************************************************************************/
	public int getCurrCapacity(){
		return currCapacity;
	}
	/***************************************************************************************/
	public void setCurrCapacity(int currCapacity)
	{
		this.currCapacity = currCapacity;
	}
	/***************************************************************************************/
	public int[] getStopQ() 	{
		return stopQ;
	}
	/***************************************************************************************/
	public void setStopQ(int[] stopQ)	{
		this.stopQ = stopQ;
	}
	/***************************************************************************************/
	public DisplayPanel getPanel()	{
		return this.Panel;
	}
	/***************************************************************************************/
	public boolean getDIRECTION()	{
		return DIRECTION;
	}
	/***************************************************************************************/
	public void setDIRECTION(boolean dIRECTION)	{
		DIRECTION = dIRECTION;
	}
	/***************************************************************************************/
	public int getCurrentFloor() 	{
		return currentFloor;
	}
	/***************************************************************************************/
	public boolean isWORKING()	{
		return WORKING;
	}
	/***************************************************************************************/
	public void setWORKING(boolean wORKING)	{
		WORKING = wORKING;
	}
	/***************************************************************************************/
	public void setCurrentFloor(int currentFloor)	{
		this.currentFloor = currentFloor;
	}
	/***************************************************************************************/
	public Boolean[] getPickupQ()	{
		return pickupQ;
	}
	/***************************************************************************************/
	public int getE_number()	{
		return E_number;
	}
	/***************************************************************************************/
	public void setE_number(int e_number) 	{
		E_number = e_number;
	}
	/***************************************************************************************/
	public boolean isrunToPickUp() 	{
		return runToPickUp;
	}
	/***************************************************************************************/
	public void setrunToPickUp(boolean runToPickUp)	{
		this.runToPickUp = runToPickUp;
	}
	/***************************************************************************************/
	public HashMap<Integer, ArrayList<Request>> getNewReqests() {
		return newReqests;
	}
	/***************************************************************************************/
	public HashMap<Integer, ArrayList<Request>> getInProgress() {
		return inProgress;
	}
	/***************************************************************************************/
	public void setPowerON(boolean powerON) {
		this.powerON = powerON;
	}
	/***************************************************************************************/
	public void run() {	}
	/***************************************************************************************/
	public boolean noMoreUpReqThan(int destination)
	{
		for(int i = destination+1 ; i< this.topFloor+1; i++)
			if(pickupQ[i] == true || stopQ[i] != 0)
				return false;

		return true;
	}
	/***************************************************************************************/
	public boolean noMoreLowReqThan(int destination)
	{
		for(int i = destination-1 ; i >= 0; i--)
			if(pickupQ[i] == true || stopQ[i] != 0)
				return false;

		return true;
	}
	/***************************************************************************************/
	public boolean isAlgoFlag() {
		return algoFlag;
	}
	/**************************************************************************************/

	public void setAlgoFlag(boolean algoFlag) {
		this.algoFlag = algoFlag;
	}
	/**************************************************************************************/
	protected void checkIfTurnOff()
	{
		boolean stop;
			stop =true;
			for(int i=0 ; i< this.topFloor+1; i++)
				if(stopQ[i] != 0 || pickupQ[i])
				{
					stop= false;
					break;
				}

			if(stop)
				this.WORKING = false;
	}
	/**************************************************************************************/
}


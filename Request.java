/**
* @author  Sklonnie Konstantin
* @since   2019-16-01 
 */

package elevatorSimulation;

public class Request 
{
	/***************************************************************************************/
	public int from;
	public int destination;
	public boolean goingUP;
	public int people_Number;
	public int distance;
	private int startingTime;
	private int gotService;
	private int endTime;
	public int num;
	public double p;
	/***************************************************************************************/
	public Request(int dest, int maxlevel, int number) {
		people_Number= (int) ((Math.random()*15) % 3)+1;
		setGotService(0);
		endTime = 0;
		num=number;
		p= Math.random();
		if(p > 0.5)		{
			from =0 ;
			destination = dest;		}
		else		{
			destination =0 ;
			from =dest;
		}
		distance = Math.abs(destination - from);
		if( (destination - from ) > 0)
			goingUP = true;      /*  GOING UP   */
		else
			goingUP= false;
	}
	/***************************************************************************************/
	public Request(int dest, int maxlevel , Request goRequest) {
		people_Number = goRequest.people_Number;
		setGotService(0);
		endTime = 0;
		num=goRequest.num;
		from = goRequest.destination;
		if(from == 0)
			destination = goRequest.from;
		else
			destination =0 ;
	
		distance = Math.abs(destination - from);
		goingUP = !goRequest.goingUP;
	}
	/***************************************************************************************/
	public int getStartingTime() {
		return startingTime;
	}
	/***************************************************************************************/
	public void setStartingTime(int startingTime) {
		this.startingTime = startingTime;
	}
	/***************************************************************************************/
	public int getEndTime() {
		return endTime;
	}
	/***************************************************************************************/
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	/***************************************************************************************/
	public int getGotService() {
		return gotService;
	}
	/***************************************************************************************/
	public void setGotService(int gotService) {
		this.gotService = gotService;
	}
}

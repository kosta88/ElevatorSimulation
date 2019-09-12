/**
 * @author  Sklonnie Konstantin
 * @since   2019-25-01 
 */

package elevatorSimulation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class DisplayPanel extends JPanel
{
	/***************************************************************************************/
	private static final long serialVersionUID = 1082455875299232643L;
	private int yPosition;
	private int max;
	private JFrame frame;
	BufferedImage elevatorImg ,openElevatorImg;

	public boolean closedDoors;

	/***************************************************************************************/
	public DisplayPanel( JFrame mainframe, int topFloor, int currPos,  int IDnum ) throws IOException
	{
		closedDoors= true;
		max = topFloor;
		frame = mainframe;
		yPosition = currPos;
		setLayout( null );
		setSize( new Dimension( (Main.screenWidth) / (Building.numElevetors*3)+5, Main.screenHeight-85 ) );
		setLocation( IDnum *(Main.screenWidth / (Building.numElevetors*3))+5, 0 );
		setBorder( BorderFactory.createLineBorder(Color.darkGray, 3 ) );
		
		
		elevatorImg =  ImageIO.read(new File("elevator.png"));	
		openElevatorImg =  ImageIO.read(new File("elevator3.png"));
		frame.repaint();
	}
	/***************************************************************************************/
	public void setFloor(int floor)
	{
		yPosition = floor;

		frame.repaint();
	}
	/***************************************************************************************/
	public void draw(Graphics g)
	{
		if(closedDoors)
			g.drawImage(elevatorImg, 5,  14*(max - yPosition), this);
		else
			g.drawImage(openElevatorImg, 5,  14*(max - yPosition), this);
	}
	/***************************************************************************************/
	public void paintComponent(Graphics g)
	{
		draw(g);
	}
	/***************************************************************************************/	
}

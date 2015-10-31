import java.util.*;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class PlayerAI extends ClientAI {
	
	
	int newx=-1;
	int newy=-1;
	boolean gotPU = true;
	String ClosestPU = null;
	public PlayerAI() {
		//Write your initialization here
	}

	@Override
	public Move getMove(Gameboard gameboard, Opponent opponent, Player player) throws NoItemException, MapOutOfBoundsException {
		
		//Write your AI here
		int x;
		int y;
		int i=0;
		x= player.getX();
		y= player.getY();
		int oppoX=opponent.getX();
		int oppoY=opponent.getY();
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("filename.txt", true)))) 
		{
			writer.write("before anything "+x+" "+y+"\n");
			if(gotPU)
			{
				gotPU=false;
				for(i=0;i<11;i++)
				{
					ClosestPU=findClosestPU(x,y,i,gameboard);
					if(ClosestPU !=",")
					{
						writer.write("closest "+x+" "+y+ " "+i+" "+ ClosestPU +"\n");
						break;
					}
				}
			}
			writer.write("before parsing "+x+" "+y+"\n");
			String a = ClosestPU.substring(0,ClosestPU.indexOf(","));
			newx=Integer.parseInt(a);
			String b = ClosestPU.substring(ClosestPU.indexOf(",")+1, ClosestPU.length());
			newy=Integer.parseInt(b);
			writer.write("before firing "+x+" "+y+"\n");
			if(ifShouldWait(x,y,gameboard))
			{
				return Move.NONE;
				
			}
			writer.write("before check face "+x+" "+y+ " "+newx+" "+ newy+"\n");
			if(checkFace(x,y,newx,newy,oppoX,oppoY,player.getDirection().toString(),gameboard))
			{
				writer.write("Forward "+x+" "+y+ " "+a+" "+ b+"\n");
				if ((Math.abs(x-newx)+Math.abs(y-newy))== 1)
				{
				gotPU = true;
				}
				return Move.FORWARD;
			}
			else
			{
				writer.write("Change Face"+x+" "+y+ " "+a+" "+ b+"\n");
				return FaceOrMove(changeFace(x,y,newx,newy,oppoX,oppoY,gameboard,player.getDirection().toString()),player.getDirection().toString());
			}
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return Move.NONE;
	}
	
	private String findClosestPU(int x, int y, int d,Gameboard gameboard) throws MapOutOfBoundsException
	{

			
		int a,b;
		for (a=x-d;a<=(x+d);a++)
		{
			b=y+(d-Math.abs(x-a));
			if(a>=0 && b>=0 && a<gameboard.getWidth() && b<gameboard.getHeight())
			{
				if (gameboard.isPowerUpAtTile(a,b))
				{
					String coord= a+","+b;
					return coord;
				}
			}
			b=y-(d-Math.abs(x-a));
			if(b>=0 && a>=0 && a<gameboard.getWidth() && b<gameboard.getHeight())
			{
				if (gameboard.isPowerUpAtTile(a,b))
				{
					String coord= a+","+b;
					return coord;
				}
			}	
		}
		return ",";
	}
	
	private boolean checkFace(int x1, int y1, int x2, int y2, int x3, int y3,String Dir, Gameboard gameboard) throws MapOutOfBoundsException
	{
		if (Dir=="RIGHT")
		{
			if (x2 > x1)
			{
				return validMove(x1,y1,x3,y3,"RIGHT",gameboard);
			}
		}
		else if (Dir=="LEFT")
		{
			if (x1 > x2)
			{
				return validMove(x1,y1,x3,y3,"LEFT",gameboard);
			}
		}
		else if (Dir=="UP")
		{
			if (y1 > y2)
			{
				return validMove(x1,y1,x3,y3,"UP",gameboard);
			}
		}
		else if(Dir=="DOWN")
		{
			if (y2 > y1)
			{
				return validMove(x1,y1,x3,y3,"DOWN",gameboard);
			}
		}

		return false;
	}
	
	private Move changeFace(int x1, int y1, int x2, int y2,int x3, int y3,Gameboard gameboard,String Dir) throws MapOutOfBoundsException
	{
			if (x2>x1)
			{
				if (validMove(x1,y1,x3,y3,"RIGHT",gameboard))
				{
				    if(!(gotPU==false && Dir == "LEFT"))
				    	return Move.FACE_RIGHT;
				}
			}
			if (y1 < y2)
			{
			    if (validMove(x1,y1,x3,y3,"DOWN",gameboard))
			    {
			    	if(!(gotPU==false && Dir == "UP"))
			    		return Move.FACE_DOWN;
			    }
			}
			if(x1 >x2)
			{
				if (validMove(x1,y1,x3,y3,"LEFT",gameboard))
				{
					if(!(gotPU==false && Dir == "RIGHT"))
						return Move.FACE_LEFT;
				}
			}
			if (y2 < y1 )
			{
				if (validMove(x1,y1,x3,y3,"UP",gameboard))
				{
					if(!(gotPU==false && Dir == "DOWN"))
					 return Move.FACE_UP;
				}
			}
		   //priority given to the current direction
			if(Dir=="RIGHT")
			{
				 if (x1<gameboard.getWidth())
					{
						if (validMove(x1,y1,x3,y3,"RIGHT",gameboard))
						return Move.FACE_RIGHT;
					}
			}
			if(Dir=="DOWN")
			{
				if (y1<gameboard.getHeight())
				{
					if (validMove(x1,y1,x3,y3,"DOWN",gameboard))
					return Move.FACE_DOWN;
				}
			}
			if(Dir=="LEFT")
			{
				if (x1>0)
				{
					if (validMove(x1,y1,x3,y3,"LEFT",gameboard))
					return Move.FACE_LEFT;
				}
			}
			if(Dir=="UP")
			{
				if (y1>0)
				{
					if (validMove(x1,y1,x3,y3,"UP",gameboard))
					return Move.FACE_UP;
				}
			}
		    if (x1<gameboard.getWidth())
			{
				if (validMove(x1,y1,x3,y3,"RIGHT",gameboard))
				return Move.FACE_RIGHT;
			}
		    if (y1<gameboard.getHeight())
			{
				if (validMove(x1,y1,x3,y3,"DOWN",gameboard))
				return Move.FACE_DOWN;
			}
		    if (x1>0)
			{
				if (validMove(x1,y1,x3,y3,"LEFT",gameboard))
				return Move.FACE_LEFT;
			}
		    if (y1>0)
			{
				if (validMove(x1,y1,x3,y3,"UP",gameboard))
				return Move.FACE_UP;
			}
		 
			return Move.NONE;
	}
	
	private boolean validMove( int x1,int y1,  int x2,int y2,String Dir, Gameboard gameboard) throws MapOutOfBoundsException//check if it's safe to move to the next cell
	{
		if (Dir=="RIGHT")
		{
			if(x2!=(x1+1)||y2!=y1)
			{
				if(!gameboard.areBulletsAtTile(x1+1,y1)&&!gameboard.isWallAtTile(x1+1,y1)&&!gameboard.isTurretAtTile(x1+1,y1))
				{
					return true;
				}
			}
		}
		else if (Dir=="LEFT")
		{
			if(x2!=(x1-1)||y2!=y1)
			{
				if(!gameboard.areBulletsAtTile(x1-1,y1)&&!gameboard.isWallAtTile(x1-1,y1)&&!gameboard.isTurretAtTile(x1-1,y1))
				{
					return true;
				}
			}
		}
		else if (Dir=="UP")
		{
			if(x2!=x1||y2!=(y1-1))
			{
				if(!gameboard.areBulletsAtTile(x1,y1-1)&&!gameboard.isWallAtTile(x1,y1-1)&&!gameboard.isTurretAtTile(x1,y1-1))
				{
					return true;
				}
			}
		}
		else if(Dir=="DOWN")
		{
			if(x2!=x1||y2!=(y1+1))
			{
				if(!gameboard.areBulletsAtTile(x1,y1+1)&&!gameboard.isWallAtTile(x1,y1+1)&&!gameboard.isTurretAtTile(x1,y1+1))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	private Move FaceOrMove(Move move,String Dir) 
	{
		if((move ==Move.FACE_RIGHT&&Dir=="RIGHT")||(move ==Move.FACE_LEFT&&Dir=="LEFT")||(move ==Move.FACE_UP&&Dir=="UP")||(move ==Move.FACE_DOWN&&Dir=="DOWN"))
		return Move.FORWARD;
		return move;
	}
	




private boolean ifShouldWait(int x, int y, Gameboard gameboard) throws MapOutOfBoundsException, NoItemException{
	if(x>0&&y>0)
	{
		if(gameboard.isTurretAtTile(x-1,y-1)&&((gameboard.getTurretAtTile(x-1,y-1).isFiringNextTurn())||gameboard.getTurretAtTile(x-1,y-1).didFire()))
				{
			
					return true;
				}
	}
	if(y>0)
	{
		if(gameboard.isTurretAtTile(x,y-1)&&((gameboard.getTurretAtTile(x,y-1).isFiringNextTurn())||gameboard.getTurretAtTile(x,y-1).didFire()))
				{
			
					//return true;
				}
	}
	if(x<gameboard.getWidth()&&y>0)
	{
		if(gameboard.isTurretAtTile(x+1,y-1)&&((gameboard.getTurretAtTile(x+1,y-1).isFiringNextTurn())||gameboard.getTurretAtTile(x+1,y-1).didFire()))
				{
			
					return true;
				}
	}
	if(x>0)
	{
		if(gameboard.isTurretAtTile(x-1,y)&&((gameboard.getTurretAtTile(x-1,y).isFiringNextTurn())||gameboard.getTurretAtTile(x-1,y).didFire()))
				{
			
					//return true;
				}
	}
	if(x<gameboard.getWidth())
	{
		if(gameboard.isTurretAtTile(x+1,y)&&((gameboard.getTurretAtTile(x+1,y).isFiringNextTurn())||gameboard.getTurretAtTile(x+1,y).didFire()))
				{
			
					//return true;
				}
	}
	if(x>0&&y<gameboard.getHeight())
	{
		if(gameboard.isTurretAtTile(x-1,y+1)&&((gameboard.getTurretAtTile(x-1,y+1).isFiringNextTurn())||gameboard.getTurretAtTile(x-1,y+1).didFire()))
				{
			
					return true;
				}
	}
	if(y<gameboard.getHeight())
	{
		if(gameboard.isTurretAtTile(x,y+1)&&((gameboard.getTurretAtTile(x,y+1).isFiringNextTurn())||gameboard.getTurretAtTile(x,y+1).didFire()))
				{
			
				//	return true;
				}
	}
	if(x<gameboard.getWidth()&&y<gameboard.getHeight())
	{
		if(gameboard.isTurretAtTile(x+1,y+1)&&((gameboard.getTurretAtTile(x+1,y+1).isFiringNextTurn())||gameboard.getTurretAtTile(x+1,y+1).didFire()))
				{
			
					return true;
				}
	}
	
	
return false;
}
}


//@author SufiyaanNadeem

import becker.robots.*;
import java.awt.Color;

public class Papers extends Object
{
	//Global vars for control flow and distances
	public static int move_down;
	public static int cycle;
	public static int away;
	
	public static void main(String[] args) 
	{
		// Uncomment one line to test different cases.
		//City city = new City("House1.txt");
		//City city = new City("House2.txt");
		City city = new City("House3.txt");

		final Robot reader = new Robot(city, 2, 2, Direction.EAST);
		final Robot carrier = new Robot(city, 8, 1, Direction.EAST, 6);
		
		// Your solution goes here
		
		//Labelling
		carrier.setColor(Color.ORANGE);
		carrier.setLabel(Integer.toString(carrier.countThingsInBackpack()));
		reader.setLabel("Sue");
		
		carrier.move();//First Definite Step of each test case
		carrier.turnLeft();//Second Step
		
		
		int streetDist=reader.getStreet()-carrier.getStreet();//Initial Dist between carrier and reader
		move_down=0;//Distance to porch
		cycle=0;//Loop cycle set to 0
		away=0;//Counts how many times the customer was missed and cancels subscription when more than once
		
		//To calculate move_down and for first delivery
		for (int i=0;i<Math.abs(streetDist);i++) {
			if (carrier.frontIsClear()) {
				carrier.move();
				move_down++;
			}
		}
		
		//First time putting Paper
		carrier.putThing();
		carrier.setLabel(Integer.toString(carrier.countThingsInBackpack()));
		carrier.turnLeft();
		carrier.turnLeft();
		//Back to Post office
		for (int i=0;i<move_down;i++) {
			if (carrier.frontIsClear()) {
				carrier.move();
			}
		}	
		carrier.turnLeft();
		carrier.move();
		
		//Setting Reader to Reading Spot
		reader.turnLeft();
		for (int i=0;i<2;i++) {
			if (reader.frontIsClear()) {
				reader.move();
			}
		}		
		reader.turnLeft();
		reader.move();
		reader.turnLeft();
		for (int i=0;i<5;i++) {
			if (reader.frontIsClear()) {
				reader.move();
			}
		}
		reader.turnLeft();
		
		//Thread for independent movement of reader
		Thread readerThread = new Thread(new Runnable() {
            public void run() {
            	
            	//Follows normal pickup routine for 3 cycles
        		while (cycle<3) {
        			pickup(reader);
        		}
        		
        		for (int i=0;i<4;i++) {
        			reader.setLabel("Leave");
        			if (reader.frontIsClear()) {
        				reader.move();
        			}
        		}	
   		
            }
        });
		
		//Thread for independent movement of carrier
        Thread carrierThread = new Thread(new Runnable() {
            public void run() {
            	//Follows normal drop of routine until reader has been away twice or there are 0 items in backpack
        		while(carrier.countThingsInBackpack()>0 && away<2) {
        			deliver(carrier,move_down);
        		}
        		carrier.setLabel("END");
            }
        });
		
        readerThread.start();
        carrierThread.start();
	}
	
	//Reader's Pick Up routine
	private static void pickup(Robot reader) {
		reader.move();
		if (reader.canPickThing()) {
			reader.pickThing();
			reader.setLabel("Pick");
			reader.turnLeft();
			reader.turnLeft();
			reader.move();
			//Waiting
			reader.setLabel("Read");
			for(int i=0;i<10;i++) {
				reader.turnLeft();
			}
			reader.putThing();
			reader.setLabel("Drop");
		} else {
			reader.setLabel("Leave");
			for (int i=0;i<4;i++) {
				reader.move();
			}
			reader.turnLeft();
			reader.turnLeft();
		}

	}

	//Carrier's Delivery routine
	private static void deliver(Robot carrier, int move_down) {
		carrier.turnLeft();
		carrier.turnLeft();
		carrier.move();
		
		carrier.turnLeft();
		carrier.turnLeft();
		carrier.turnLeft();
		
		for (int i=0;i<move_down;i++) {
				carrier.move();
		}
		
		if (carrier.canPickThing()) {
			carrier.turnLeft();
			carrier.turnLeft();
			away++;
			if (away==1) {
				carrier.setLabel("!");
			} else if (away==2) {
				carrier.setLabel("!!");
			}
		} else {
			carrier.putThing();
			carrier.setLabel(Integer.toString(carrier.countThingsInBackpack()));
			carrier.turnLeft();
			carrier.turnLeft();
		}
		for (int i=0;i<move_down;i++) {
			if(carrier.frontIsClear()){
				carrier.move();
			}
		}
		
		carrier.turnLeft();
		carrier.move();
		cycle++;
	}
}

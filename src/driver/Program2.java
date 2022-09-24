package driver;

import java.util.*;
import java.io.*;
import hash341.City;
import hash341.CityTable;

public class Program2 {
	static Scanner sc = new Scanner(System.in);
	
	public static void main(String [] args) {
		CityTable US_Cities ;
	    String cName;
	    
	    US_Cities = CityTable.readFromFile("US_Cities_LL.ser") ;
	    
	    System.out.print("Enter City, State (or 'quit'): ");
		cName = sc.nextLine();
	    
		while (!cName.equals("quit")) {
			City aCity = US_Cities.find(cName);
			if(aCity == null) {
				System.out.println("Could not find '" + cName + "'");
			} else {
				System.out.println("Found " + aCity.name + " (" + aCity.latitude + "," + aCity.longitude + ")" );
				System.out.println("http://www.google.com/maps?z=10&q=" + aCity.latitude + "," + aCity.longitude);
			}
			
			System.out.print("Enter City, State (or 'quit'): ");
			cName = sc.nextLine();
			
		}
		
	}
}

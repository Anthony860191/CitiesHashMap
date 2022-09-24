package driver;

import java.util.*;
import java.io.*;
import hash341.City;
import hash341.CityTable;

public class Program1 {
	public static void main(String [] args) {
		CityTable cityTable = new CityTable("US_Cities_LL.txt", 16000);
		cityTable.writeToFile("US_Cities_LL.ser");
	}
}

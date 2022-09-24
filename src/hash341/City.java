package hash341;
import java.io.*;
import java.util.*;

public class City implements Serializable{
	public String name;
	public float latitude;
	public float longitude;
	
	public City (String Name, float Latitude, float Longitude) {
		name = Name;
		latitude = Latitude;
		longitude = Longitude;
	}
}

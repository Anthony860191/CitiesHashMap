package hash341;
import java.io.*;
import java.util.*;

public class CityTable implements Serializable{
	public int totalCities = 0; // the total number of cities in the file
	public int Tsize; // the size of the primary hash table
	public int collisions[]; // the number of collisions and a particular index of the primary hash table
	public int maxCollisions = 0; // the maximum number of collisions that occurs in the primary hash table
	Hash24 primaryHash = new Hash24(); // the primary hash function to be used
	public Hash24[] hashFunctions; // an array that will store the secondary hash tables hash functions of each index
	public ArrayList<ArrayList<City>> hashTable; // an array list that will store our hash table of cities
	public ArrayList<City> citiesMostCollisions; // array list to hold the cities in the index with the most collisions
	
	public CityTable (String fname, int tsize) {
		boolean debug = true;
		Scanner infile = null;
		try {
			infile = new Scanner (new FileReader(fname));
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
			System.exit(0);
		}
		
		Tsize = tsize; // set the primary hash table size for our city table object
		hashFunctions = new Hash24[tsize]; // fully initialize hashFunction to the tsize passed in
		hashTable = new ArrayList<ArrayList<City>>(); // fully initialize the HashTable
		for(int i = 0; i < tsize; i++) {
			hashTable.add(new ArrayList<City>()); // go through and initialize every empty ArrayList<City> element in our primary hash table
		}
		collisions = new int[tsize]; // fully initialize collisions to the tsize passed in 
		
		while (infile.hasNextLine()) {
			String cityName = infile.nextLine(); // read in the city name from the file
			String line = infile.nextLine();
			StringTokenizer tokenizer = new StringTokenizer(line);
			float latitude = Float.parseFloat(tokenizer.nextToken()); // read and store the latitude of the city read
			float longitude = Float.parseFloat(tokenizer.nextToken()); // read and store the longitude of the city read
			
			City newCity = new City(cityName, latitude, longitude); // initialize a new City object with the data read in from the file
			int index = primaryHash.hash(cityName)% tsize; // find the appropriate index of the object using our primaryHash funciton
			
			hashTable.get(index).add(newCity); // 
			collisions[index]++; // increment the collisions at the index we added the City into
			if(maxCollisions < collisions[index]) { // make sure to update maxCollisions and citiesMostCollisions
				maxCollisions = collisions[index];
				citiesMostCollisions = hashTable.get(index);
			}
			totalCities++; // increment the total number of cities 
		}
		int numCities[] = new int[25]; // create an array that will count the number of primary hash table slots with 0 - 24 cities in them
		int hashFunctionsTried [] = new int[21]; // create an array to store the number of hash functions tried for each secondary hash table
		for(int i = 0; i <tsize; i++) { 
			numCities[collisions[i]]++; // iterate through the whole collisions array and increment the proper numCities element
			if(collisions[i] > 1) { // if the number of cities in a slot is greater than 1 than create a secondary hash function
				Hash24 secondaryHash = null; // initialize a secondaryHash object to store the secondary Hash function
				int numHashes = 0; // keep track of the total number of hashed required to get a collisionless one
				while(secondaryHash == null) {
					secondaryHash = findSecondaryHash(hashTable.get(i), collisions[i]); // calculate a new secondary hash function
					numHashes++;
				}
				hashFunctionsTried[numHashes]++; // store the amount of hash functions tried
				hashFunctions[i] = secondaryHash; // store the secondary table hash function 
				ArrayList<City> secondaryHashTable = new ArrayList<City>(); 
				for(int j = 0; j < collisions[i]*collisions[i]; j++) { // initialize the secondaryHashTable to have the proper size
					secondaryHashTable.add(null);
				}
				for(int j = 0; j < hashTable.get(i).size(); j++) {
					String cName = hashTable.get(i).get(j).name; // obtain the name of the city we are going to hash
					int index = secondaryHash.hash(cName)%(collisions[i]*collisions[i]); // get index of the city using secondary hash function
					secondaryHashTable.set(index, hashTable.get(i).get(j)); // insert the city into the proper hashed index
				}
				hashTable.set(i,secondaryHashTable); // insert the new hashed secondary table into the correct index in our hash table
			} else {
				hashFunctions[i] = primaryHash; // store the primary hash for indices in our hash functions array where there were no collisions
			}
		}
		
		if(debug) {
			System.out.println("Primary has table hash funciton:");
			primaryHash.dump();
			System.out.println("\nPrimary hash table statistics");
			System.out.println("   Number of cities: " + totalCities);
			System.out.println("   Table size: " + tsize);
			System.out.println("   Max collisions = " + maxCollisions);
			for(int i = 0; i < 25; i++) {
				System.out.println("    # of primary slots with " + i + " cities = " + numCities[i]);
			}
			System.out.println("\n\n*** Cities in the slot with most collisions ***");
			for(int i = 0; i < citiesMostCollisions.size(); i++) {
				System.out.println("   " + citiesMostCollisions.get(i).name + " (" + citiesMostCollisions.get(i).latitude + "," + citiesMostCollisions.get(i).longitude + ")");
			}
			System.out.println("\nSecondary hash table statistics:");
			int numSecondaryHashTables = 0; // number to keep track of the number of hash tables with more than 1 item
			float totalSecondaryHashFunctions = 0; // number to keep track of the total number of Secondary hash functions tried 
			for(int i = 1; i < 21; i++) {
				System.out.println("   # of secondary hash tables trying " + i + " hash functions = " + hashFunctionsTried[i]);
				numSecondaryHashTables += hashFunctionsTried[i];
				totalSecondaryHashFunctions += i*hashFunctionsTried[i];
			}
			System.out.println("\nNumber of secondary hash tables with more than 1 item = " + numSecondaryHashTables);
			System.out.println("Average # of hash functions tried = " + totalSecondaryHashFunctions/numSecondaryHashTables);
			
		}
		infile.close();
	}
	
	public Hash24 findSecondaryHash(ArrayList<City> cities, int numCollisions) { // function that will return a collionsless hash function otherwise it will return null
		int collisions[] = new int[numCollisions*numCollisions]; // array to keep track of the total number of collisions at specific indices
		Hash24 h2 = new Hash24(); // new hash function to hash the ArrayList of cities passed in
		for(int i = 0; i < cities.size(); i++) {
			int index  = h2.hash(cities.get(i).name)%(numCollisions*numCollisions); //hash all of the cities in the passed in ArrayList of cities
			collisions[index]++; // increment the collisions at the index we hashed
			if (collisions[index] > 1){ // if there is ever a collision between two cities at an index then return null
				return null;
			}
		}
		return h2;
	}
	
	public City find(String cName) {
		int primaryIndex = primaryHash.hash(cName)%Tsize; // find the primaryIndex of the city name given
		if(!hashTable.get(primaryIndex).isEmpty()) { // check if the index is empty
			Hash24 h2 = hashFunctions[primaryIndex]; // find the secondaryHash function from our array
			int secondaryIndex = h2.hash(cName)%hashTable.get(primaryIndex).size(); // use secondary hash function to calculate index in the secondary table
			if(hashTable.get(primaryIndex).get(secondaryIndex) != null && hashTable.get(primaryIndex).get(secondaryIndex).name.equals(cName)) { // check if the city's name at this index in our hash table is the same as the one passed in
				return hashTable.get(primaryIndex).get(secondaryIndex); // if so return the city
			}
		
		} 
		return null;
	}
	
	public void writeToFile(String fName) {
		try {
			FileOutputStream out = new FileOutputStream(fName);
			ObjectOutputStream oout = new ObjectOutputStream(out);
		
			oout.writeObject(this);
			
			oout.close();
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	public static CityTable readFromFile(String fName) {
		
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fName));
			
			CityTable cityTable = (CityTable) ois.readObject();
			
			ois.close();
			
			return cityTable;
			
		} catch (Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

	
}

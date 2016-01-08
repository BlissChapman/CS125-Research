import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
/**
 *  A simple map-like list of NetIDPairs with a file argument constructor.
 *  Can return the code associated with a particular netID passed into its
 *  getSecret() method.
 *  @author CS125Research
 */
public class NRList implements Iterable<NetIDPair>{
	
	/* A list of all NetIDPairs extracted from the roster */
	private ArrayList <NetIDPair> pairList = new ArrayList<NetIDPair>();
	/* The maximum allowable size of this NRList */
	private int capacity;
	
	/**
	 * String collection constructor. Takes an ArrayList of netIDs and then
	 * makes a NRList from these netIDs, randomly assigning netIDs to codes in
	 * the interval [0, range-1]. 
	 * 
	 * @param netIDs The collection of netIDs used to form the NRList.
	 * @param range  Range element for NetIDPair random generator.
	 */
	public NRList(Iterable<String> netIDs, int range){
		capacity = range;
		for (String elem : netIDs)
			addPair(elem);
	}
	
	/**
	 * File argument constructor. Reads input file and fills up data
	 * structure with NetIDPair objects from the file. The codes of these
	 * NetIDPairs will be in the interval [0, range-1].
	 *
	 * @param inFile  The name of the file from which we read netIDs.
	 * @param range   Range element for NetIDPair random generator.
	 */
	public NRList(String inFile, int range){
		capacity = range;
		fillList(inFile);
	}
	
	/**
	 * Helper function for constructor that does all the work.
	 * Takes in filenames to get a source of NetIDs and a
	 * range to determine the range of ID codes assigned. Also used
	 * to add new NetIDPairs to an existing NRList.
	 * 
	 * @param readFilePath The source file for all netIDs.
	 */
	public void fillList(String readFilePath)
	{
		TextIO.readFile(readFilePath);		
		while(!TextIO.eof())
		{
			String line = TextIO.getln();
			String netID = line.split(",")[1];
			addPair(netID);
		}
	}
	
	/**
	 * Adds a NetIDPair object to this NRList whose netID field
	 * matches the argument of this method. The NRList must not
	 * already be at full capacity or an exception will be thrown.
	 *  
	 * @param netID The netID of the NetIDPair to be added to this NRList.
	 */
	public void addPair(String netID){
		NetIDPair potentialPair = null;
		if (pairList.size() == capacity)
			throw new IndexOutOfBoundsException("NRList is full.");
		do{
			int randomID = (int) (Math.random()*capacity);
			potentialPair = new NetIDPair(netID,randomID);
		} while(check(potentialPair));
		pairList.add(potentialPair);
	}
	
	/**
	 * @return The number of NetIDPairs in this NRList. 
	 */
	public int size(){
		return pairList.size();
	}
	
	/**
	 * @return The list of net id pairs.. 
	 */
	public ArrayList<NetIDPair> getList(){
		return pairList;
	}
	
	/**
	 * @return The maximum allowed number of NetIDPairs in this NRList.
	 */
	public int capacity(){
		return capacity;
	}
	
	/**
	 *  Private helper function that searches the NRList to ensure that
	 *  no two NetIDPair instances therein correspond to the same code
	 *  or netID.
	 *
	 *  @param potentialPair  A potential NetIDPair that may be added to the list.
	 *  @return True if the code of potentialPair isn't already found in
	 *          the NRList.
	 */
	private boolean check(NetIDPair potentialPair){
		for (NetIDPair elem : pairList){
			if (elem.equals(potentialPair)){
				throw new IllegalArgumentException(
				    "Attempted to add existing netID to this Roster.");
			}
			if (elem.getCode() == potentialPair.getCode())
				return true;
		}
		return false;
	}
	
	/**
	 *  Returns the code corresponding to a given netID.
	 *  
	 *  @param netID  The netID whose code is being searched.
	 *  @return  The code of the netID argument if it is found or -1 otherwise.
	 */
	public int getSecret(String netID)
	{
		for(NetIDPair elem : pairList)
			if (elem.equals(netID))
				return elem.getCode();
		return -1;
	}
	
	/**
	 * Prints out the contents of this NRList separated by
	 * newlines.
	 */
	public void printList() {
		for (int i = 0; i < pairList.size(); i++) {
			System.out.println(pairList.get(i));
		}
	}
	
	/**
	 * @return an iterator to the start of this NRList.
	 */
	public Iterator<NetIDPair> iterator() { return pairList.iterator(); }
}

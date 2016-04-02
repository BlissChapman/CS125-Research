import java.util.*;
/**
 *  A simple map-like list of NetIDPairs with a file argument constructor.
 *  Supports the addition of new students through their netIDs, the removal
 *  of existing students through their netIDs, and will return a randomized
 *  (but consistent) code associated with each netID in the NRList given a
 *  particular netID.
 *  
 *  TODO Possibly implement resizing to parallel Roster. Discuss changing data
 *       structure to allow O(1) operations.
 *  @author CS125Research
 */
public class NRList implements Iterable<NetIDPair>{
	
	/* A list of all NetIDPairs extracted from the roster */
	private ArrayList <NetIDPair> pairList = new ArrayList<NetIDPair>();
	/* The maximum allowable size of this NRList */
	private int capacity;
	
	
	/**
	 * Capacity constructor. Takes an integer and merely assigns it as the
	 * maximum capacity of the NRList. The NRList is completely empty.
	 * 
	 * @param range Number of NetIDPairs that this list can contain.
	 *              Accordingly, also sets integer range of randomly generated
	 *              codes.
	 */
	public NRList(int range){
		capacity = range;
	}
	
	/**
	 * String collection constructor. Takes an ArrayList of netIDs and then
	 * makes a NRList from these netIDs, randomly assigning netIDs to codes in
	 * the interval [0, range-1]. 
	 * 
	 * @param netIDs The collection of netIDs used to form the NRList.
	 * @param range  Range element for NetIDPair random generator.
	 */
	public NRList(Iterable<String> netIDs, int range){
		this(range);
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
		this(range);
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
	
	public NetIDPair removePair(int code){
		Iterator<NetIDPair> destroyer = pairList.listIterator();
		while (destroyer.hasNext()){
			NetIDPair check = destroyer.next();
			if (check.getCode() == code){
				NetIDPair out = check;
				destroyer.remove();
				return out;
			}
		}
		return null; //Not found
	}
	
	/**
	 * Searches for a NetIDPair with a netID field matching the argument. If a
	 * match is found, the method removes and returns that NetIDPair from the
	 * NRList. Otherwise, the method returns null.
	 * 
	 * @param netID The netID of the NetIDPair to be removed.
	 * @return The removed NetIDPair matching the argument.
	 */
	public NetIDPair removePair(String netID){
		Iterator<NetIDPair> destroyer = pairList.listIterator();
		while (destroyer.hasNext()){
			NetIDPair check = destroyer.next();
			if (check.equals(netID)){
				NetIDPair out = check;
				destroyer.remove();
				return out;
			}
		}
		return null; //Not found
	}
	
	/**
	 * @return The number of NetIDPairs in this NRList. 
	 */
	public int size(){
		return pairList.size();
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
	 *  @param potentialPair  A potential NetIDPair that may be added to the 
	 *                        list.
	 *  @return True if the code of potentialPair isn't already found in
	 *          the NRList.
	 *          
	 *  @TODO Discuss using a data structure that allows O(1) checks.
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
	 * Prints out the contents of this NRList separated by newlines.
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

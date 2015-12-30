import java.util.ArrayList;
import java.util.Iterator;
/**
 *  A simple map-like list of NetIDPairs with a file argument constructor.
 *  Can return the code associated with a particular netID passed into its
 *  getSecret() method.
 *  @author CS125Research
 */
public class NRList implements Iterable<NetIDPair>{
	
	/** A list of all NetIDPairs extracted from the roster */
	ArrayList <NetIDPair> pairList = new ArrayList<NetIDPair>();
	
	/**
	 * File argument constructor. Reads input file and fills up data
	 * structure with NetIDPair objects from the file.
	 *
	 * @param inFile  The name of the file from which we read netIDs.
	 * @param outFile Debug output file.
	 * @param range   Range element for NetIDPair random generator.
	 */
	public NRList(String inFile, String outFile, int range){
		fillList(inFile, outFile, range);
	}
	
	/**
	 * Helper function for constructor that does all the work.
	 * Takes in filenames to get a source of NetIDs and a
	 * range to determine the range of ID codes assigned. Also used
	 * to add new NetIDPairs to an existing NRList.
	 * 
	 * @param readFilePath The source file for all NetIDs.
	 * @param range        Upper bound on ID code assigned.
	 */
	public void fillList(String readFilePath, String writeFilePath, int range)
	{
		TextIO.readFile(readFilePath);
		
		if (writeFilePath != null)
			TextIO.writeFile(writeFilePath);
		
		while(!TextIO.eof())
		{
			String line = TextIO.getln();
			String netID = line.split(",")[1];
			NetIDPair potentialPair;
			
			do{
				int randomID = (int)(Math.random()*range);
				potentialPair = new NetIDPair(netID,randomID);
			} while(check(potentialPair));
	
			pairList.add(potentialPair);
		}
	}
	
	/**
	 *  Private helper function that searches the NRList to ensure that
	 *  no two NetIDPair instances therein correspond to the same code.
	 *
	 *  @param potentialPair  A potential NetIDPair that may be added to the list.
	 *  @return True if the code of potentialPair isn't already found in
	 *          the NRList.
	 */
	private boolean check(NetIDPair potentialPair){
		boolean duplicate = false;
		for(int x = 0; x < pairList.size(); x++){
			duplicate = pairList.get(x).getRandom()==potentialPair.getRandom();	
			if(duplicate) break;
		}
		return duplicate;
	}
	
	/**
	 *  Returns the code corresponding to a given netID.
	 *  
	 *  @param netID  The netID whose code is being searched.
	 * 
	 *  @return  The code of the netID argument if it is found or -1 otherwise.
	 */
	public int getSecret(String netID)
	{
		for(NetIDPair elem : pairList)
			if (elem.equals(netID))
				return elem.getRandom();
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
	public NRIterator iterator() { return new NRIterator(); }
	
	/**
	 * Iterator class for NRList. Allows sequential access of all
	 * elements in this container.
	 */
	public class NRIterator implements Iterator<NetIDPair>{
		private int curr;
		
		public NRIterator()      { curr = 0; }
		public boolean hasNext() { return curr < pairList.size(); }
		public NetIDPair next()  { return pairList.get(curr++); }
		public void remove()     { throw new UnsupportedOperationException(); }
	}
}

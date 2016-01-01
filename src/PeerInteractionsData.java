import java.util.*;

/**
 * @author CS125Research
 */
public class PeerInteractionsData {

	/**Raw (uncleaned) PeerInteraction objects constructed
	 *  from the "peerInteractions.fa2015.final.csv" file.
	 *	NOTE: entries could be corrupt **/
	public static ArrayList<PeerInteraction> rawData;
	/** The valid PeerInteraction objects filtered from the "rawData" list. */
	public static ArrayList<PeerInteraction> cleanData;
	
	/** The percent of valid PeerInteraction objects constructed
	 *  from the CSV file and held in the cleanData list. */
	public static double percentValid;
	/** The number of valid PeerInteraction objects constructed
	 *  from the CSV file and held in the cleanData list. */
	public static int numberOfValidEntries;
	/** The number of PeerInteraction objects constructed
	 *  from the CSV file and held in the rawData list. */
	public static int numberOfEntries;


	/** Initialize reads through the "peerInteractions.fa2015.final.csv"
	 *  file constructing PeerInteraction objects from each line.
	 *  It then goes through the list of entry objects it just created 
	 *  and creates another list of valid entries */
	public static void initialize() 
	{
		System.out.println("Calling PeerInteractionsData.initialize()");
				
		rawData = new ArrayList<PeerInteraction>();
		cleanData = new ArrayList<PeerInteraction>();

		TextIO.readFile("./src/peerInteractions.fa2015.final.csv");
		while (!TextIO.eof())
			rawData.add(new PeerInteraction(TextIO.getln()));

		for (PeerInteraction elem : rawData){
			numberOfEntries++;
			if (elem.valid()){
				++numberOfValidEntries;
				cleanData.add(elem);
			}
		}
		
		percentValid = 100*((double)numberOfValidEntries/(double)numberOfEntries);
	}
}




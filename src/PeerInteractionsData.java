import java.util.*;

/**
 * @author CS125Research
 */
public class PeerInteractionsData {

	/** Uncleaned feedback entries constructed from the csv file.
	 *	NOTE: entries could be corrupt **/
	public static ArrayList<FeedbackEntry> rawData;
	/** The valid feedback entry objects filtered from the "rawData" list. */
	public static ArrayList<FeedbackEntry> cleanData;
	
	/** The percent of valid feedback entries constructed
	 *  from the csv file and held in the raw data list. */
	public static double percentValid;
	/** The number of valid feedback entries constructed
	 *  from the csv file and held in the raw data list. */
	public static int numberOfValidEntries;
	/** The number of feedback entry objects (corrupt and valid) constructed
	 *  from the csv file and held in the raw data list. */
	public static int numberOfEntries;


	/** Initialize reads through the "peerInteractions.fa2015.final.csv"
	 *  file constructing FeedbackEntry objects from each line.
	 *  It then goes through the list of entry objects it just created 
	 *  and creates another list of valid entries */
	public static void initialize() 
	{
		System.out.println("Calling PeerInteractionsData.initialize()");
				
		rawData = new ArrayList<FeedbackEntry>();
		cleanData = new ArrayList<FeedbackEntry>();

		TextIO.readFile("./src/peerInteractions.fa2015.final.csv");
		while (!TextIO.eof())
			rawData.add(new FeedbackEntry(TextIO.getln()));

		for (FeedbackEntry elem : rawData){
			numberOfEntries++;
			if (elem.valid()){
				++numberOfValidEntries;
				cleanData.add(elem);
			}
		}
		
		percentValid = 100*((double)numberOfValidEntries/(double)numberOfEntries);
	}
}




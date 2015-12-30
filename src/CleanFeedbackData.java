import java.util.*;
public class CleanFeedbackData {

	///TODO - document each of these array lists
	public static ArrayList<FeedbackEntry> our_data;
	public static ArrayList<FeedbackEntry> clean_data;
	
	///TODO - document each of these properties
	public static double percentValid;
	public static int numberOfValidEntries;
	public static int numberOfEntries;


	///TODO - document and possibly rename
	public static void initialize() 
	{
		System.out.println("Calling CleanFeedbackData.initialize()");
				
		our_data = new ArrayList<FeedbackEntry>();
		clean_data = new ArrayList<FeedbackEntry>();

		TextIO.readFile("./src/peerInteractions.fa2015.final.csv");
		while (!TextIO.eof())
			our_data.add(new FeedbackEntry(TextIO.getln()));

		for (FeedbackEntry elem : our_data){
			numberOfEntries++;
			if (elem.valid()){
				++numberOfValidEntries;
				clean_data.add(elem);
			}
		}
		
		percentValid = 100*((double)numberOfValidEntries/(double)numberOfEntries);
	}
}




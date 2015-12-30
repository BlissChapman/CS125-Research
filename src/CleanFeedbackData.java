import java.util.*;
public class CleanFeedbackData {

	///TODO - document each of these array lists
	public static ArrayList<FeedbackEntry> our_data;
	public static ArrayList<FeedbackEntry> clean_data;

	public static void initialize() 
	{
		System.out.println("Calling CleanFeedbackData.initialize()");
				
		our_data = new ArrayList<FeedbackEntry>();
		clean_data = new ArrayList<FeedbackEntry>();

		TextIO.readFile("./src/peerInteractions.fa2015.final.csv");
		while (!TextIO.eof())
			//our_data.add(new FeedbackEntry(TextIO.getln(),awesomeList));
			our_data.add(new FeedbackEntry(TextIO.getln()));
		double num_valid = 0.0;
		double count = 0.0;

		for (FeedbackEntry elem : our_data){
			++count;
			if (elem.valid()){
				++num_valid;
				clean_data.add(elem);
			}
		}
		System.out.printf("Percentage of entries valid: %f%%\nNumber of valid entries: %d\nNumber of entries: %d\n", 100*num_valid/count, (int) num_valid, (int) count);
		System.out.println("======================================================================================\n");
	}
}




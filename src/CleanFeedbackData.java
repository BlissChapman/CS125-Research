import java.util.*;
public class CleanFeedbackData {

	public static ArrayList<FeedbackEntry> our_data;
	public static ArrayList<FeedbackEntry> clean_data;

	public static void initialize() 
	{
		System.out.println("Calling CleanFeedbackData.initialize()");
		NRList awesomeList= new NRList("/Users/chapman/Documents/workspaceResearch/EncodeNetID/src/roster.txt",null,9000);
		TextIO.readFile("/Users/chapman/Documents/workspaceResearch/secretText.txt");
		our_data = new ArrayList<FeedbackEntry>();
		clean_data = new ArrayList<FeedbackEntry>();
		//TextIO.putln(our_data.size()); 
		
		TextIO.readFile("/Users/chapman/Documents/workspaceResearch/CleanFeedBackData/src/peerInteractions.fa2015.final.csv");
		while (!TextIO.eof())
			our_data.add(new FeedbackEntry(TextIO.getln(),awesomeList));
		double num_valid = 0.0;
		double count = 0.0;

		for (FeedbackEntry elem : our_data){
			++count;
//			TextIO.putln(elem);
			if (elem.valid()){
				++num_valid;
				clean_data.add(elem);
			}
		}
		System.out.printf("Percentage of entries valid: %f%%\nNumber of valid entries: %d\nNumber of entries: %d\n", 100*num_valid/count, (int) num_valid, (int) count);
		System.out.println("======================================================================================\n");
	}
}




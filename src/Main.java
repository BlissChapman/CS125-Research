import java.util.ArrayList;

/**
 * The runner class that 
 * @author CS125Research
 */
public class Main {

	public static void main(String[] args) {
		CleanFeedbackData.initialize();
		System.out.printf("Percentage of entries valid: %f%%\nNumber of valid entries: %d\nNumber of entries: %d\n", CleanFeedbackData.percentValid, CleanFeedbackData.numberOfValidEntries, CleanFeedbackData.numberOfEntries);
		System.out.println("======================================================================================\n");
		
		Lecture.initialize();
		
		//Print formatted information about each lecture:
		ArrayList<Lecture> lectures = Lecture.lectures;
		for(int j = 0; j < lectures.size(); j++) {
			System.out.println(lectures.get(j).toString());
			System.out.println("\tRating Distribution:");
			System.out.print("\t\t");
			
			int[] distribution = lectures.get(j).ratingDistribution();
			for(int i = 0; i < distribution.length; i++) {
				System.out.print((i + 1) + ": " + distribution[i] + ", ");
			}
			System.out.println();
		}
		
		System.out.printf("Average: %f, Standard Deviation: %f\n", Lecture.mean, Lecture.stdDev);
		System.out.println("Number of lectures: " + lectures.size());
		System.out.println("======================================================================================\n");
	}
}

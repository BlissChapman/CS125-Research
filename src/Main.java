import java.util.ArrayList;

/**
 * The runner class
 * @author CS125Research
 */
public class Main {

	public static boolean buildSucceeded = false;

	public static void main(String[] args) {
		PeerInteractionsData.initialize();
		System.out.printf("Percentage of entries valid: %f%%\nNumber of valid entries: %d\nNumber of entries: %d\n", PeerInteractionsData.percentValid, PeerInteractionsData.numberOfValidEntries, PeerInteractionsData.numberOfEntries);
		System.out.println("======================================================================================\n");

		LectureData.initialize();

		//Print each lecture object:
		ArrayList<Lecture> lectures = LectureData.lectures;
		for(int j = 0; j < lectures.size(); j++) {
			System.out.println(lectures.get(j));
		}

		//Print stats:
		System.out.printf("Average: %f, Standard Deviation: %f\n", LectureData.mean, LectureData.stdDev);
		System.out.println("Number of lectures: " + lectures.size());
		System.out.println("======================================================================================\n");
		System.out.println("BUILD FINISHED");
		buildSucceeded = true;
	}
}

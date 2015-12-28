import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class LectureInitializer {

	public static double mean, stdDev;
	
	public static ArrayList<Lecture> lectures;
	
	private static void addToLecture(Date d, FeedbackEntry entry){
		if(lectures.size()>0 && lectures.get(lectures.size()-1).getDate().equals(d)){
			lectures.get(lectures.size()-1).add(entry);
		} else {
			lectures.add(new Lecture(d, entry));
		}	
	}
	
	private static void initialize(){
		CleanFeedbackData.initialize();
		lectures = new ArrayList<>();
		int[] months = {9,  9,  9,  9,  9,   9, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 12, 12, 12, 12, 12};
		int[] days =   {18, 21, 23, 25, 28, 30, 2 ,  5,  7,  9, 12, 14, 16, 19, 21, 23, 26, 28, 30, 02, 04, 06, 9,  11, 13, 16, 18, 20, 30, 2,   4,  7,  9, 31};
		Date[] lectureDates = new Date[months.length];
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			for (int i = 0; i < months.length; ++i){
				String toParse = String.format("%d-%d-%02d 09:00:00", 2015, months[i], days[i]);
				lectureDates[i] = df.parse(toParse);
			}
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		int currTimeIndex = 0;
		double sum = 0;
		double sumSq = 0;
		int counter = 0;
				
		for(int i = 0; i < CleanFeedbackData.clean_data.size(); i++){
		
			while(CleanFeedbackData.clean_data.get(i).getDate().after(lectureDates[currTimeIndex + 1]))
				currTimeIndex++;
			
			addToLecture(lectureDates[currTimeIndex], CleanFeedbackData.clean_data.get(i));
			double temp = CleanFeedbackData.clean_data.get(i).getGrade();
			sum += temp;
			sumSq += temp*temp;
			++counter;
		}
		mean = sum/counter;
		stdDev = Math.sqrt(sumSq/counter - sum*sum/(counter*counter));
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Calling LectureInitializer.initialize()");
		initialize();
		
		for(int j = 0; j < lectures.size(); j++) {
			System.out.println(lectures.get(j).toString());
			System.out.println("\tRating Distribution:");
			System.out.print("\t\t");
			
			int[] distribution = lectures.get(j).ratingDistribution();
			//int[] values = lectures.get(j).getValues(); //Method name change
			for(int i = 0; i < distribution.length; i++) {
				System.out.print((i + 1) + ": " + distribution[i] + ", ");
			}
			System.out.println();
		}
		
		System.out.printf("Average: %f, Standard Deviation: %f\n", mean, stdDev);
		System.out.println("Number of lectures: " + lectures.size());
		System.out.println("======================================================================================\n");
	}
}

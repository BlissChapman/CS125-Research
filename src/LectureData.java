import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LectureData {
		///TODO - document these properties
		public static double mean, stdDev;
		public static ArrayList<Lecture> lectures;
		
		///TODO - document this method
		public static void initialize() {
			System.out.println("Calling LectureData.initialize()");
			
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
			} catch(Exception e){
				e.printStackTrace();
			}
			int currTimeIndex = 0;
			double sum = 0;
			double sumSq = 0;
			int counter = 0;
			
			ArrayList<PeerInteraction> cleanedData = PeerInteractionsData.cleanData;
			for(int i = 0; i < cleanedData.size(); i++){
			
				while(cleanedData.get(i).getDate().after(lectureDates[currTimeIndex + 1]))
					currTimeIndex++;
				
				addToLecture(lectureDates[currTimeIndex], cleanedData.get(i));
				double temp = cleanedData.get(i).getGrade();
				sum += temp;
				sumSq += temp*temp;
				++counter;
			}
			
			mean = sum/counter;
			stdDev = Math.sqrt(sumSq/counter - sum*sum/(counter*counter));
		}
		
		private static void addToLecture(Date d, PeerInteraction entry){
			if(lectures.size()>0 && lectures.get(lectures.size()-1).getDate().equals(d)){
				lectures.get(lectures.size()-1).add(entry);
			} else {
				lectures.add(new Lecture(d, entry));
			}	
		}
}

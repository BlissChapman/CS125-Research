import java.util.ArrayList;
import java.util.Date;

//A model for a lecture object with a unique id corresponding to the lecture number,
//all the associated feedback entries, the date, and the lecture topics.
public class Lecture {

	private static int AUTO_INCREMENT = 0;
	private int ID; //TODO: Discuss this name
	public ArrayList<FeedbackEntry> recordsByTime;
	private Date date;
	private String[] topics;// OR ArrayList<String>
	
	///CONSTRUCTORS
	//Constructor that is always called by other constructors
	public Lecture(Date d){
		ID = AUTO_INCREMENT++;
		recordsByTime = new ArrayList<>();
		date = d;
	}
	
	public Lecture(Date d, String[] topics){
		this(d);
		this.topics = topics;
	}
	
	public Lecture(Date d, FeedbackEntry entry){
		this(d);
		this.add(entry);
	}
	
	///GETTERS/SETTERS
	public Date getDate(){
		return date;
	}
	
	public void add(FeedbackEntry entry){
		recordsByTime.add(entry);
	}
	
	public int[] getValues() {
		int[] values = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		for (FeedbackEntry element : recordsByTime)
			values[element.getGrade() - 1]++;
		return values;
	}
	
	///ANALYSIS
	public double getAverage(){
		if (recordsByTime.isEmpty())
			return -1;
		double sum = 0;
		for (FeedbackEntry element : recordsByTime)
			sum += element.getGrade();
		return sum/recordsByTime.size();
	}
	
	public double getStdDev(){
		if (recordsByTime.isEmpty())
			return -1;
		double devSq = 0;
		double mean = getAverage();
		for (FeedbackEntry element : recordsByTime)
			devSq += Math.pow(element.getGrade()-mean,2);
		return Math.sqrt(devSq/recordsByTime.size());
	}
	
	@Override
	public String toString(){
		return String.format("ID: %d\n\tDate: ", ID) + date + String.format("\n\tNumber of Entries: %d\n\t"
				+ "Mean: %f\n\tStandard Deviation: %f", recordsByTime.size(), getAverage(), getStdDev());		
	}	
}

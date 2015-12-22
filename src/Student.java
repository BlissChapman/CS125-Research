import Java.util.ArrayList;

/**
 * Important: Student class. Stores information regarding a particular Student in class, 
 * including their lecture attendance and possibly grades. Each Student should probably
 * contain all the FeedbackEntries made by the student.
 *
 * @author CS125 Research
 * @todo Implement everything
 */
public class Student{
	private int ID;
	private boolean female; //One possibility for what we could store here
	private ArrayList<FeedbackEntry> feedbackGiven;
        private double weight;
        


    /**
     * Static method that uses a right-biased binary search of
     * existing Lectures to associate a FeedbackEntry with a Lecture. 
     * This method may be better in another class. Runs in logarithmic time
     * with respect to the number of Lectures.
     *
     * @param key The entry to be associated with a Lecture.
     *
     * @return The Lecture corresponding to an entry.
     */
    public static Lecture get(FeedbackEntry key){
        ArrayList<Lecture> lecs = LectureInitializer.lectures;
        int lo = 0;
        int hi = lecs.size()-1;
        Date search = key.getDate();
        while (hi != lo){
            int mid = (lo + hi + 1)/2;
            Date comp = lecs.get(mid).getDate();
            switch (search.compareTo(comp)){
            case -1:
                hi = mid-1; break;
            case 1:
                lo = mid; break;
            default:
                return lecs.get(mid);
            }
        }
        return lecs.get(mid);
    }

 	/**
 	 * Ctor. Needs work.
 	 */
	public Student(int code){
		ID = code;
	}

        /**
         * Computes and returns the weight of this
         * Student, used when calculating weighted
         * grade distributions for a Lecture. 
         * @todo Insert implementation.
         *
         * @return The weight given to this Student's feedback.
         */
        public double getWeight(){
            return 1;
        }

        /**
         * Add a FeedbackEntry to this Student's record thereof.
         * This method imposes a chronologial precondition.
         * @todo Implement.
         *
         * @param entry Feedback made by this Student
         */
        public void addEntry(FeedbackEntry entry){
            return;
        }

        /**
         * @return The ID code of this student.
         */
        public int getID(){
           return ID;
        }

        /**
         * Setter for ID (is this really useful?)
         */
        public void setID(int code){
           ID = code;
        }

        /**
         * @return Number of valid feedback data points given by this Student.
         * @todo Implement.
         */
        public int feedbackGiven(){
            return -1;
        }

        /**
         * @return The average rating given by this Student for all
         *         lectures so far in the semester.
         * @todo Implement if needed.
         */
        public double ratingMean(){
            return -1;
        }

        /**
         * @return The standard deviation of all ratings given by
         *         this Student throughout the semester
         * @todo Implement if needed.
         */
        public double ratingStdDev(){
            return -1;
        }

        
}

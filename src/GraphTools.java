import java.util.Date;

public class GraphTools<T>{
	
	private final Roster students;
	
	public GraphTools(Roster source){
		students = source;
	}
	
	public class ALL_ENTRIES_c implements Weighter<PeerInteraction>{
		public double weight(PeerInteraction any) {
			return 1;
		}
	}
	
	
	public class RATING_STD_DEV_c implements Weighter<PeerInteraction>{
		public double 
			minDev = 0, 
			maxDev = Double.POSITIVE_INFINITY;
		public boolean
			inRange = true;
		
		public double weight(PeerInteraction element){
			Student s = students.get(element);
			if (s == null)
				return 0;
			if (s.ratingStdDev() >= minDev && 
				s.ratingStdDev() <= maxDev && inRange)
				return 1;
			else if (s.ratingStdDev() <= minDev && 
					 s.ratingStdDev() >= maxDev && !inRange)
				return 1;
			else
				return 0;
		}
	};
		
	public class UNCOMMON_ENTRIES_c implements Weighter<PeerInteraction>{
		
		public double weight(PeerInteraction element){
			Student s = students.get(element);
			if (s == null)
				return 0;
			return s.feedbackWeight();
		}
	};
	
	public class RATING_MEAN_c implements Weighter<PeerInteraction>{
		
		public double
			minRating = 0,
			maxRating = Double.POSITIVE_INFINITY;
		public boolean
			inRange = true;
		
		public double weight(PeerInteraction element){
			Student s = students.get(element);
			if (s == null)
				return 0;
			if (s.ratingMean() >= minRating && 
				s.ratingMean() <= maxRating && inRange)
				return 1;
			else if (s.ratingMean() <= minRating &&
					 s.ratingMean() >= maxRating && !inRange)
				return 1;
			return 0;
		}
	};
	
	public class DATE_FILTER_c implements Weighter<PeerInteraction>{		
		public Date
			earliest = new Date(),
			latest = new Date();
		public boolean
			inRange = true;
		
		public double weight(PeerInteraction element){
			int comp1 = element.getDate().compareTo(earliest),
			    comp2 = element.getDate().compareTo(latest);
			if (comp1 == comp2 && comp1 != 0 && comp2 != 0)
				return 0;
			else if (comp1 > -1)
				return inRange ? 1 : 0;
			return 0;
		}
	};
	
	public final ALL_ENTRIES_c ALL_ENTRIES = new ALL_ENTRIES_c();
	public final UNCOMMON_ENTRIES_c UNCOMMON_ENTRIES = new UNCOMMON_ENTRIES_c();
	public final RATING_STD_DEV_c RATING_STD_DEV = new RATING_STD_DEV_c();
	public final RATING_MEAN_c RATING_MEAN = new RATING_MEAN_c();
	public final DATE_FILTER_c DATE_FILTER = new DATE_FILTER_c();
	
}
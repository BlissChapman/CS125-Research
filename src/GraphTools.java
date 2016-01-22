public class GraphTools<T>{
	
	Roster students;
	
	public GraphTools(Roster source){
		students = source;
	}
	
	public Weighter<PeerInteraction> ALL_ENTRIES 
	  = new Weighter<PeerInteraction>(){
		public double weight(PeerInteraction any) {
			return 1;
		}
	};
	
	public Weighter<PeerInteraction> RATING_STD_DEV 
	  = new Weighter<PeerInteraction>(){
		
		public double 
			minDev = 0, 
			maxDev = Double.POSITIVE_INFINITY;
		public boolean
			inRange = true;
		
		public double weight(PeerInteraction element){
			Student s = students.get(element);
			if (s == null)
				return 0;
			if (s.ratingStdDev() > minDev && 
				s.ratingStdDev() < maxDev)
				return inRange ? 1 : 0;
			return 0;
		}
	};
	
	public Weighter<PeerInteraction> UNCOMMON_ENTRIES
	  = new Weighter<PeerInteraction>(){
		
		public double weight(PeerInteraction element){
			Student s = students.get(element);
			if (s == null)
				return 0;
			return s.feedbackWeight();
		}
	};
	
	public Weighter<PeerInteraction> RATING_MEAN
	  = new Weighter<PeerInteraction>(){
		
		public double
			minRating = 0,
			maxRating = Double.POSITIVE_INFINITY;
		public boolean
			inRange = true;
		
		public double weight(PeerInteraction element){
			Student s = students.get(element);
			if (s == null)
				return 0;
			if (s.ratingMean() > minRating && 
				s.ratingMean() < maxRating)
				return inRange ? 1 : 0;
			return 0;
		}
	};
	
	public Weighter<PeerInteraction> DATE
	  = new Weighter<PeerInteraction>(){
		
		public Date
			earliest = new Date(),
			latest = new Date();
		public boolean
			inRange = true;
		
		public double weight(PeerInteraction element){
			if (element.getDate().greater(earliest) &&
				element.getDate().less(greatest))
				return inRange ? 1 : 0;
			return 0;
		}
	};

}

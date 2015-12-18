/*
public class FeedbackEntry {

	private boolean good=true;
	private int grading=-1;
	private int userID;
	private int partnerID;
	public FeedbackEntry(String swagString, NRList map)
	{
		
			System.out.println(swagString);
			String[] seperated= swagString.split(",");
			if(seperated.length != 6){
				good = false;
				return;
			}
			if(seperated[0].equals("")||seperated[1].equals(""))
			{
				good=false;
				
				return;
			}
			String tempUser=seperated[0].substring(1,seperated[0].length()-1);
			String tempPartner=seperated[1].substring(1,seperated[1].length()-1);
			
	}
	
	public int getUserID() {return userID;}
	public int getPartnerID() {return partnerID;}
	public boolean valid()  {return good;}
	public int getGrading() {return grading;}
	
	public String toString(){
		if (!good)
			return "INVALID ENTRY";
		return userID + "." + partnerID + " " + grading;
	}
			
}
*/
	import java.text.SimpleDateFormat;
/**
	 *  This class is a simple data structure that represents an entry from the CS125
	 *  lecture feedback app. It contains a pair of encoded NetIDs, a grade, two
	 *  feedback strings, and a date string.
	 *  @author CS125 Research
	 */
	import java.util.*;
	public class FeedbackEntry{
		
		private boolean good = false;
		private int grade = -1;
		private int personID = -1;
		private int partnerID = -1;
		private String strengths;
		private String weaknesses;
		private Date date;
		
		/**
		 * String constructor. Takes an unprocessed line from a CVS file and
		 * parses it as a valid FeedbackEntry using a passed-in NRList to
		 * verify NetIDs. The constructor exits early and marks the entry as
		 * bad if any NetIDs are missing or do not belong to any students in
		 * the NRList. [It also throws an exception if the data clearly
		 * cannot represent an entry made by a student. Useful for debugging.]
		 *
		 *  @param data  An unprocessed line to parse. Must be formatted in the
		 *               form "netid1", "netid2", "5", "Strengths", "Weaknesses", 
		 *               "Date";
		 *  @param map   A list of all netIDs of students in the class
		 *               and their corresponding codes.
		 */
		public FeedbackEntry(String data, NRList map){
			good = true;
			checkCorruptData(data);
			String[] separated = splitCommas(data);
			for (int i = 0; i < separated.length; ++i)
				separated[i] = process(separated[i]);
			personID = map.getSecret(separated[0]);
			partnerID = map.getSecret(separated[1]);
			grade = Integer.parseInt(separated[2]);
			if (personID == -1 || partnerID == -1 || grade > 10 || grade < 1 || 
			    personID == partnerID){
				good = false;
			}
			strengths = separated[3];
			weaknesses = separated[4];
			try{
				date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(separated[5]);
			}catch(Exception e){}
		}
		
		/**
		 *  Returns true if this entry is valid. This implies that both NetID codes
		 *  were entered and actually represent real students and that the grade was
		 *  between 1 and 10. We might also want to check that the dates are valid.
		 *
		 *  @return True for valid entries, false for invalid entries.
		 */
		public boolean valid()        {return good;}

		/**
		 *  @return How well the student rated this lecture, from 1 to 10.
		 */
		public int     getGrade()     {return grade;}

		/**
		 *  @return The int code representing the netID of the first person.
		 */
		public int     getPersonID()  {return personID;}

		/**
		 *  @return The int code representing the netID of the second person.
		 */
		public int     getPartnerID() {return partnerID;}

		/**
		 *  @return The student's report on what he/she understood from lecture.
		 */
		public String  getStrength()  {return strengths;}

		/**
		 *  @return The student's report on what he/she did not understand from lecture.
		 */
		public String  getWeakness()  {return weaknesses;}

		/**
		 *  @return The date of this entry.
		 */
		public Date  getDate()      {return date;}

		/**
		 *  Method that indicates whether this entry has any written
		 *  feedback information at all. Potentially useful later on?
		 *
		 *  @return True if either weaknesses or strengths is nonempty, false
		 *          otherwise.
		 */
		public boolean hasFeedback(){
			return (strengths.length() > 0 || weaknesses.length() > 0);
		}

		/**
		 *  Returns a simple but complete representation of this entry. Note
		 *  that invalid entries return "INVALID ENTRY". Not for file writing.
		 *
		 *  @return "INVALID_ENTRY" for invalid entries or a single-line representation
		 *          of all data members of this entry.
		 */
		public String toString(){
			String result = String.format("Student: %d Partner: %d Grade: %d Good: " + 
			                     "\"%s\" Bad: \"%s\" Date: %s", 
			                     personID, partnerID, grade,
			                     strengths, weaknesses, date);
			return (!good ? "INVALID ENTRY: ":"") + result;
		}
		
		/**
		 *  Checks to see that a line passed into the constructor could possibly
		 *  represent an entry (valid or otherwise) made by a student and stored
		 *  in a CVS file. The method throws an IllegalArgumentException if the
		 *  line is invalid, namely if it doesn't have the right number of elements
		 *  or its elements are not surrounded with quotation marks.
		 *
		 *  @param line The line to be validated. 
		 */
		private void checkCorruptData(String line){
			try{
				String[] separated = splitCommas(line);
				date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(separated[5]);
			}catch(Exception e){
				throw new IllegalArgumentException(line);
			}
		}

		/**
		 *  Possibly useful method that processes written feedback for better
		 *  representation. For example, should remove newlines and excessive
		 *  spaces.
		 *
		 *  @param An unprocessed string of written feedback.
		 *  @return A processed string of written feedback.
		 */
		private String process(String unprocessed){
			if (unprocessed.length() == 0)
				return unprocessed;
			unprocessed = unprocessed.replace("\n", " "); //Not sure if matters, but removes newlines
			return unprocessed.trim();      //Trims gets rid of trailing/leading whitespace
		}
	
	/*
	 * @todo Implement this to make sure to ignore commas in quotes.
	 */
		 /**
		  * Private helper method for constructor. Splits a string into a String[],
		  * splitting at (and getting rid of) commas ONLY IF those commas are not 
		  * enclosed by quotes. Note that CSV files represent user-input quotation
		  * marks as double-double quotes (""). So {"We got into ""aggressive 
		  * negotations""", "What's that?", "Negotiations with a lightsaber."} 
		  * should be parsed as {We got into "aggressive negotations"} {What's 
		  * that?} {Negotations with a lightsaber.}. Also converts double-double 
		  * quotes into ordinary double quotes and implicitly throws an exception 
		  * if the number of partitions is not exactly 6.
		  *
		  * @params input The string to be parsed from a CVS file.
		  *
		  * @return A String array containing the formatted contents of input, 
		  *         split at commas only if they are enclosed by quotation marks.
		  */
		private static String[] splitCommas(String input)
		{
		    String[] partitions = new String[6];
		    int partition = 0;
		    StringBuilder build = new StringBuilder();
		    boolean evenQuotes = true;
		    boolean lastQuote = false;
		    int i = 0;
		    char current = '\0';
		    while (i < input.length()){
		        while(i < input.length() && (current = input.charAt(i++)) != ',' || !evenQuotes){
		            if (current == '\"'){
		                evenQuotes = !evenQuotes;
		                if (lastQuote && !evenQuotes){
		                    //System.out.print(current);
		                    build.append(current);
		                    lastQuote = false;
		                    continue;
		                }else
		                    lastQuote = true;
		            }else if (!evenQuotes){
		                build.append(current);
		                //System.out.print(current);
		                lastQuote = false;
		            }
		        }
		        if (i == input.length() && input.charAt(i-1) != ',')
		        	break;
		        partitions[partition++] = build.toString();
		        build.setLength(0);
		        lastQuote = false;
		    }
		    partitions[partition++] = build.toString();
		    if(partition != 6)
		    	throw new IllegalArgumentException();
		    return partitions;
		}
}
		

		



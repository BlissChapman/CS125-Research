import java.util.Arrays;
import java.util.HashMap;

/**
 * A simple pair POD that couples an integer ID code with 
 * a NetID and stores the two for use in encrypting data.
 * @author CS125Research
 */
public class NetIDPair {
	private String netID;
	private int code;

	//HashMap to hold qualitative data on student
	private HashMap <String, String> qualitativeInfo;

	//HashMap to hold quantitative data on student
	private HashMap <String, Double> quantitativeInfo;


	/**
	 * Struct constructor.
	 * 
	 * @param newNetid  NetID of student.
	 * @param newCode   Code associated with this NetID.
	 */
	public NetIDPair(String newNetID, int newCode){
		netID = newNetID;
		code = newCode;	
	}

	/**
	 * @return The code associated with this netID.
	 */	
	public int getCode() { return code; }

	/**
	 * @return The string representation of this pair. Just a comma
	 *         separated juxtaposition of netID and code.
	 */
	public String toString(){
		char[] build = new char[netID.length()];
		Arrays.fill(build, '*');
		build[0] = netID.charAt(0);
		int last = netID.length()-1;
		build[last] = netID.charAt(last);
		return (new String(build)) + ": " + code;
	}

	/**
	 * @return True iff searchID matches this NetIDPair's netID.
	 */
	public boolean equals(String searchID){
		return this.netID.equals(searchID);
	}

	/**
	 * @return True iff other.netID matches this NetIDPair's netID.
	 */
	public boolean equals(NetIDPair other){
		return this.netID.equals(other.netID);
	}

	/**Add method for qualitative information on students.
	 * 
	 * 
	 * @param characteristic	key to add
	 * @param information		value tied to 'characteristic'
	 */

	public void addQualCharacteristic(String characteristic, String information){
		qualitativeInfo.put(characteristic, information);
	}

	/** Remove method for qualitative information on students.
	 * Removes the key entered and value tied to the key, returns value 
	 * 
	 * @param characteristic	key to remove
	 * @return					value tied to 'characteristic'
	 */

	public String removeQualCharacteristic(String characteristic){
		return qualitativeInfo.remove(characteristic);
	}

	/** Mutator method for qualitative information on students.
	 *  Changes data tied to key 'characteristic' to 'value' and
	 *  returns value of data overwritten
	 * 
	 * @param characteristic	key to access	
	 * @param value				new desired value tied to 'characteristic'
	 * @return					value previously tied to 'characteristic'
	 */
	public String setQualCharacteristic(String characteristic, String info){
		return qualitativeInfo.replace(characteristic, info);

	}

	/**Add method for quantitative information on students.
	 * 
	 * 
	 * @param characteristic	key to add
	 * @param information		value tied to 'characteristic'
	 */

	public void addQuantCharacteristic(String characteristic, Double information){
		quantitativeInfo.put(characteristic, information);
	}

	/** Remove method for quantitative information on students.
	 * Removes the key entered and value tied to the key, returns value 
	 * 
	 * @param characteristic	key to remove
	 * @return					value tied to 'characteristic'
	 */

	public Double removeQuantCharacteristic(String characteristic){
		return quantitativeInfo.remove(characteristic);
	}

	/** Mutator method for quantitative information on students.
	 *  Changes data tied to key 'characteristic' to 'value' and
	 *  returns value of data overwritten
	 * 
	 * @param characteristic	key to access	
	 * @param value				new desired value tied to 'characteristic'
	 * @return					value previously tied to 'characteristic'
	 */
	public Double setQuantCharacteristic(String characteristic, Double info){
		return quantitativeInfo.replace(characteristic, info);
	}

	/** Accessor method to obtain map of qualitative info. Necessary for mapping
	 * a Student Object's 'code' to certain characteristics because NetIDPair
	 * class is last point at which student's netid is visible and all traces of
	 * student identifiers are hidden afterwards
	 * 
	 * @return map of qualitative info on student
	 */
	public HashMap<String,String> getQualMap(){
		return qualitativeInfo;
	}

	/** Accessor method to obtain map of quantitative info. Necessary for mapping
	 * a Student Object's 'code' to certain characteristics because NetIDPair
	 * class is last point at which student's netid is visible and all traces of
	 * student identifiers are hidden afterwards
	 * 
	 * @return map of quantitative info on student
	 */

	public HashMap<String,Double> getQuantMap(){
		return quantitativeInfo;
	}


	/** Fills map 'qualitativeInfo' with desired characteristics. 
	 * Pulls data from .csv file using TextIO interface. With proper
	 * .csv formatting method should find columns relating to desired
	 * characteristics without hardcoding in actual column indices
	 */

	/*
	public void fillQualCharacteristics(String dataFilePath){

		TextIO.readFile(dataFilePath);

		//code to automate finding of characteristics columns
		int netidIndex, genderIndex, majorIndex, collegeIndex, etc. 

		String [] col = TextIO.getln().split(",");
		for(int i = 0; i < col.length; i++){ // need to find actual col.length value
			if(col[i].equals("netid"))
				netidIndex = i;
			else if(col[i].equals("Gender"))
				genderIndex = i;
			else if(col[i].equals("Major"))
				majorIndex = i;
			else if(col[i].equals("College"))
				collegeIndex = i;
			//else if etc.
		}



		while(!TextIO.eof())
		{
			String [] line = TextIO.getln().split(",");
			if(line[indexOfNetID].equals(this.netID){
				addQualCharacteristic("Gender", line[indexOfGender]);
				addQualCharacteristic("Major", line[indexOfMajor]);
				// rinse and repeat for all desired characteristics
				return;
			}
		}
	}
	 */

	/** Fills map 'quantitativeInfo' with desired characteristics. 
	 * Pulls data from .csv file using TextIO interface. With proper
	 * .csv formatting method should find columns relating to desired
	 * characteristics without hardcoding in actual column indices
	 */

	/*

	public void fillQuantCharacteristics(String dataFilePath){

		TextIO.readFile(dataFilePath);	

		//code to automate finding of characteristics columns
		int netidIndex, gpaIndex, ageIndex, quizAvgIndex, etc. 

		String [] col = TextIO.getln().split(",");
		for(int i = 0; i < col.length; i++){ //what is column length actually
			if(col[i].equals("netid"))
				netidIndex = i;
			else if(col[i].equals("GPA"))
				gpaIndex = i;
			else if(col[i].equals("Age"))
				ageIndex = i;
			else if(col[i].equals("Quiz Average"))
				quizAvgIndex = i;
			//else if etc.
		}

		while(!TextIO.eof())
		{
			String [] line = TextIO.getln().split(",");
			if(line[indexOfNetID].equals(this.netID){
				addQuantCharacteristic("GPA", new Double(line[gpaIndex]));
				addQuantCharacteristic("Quiz Average", line[quizAvgIndex]);
				// rinse and repeat for all desired characteristics
				return;
			}
		}


	}
	 */

}

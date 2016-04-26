public class StudentTest{

	public static void main(String[] args){
		//need actual .csv or .txt with same encoded IDs
		//that are found in EncodedRoster.txt

		Student [] studentList = new Student [642];
		int [] codeList = new int[642];
		int cnt = 0;
		TextIO.readFile("./src/EncodedRoster.txt");


		//fill array codeList with encodedIDs of students
		//necessary because of weird edge-case behavior within TextIO.eof() where
		//was reading .eof() == true after Student created so requires
		//Student object creation after all codes have been read and array filled
		while(!TextIO.eof()){

			String [] line = TextIO.getln().split(",");
			codeList[cnt] = new Integer(line[0]);
			cnt++;

		}
		
		for(int i = 0; i < codeList.length; i++){
			studentList[i] = new Student(codeList[i]);
		}

	}
}
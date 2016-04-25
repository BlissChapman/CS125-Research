import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * A runner class that analyzes the input data (csv) using our Java infrastructure
 * and outputs the results to JSON files. 
 * @author CS125Research
 */
public class Export {

	public static void main(String[] args){

		exportAnalysisToFile("src/results.json");
	}
	
	
	/**
	 * .....DOCUMENTATION NEEDED.....
	 * @param filename
	 */
	public static void exportAnalysisToFile(String filename){
		try {
			PrintWriter writer;
			writer = new PrintWriter(filename, "UTF-8");
			
			PeerInteractionsData.initialize();
			LectureData.initialize();
			
			//Print each lecture object:
			ArrayList<Lecture> lectures = LectureData.lectures;
			for(int j = 0; j < lectures.size(); j++) {
				writer.println(lectures.get(j).toJSONString());
			}
			writer.close();
			
			System.out.println("Exported results of analysis to " + filename);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

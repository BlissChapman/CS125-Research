
public class CleanFeedbackTesting {

	public static void main(String[] args) {
//		NRList instanceCleanFeedback= new NRList("/Users/chapman/Documents/workspaceResearch/EncodeNetID/src/roster.txt","/Users/chapman/Documents/workspaceResearch/secretText.txt",9000);
			TextIO.readFile("/Users/chapman/Documents/workspaceResearch/CleanFeedBackData/src/lecture.feedback.11.13.2015.csv");
			while(!TextIO.eof())
			{
				String line = TextIO.getln();
				String[] info = line.split(",");
				//if(info[0].equals("")|| info[1].equals(""))
				//TextIO.putln(info[0]+","+info[1]);
				if(info[2].equals("\"0\""))
				TextIO.putln(info[0]+","+info[1]+","+info[2]);	
			}
			

	}
}

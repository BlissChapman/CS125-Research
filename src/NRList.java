import java.util.*;
public class NRList {
	ArrayList <NetIDPair> pairList = new ArrayList<NetIDPair>();
	/**
	 * 
	 * @param fileName
	 */
	public NRList(String readFilePath, String writeFilePath, int randomness){
		
	
		fillList(readFilePath, writeFilePath, randomness);
		//printList();
		
	}
	
	

	
	public void fillList(String readFilePath, String writeFilePath, int randomness)
	{
		TextIO.readFile(readFilePath);
		if (writeFilePath != null)
			TextIO.writeFile(writeFilePath);
		
		
		while(!TextIO.eof())
		{
			
			String line = TextIO.getln();
			String netID = line.split(",")[1];
			NetIDPair potentialPair;
			
			do{
				int randomID = (int)(Math.random()*randomness);
				potentialPair = new NetIDPair(netID,randomID);
			} while(check(potentialPair));
	
			pairList.add(potentialPair);
		}
	}
	
	public boolean check(NetIDPair potentialPair)
	{
		boolean duplicate = false;
		for(int x = 0; x<pairList.size();x++)
		{
			duplicate = pairList.get(x).getRandom()==potentialPair.getRandom();	
			if(duplicate) break;
		}

		return duplicate;
	}
	//may need to change to for loop
	public int getSecret(String netID)
	{
		//System.out.println("Runs getSecret");
//		for(NetIDPair elem : pairList){
//			if (elem.equals(netID))
//				return elem.getRandom();
//		}
//		System.out.println(netID);
		if(netID.equals("null"))
			return -1;
		return Integer.parseInt(netID);
	}
	

	public void printList() {
		for (int i = 0; i < pairList.size(); i++) {
			System.out.println(pairList.get(i));
			TextIO.putln(pairList.get(i));
		}
	
	}
}
package wolf.games.mobile.tools;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class SplitableString {
	
	private String inputString;
	private int numStrings = 1;

	private Map <Integer, String> splitStrings;
	
	public SplitableString(String inputString){
		this.inputString = inputString;
		splitStrings = new HashMap<Integer, String>(30);
	}
	
	public void findTabStringDenotation(){
		//stub. this should find the markings that denote each string, then remove them from each tab portion. ex:
		//E
		//B
		//G
		//D
		//A
		//E
	}
	
	public void SplitByChar(char splitChar){
		String part = "";
		for (int i = 0; i<=inputString.length(); i++){
			if (i == inputString.length()){
				if (!part.equals("")){
					splitStrings.put(numStrings, part);
					part = "";
					numStrings++;
				}
			}
			else if (inputString.charAt(i) == splitChar){
				splitStrings.put(numStrings, part);
				part = "";
				numStrings++;
				//Log.e("stuff", "string" + i);
				continue;
			}
			else{
				part = part + inputString.charAt(i);
			}
		}
		
		for (int x = 1; x<=splitStrings.size(); x++){
			//Log.i("stuff",splitStrings.get(x) + "/n");
		}
	}
	
	public int getNumSplits(){
		return this.numStrings;
	}
	
	/**
	 * This starts counting at 1!!
	 * @param position the split number to get. starts counting at 1.
	 * @return
	 */
	public String getStringAt(int position)
	{
		if (splitStrings.containsKey(position)){
			return splitStrings.get(position);
		}
		//it does not exist, so retun "empty"
		return "empty";
	}
	@Override
	public String toString(){
		return inputString;
	}

	public void replaceString(int i, String string) {
		splitStrings.remove(i);
		splitStrings.put(i, string);
	}

}

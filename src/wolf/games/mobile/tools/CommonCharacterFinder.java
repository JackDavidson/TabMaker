package wolf.games.mobile.tools;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import wolf.games.mobile.shared.TabPortion;

public class CommonCharacterFinder {
	String mString;
	private Map <Integer, Character> allCharacters;
	private char mostCommonChar;
	
	public CommonCharacterFinder(String mString){
		allCharacters = new HashMap<Integer, Character>(10);
		this.mString = mString;
		for (int i = 0; i<mString.length(); i++){
			Character mChar = mString.charAt(i);
			boolean characterAllreadyAdded = false;
			for(int x = 0; x<allCharacters.size(); x++){
				if (allCharacters.get(x).equals(mChar)){
					characterAllreadyAdded = true;
				}
			}
			if (!characterAllreadyAdded){
				allCharacters.put(allCharacters.size(), mChar);
			}
		}
		
		int mostRepetitionsSoFar = 0;
		for(int x = 0; x<allCharacters.size(); x++){
			char currentChar = allCharacters.get(x);
			int numRepetitionsCurrentChar = 0;
			for( int i=0; i<mString.length(); i++ ) {
				if(mString.charAt(i) == currentChar) {
					numRepetitionsCurrentChar++;
				}
			}
			if (numRepetitionsCurrentChar > mostRepetitionsSoFar){
				mostRepetitionsSoFar = numRepetitionsCurrentChar;
				mostCommonChar = currentChar;
				Log.e("stuff", "the most common char so far is: " + currentChar);
			}
		}
	}
	
	public char mostCommonCharacter(){
		//stub
		return mostCommonChar;
	}

}

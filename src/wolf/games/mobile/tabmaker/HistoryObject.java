package wolf.games.mobile.tabmaker;

public class HistoryObject {
	
	HistoryTypes historyType;
	public int stringNum;
	public int charPosition;
	public char oldChar;
	public char newChar;
	
	public int changeLocation;
	public String stringModification;
	
	public int endStringNum;
	public int endCharPosition;
	
	public HistoryObject(HistoryTypes mHistoryType, int StringNum, int charPosition){
		int pasteLocation;
		int pasteLength;
	}
	public HistoryObject(HistoryTypes mHistoryType, int changeLocation, String stringModification){
		this.historyType = mHistoryType;
		this.changeLocation = changeLocation;
		this.stringModification = stringModification;
	}
	public HistoryObject(HistoryTypes mHistoryType, int stringNum, int charPosition, char oldChar, char newChar){
		this.historyType = mHistoryType;
		this.stringNum = stringNum;
		this.charPosition = charPosition;
		this.oldChar = oldChar;
		this.newChar = newChar;
		//this is for making a single replace history object.
	}
	public HistoryObject(HistoryTypes mHistoryType, int stringNum, int charPosition, int endStringNum, int endCharPosition, char oldChar, char newChar) {
		this.historyType = mHistoryType;
		this.stringNum = stringNum;
		this.charPosition = charPosition;
		this.endStringNum = endStringNum;
		this.endCharPosition = endCharPosition;
		this.oldChar = oldChar;
		this.newChar = newChar;
	}
	public HistoryTypes getHistoryType(){
		return this.historyType;
	}
}

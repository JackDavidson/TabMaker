package wolf.games.mobile.tabmaker;

import java.util.HashMap;
import java.util.Map;

public class HistoryManager {
	
	private Map <Integer, HistoryObject> historyMap;
	private int currentHistoryPoint = 0;
	private int historyEnd = 0;
	
	public HistoryManager(){
		historyMap = new HashMap<Integer, HistoryObject>(30);
		currentHistoryPoint = 0;
	}
	
	public void addSingleReplaceHistory(int stringNum, int charPosition, char oldChar, char newChar){
		historyMap.put(currentHistoryPoint, new HistoryObject(HistoryTypes.SINGLE_REPLACE, stringNum, charPosition, oldChar, newChar));
		currentHistoryPoint++;
		historyEnd = currentHistoryPoint;
	}
	public void addInsertionHistory(int charPosition, String insertion){
		historyMap.put(currentHistoryPoint, new HistoryObject(HistoryTypes.INSERTION, charPosition, insertion));
		currentHistoryPoint++;
		historyEnd = currentHistoryPoint;
	}
	public void addDelitionHistory(int charPosition, String deletion){
		historyMap.put(currentHistoryPoint, new HistoryObject(HistoryTypes.DELETION, charPosition, deletion));
		currentHistoryPoint++;
		historyEnd = currentHistoryPoint;
	}
	public void addMoveHistory( int stringNum, int charPosition, int endStringNum, int endCharPosition, char overWrittenChar, char movedChar){
		historyMap.put(currentHistoryPoint, new HistoryObject(HistoryTypes.MOVEMENT, stringNum, charPosition, endStringNum, endCharPosition, overWrittenChar, movedChar));
		currentHistoryPoint++;
		historyEnd = currentHistoryPoint;
	}
	public HistoryObject undo(){
		if (historyMap.containsKey(currentHistoryPoint - 1)){
			currentHistoryPoint--;
			return historyMap.get(currentHistoryPoint);
		}
		return null;
	}
	public HistoryObject redo(){
		if (historyEnd > currentHistoryPoint){
			if (historyMap.containsKey(currentHistoryPoint)){
				currentHistoryPoint++;
				return historyMap.get(currentHistoryPoint - 1);
			}
		}
		return null;
	}
	public HistoryObject getLastHistory(){
		if (historyMap.containsKey(currentHistoryPoint - 1)){
			return historyMap.get(currentHistoryPoint -1);
		}
		return null;
	}

	public boolean canUndo() {
		if (currentHistoryPoint > 0)
			return true;
		return false;
	}

	public boolean canRedo() {
		if (currentHistoryPoint < historyEnd)
			return true;
		return false;
	}
}

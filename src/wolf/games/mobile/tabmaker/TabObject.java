package wolf.games.mobile.tabmaker;

import java.util.HashMap;
import java.util.Map;

import org.andengine.input.touch.TouchEvent;

import android.content.ClipData;
import android.text.ClipboardManager;
import android.util.Log;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;

import wolf.games.mobile.shared.SharedData;
import wolf.games.mobile.tabmaker.menu.MenuManager;
import wolf.games.mobile.tools.SDCardWriter;
import wolf.games.mobile.tools.SplitableString;
import wolf.games.mobile.tools.SpriteManager;

public class TabObject {

	// map to hold strings that represent each guitar string

	// int representing total number of comments
	// map to hold comments.
	// int map to hold location of the start of each comment. (represents the
	// number of spaces before each comment starts)
	// this should hold some history!!!!!!!!!!
	public int numStrings = 0;
	int width = 0;
	private Map<Integer, String> denotationMap;
	private Map<Integer, String> stringsMap;
	private HistoryManager mHistoryManager;

	public TabObject(int numStrings, int numTextLines, int width) {
		mHistoryManager = new HistoryManager();

		this.numStrings = numStrings;
		this.width = width;

		stringsMap = new HashMap<Integer, String>(30);
		denotationMap = new HashMap<Integer, String>(30);
		String defaultString = "";
		for (int i = 0; i < width; i++) {
			defaultString += "-";
		}
		for (int i = 0; i < numStrings; i++) {
			/* fill the denotationMap with the defaults, and the stringsMap with dashes */
			denotationMap.put(i, "");
			stringsMap.put(i, defaultString);
		}
	}

	public void modifyString(int stringNum, final String newString) {
		stringsMap.put(stringNum, newString);
	}

	public String getString(int stringNum) {
		// this counts from 0
		return stringsMap.get(stringNum);
	}

	public String getDenotation(int stringNum) {
		// this counts from 0
		return denotationMap.get(stringNum);
	}

	public void modifyDenotation(int stringNum, final String newDenotation) {
		denotationMap.put(stringNum, newDenotation);
	}

	private void pasteStringNoHistory(int position, String pasteText) {
		SplitableString mSplitText = new SplitableString(pasteText);
		mSplitText.SplitByChar('\n');
		for (int i = 1; i <= this.getNumStrings(); i++) {
			StringBuilder tmpStringBuilder = new StringBuilder(stringsMap.get(i - 1));
			tmpStringBuilder.insert(position, mSplitText.getStringAt(i));
			stringsMap.put(i - 1, tmpStringBuilder.toString());
		}
	}

	public void modifyChar(int stringNum, int position, char newChar) {
		mHistoryManager.addSingleReplaceHistory(stringNum, position, stringsMap.get(stringNum).charAt(position),
				newChar);
		modifyCharNoHistory(stringNum, position, newChar);
		SharedData.canWriteRecoveryFile = true;
	}

	private void modifyCharNoHistory(int stringNum, int position, char newChar) {
		StringBuilder tmpStringBuilder = new StringBuilder(stringsMap.get(stringNum));
		tmpStringBuilder.deleteCharAt(position);
		tmpStringBuilder.insert(position, newChar);
		stringsMap.put(stringNum, tmpStringBuilder.toString());
	}

	public void addChar(int stringNum, int position, char newChar) {
		StringBuilder tmpStringBuilder = new StringBuilder(stringsMap.get(stringNum));
		tmpStringBuilder.insert(position, newChar);
		stringsMap.put(stringNum, tmpStringBuilder.toString());
	}

	public void insertBrake(int position) {
		for (int i = 0; i < numStrings; i++) {
			addChar(i, position, '|');
		}
		String insertion = "";
		for (int x = 0; x < numStrings; x++) {
			insertion += "|";
			insertion += '\n';
		}
		mHistoryManager.addInsertionHistory(position, insertion);
	}

	public void insertNewLine(int position) {
		for (int i = 0; i < numStrings; i++) {
			addChar(i, position, 'N');
		}
		String insertion = "";
		for (int x = 0; x < numStrings; x++) {
			insertion += "N";
			insertion += '\n';
		}
		mHistoryManager.addInsertionHistory(position, insertion);
	}

	public void deleteChar(int position) {
		// stub
	}

	public void deleteSection(int start, int end) {
		if (end < start) {
			int newEnd = start;
			start = end;
			end = newEnd;
		}

		String deletedString = "";
		for (int x = 0; x < numStrings; x++) {
			StringBuilder tmpString = new StringBuilder(stringsMap.get(x));
			deletedString += tmpString.substring(start, end) + '\n';
		}
		mHistoryManager.addDelitionHistory(start, deletedString);
		deleteSelectionNoHistory(start, end);
	}

	private void deleteSelectionNoHistory(int start, int end) {
		if (end < start) {
			int newEnd = start;
			start = end;
			end = newEnd;
		}
		for (int x = 0; x < numStrings; x++) {
			StringBuilder tmpString = new StringBuilder(stringsMap.get(x));
			tmpString.delete(start, end);
			stringsMap.put(x, tmpString.toString());
		}
	}

	public void addCharSpace(int position) {
		// stub
	}

	public void addComment(int position, int lineNum, String comment) {
		// stub
	}

	public int getNumStrings() {
		return numStrings;
	}

	public void saveFile(MenuManager mMenuManager) {
		String fileContents = "";
		fileContents += "<xml>";
		fileContents += "<tab>";
		for (int i = 0; i < numStrings; i++) {
			if (i < numStrings - 1) {
				fileContents += stringsMap.get(i) + '\n';
			} else {
				fileContents += stringsMap.get(i);
			}
		}
		fileContents += "</tab>";
		fileContents += "<denotation>";
		for (int i = 0; i < numStrings; i++) {
			if (i < numStrings - 1) {
				fileContents += denotationMap.get(i) + '\n';
			} else {
				fileContents += denotationMap.get(i);
			}
		}
		fileContents += "</denotation>";
		fileContents += "\n</xml>";
		mMenuManager.onSaveScene(fileContents);
	}

	public void saveFileDontAsk(SpriteManager mSpriteManager) {
		String fileContents = "";
		fileContents += "<tab>";
		for (int i = 0; i < numStrings; i++) {
			if (i < numStrings - 1) {
				fileContents += stringsMap.get(i) + '\n';
			} else {
				fileContents += stringsMap.get(i);
			}
		}
		fileContents += "</tab>";
		if (!SharedData.activeFile.endsWith(".xml") && !SharedData.activeFile.endsWith(".XML")) {
			SharedData.activeFile += ".xml";
		}
		SDCardWriter.writeFile(Environment.getExternalStorageDirectory() + "/TabMaker/MyTabs/", SharedData.activeFile,
				fileContents);

		mSpriteManager.getContext().finish();
	}

	public String exportToString(boolean isRecoveryFile) {
		String fileContents = "";
		fileContents += "<xml>";
		if (isRecoveryFile) {
			if (!SharedData.exitingCorrectly) {
				fileContents += "<ExitFailedFlag/>";
			}
			fileContents += "<name>";
			fileContents += SharedData.activeFile;
			fileContents += "</name>";

		}
		fileContents += "<tab>";
		for (int i = 0; i < numStrings; i++) {
			if (i < numStrings - 1) {
				fileContents += stringsMap.get(i) + '\n';
			} else {
				fileContents += stringsMap.get(i);
			}
		}
		fileContents += "</tab>";
		fileContents += "</xml>";

		return fileContents;
	}

	public int getStringLength() {
		return stringsMap.get(1).length();
	}

	public void loadFromFile() {
		// stub
	}

	public void expandStrings(int length) {
		// stub
	}

	public void insertSpace(int position, int length) {
		for (int x = 0; x < numStrings; x++) {
			StringBuilder tmpString = new StringBuilder(stringsMap.get(x));
			for (int i = 0; i < length; i++) {
				tmpString.insert(position, "-");
			}
			stringsMap.put(x, tmpString.toString());
		}

		String insertion = "";
		for (int x = 0; x < numStrings; x++) {
			for (int i = 0; i < length; i++) {
				insertion += "-";
			}
			insertion += '\n';
		}
		mHistoryManager.addInsertionHistory(position, insertion);
	}

	public void loadFromParser(TabParser mTabParser) {
		// this is still a stub.
		this.numStrings = mTabParser.getNumStrings();
		for (int i = 1; i <= numStrings; i++) {
			stringsMap.put(i - 1, mTabParser.getTabString(i));
		}

		for (int i = 1; i <= numStrings; i++) {
			denotationMap.put(i - 1, mTabParser.getStringDenotation(i));
			Log.v("Tab Object, loading", "this strings denotation is: " + mTabParser.getStringDenotation(i));
		}

	}

	public void moveLastItem(final float xMovement, final float yMovement) {
		HistoryObject mLastHistory = mHistoryManager.getLastHistory();
		if (mLastHistory != null) {
			if (mLastHistory.getHistoryType() == HistoryTypes.SINGLE_REPLACE) {
				if (Math.abs(xMovement) >= Math.abs(yMovement)) {
					// it must be either left or right.
					if (xMovement >= 0) {
						// Log.i("stuff", "to right");
						if (mLastHistory.charPosition + 1 >= this.width) {
							return;
						}
						mHistoryManager.addMoveHistory(mLastHistory.stringNum, mLastHistory.charPosition,
								mLastHistory.stringNum, mLastHistory.charPosition + 1,
								this.getString(mLastHistory.stringNum).charAt(mLastHistory.charPosition + 1),
								mLastHistory.newChar);

						this.modifyCharNoHistory(mLastHistory.stringNum, mLastHistory.charPosition,
								mLastHistory.oldChar);
						this.modifyCharNoHistory(mLastHistory.stringNum, mLastHistory.charPosition + 1,
								mLastHistory.newChar);
					} else {
						if (mLastHistory.charPosition - 1 < 0) {
							return;
						}
						// Log.i("stuff", "to left");
						mHistoryManager.addMoveHistory(mLastHistory.stringNum, mLastHistory.charPosition,
								mLastHistory.stringNum, mLastHistory.charPosition - 1,
								this.getString(mLastHistory.stringNum).charAt(mLastHistory.charPosition - 1),
								mLastHistory.newChar);

						this.modifyCharNoHistory(mLastHistory.stringNum, mLastHistory.charPosition,
								mLastHistory.oldChar);
						this.modifyCharNoHistory(mLastHistory.stringNum, mLastHistory.charPosition - 1,
								mLastHistory.newChar);
					}
				} else {
					// it must be either up or down
					if (yMovement >= 0) {
						if (mLastHistory.stringNum - 1 < 0) {
							return;
						}
						// Log.i("stuff", "to Up");
						mHistoryManager.addMoveHistory(mLastHistory.stringNum, mLastHistory.charPosition,
								mLastHistory.stringNum - 1, mLastHistory.charPosition,
								this.getString(mLastHistory.stringNum - 1).charAt(mLastHistory.charPosition),
								mLastHistory.newChar);

						this.modifyCharNoHistory(mLastHistory.stringNum, mLastHistory.charPosition,
								mLastHistory.oldChar);
						this.modifyCharNoHistory(mLastHistory.stringNum - 1, mLastHistory.charPosition,
								mLastHistory.newChar);
					} else {
						if (mLastHistory.stringNum + 1 >= this.numStrings) {
							return;
						}
						// Log.i("stuff", "to Down");
						mHistoryManager.addMoveHistory(mLastHistory.stringNum, mLastHistory.charPosition,
								mLastHistory.stringNum + 1, mLastHistory.charPosition,
								this.getString(mLastHistory.stringNum + 1).charAt(mLastHistory.charPosition),
								mLastHistory.newChar);

						this.modifyCharNoHistory(mLastHistory.stringNum, mLastHistory.charPosition,
								mLastHistory.oldChar);
						this.modifyCharNoHistory(mLastHistory.stringNum + 1, mLastHistory.charPosition,
								mLastHistory.newChar);
					}
				}
			} else if (mLastHistory.getHistoryType() == HistoryTypes.MOVEMENT) {
				if (Math.abs(xMovement) >= Math.abs(yMovement)) {
					// it must be either left or right.
					if (xMovement >= 0) {
						if (mLastHistory.endCharPosition + 1 >= this.width) {
							return;
						}
						// Log.i("stuff", "to right");
						mHistoryManager.addMoveHistory(mLastHistory.endStringNum, mLastHistory.endCharPosition,
								mLastHistory.endStringNum, mLastHistory.endCharPosition + 1,
								this.getString(mLastHistory.endStringNum).charAt(mLastHistory.endCharPosition + 1),
								mLastHistory.newChar);

						this.modifyCharNoHistory(mLastHistory.endStringNum, mLastHistory.endCharPosition,
								mLastHistory.oldChar);
						this.modifyCharNoHistory(mLastHistory.endStringNum, mLastHistory.endCharPosition + 1,
								mLastHistory.newChar);
					} else {
						if (mLastHistory.endCharPosition - 1 < 0) {
							return;
						}
						// Log.i("stuff", "to left");
						mHistoryManager.addMoveHistory(mLastHistory.endStringNum, mLastHistory.endCharPosition,
								mLastHistory.endStringNum, mLastHistory.endCharPosition - 1,
								this.getString(mLastHistory.endStringNum).charAt(mLastHistory.endCharPosition - 1),
								mLastHistory.newChar);

						this.modifyCharNoHistory(mLastHistory.endStringNum, mLastHistory.endCharPosition,
								mLastHistory.oldChar);
						this.modifyCharNoHistory(mLastHistory.endStringNum, mLastHistory.endCharPosition - 1,
								mLastHistory.newChar);
					}
				} else {
					// it must be either up or down
					if (yMovement >= 0) {
						// Log.i("stuff", "to Up");
						if (mLastHistory.endStringNum - 1 < 0) {
							return;
						}
						mHistoryManager.addMoveHistory(mLastHistory.endStringNum, mLastHistory.endCharPosition,
								mLastHistory.endStringNum - 1, mLastHistory.endCharPosition,
								this.getString(mLastHistory.endStringNum - 1).charAt(mLastHistory.endCharPosition),
								mLastHistory.newChar);

						this.modifyCharNoHistory(mLastHistory.endStringNum, mLastHistory.endCharPosition,
								mLastHistory.oldChar);
						this.modifyCharNoHistory(mLastHistory.endStringNum - 1, mLastHistory.endCharPosition,
								mLastHistory.newChar);
					} else {
						if (mLastHistory.endStringNum + 1 >= this.numStrings) {
							return;
						}
						// Log.i("stuff", "to Down " + mLastHistory.endStringNum
						// + " " + this.numStrings);
						mHistoryManager.addMoveHistory(mLastHistory.endStringNum, mLastHistory.endCharPosition,
								mLastHistory.endStringNum + 1, mLastHistory.endCharPosition,
								this.getString(mLastHistory.endStringNum + 1).charAt(mLastHistory.endCharPosition),
								mLastHistory.newChar);

						this.modifyCharNoHistory(mLastHistory.endStringNum, mLastHistory.endCharPosition,
								mLastHistory.oldChar);
						this.modifyCharNoHistory(mLastHistory.endStringNum + 1, mLastHistory.endCharPosition,
								mLastHistory.newChar);
					}
				}
			}
		}
	}

	public int undo() {
		// stub. For now can only undo single replacements
		HistoryObject mLastHistory = mHistoryManager.undo();
		int lastCharPos = 0;
		if (mLastHistory != null) {
			switch (mLastHistory.getHistoryType()) {
			case SINGLE_REPLACE:
				modifyCharNoHistory(mLastHistory.stringNum, mLastHistory.charPosition, mLastHistory.oldChar);
				lastCharPos = mLastHistory.charPosition;
				break;
			case DELETION:
				pasteStringNoHistory(mLastHistory.changeLocation, mLastHistory.stringModification);
				lastCharPos = mLastHistory.changeLocation;
				break;
			case INSERTION:
				Log.e("stuff", "undoing a deletion");
				SplitableString mSplitText = new SplitableString(mLastHistory.stringModification);
				mSplitText.SplitByChar('\n');
				int deletionLength = mSplitText.getStringAt(1).length();
				deleteSelectionNoHistory(mLastHistory.changeLocation, deletionLength + mLastHistory.changeLocation);
				lastCharPos = mLastHistory.changeLocation;
				break;
			case MOVEMENT:
				Log.e("stuff", "undoing a movement");
				int fromChar = mLastHistory.charPosition;
				int fromString = mLastHistory.stringNum;
				int movedToChar = mLastHistory.endCharPosition;
				int movedToString = mLastHistory.endStringNum;
				modifyCharNoHistory(fromString, fromChar, mLastHistory.newChar);
				modifyCharNoHistory(movedToString, movedToChar, mLastHistory.oldChar);
				lastCharPos = mLastHistory.charPosition;
				break;
			default:
				break;
			}
		}
		return lastCharPos;
	}

	public boolean lastHistoryIsSingleReplace() {
		HistoryObject mLastHistory = mHistoryManager.getLastHistory();
		if (mLastHistory != null) {
			if (mLastHistory.getHistoryType() == HistoryTypes.SINGLE_REPLACE) {
				return true;
			}
		}
		return false;
	}

	public void redo() {
		// stub. For now can only redo single replacements
		char oldChar = '-';
		if (mHistoryManager.canUndo()) {
			oldChar = mHistoryManager.getLastHistory().oldChar;
		}
		HistoryObject mLastHistory = mHistoryManager.redo();
		if (mLastHistory != null) {
			switch (mLastHistory.getHistoryType()) {
			case SINGLE_REPLACE:
				modifyCharNoHistory(mLastHistory.stringNum, mLastHistory.charPosition, mLastHistory.newChar);
				break;
			case DELETION:
				Log.e("stuff", "redoing a deletion");
				SplitableString mSplitText = new SplitableString(mLastHistory.stringModification);
				mSplitText.SplitByChar('\n');
				int deletionLength = mSplitText.getStringAt(1).length();
				deleteSelectionNoHistory(mLastHistory.changeLocation, deletionLength + mLastHistory.changeLocation);
				break;
			case INSERTION:
				Log.e("stuff", "redoing a insertion");
				pasteStringNoHistory(mLastHistory.changeLocation, mLastHistory.stringModification);
				break;
			case MOVEMENT:
				Log.e("stuff", "redoing a movement");
				int fromChar = mLastHistory.charPosition;
				int fromString = mLastHistory.stringNum;
				int movedToChar = mLastHistory.endCharPosition;
				int movedToString = mLastHistory.endStringNum;
				modifyCharNoHistory(fromString, fromChar, oldChar);
				modifyCharNoHistory(movedToString, movedToChar, mLastHistory.newChar);
				break;
			default:
				break;
			}
		}
	}

	public void copyToClipboard(int startPosition, int endPosition) {
		android.text.ClipboardManager clipboard = SharedData.textClipboard;
		if (endPosition < startPosition) {
			int newEnd = startPosition;
			startPosition = endPosition;
			endPosition = newEnd;
		}

		String copyText = "";
		for (int x = 0; x < numStrings; x++) {
			String tmpString = stringsMap.get(x).substring(startPosition, endPosition);
			copyText += tmpString + '\n';
		}
		clipboard.setText(copyText);
	}

	public void copyToClipboard(String copyText) {
		android.text.ClipboardManager clipboard = SharedData.textClipboard;
		clipboard.setText(copyText);
	}

	public void pasteFromClipboard(int position) {
		android.text.ClipboardManager clipboard = SharedData.textClipboard;
		String insertion = String.valueOf(clipboard.getText());
		SplitableString mSplitText = new SplitableString(insertion);
		mSplitText.SplitByChar('\n');
		int length = mSplitText.getStringAt(1).length();
		for (int i = 1; i <= this.getNumStrings(); i++) {
			if (mSplitText.getStringAt(i).equals("empty"))
				return;
			if (mSplitText.getStringAt(i).length() != length)
				return;
		}
		// if we got to here, there will be no problems pasting in the string.
		mHistoryManager.addInsertionHistory(position, insertion);
		this.pasteStringNoHistory(position, insertion);
	}

	public void insertChord(String toInsert, int position) {
		String insertion = toInsert;
		SplitableString mSplitText = new SplitableString(insertion);
		mSplitText.SplitByChar('\n');
		int length = mSplitText.getStringAt(1).length();
		/*
		 * TODO: we should check the numSplits against the number of strings, then add dashes or get rid of part of the
		 * string as necessary
		 */
		for (int i = 1; i <= this.getNumStrings(); i++) {
			if (mSplitText.getStringAt(i).equals("empty")) {
				/* add lines for every string we're missing */
				insertion += "-";
				/* and is we need to add another dash, then do so */
				if (length == 2)
					insertion += "-";
				insertion += "\n";
			} else if (mSplitText.getStringAt(i).length() != length)
				return;
		}
		// if we got to here, there will be no problems pasting in the string.
		mHistoryManager.addInsertionHistory(position, insertion);
		this.pasteStringNoHistory(position, insertion);
	}

	public boolean canUndo() {
		return this.mHistoryManager.canUndo();
	}

	public boolean canRedo() {
		return this.mHistoryManager.canRedo();
	}

	public boolean lastHistoryIsMove() {
		HistoryObject mLastHistory = mHistoryManager.getLastHistory();
		if (mLastHistory != null) {
			if (mLastHistory.getHistoryType() == HistoryTypes.MOVEMENT) {
				return true;
			}
		}
		return false;
	}

	String defaultDenotation[] = { "E-|", "A-|", "D-|", "G-|", "B-|", "e-|", "C-|", "bb|", "Ab|", "Gb|", "e-|", "e-|" };

	public void setDefaultDenotation() {
		for (int i = 0; i < numStrings; i++)
			denotationMap.put(i, defaultDenotation[(numStrings - 1) - i]);
	}

}

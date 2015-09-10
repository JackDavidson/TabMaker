package wolf.games.mobile.shared;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.levien.synthesizer.core.music.Note;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import wolf.games.mobile.tools.SplitableString;

public class TabPortion {
	private String entireTabString;
	private SplitableString entireTab;
	private Map<Integer, String> stringsMap;
	private int numStrings;
	private Handler handler;
	private int length;
	private int currentPlayPosition = 0;
	private int endPosition = 0;
	private int startingPosition = 0;
	private boolean usingDoubleDigitNotes = true;

	private static int HIGHESTFRET = 18;

	private boolean[] continueNote = { false, false, false, false, false, false, false, false, false, false, false,
			false, false, false, false, false };

	public TabPortion(String entireTabString) {

		this.entireTabString = entireTabString;
		stringsMap = new HashMap<Integer, String>(10);
		entireTab = new SplitableString(entireTabString);
		entireTab.SplitByChar('\n');
		length = entireTab.getStringAt(1).length();
		numStrings = entireTab.getNumSplits();
	}

	public int getLength() {
		return length;
	}

	public String getEntirePortion() {
		String entireTabPortionInOneString = "";
		for (int i = 1; i < entireTab.getNumSplits(); i++) {
			entireTabPortionInOneString += entireTab.getStringAt(i) + '\n';
		}
		return entireTabPortionInOneString;
	}

	public String getEntirePortionWithNewLineAt(int numCharacters) {
		String entireTabPortionInOneString = "";
		String secondSetOfStrings = "";
		for (int i = 1; i < entireTab.getNumSplits(); i++) {
			if (entireTab.getStringAt(i).length() > numCharacters) {
				entireTabPortionInOneString += entireTab.getStringAt(i).substring(0, numCharacters) + '\n';
				secondSetOfStrings += entireTab.getStringAt(i).substring(numCharacters) + '\n';
			} else {
				entireTabPortionInOneString += entireTab.getStringAt(i) + '\n';
			}
		}
		if (!secondSetOfStrings.equals("")) {
			entireTabPortionInOneString += secondSetOfStrings;
		}
		// stub
		return entireTabPortionInOneString;
	}

	private void parseSingleNote(SharedPreferences prefs) {
		for (int string = 1; string < numStrings; string++) {
			try {
				// if anything is to be played, we should first
				// stop everything. then we can break.
				Integer.parseInt(String.valueOf(entireTab.getStringAt(string).charAt(currentPlayPosition)));
				if (SharedData.playStacato) {
					stopAllNotes();
				}
				break;
			} catch (Exception e) {
			}
		}
		if (currentPlayPosition >= length) {
			// if the tab is finished, we should stop playing
			// it.
			currentPlayPosition = 0;
			handler.removeCallbacksAndMessages(null);
		} else {
			for (int string = 1; string < numStrings; string++) {
				try {
					// first, check if it is an integer
					int noteNum = Integer.parseInt(String.valueOf(entireTab.getStringAt(string).charAt(
							currentPlayPosition)));

					try {
						// check if the previous character is an
						// integer.
						String previousNoteNum = String.valueOf(Integer.parseInt(String.valueOf(entireTab.getStringAt(
								string).charAt(currentPlayPosition - 1))));
						// if we made it to here, previous is an
						// integer.
						if (!prefs.getBoolean("IgnoreDoubleDigitNotes", false)) {
							// only do something if we are
							// looking for double digits
							int tmpNoteNum = Integer.parseInt(previousNoteNum + noteNum);
							if (tmpNoteNum <= HIGHESTFRET && tmpNoteNum > 9) {
								continueNote[string] = true;
								noteNum = tmpNoteNum;
							}
						}

					} catch (Exception e) {
						// the previous character is not an
						// integer. check if the next note is an
						// integer.
						try {
							// check if the next note is an
							// integer.
							String secondNoteNum = String.valueOf(Integer.parseInt(String.valueOf(entireTab
									.getStringAt(string).charAt(currentPlayPosition + 1))));
							// if we made it to here, it is an
							// integer.
							if (!prefs.getBoolean("IgnoreDoubleDigitNotes", false)) {
								int tmpNoteNum = Integer.parseInt(noteNum + secondNoteNum);
								Log.v("stuff", "the second note is an integer. " + noteNum);
								if (tmpNoteNum <= HIGHESTFRET && tmpNoteNum > 9) {
									noteNum = tmpNoteNum;
								}
							}

						} catch (Exception e1) {
							// the next character is not an
							// integer. continue on.
						}
					}
					noteNum = getGuitarNoteNumber(noteNum, string);
					if (!continueNote[string]) {

						if (SharedData.noteType == "Classical") {
							playClassicalNote(noteNum);
						} else if (SharedData.noteType == "Acoustic") {
							playAcousticNote(noteNum);
						}
					} else {
						continueNote[string] = false;
						if (SharedData.noteType == "Classical") {
							playClassicalNoteNoReset(noteNum);
						} else if (SharedData.noteType == "Acoustic") {
							playAcousticNoteNoReset(noteNum);
						}
					}
				} catch (Exception e) {
					// this character does not happen to be an
					// integer
					// Log.e("tab maker ", "character at" +
					// currentPlayPosition +
					// "is not an integer. string is: " + string
					// + entireTab.getStringAt(string));
				}
			}

		}
	}

	public void playSection() {
		SharedData.soundsManager.mBaseActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				handler = new Handler();
				currentPlayPosition = 0;
				final Runnable r = new Runnable() {
					public void run() {
						SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(SharedData.soundsManager.mBaseActivity.getBaseContext());
						handler.removeCallbacksAndMessages(null);

						try {
							if (entireTab.getStringAt(1).charAt(currentPlayPosition) == '|'
									|| entireTab.getStringAt(1).charAt(currentPlayPosition) == 'N') {
								// this is just whitespace. skip to the next
								// char because we don't want a pause in the
								// music.
								handler.post(this);
								// this should eventually be edited in settings
								currentPlayPosition += 1;
								SharedData.notesToSkip += 1;
								return;
							} else {
								/*
								 * this is where we call this method again to play the next note. this is done as eary
								 * las possible in order to avoid lag
								 */
								int delay = getIntPreference("NPM", prefs, "400");
								delay = 60000 / delay;
								handler.postDelayed(this, delay);
							}
						} catch (Exception e) {
							// the tab is probably already finished. just quit.
							return;
						}

						parseSingleNote(prefs);
						/*
						 * done playing the note. go to next one. implicit return here.
						 */
						currentPlayPosition++;
					}

				};
				handler.post(r);
			}
		});
		// this should create a new thread that repeats to go through the tab.
	}

	public void playSection(final int startPoint, final int stopPoint) {
		SharedData.soundsManager.mBaseActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				handler = new Handler();
				if (startPoint > stopPoint) {
					currentPlayPosition = stopPoint;
					endPosition = startPoint;
					startingPosition = currentPlayPosition;

				} else {
					currentPlayPosition = startPoint;
					endPosition = stopPoint;
					startingPosition = currentPlayPosition;
				}
				final Runnable r = new Runnable() {
					public void run() {
						SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(SharedData.soundsManager.mBaseActivity.getBaseContext());
						handler.removeCallbacksAndMessages(null);

						if (currentPlayPosition >= endPosition)
							return;

						try {
							if (entireTab.getStringAt(1).charAt(currentPlayPosition) == '|'
									|| entireTab.getStringAt(1).charAt(currentPlayPosition) == 'N') {
								// this is just whitespace. skip to the next
								// char because we don't want a pause in the
								// music.
								handler.post(this);
								// this should eventually be edited in settings
								currentPlayPosition += 1;
								SharedData.notesToSkip += 1;
								return;
							} else {
								/*
								 * this is where we call this method again to play the next note. this is done as eary
								 * las possible in order to avoid lag
								 */
								int delay = getIntPreference("NPM", prefs, "400");
								delay = 60000 / delay;
								handler.postDelayed(this, delay);
							}
						} catch (Exception e) {
							// the tab is probably already finished. just quit.
							return;
						}

						parseSingleNote(prefs);
						/*
						 * done playing the note. go to next one. implicit return here.
						 */
						currentPlayPosition++;
					}

				};
				handler.post(r);
			}
		});
		// this should create a new thread that repeats to go through the tab.
	}

	private int getGuitarNoteNumber(int num, int string) {
		string = this.numStrings - string;

		// Log.i("asdf", "string: " + string);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SharedData.soundsManager.mBaseActivity
				.getBaseContext());
		int octaveOffset;
		int noteOffset;
		switch (string) {
		case 13:
			return num - 3;
		case 12:
			return num - 3;
		case 11:
			return num - 3;
		case 10:
			return num - 3;
		case 9:
			return num - 3;
		case 8:
			return num - 3;
		case 7:
			return num - 3;
		case 6:
			octaveOffset = getIntPreference("SixthOctaveOffset", prefs, "2");
			noteOffset = getIntPreference("SixthNoteOffset", prefs, "7");
			num = num + 12 * octaveOffset + noteOffset;
			return num - 3;
		case 5:
			octaveOffset = getIntPreference("FifthOctaveOffset", prefs, "2");
			noteOffset = getIntPreference("FifthNoteOffset", prefs, "2");
			num = num + 12 * octaveOffset + noteOffset;
			return num - 3;
		case 4:
			octaveOffset = getIntPreference("FourthOctaveOffset", prefs, "1");
			noteOffset = getIntPreference("FourthNoteOffset", prefs, "10");
			num = num + 12 * octaveOffset + noteOffset;
			return num - 3;
		case 3:
			octaveOffset = getIntPreference("ThirdOctaveOffset", prefs, "1");
			noteOffset = getIntPreference("ThirdNoteOffset", prefs, "5");
			num = num + 12 * octaveOffset + noteOffset;
			return num - 3;
		case 2:
			octaveOffset = getIntPreference("SecondOctaveOffset", prefs, "1");
			noteOffset = getIntPreference("SecondNoteOffset", prefs, "0");
			num = num + 12 * octaveOffset + noteOffset;
			return num - 3;
		case 1:
			octaveOffset = getIntPreference("FirstOctaveOffset", prefs, "0");
			noteOffset = getIntPreference("FirstNoteOffset", prefs, "7");
			num = num + 12 * octaveOffset + noteOffset;
			// Log.i("afd", "the note is: " + num);
			return num - 3;
		}

		/*
		 * the calculations abovew will always return a value thats just 3 half-steps too high
		 */
		return num - 3;
	}

	public void playAcousticNote(int noteNum) {
		if (SharedData.soundsManager != null) {
			Log.i("asd", "playing acoustic");
			if (SharedData.soundsManager.guitarNotes.get(noteNum).isPlaying()) {
				SharedData.soundsManager.guitarNotes.get(noteNum).pause();
				SharedData.soundsManager.guitarNotes.get(noteNum).seekTo(0);
				SharedData.soundsManager.guitarNotes.get(noteNum).play();
			} else {
				SharedData.soundsManager.guitarNotes.get(noteNum).seekTo(0);
				SharedData.soundsManager.guitarNotes.get(noteNum).play();
			}
		}
	}

	private int getIntPreference(String prefString, SharedPreferences prefs, String defaultSetting) {
		String regOffsetString = prefs.getString(prefString, defaultSetting);
		int prefResult = 0;
		try {
			prefResult = Integer.valueOf(regOffsetString);
		} catch (Exception e) {
			Log.v("stuff", e.toString());
			prefResult = 0;
		}

		return prefResult;
	}

	public void playAcousticNoteNoReset(int noteNum) {
		if (SharedData.soundsManager != null) {
			if (SharedData.soundsManager.guitarNotes.get(noteNum).isPlaying()) {
				// SharedData.soundsManager.guitarNotes.get(noteNum).pause();
				// SharedData.soundsManager.guitarNotes.get(noteNum).seekTo(0);
				// SharedData.soundsManager.guitarNotes.get(noteNum).play();
			} else {
				// SharedData.soundsManager.guitarNotes.get(noteNum).seekTo(0);
				SharedData.soundsManager.guitarNotes.get(noteNum).play();
			}
		}
	}

	public void stopAllNotes() {
		/*
		 * for (int noteNum = 1; noteNum<= 42; noteNum++){ if
		 * (SharedData.soundsManager.guitarNotes.get(noteNum).isPlaying()){
		 * SharedData.soundsManager.guitarNotes.get(noteNum).pause(); } } for (int noteNum = 3; noteNum<= 39;
		 * noteNum++){ if (SharedData.soundsManager.classicalNotes.get(noteNum).isPlaying()){
		 * SharedData.soundsManager.classicalNotes.get(noteNum).pause(); } }
		 */
		/*
		 * for (int i = 0; i < this.numStrings; i++) SharedData.soundsManager.androidGlue_.onNoteOff(0, i, 64);
		 */

	}

	int channel = 0;

	ArrayList<Integer> notes = null;

	public void playClassicalNote(int noteNum) {
		if (notes == null)
			notes = new ArrayList<Integer>(50);
		if (SharedData.soundsManager != null) {
			// Log.i("asd", "playing classical");

			if (SharedData.soundsManager.androidGlue_ != null) {
				// Log.i("asdf", "playing the note");

				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(SharedData.soundsManager.mBaseActivity.getBaseContext());

				int primaryOctave = this.getIntPreference("PrimeOctave", prefs, "3");

				double logFrequency = Note.computeLog12TET(noteNum, primaryOctave);
				final int midiNote = Note.getKeyforLog12TET(logFrequency);
				// int presetNum = 11;
				// SharedData.soundsManager.androidGlue_.sendMidi(new byte[]
				// {(byte)0xc0, (byte)presetNum});
				// schedule it
				final int chan = channel;
				if (channel < 5) {
					channel++;
				} else {
					channel = 0;
				}

				SharedData.soundsManager.mBaseActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						final Handler handle = new Handler();
						final Runnable r = new Runnable() {
							public void run() {
								if (notes.remove((Integer) midiNote))
									if (!notes.contains((Integer) midiNote))
										SharedData.soundsManager.androidGlue_.onNoteOff(chan, midiNote, 127);
							}
						};
						handle.postDelayed(r, 1500);
					}
				});

				/* if the note is already playing, stop it then start it again */
				if (notes.contains((Integer) midiNote))
					SharedData.soundsManager.androidGlue_.onNoteOff(chan, midiNote, 127);
				/*
				 * otherwise, add it to the list of notes that are playing and start playing it
				 */
				notes.add((Integer) midiNote);
				SharedData.soundsManager.androidGlue_.onNoteOn(chan, midiNote, 127);
			} else {
				Log.e("its null!", "Its null!");
			}

			if (SharedData.soundsManager.classicalNotes.get(noteNum).isPlaying()) {
				SharedData.soundsManager.classicalNotes.get(noteNum).pause();
				SharedData.soundsManager.classicalNotes.get(noteNum).seekTo(0);
				SharedData.soundsManager.classicalNotes.get(noteNum).play();
			} else {
				SharedData.soundsManager.classicalNotes.get(noteNum).seekTo(0);
				SharedData.soundsManager.classicalNotes.get(noteNum).play();
			}
		}
	}

	public void playClassicalNoteNoReset(int noteNum) {
		if (SharedData.soundsManager != null) {
			if (SharedData.soundsManager.classicalNotes.get(noteNum).isPlaying()) {
				// SharedData.soundsManager.classicalNotes.get(noteNum).pause();
				// SharedData.soundsManager.classicalNotes.get(noteNum).seekTo(0);
				// SharedData.soundsManager.classicalNotes.get(noteNum).play();

				// just allow it to play
			} else {
				// SharedData.soundsManager.classicalNotes.get(noteNum).seekTo(0);
				SharedData.soundsManager.classicalNotes.get(noteNum).play();
			}
		}
	}

	public void pause() {
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
	}

	public String findStringDenotation(char dashCharacter) {
		int longestDenotation = getStringDenotationLength(dashCharacter);
		Log.i("stuff", "longest denotation is: " + longestDenotation);
		String denotation = "";
		for (int i = 1; i < entireTab.getNumSplits(); i++) {
			for (int x = 0; x < longestDenotation; x++) {
				denotation += entireTab.getStringAt(i).charAt(x);
			}
			denotation += '\n';
		}
		return denotation;
	}

	private int getStringDenotationLength(char dashCharacter) {
		int longestDenotation = 0;
		for (int i = 1; i < entireTab.getNumSplits(); i++) {
			int currentDenotationLength = 0;
			for (int x = 0; x < length; x++) {
				try {
					if (entireTab.getStringAt(i).charAt(x) == dashCharacter) {
						// if we find a '|' after the dash, really we must have a slightly longer denotation.
						if (entireTab.getStringAt(i).charAt(x + 1) == '|')
							currentDenotationLength += 2;
						Log.v("stuff", "found - at: " + currentDenotationLength);
						// probably a part of the tab. break to stop counting.
						break;
					} else {
						try {
							int blah = Integer.parseInt(String.valueOf(entireTab.getStringAt(i).charAt(x)));
							Log.v("stuff", "not incrementing. found num equal to: " + blah);
							// if we made it here, it is a part of the tab, so
							// break.
							break;
						} catch (Exception e) {
							Log.v("stuff", "incrementing. " + currentDenotationLength);
							currentDenotationLength++;
						}
					}
				} catch (Exception e) {
					Log.e("TabMaker", e.toString());
					// the string has reached its end, so just break.
					break;
				}
			}

			if (currentDenotationLength > longestDenotation)
				longestDenotation = currentDenotationLength;
			if (entireTab.getStringAt(i).equals("empty")) {
				Log.e("TabMaker", "resetting longest denotation");
				longestDenotation = 0;
			}

		}
		return longestDenotation;
	}

	public String getSectionWithoutStringDenotation(char dashCharacter) {
		// stub
		String sectionWithoutDenotation = "";
		int longestDenotation = getStringDenotationLength(dashCharacter);
		Log.i("stuff", "longest denotation is: " + longestDenotation);
		for (int i = 1; i < entireTab.getNumSplits(); i++) {
			try {
				String newString = entireTab.getStringAt(i).substring(longestDenotation,
						entireTab.getStringAt(1).length());
				sectionWithoutDenotation += newString + '\n';
			} catch (Exception e) {
				break;
			}
		}
		Log.i("TabMaker", "successfully modified the string. " + sectionWithoutDenotation);
		return sectionWithoutDenotation;
	}

	public void replaceDenotationWith(String mStringDenotation, char dashCharacter) {
		int longestDenotation = getStringDenotationLength(dashCharacter);
		SplitableString tmpSplitableDenotation = new SplitableString(mStringDenotation);
		tmpSplitableDenotation.SplitByChar('\n');
		Log.i("stuff", "longest denotation is: " + longestDenotation);
		for (int i = 1; i < entireTab.getNumSplits(); i++) {
			try {
				String newString = entireTab.getStringAt(i).substring(longestDenotation);
				newString = tmpSplitableDenotation.getStringAt(i).concat(newString);
				entireTab.replaceString(i, newString);
				Log.i("TabMaker", "successfully modified the string. " + newString);
			} catch (Exception e) {
				Log.i("TabMaker", "failed to modify this tab section." + e.toString());
				break;
			}
		}

	}

}

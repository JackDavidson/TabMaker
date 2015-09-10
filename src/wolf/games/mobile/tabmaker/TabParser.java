package wolf.games.mobile.tabmaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import wolf.games.mobile.shared.SharedData;
import wolf.games.mobile.shared.TabPortion;
import wolf.games.mobile.tools.CommonCharacterFinder;
import wolf.games.mobile.tools.SDCardWriter;
import wolf.games.mobile.tools.SplitableString;
import wolf.games.mobile.tools.XMLParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Environment;
import android.util.Log;

public class TabParser {
	SplitableString entireTab;
	SplitableString denotationSplitable;
	String entireTabString;
	String tabDenotation;
	String startComment = "";
	char newLine = '\n';
	int numTabStrings;
	int startPoint = 1;
	public static char mostCommonChar;
	private Map<Integer, TabPortion> tabPortionsMap;
	private Map<Integer, String> comments;
	XMLParser mXMLParser;
	
	String defaultDenotation[] = {"E-|", "A-|", "D-|", "G-|", "B-|", "e-|", "C-|", "bb|", "Ab|", "Gb|", "e-|", "e-|"};

	private static final float MIN_DASH_PERCENTAGE_COUNTSTRINGS = 0.3f;
	private static final float MIN_DASH_PERCENTAGE_WITHIN_TAB = 0.1f;

	public TabParser(String filePath, String fileName, int numTabStrings) {
		tabPortionsMap = new HashMap<Integer, TabPortion>(40);
		mXMLParser = new XMLParser();
		this.numTabStrings = numTabStrings;
		if (fileName.endsWith(".XML") || fileName.endsWith(".xml")) {
			mXMLParser.loadFile(filePath + "/" + fileName);
			entireTabString = mXMLParser.getElement("tab");
			if (entireTabString != null) {
				// success! we read the file. we can load it up, then be done
				// but first, we need to grab the denotation too
				tabDenotation = mXMLParser.getElement("denotation");
				Log.v("Tab Parser", "loaded denotation is: " + tabDenotation);
				if (tabDenotation != null) {
					denotationSplitable = new SplitableString(tabDenotation);
					denotationSplitable.SplitByChar(newLine);
				} else {
					String denotationToSplit = "";
					for(int i = 1; i <= numTabStrings; i++){
						denotationToSplit += defaultDenotation[numTabStrings - i] + '\n';
					}
					denotationSplitable = new SplitableString(denotationToSplit);
					denotationSplitable.SplitByChar(newLine);
				}

				// with this.
				entireTab = new SplitableString(entireTabString);
				entireTab.SplitByChar(newLine);
				this.numTabStrings = entireTab.getNumSplits() - 1;
				return;
			}

			// we can load up the comment here later on.
		}
		// if we get here, the tab is either not xml, or not made by tab maker.
		entireTabString = SDCardWriter.readFile(filePath + "/" + fileName);
		entireTab = new SplitableString(entireTabString);
		entireTab.SplitByChar(newLine);
		findNumTabStringsAndInitialComment();
		separateTabsAndComments();
	}

	public TabParser() {
		SharedData.messageToUser = "";
		tabPortionsMap = new HashMap<Integer, TabPortion>(40);
		// this means that we should grab the tab from the url in the clipboard.
		String fileName = null;
		if (SharedData.currentURL != null) {
			fileName = handleLoadFromURL();
		} else if (SharedData.entireTabAsString != null) {
			fileName = SharedData.activeFile;
			entireTabString = SharedData.entireTabAsString;
		}
		if (fileName == null)
			return;

		SDCardWriter.writeFile(Environment.getExternalStorageDirectory() + "/TabMaker/DownloadedTabs/", fileName,
				entireTabString);
		entireTab = new SplitableString(entireTabString);
		entireTab.SplitByChar(newLine);

		if (SharedData.currentURL != null) {
			if (SharedData.currentURL.contains("ultimate-guitar")) {
				startPoint = 14;
				for (int i = 0; i <= startPoint; i++) {
					SharedData.messageToUser += entireTab.getStringAt(i);
				}
			}
		}

		findNumTabStringsAndInitialComment();
		separateTabsAndComments();
		// get page title
	}

	private String handleLoadFromURL() {
		String url;
		url = SharedData.currentURL;

		Document doc;
		String fileName = null;
		if (url.endsWith(".txt")) {
			try {
				URL realUrl = new URL(url);
				BufferedReader in = new BufferedReader(new InputStreamReader(realUrl.openStream()));

				entireTabString = "";
				String inputLine;
				while ((inputLine = in.readLine()) != null)
					// System.out.println(inputLine);
					entireTabString += inputLine + "\n";
				in.close();
				fileName = realUrl.getFile();

			} catch (Exception e) {
				SharedData.messageToUser = "Failed To Connect. " + e.toString();
				SharedData.failedToLoadWebsite = true;
				e.printStackTrace();
				return null;
			}
		} else {
			try {
				doc = Jsoup.connect(url).get();
				fileName = doc.title() + ".txt";

				entireTabString = doc.body().text();

				if (!entireTabString.contains("\n")) {
					Log.e("Stuff", "");
					/*
					 * if lines are separated by html rather than "\n"s, we need to use a different method. lets
					 * reformat by putting a newline instead of each html separator
					 */
					entireTabString = Jsoup.parse(doc.body().html().replaceAll("(?i)<br[^>]*>", "br2n")).text();
					/*
					 * some places use <br></br> instead of \n, which is what makes all this necessary
					 */
					entireTabString = entireTabString.replaceAll("br2n ", "\n");
					entireTabString = entireTabString.replaceAll("br2n", "\n");
				}

				if (!entireTabString.contains("\n")) {
					/* and if we still have a problem with new lines */
					SharedData.messageToUser = "Theres been an error parsing. For some reason, theres no newline in your entire tab";
				}

				Log.e("Stuff", "entirety downloaded is:" + entireTabString);

				Elements newUrl = doc.select("iframe");
				if (newUrl != null && newUrl.size() >= 2) {
					Log.i("stuff", newUrl.first().attr("src").toString());
					Log.i("stuff", newUrl.get(1).attr("src").toString());
					url = newUrl.get(1).attr("src").toString();

					doc = Jsoup.connect(url).get();
					fileName = doc.title() + ".txt";
					entireTabString = doc.body().text();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				SharedData.messageToUser = "Failed To Connect. " + e.toString();
				SharedData.failedToLoadWebsite = true;
				e.printStackTrace();
				return null;
			}
		}

		return fileName;
	}

	private void separateTabsAndComments() {
		// for now, this just pulls out the tab bits.
		for (int i = startPoint; i < entireTab.getNumSplits(); i++) {
			String portion = entireTab.getStringAt(i);
			int numDashes = 0;
			// CommonCharacterFinder mCommonCharFinder = new
			// CommonCharacterFinder(portion);
			for (int x = 0; x < portion.length(); x++) {
				if (portion.length() > 5) {
					if (((Character) portion.charAt(x)).equals(mostCommonChar)) {
						numDashes++;
					}
				} else {
					break;
				}
			}

			// Log.e("Stuff", "we are on line number: " + i);

			if (((float) numDashes / (float) portion.length()) > MIN_DASH_PERCENTAGE_WITHIN_TAB) {
				Boolean success = true;
				// Log.e("Stuff", "we detected a string ");

				int previousStringLength = entireTab.getStringAt(i).length();
				String mTmpTabPortionString = "";
				for (int y = 0; y < numTabStrings; y++) {
					if (y < numTabStrings - 1) {
						portion = entireTab.getStringAt(i + y);
						if (portion.length() <= 5) {
							// its too short. not a piece of tablature.
							success = false;
							break;
						}
						if (Math.abs(previousStringLength - entireTab.getStringAt(i + y).length()) > entireTab
								.getStringAt(i + y).length() / 5) {
							// if this strings length and the previous string's
							// length are more than 20% different, there is a
							// problem and we should not consider this to be a
							// portion of the tab.
							success = false;
							break;
						}
						numDashes = 0;
						for (int x = 0; x < portion.length(); x++) {
							if (portion.length() > 6) {
								if (portion.charAt(x) == mostCommonChar) {
									numDashes++;
								}
							} else {
								// the string is not long enough.
								success = false;
								break;
							}
						}

						// Log.e("Stuff", "we are on line number: " + i);

						if (((float) numDashes / (float) portion.length()) > MIN_DASH_PERCENTAGE_WITHIN_TAB) {
							// we made it! this one line is really a string.
							mTmpTabPortionString += entireTab.getStringAt(i + y) + '\n';
							previousStringLength = entireTab.getStringAt(i + y).length();
						} else {
							// not a large enough percentage of dashes - must
							// not be a part of the tab
							success = false;
							break;
						}
					} else {
						// this is the last piece of the string. since the other
						// strings all worked out, we can be pretty sure this one
						// will too.
						mTmpTabPortionString += entireTab.getStringAt(i + y);
					}
				}
				if (success) {
					// this section has been confirmed to be a tab section. add
					// it to our tab portion map.
					i += numTabStrings - 1;
					tabPortionsMap.put(tabPortionsMap.size(), new TabPortion(mTmpTabPortionString));
				}
			} else {
				// Log.e("Stuff", "not a string.");
				continue;
			}
		}
	}

	private void findNumTabStringsAndInitialComment() {
		Boolean getingInitialComment = true;
		numTabStrings = 0;
		CommonCharacterFinder mCommonCharFinder = new CommonCharacterFinder(entireTabString);
		mostCommonChar = mCommonCharFinder.mostCommonCharacter();
		for (int i = startPoint; i < entireTab.getNumSplits(); i++) {
			String portion = entireTab.getStringAt(i);
			int numDashes = 0;
			char countedChar = '-';
			for (int x = 0; x < portion.length(); x++) {
				if (portion.length() > 10) {
					if (portion.charAt(x) == mCommonCharFinder.mostCommonCharacter()) {
						numDashes++;
					}
				}
			}
			// Log.i("stuff", "percentage - is: " + (float) numDashes / (float)
			// portion.length());

			if (((float) numDashes / (float) portion.length()) > MIN_DASH_PERCENTAGE_COUNTSTRINGS) {
				numTabStrings++;
				getingInitialComment = false;
				// break;
			} else {
				if (getingInitialComment) {
					startComment += entireTab.getStringAt(i) + '\n';
					entireTab.replaceString(i, "");
				} else {
					break;
				}
			}
		}
		// Log.e("stuff", "the number of strings we have here is: " +
		// numTabStrings);

		entireTabString = "";
		entireTabString += startComment + "\n\n END OF STARTING COMMENT \n\n";
		if (numTabStrings <= 3) {
			// we had a problem identifying the number of strings. there can't
			// be just 3 strings, so default to 6 strings.
			// in the future, we should ask the user how many strings are in the
			// tab if we cant identify it.
			Log.e("Stuff", "problem identifying the number of strings. defaulting to 6");
			numTabStrings = 6;
		}

		Log.e("Stuff", "We have this many stings:" + numTabStrings);

	}

	public TabParser(String url) {
		tabPortionsMap = new HashMap<Integer, TabPortion>(40);
		// stub
	}

	private void addTabPortion(String tabPortion) {
		// stub
	}

	public String getTabStartComment() {
		return entireTabString;
	}

	public int getNumStrings() {
		return this.numTabStrings;
	}

	public String getTabString(int stringNum) {
		return entireTab.getStringAt(stringNum);
	}

	public String getStringDenotation(int stringNum) {
		if (denotationSplitable != null)
			return denotationSplitable.getStringAt(stringNum);
		String denotationToSplit = "";
		for(int i = 1; i <= numTabStrings; i++){
			denotationToSplit += defaultDenotation[numTabStrings - i] + '\n';
		}
		denotationSplitable = new SplitableString(denotationToSplit);
		denotationSplitable.SplitByChar(newLine);
		return denotationSplitable.getStringAt(stringNum);
	}

	public int numTabPortions() {
		// stub
		return tabPortionsMap.size();
	}

	public TabPortion getTabPortion(int portionNum) {
		return tabPortionsMap.get(portionNum);
	}

	public String getTabComment(int commentNum) {
		// stub
		return null;
	}

	private void writeToFile() {
		// stub
	}
}

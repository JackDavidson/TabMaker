package wolf.games.mobile.tabmaker;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import wolf.games.mobile.tools.SplitableString;

public class ZoomTabParser {

	protected Map<Integer, String> stringTabsMap;
	protected Map<Integer, String> stringCommentsMap;
	private int numberOfStrings;

	/**
	 * This constructor handles splitting the tab up into its parts, into comments and tab portions
	 * 
	 * @param tabToParse
	 *            the string version of the tab that needs to get parsed. This should be the exported version of the
	 *            tab, which means ---NOT XML---
	 */
	ZoomTabParser(String tabToParse) {
		stringTabsMap = new HashMap<Integer, String>(12);
		stringCommentsMap = new HashMap<Integer, String>(12);

		/* strategy is to go line by line, determining if each thing is a comment or a tab */
		SplitableString entireTabSplitable = new SplitableString(tabToParse);
		entireTabSplitable.SplitByChar('\n');

		/* first, how many strings do we have? */
		numberOfStrings = calculateNumberOfStrings(entireTabSplitable);
		Log.i("TM ZoomTabParser", "Number of strings: " + numberOfStrings);

		/* next lets finally do the real processing */
		SetupCommentsAndTabStrings(entireTabSplitable, numberOfStrings);

		/* the splitable unfortunately starts counting from 1 */
		for (int i = 1; i <= entireTabSplitable.getNumSplits(); i++) {

		}
	}

	public int getNumStrings() {
		return numberOfStrings;
	}

	int getNumTabPortions() {
		if (stringTabsMap.size() != stringCommentsMap.size())
			Log.e("TM ZoomTabParser", "ERROR! size of the two maps are not matching up!");
		return stringTabsMap.size();
	}

	/**
	 * get the associated comment (the one that belongs at the head of the tabPortion). starts counting from 0
	 * 
	 * @param portionNumber
	 * @return the associated comment, which belongs above the tabPortion
	 */
	String getCommentForPortion(int portionNumber) {
		if (stringCommentsMap.get(portionNumber) != null)
			return stringCommentsMap.get(portionNumber);
		return "ERROR! Couldn't find comment";
	}

	/**
	 * starts counting from 0.
	 * 
	 * @param portionNumber
	 * @return the string format of the tab portion
	 */
	String getTabPortion(int portionNumber) {
		if (stringTabsMap.get(portionNumber) != null)
			return stringTabsMap.get(portionNumber);
		return "ERROR! Couldn't find portion";
	}

	private int calculateNumberOfStrings(SplitableString tabToParse) {
		/*
		 * a map to hold the results for the number of strings for every tab portion. used to make doubly sure that we
		 * got it right
		 */
		Map<Integer, Integer> numStringResults = new HashMap<Integer, Integer>(5);

		int currentTabPortion = 0;
		for (int i = 1; i <= tabToParse.getNumSplits(); i++) {

			String thisLine = tabToParse.getStringAt(i);
			/* start going through and look to see when the tab starts */
			if (percentDashes(thisLine) > .2) {
				/* if the line is more than 20% dahshes, then its probably a part of the tab */
				if (!(numStringResults.get(currentTabPortion) == null)) {
					numStringResults.put(currentTabPortion, numStringResults.get(currentTabPortion) + 1);
				} else {
					numStringResults.put(currentTabPortion, 1);
				}
			} else {
				/*
				 * otherwise, we have less than 20% dahses, so this is probably a comment. We're done counting the
				 * number of strings for this set, so lets continue on.
				 */
				if (!(numStringResults.get(currentTabPortion) == null))
					currentTabPortion++;
			}
		}

		/* debug */
		for (int i = 0; i < numStringResults.size(); i++) {
			Log.i("TM ZoomTabParser", "Portion " + i + " has " + numStringResults.get(i) + " strings");
		}

		/* calculate the average */
		float total = 0;
		for (int i = 0; i < numStringResults.size(); i++) {
			total += numStringResults.get(i);
		}
		int average = Math.round(total / ((float) numStringResults.size()));
		Log.i("TM ZoomTabParser", "Average is: " + average);

		/* find any outliers, and remove them (by setting them to the average) */
		for (int i = 0; i < numStringResults.size(); i++) {
			if (Math.abs(average - numStringResults.get(i)) >= 2) {
				Log.e("TM ZoomTabParser",
						"Found a set of stings that didn't work out when calculating the number of strings: set " + i);
				numStringResults.put(i, average);
			}
		}

		/* and recalculate the average */
		total = 0;
		for (int i = 0; i < numStringResults.size(); i++) {
			total += numStringResults.get(i);
		}
		average = Math.round(total / ((float) numStringResults.size()));
		Log.i("TM ZoomTabParser", "New average is: " + average);

		return average;
	}

	private float percentDashes(String toCountFrom) {
		int numDashes = 0;
		for (int i = 0; i < toCountFrom.length(); i++) {
			if (toCountFrom.charAt(i) == '-')
				numDashes++;
		}
		float percentDashes = ((float) numDashes) / ((float) toCountFrom.length());

		return percentDashes;
	}

	private void SetupCommentsAndTabStrings(SplitableString tabToParse, int numStrings) {
		/* before we begin, we need to clear the old stringsMap */
		stringTabsMap.clear();
		stringCommentsMap.clear();

		int currentTabPortion = 0;
		int lengthOfCurrentPortion = 0;
		for (int i = 1; i <= tabToParse.getNumSplits(); i++) {

			String thisLine = tabToParse.getStringAt(i);
			/* start going through and look to see when the tab starts */
			if ((percentDashes(thisLine) > .2)) {
				/* if the line is more than 20% dahshes, then its probably a part of the tab */
				if (!(stringTabsMap.get(currentTabPortion) == null)) {
					stringTabsMap.put(currentTabPortion, stringTabsMap.get(currentTabPortion) + '\n' + thisLine);
				} else {
					stringTabsMap.put(currentTabPortion, thisLine);
				}
				lengthOfCurrentPortion++;
			} else {
				/*
				 * otherwise, we have less than 20% dahses, so this is probably a comment. At the least, the tab portion
				 * is done. Also, we can add this comment to the comments map.
				 */
				if (stringCommentsMap.get(currentTabPortion) == null) {
					/* if its null, we need to create the comment */
					stringCommentsMap.put(currentTabPortion, thisLine);
				} else {
					/* otherwise, we need to add to the end of the comment */
					stringCommentsMap
							.put(currentTabPortion, stringCommentsMap.get(currentTabPortion) + '\n' + thisLine);
				}

				/*
				 * lets move on to the next tab portion. Notice we only need to increment currentTabPortion if we have
				 * something in the currentTabPortion map at the current position
				 */
				if (!(stringTabsMap.get(currentTabPortion) == null)) {

					if (lengthOfCurrentPortion != numStrings) {
						/*
						 * we have a problem! the length isn't right. Probably we just tried to add some text that had
						 * dashes in it to a tab portion. Lets instead put it in the comments
						 */
						stringCommentsMap.put(currentTabPortion, stringTabsMap.remove(currentTabPortion));
					}

					currentTabPortion++;

					lengthOfCurrentPortion = 0;
				}
			}
		}
		/* debug */
		for (int i = 0; i < stringTabsMap.size(); i++) {
			Log.i("TM ZoomTabParser", "Comment " + i + " is: " + stringCommentsMap.get(i));
			Log.i("TM ZoomTabParser", "Portion " + i + " is: " + stringTabsMap.get(i));
		}
	}
}

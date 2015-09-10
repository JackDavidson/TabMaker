package wolf.games.mobile.tabmaker;

import java.util.HashMap;
import java.util.Map;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.ui.activity.SimpleBaseGameActivity;

import wolf.games.mobile.shared.SharedData;
import android.graphics.Typeface;
import android.util.Log;

public class ZoomedoutTab {
	private Text commentText;
	Map<Integer, Text> tabsMap;
	Map<Integer, Text> commentsMap;
	int numberOfStrings;

	Scene scene;
	Font font;
	SimpleBaseGameActivity activity;

	/* for handling move events */
	private float downTouchX;
	private float downTouchY;
	private float originalTabPositionY;
	private float totalHeight = 0;
	private boolean isEditEvent;
	private boolean setupStillOngoing = true;

	ZoomedoutTab(SimpleBaseGameActivity activity, Scene scene, int length) {
		Font mFont = FontFactory.create(activity.getFontManager(), activity.getTextureManager(), 500, 500,
				TextureOptions.BILINEAR, Typeface.create(Typeface.MONOSPACE, Typeface.BOLD),
				SharedData.defaultFontSize / 3.2f);
		mFont.load();

		this.tabsMap = new HashMap<Integer, Text>(20);
		this.commentsMap = new HashMap<Integer, Text>(20);
		this.scene = scene;
		this.font = mFont;
		this.activity = activity;
		setupStillOngoing = true;
	}

	public int getNumStrings() {
		return numberOfStrings;
	}

	/**
	 * Strategy: The tab is parsed by ZoomTabParser. each portion and comment is then extracted and added to the scene
	 * as an andengine "Text" object
	 * side effects: moves the tab to the top of the screen
	 * 
	 * @param text
	 *            the entire text of the tab, in exported format.
	 */
	void setText(String text) {

		/* first, we need to parse the tab */
		ZoomTabParser parsedTab = new ZoomTabParser(text);
		numberOfStrings = parsedTab.getNumStrings();
		int heightOfLastTabPortion = 0;

		for (int i = 0; i < parsedTab.getNumTabPortions(); i++) {

			/*--------------------------COMMENTS-----------------------*/
			/* Handle adding the comment to the scene */
			String currCommentString = parsedTab.getCommentForPortion(i);
			if (currCommentString == null) {
				Log.e("TM ZoomedOutTab", "Error, tab comment " + i + " is null!");
				continue;
			} else {
				/* if the comment already exists, then just change its text */
				if (commentsMap.get(i) != null) {
					commentsMap.get(i).setText(currCommentString);
					commentsMap.get(i).setPosition(0, heightOfLastTabPortion);
					heightOfLastTabPortion += commentsMap.get(i).getHeight();
				} else {

					/* create the text (like a sprite, this is what will show on the screen) */
					Text currCommentText = new Text(0, heightOfLastTabPortion, font, "blah",
							currCommentString.length() + 200, activity.getVertexBufferObjectManager());
					/* and attach to scene, then set the text to what it should be */
					scene.attachChild(currCommentText);
					currCommentText.setText(currCommentString);

					/* move everything left to be created down a bit */
					heightOfLastTabPortion += currCommentText.getHeight();
					/* and finally add the comment to the map of comments */
					commentsMap.put(i, currCommentText);
				}
			}

			/*----------------------------TABS----------------------------*/
			/* And Handle adding the tab its self to the scene */
			String currPortionString = parsedTab.getTabPortion(i);
			if (currPortionString == null) {
				Log.e("TM ZoomedOutTab", "Error, tab portion " + i + " is null!");
				continue;
			} else {
				/* if the tab portion already exists, then just change its text */
				if (tabsMap.get(i) != null) {
					tabsMap.get(i).setText(currPortionString);
					tabsMap.get(i).setPosition(0, heightOfLastTabPortion);
					heightOfLastTabPortion += tabsMap.get(i).getHeight();
				} else {

					/* create the text (like a sprite, this is what will show on the screen) */
					Text currPortionText = new Text(0, heightOfLastTabPortion, font, "blah",
							currPortionString.length() + 500, activity.getVertexBufferObjectManager());
					/* and attach to scene, then set the text to what it should be */
					scene.attachChild(currPortionText);
					currPortionText.setText(currPortionString);

					/* move everything left to be created down a bit */
					heightOfLastTabPortion += currPortionText.getHeight();
					/* and finally add the tab portion to the map of tabss */
					tabsMap.put(i, currPortionText);
				}
			}
		}
		
		setupStillOngoing = true;
	}

	/**
	 * strategy: loop through everything, and move all comments and portions in a group together
	 * 
	 * @param yPosition
	 *            the position to set the top of the tab(first comment of first tab)
	 */
	void setPosition(float yPosition) {
		float maxHeight = totalHeight;
		float currentHeight = -yPosition;
		if (currentHeight < 0)
			yPosition = 0;
		else if (currentHeight > maxHeight)
			yPosition = -maxHeight;
		int yOffset = 0;

		for (int i = 0; i < tabsMap.size(); i++) {
			commentsMap.get(i).setPosition(0, yPosition + yOffset);
			yOffset += commentsMap.get(i).getHeight();
			tabsMap.get(i).setPosition(0, yPosition + yOffset);
			yOffset += tabsMap.get(i).getHeight();
		}
		totalHeight = yOffset;
	}

	/**
	 * strategy: loop through everything, and move all comments and portions in a group together
	 * 
	 * @param yPosition
	 *            the position to set the top of the tab(first comment of first tab)
	 */
	void setPositionNoChecks(float yPosition) {
		int yOffset = 0;
		for (int i = 0; i < tabsMap.size(); i++) {
			commentsMap.get(i).setPosition(0, yPosition + yOffset);
			yOffset += commentsMap.get(i).getHeight();
			tabsMap.get(i).setPosition(0, yPosition + yOffset);
			yOffset += tabsMap.get(i).getHeight();
		}
		totalHeight = yOffset;
	}

	/**
	 * 
	 * @param pSceneTouchEvent
	 *            the touchEvent to process
	 * @return 0 for nothing to do, regular number for position to return to, negative number for comment to edit
	 */
	int handleMoveTab(final TouchEvent pSceneTouchEvent, int denotationLength) {
		if (pSceneTouchEvent.isActionDown()) {
			if (setupStillOngoing)
				return 0;
			downTouchX = pSceneTouchEvent.getX();
			downTouchY = pSceneTouchEvent.getY();
			originalTabPositionY = commentsMap.get(0).getY();
			Log.i("TM ZoomedoutTab", "height of top comment: " + originalTabPositionY);
			isEditEvent = true;
		} else if (pSceneTouchEvent.isActionMove()) {
			if (setupStillOngoing)
				return 0;
			setPosition((pSceneTouchEvent.getY() - downTouchY) + originalTabPositionY);
			if (Math.abs(pSceneTouchEvent.getY() - downTouchY) > 30)
				isEditEvent = false;
		} else if (pSceneTouchEvent.isActionUp()) {
			/* setup is still ongoing until the user finishes the pinch zoom */
			if (isEditEvent && !setupStillOngoing) {
				/* get which tab was clicked on */
				isEditEvent = false;
				setupStillOngoing = false;
				return getTouchedCharacter(pSceneTouchEvent, denotationLength);
			}
			setupStillOngoing = false;
		}
		return 0;
	}

	private int getTouchedCharacter(TouchEvent pSceneTouchEvent, int denotationLength) {
		float yToLookFor = pSceneTouchEvent.getY();
		int tabPosition = 0;
		Log.i("TM ZoomedoutTab", "yToLookFor: " + yToLookFor);
		for (int i = 0; i < tabsMap.size(); i++) {
			float topOfTab = tabsMap.get(i).getY();
			float bottomOfTab = topOfTab + tabsMap.get(i).getHeight();
			Log.i("TM ZoomedoutTab", "top: " + topOfTab + "Botom: " + bottomOfTab);
			if ((yToLookFor > topOfTab) && (yToLookFor < bottomOfTab)) {
				float percentageOfPortionWidth = pSceneTouchEvent.getX() / tabsMap.get(i).getWidth();
				int XCharacterPosition = 0;
				if (pSceneTouchEvent.getX() < tabsMap.get(i).getWidth()) {
					XCharacterPosition = (int) (percentageOfPortionWidth * (float) (tabsMap.get(i).getText().length() / numberOfStrings));
				} else {
					XCharacterPosition = tabsMap.get(i).getText().length() / numberOfStrings;
				}
				return tabPosition + XCharacterPosition;
			} else if (yToLookFor > topOfTab) {
				tabPosition += tabsMap.get(i).getText().length() / numberOfStrings - denotationLength;
			} else if (yToLookFor < bottomOfTab)
				break;
		}
		return 0;
	}
}

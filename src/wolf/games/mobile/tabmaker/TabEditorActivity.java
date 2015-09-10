package wolf.games.mobile.tabmaker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.modifier.ease.EaseExponentialOut;

import wolf.games.mobile.shared.SharedData;
import wolf.games.mobile.shared.TabPortion;
import wolf.games.mobile.tabmaker.menu.MenuManager;
import wolf.games.mobile.tools.ManageableSprite;
import wolf.games.mobile.tools.SDCardWriter;
import wolf.games.mobile.tools.SplitableString;
import wolf.games.mobile.tools.SpriteManager;
import wolf.games.mobile.tools.XMLParser;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga
 * 
 * @author Nicolas Gramlich
 * @since 18:47:08 - 19.03.2010
 */
public class TabEditorActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final int width = 1280;
	static int height;

	protected static final float pxLetterWidth = 50;
	protected static final float pxselectorLetterHeight = 80;
	protected float pxStringHeight = 90;
	private static float PxPerLetter;

	private int stringPositionOffset = 0;
	// ===========================================================
	// Fields
	// ===========================================================
	private static int numTimesAppOpened = 0;

	protected SpriteManager mSpriteManager;

	private BitmapTextureAtlas mBitmapTextureAtlas;

	ManageableSprite modifyBackgroundSprite;

	protected Scene mScene;

	protected int yVal = 0;
	protected int numStrings = 13;
	private int numTextLines = 2;
	public TabObject mTabObject;
	TabPortion mTabPortion;

	protected ManageableSprite bigMove;

	protected Font mFont;
	Text elapsedText = null;

	Map<Integer, Text> stringsMap;
	private Map<Integer, Text> selectorTextMap;
	protected Map<Integer, Character> selectorMap;
	protected int activeString = 0;
	public int activeChar = 0;
	private int totalSelectables = 16;
	protected boolean selectorOpen = false;

	MenuManager mMenuManager;
	protected EditorActions currentAction = EditorActions.EDITOR;

	Line line = null;
	Rectangle mRectangle = null;
	Rectangle mSelectionRectangle = null;
	ManageableSprite selectionCircle;
	int selectionStartChar = 0;
	int selectionEndChar = 0;
	private Line charSelectLine = null;

	float lastDownX;
	float lastDownY;

	float pxToNativeRatio;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// we need a map to contain each string.

	@Override
	public EngineOptions onCreateEngineOptions() {
		SharedData.textClipboard = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		// Toast.makeText(this, "Touch the screen to add objects.",
		// Toast.LENGTH_LONG).show();
		Display display = getWindowManager().getDefaultDisplay();
		int tempWidth = display.getWidth(); // deprecated

		pxToNativeRatio = tempWidth / width;
		height = display.getHeight(); // deprecated

		float heightWidthRatio = (float) height / (float) tempWidth;
		height = (int) ((Integer) width * heightWidthRatio);

		final Camera camera = new Camera(0, 0, width, height);

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(width, height), camera);
		engineOptions.getAudioOptions().setNeedsMusic(true);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);

		if (MultiTouch.isSupported(this)) {
			if (MultiTouch.isSupportedDistinct(this)) {

			} else {
				Toast.makeText(this,
						"MultiTouch detected, but your device has problems distinguishing between fingers.\n\n(You just can't do some extra stuff)",
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)",
					Toast.LENGTH_LONG).show();
		}

		// showNotification();

		return engineOptions;

	}

	@Override
	public void onCreateResources() {

		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 128, TextureOptions.BILINEAR);
		this.mBitmapTextureAtlas.load();

		stringsMap = new HashMap<Integer, Text>(10);
		selectorTextMap = new HashMap<Integer, Text>(10);
		selectorMap = new HashMap<Integer, Character>(20);

	}

	@Override
	public Scene onCreateScene() {

		// currentAction.registerOnChangeListener(runnable)

		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(.9f, .9f, .9f));
		this.mScene.setOnSceneTouchListener(this);

		mSpriteManager = new SpriteManager(this, mScene);

		// mPuck = mSpriteManager.makeNewSprite("KnockItArt/puck.png", 0, 0, 0,
		// yVal);//134x134, at 0, 0
		// yVal += 134;

		mSelectionRectangle = new Rectangle(-200, 0, 0, 0, this.getVertexBufferObjectManager());
		mSelectionRectangle.setColor(0, 1, 0);
		mScene.attachChild(mSelectionRectangle);

		mFont = FontFactory.create(getFontManager(), getTextureManager(), 500, 500, TextureOptions.BILINEAR,
				Typeface.create(Typeface.MONOSPACE, Typeface.BOLD), SharedData.defaultFontSize);
		mFont.load();

		/*
		 * this initializes a new tabObject, with a bunch of dashes. Actual loading is handled by
		 * MenuManager.seleftafiletoload()
		 */
		mTabObject = new TabObject(numStrings, numTextLines, 50);

		// elapsedText = new Text(0, 25, mFont, defaultString, 300,
		// getVertexBufferObjectManager());
		mFont.prepareLetters('1', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'i', 'm', 'e', 'L', ' ', 'm', 't', ':',
				'.');
		// mScene.attachChild(elapsedText);

		modifyBackgroundSprite = mSpriteManager.makeNewSprite("TabMakerArt/selectionBackground.jpg", -100, 360, 200, 0)
				.attachChild();// ,200 x 200 at 0, 135
		selectionCircle = mSpriteManager.makeNewSprite("TabMakerArt/greenCircle.png", -100, 360, 0, yVal).attachChild();// ,200
																														// x
																														// 200
																														// at
																														// 0,
																														// 135
		selectionCircle.getSprite().setZIndex(1);
		// modifyBackgroundSprite.getSprite().setAlpha(.8f);
		modifyBackgroundSprite.getSprite().setZIndex(1);
		yVal += 90;
		// yVal += 720;
		mRectangle = new Rectangle(-200, 0, pxLetterWidth - 5, pxselectorLetterHeight + 4,
				this.getVertexBufferObjectManager());
		mRectangle.setColor(0, 1, 0);
		mRectangle.setZIndex(1);
		mScene.attachChild(mRectangle);
		setupSelector();

		mMenuManager = new MenuManager(mSpriteManager);
		if (SharedData.makingNewTab) {
			/*
			 * String defaultNumStrings =
			 * PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("DefaultNumStr", "6"); try{
			 * this.mTabObject.numStrings = Integer.valueOf(defaultNumStrings); }catch (Exception e){ Log.e("stuff",
			 * e.toString()); this.mTabObject.numStrings = 6; } setUpStringTexts();
			 */
			askForFileName();
		} else {
			// setUpStringTexts();
			// if(there is a recovery file with the error flag set, etc..)
			XMLParser mXMLParser = new XMLParser();
			mXMLParser.loadFile(Environment.getExternalStorageDirectory() + "/TabMaker/MyTabs/Recover.xml");
			if (mXMLParser.getElement("ExitFailedFlag") != null) {
				TabParser mTabParser = new TabParser(Environment.getExternalStorageDirectory() + "/TabMaker/MyTabs",
						"Recover.xml", 6);
				/* then pass the tab parser (which now has the parsed tab) to the tabObject */
				mTabObject.loadFromParser(mTabParser);
				/* yeah, i know this is a really bad way to give the file name to everything */
				SharedData.activeFile = mXMLParser.getElement("name");
				/* allocate the memory needed for display of the strings */
				setUpStringTexts();
				updateStrings();
			} else {
				mMenuManager.selectFileToLoad(6, mTabObject, this);
			}
		}

		return this.mScene;
	}

	protected void askForFileName() {
		mMenuManager.enterFileName(this, mTabObject);
	}

	public void setUpStringTexts() {

		int lengthOfStrings = this.mTabObject.getStringLength();
		int lengthOfTexts = 0;
		if (lengthOfStrings <= 1000) {
			lengthOfTexts = 2000;
		} else {
			lengthOfTexts = lengthOfStrings + 500;
		}

		numStrings = this.mTabObject.getNumStrings();

		String defaultString = "-";
		for (int i = 0; i < 50; i++) {
			defaultString += "-";
		}

		pxStringHeight = (height - 120) / numStrings;

		if (this.mTabObject.numStrings >= 9) {
			stringPositionOffset = -20;
		}

		for (int i = 0; i < numStrings; i++) {
			final Text stringText = new Text(0, pxStringHeight * i + stringPositionOffset, mFont, defaultString,
					lengthOfTexts, getVertexBufferObjectManager());
			stringsMap.put(i, stringText);
			mScene.attachChild(stringText);
		}
		mScene.sortChildren();
		updateStrings();
	}

	public void addSpace(int spaceAmount) {
		mTabObject.insertSpace(activeChar, spaceAmount);
		updateStrings();
	}

	protected int getSelectedCharHeight(final TouchEvent pSceneTouchEvent) {
		int selectedCharacter;
		int selectedCharHeight = 0;
		for (int i = 0; i < (totalSelectables / 2); i++) {
			if (pSceneTouchEvent.getY() > i * pxselectorLetterHeight)
				selectedCharHeight = i;
			else
				break;
		}
		if (pSceneTouchEvent.getX() < modifyBackgroundSprite.getPositionX()) {
			// going to be one of the even ones
			selectedCharacter = selectedCharHeight * 2;
		} else {
			// going to be one of the odd ones
			selectedCharacter = selectedCharHeight * 2 + 1;
		}
		return selectedCharacter;
	}

	protected void findSelectedChar(final TouchEvent pSceneTouchEvent) {
		for (int i = 0; i < mTabObject.getNumStrings(); i++) {
			if (pSceneTouchEvent.getY() > i * pxStringHeight)
				activeString = i;
			else
				break;
		}
		for (int i = 0; i < mTabObject.getStringLength(); i++) {
			if (pSceneTouchEvent.getX() - stringsMap.get(0).getX() > i * pxLetterWidth)
				activeChar = i;
			else
				break;
		}
	}

	private void setupSelector() {
		selectorMap.put(0, '0');
		selectorMap.put(1, '1');
		selectorMap.put(2, '2');
		selectorMap.put(3, '3');
		selectorMap.put(4, '4');
		selectorMap.put(5, '5');
		selectorMap.put(6, '6');
		selectorMap.put(7, '7');
		selectorMap.put(8, '8');
		selectorMap.put(9, '9');
		selectorMap.put(10, '?');
		selectorMap.put(11, 'h');
		selectorMap.put(12, 'p');
		selectorMap.put(13, 'x');
		selectorMap.put(14, '-');
		selectorMap.put(15, 'b');

		for (int i = 0; i < totalSelectables; i++) {
			if (i % 2 == 0) {
				// number is even
				final Text stringText = new Text(-200, (pxselectorLetterHeight * i) / 2, mFont,
						selectorMap.get(i).toString(), 1, getVertexBufferObjectManager());
				selectorTextMap.put(i, stringText);
				mScene.attachChild(stringText);
				stringText.setZIndex(1);
			} else {
				// number is odd
				final Text stringText = new Text(-200, (pxselectorLetterHeight * (i - 1)) / 2, mFont,
						selectorMap.get(i).toString(), 1, getVertexBufferObjectManager());
				selectorTextMap.put(i, stringText);
				mScene.attachChild(stringText);
				stringText.setZIndex(1);
			}
		}
	}

	void moveSelectorOnScreen() {
		modifyBackgroundSprite.setPosition(activeChar * pxLetterWidth + stringsMap.get(0).getX() + (pxLetterWidth / 2),
				360);
		selectorOpen = true;
		for (int i = 0; i < totalSelectables; i++) {
			Text selectorMapObject = selectorTextMap.get(i);
			if (i % 2 == 0) {
				// number is even
				selectorMapObject.setPosition(
						activeChar * pxLetterWidth + stringsMap.get(0).getX() - (pxLetterWidth / 2),
						selectorMapObject.getY());
			} else {
				selectorMapObject.setPosition(
						activeChar * pxLetterWidth + stringsMap.get(0).getX() + (pxLetterWidth / 2),
						selectorMapObject.getY());
			}
		}
		selectionCircle.setPosition((activeChar + 1) * pxLetterWidth + stringsMap.get(0).getX() - (pxLetterWidth / 2),
				activeString * pxStringHeight + 50 + stringPositionOffset);
		bigMove.setPosition(bigMove.getPositionX(), height + 500);
	}

	public void moveSelectorOffScreen() {
		selectorOpen = false;
		modifyBackgroundSprite.setPosition(-200, 320);
		for (int i = 0; i < totalSelectables; i++) {
			Text selectorMapObject = selectorTextMap.get(i);
			selectorMapObject.setPosition(-200, selectorMapObject.getY());
		}
		mRectangle.setPosition(-200, 0);
		selectionCircle.setPosition(-200, 0);

		if (!PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean("DisableBigMove", true))
			bigMove.setPosition(bigMove.getPositionX(), height - 270);
		// considerShowingAdd();
	}

	@Override
	public void onResumeGame() {
		Log.e("", "resume");
		SharedData.exitingCorrectly = false;
		Log.e("", "" + SharedData.exitingCorrectly);
		super.onResumeGame();
		/*
		 * if (new File(this.getFilesDir() + "/" + "userInfo").isFile()) { SplitableString testSplitable = new
		 * SplitableString( SDCardWriter.readFile(this.getFilesDir() + "/" + "userInfo"));
		 * testSplitable.SplitByChar(','); numTimesAppOpened = Integer.valueOf(testSplitable.getStringAt(1));
		 * numTimesAppOpened++; if (numTimesAppOpened == 7 || numTimesAppOpened == 20) { //Intent pleaseRateScreen = new
		 * Intent("wolf.games.mobile.KnockIt.PLEASERATESCREEN"); //startActivityForResult(pleaseRateScreen, 0); } }
		 * SDCardWriter.writeFile(this.getFilesDir() + "/", "userInfo", numTimesAppOpened + ",");
		 */

		SharedData.canWriteRecoveryFile = false;

	}

	public void updateStrings() {
		for (int i = 0; i < mTabObject.getNumStrings(); i++) {
			updateString(i);
		}
	}

	public void updateString(int stringNum) {
		stringsMap.get(stringNum).setText(mTabObject.getString(stringNum));
	}

	@Override
	public void onPauseGame() {
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
		Log.e("", "pause");

		Log.e("", "" + SharedData.exitingCorrectly);
		if (SharedData.canWriteRecoveryFile)
			SDCardWriter.writeFile(Environment.getExternalStorageDirectory() + "/TabMaker/MyTabs/", "Recover.xml",
					mTabObject.exportToString(true));
		super.onPauseGame();
	}

	@Override
	public void onWindowFocusChanged(boolean pHasWindowFocus) {
		super.onWindowFocusChanged(pHasWindowFocus);
	}

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {

			SharedData.exitingCorrectly = true;
			SharedData.canWriteRecoveryFile = true;
			Log.e("", "back");
			Log.e("", "" + SharedData.exitingCorrectly);
			/* otherwise, returns super down below */
			if (mTabObject.canUndo() || mTabObject.canRedo()) {
				mMenuManager.onQuit(this);
				return true;
			}
		}
		if (pKeyCode == KeyEvent.KEYCODE_MENU) {
			if (pEvent.getAction() == KeyEvent.ACTION_DOWN) {

			}
			return true;
		}
		return super.onKeyDown(pKeyCode, pEvent);
	}

	@Override
	public void onDestroy() {
		// cancelNotification(32);
		super.onDestroy();
	}

	TabEditorActivity getContext() {
		return this;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {

		return false;
	}

	/**
	 * strategy: split the entire tab
	 */
	String exportToString() {
		String entireFile = "";
		Map<Integer, String> eachLine = new HashMap<Integer, String>(100);
		int offset = 0;
		int length = 0;
		String topString = mTabObject.getString(0);
		/* loop through each character of the top most string */
		for (int x = 0; x <= topString.length(); x++) {
			/* if we are on the last character */
			if (x == topString.length()) {
				/* then go through each string */
				for (int i = 0; i < mTabObject.getNumStrings(); i++) {
					/* Now, lets add the strings to the map (the pieces from the last 'N' to the end) */
					if (!mTabObject.getString(i).substring(offset, offset + length).equals("")) {
						eachLine.put(eachLine.size(), mTabObject.getString(i).substring(offset, offset + length));
						Log.e("stuff", "added line: " + mTabObject.getString(i).substring(offset, offset + length));
					}
				}
				/* if we encounted an 'N' then its time to split into another piece */
			} else if (topString.charAt(x) == 'N') {
				/* loop through each string */
				for (int i = 0; i < mTabObject.getNumStrings(); i++) {
					/* and add everything from the last 'N' up to this 'N' to the map */
					eachLine.put(eachLine.size(), mTabObject.getString(i).substring(offset, offset + length));
					Log.e("stuff", "added line: " + mTabObject.getString(i).substring(offset, offset + length));
				}
				/* update the position of the last encountered 'N' */
				offset = length + offset + 1;
				length = 0;
			} else {
				/* otherwise, keep counting up the length of the string since the last 'N' */
				length++;
			}
		}

		for (int i = 0; i < eachLine.size(); i++) {
			/* start with the denotation on each line. grab the appropriate denotation, and add it */
			int stringNumber = (i) % mTabObject.getNumStrings();
			entireFile += mTabObject.getDenotation(stringNumber);

			Log.v("Editor Activity Exporting",
					"string: " + stringNumber + " denotation will be: " + mTabObject.getDenotation(stringNumber));

			entireFile += eachLine.get(i) + '\n';
			Log.v("stuff", "line is : " + eachLine.get(i));
			if (stringNumber == (mTabObject.getNumStrings() - 1)) {
				/* add a new line after writing each piece of the tab (after you write #strings from eachLine) */
				entireFile += '\n';
				Log.e("stuff", "added line:" + '\n');
			}
		}
		return entireFile;
	}

	private Handler handler = null;

	public void setUpDefaultStringDenotation() {
		mTabObject.setDefaultDenotation();
	}

	protected EditorActions getCA() {
		return currentAction;
	}

	protected void setCA(EditorActions currentAction) {
		this.currentAction = currentAction;
	}

}

package wolf.games.mobile.tabViewer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import wolf.games.mobile.shared.SharedData;
import wolf.games.mobile.shared.TabPortion;
import wolf.games.mobile.tabmaker.TabParser;
import wolf.games.mobile.tabmaker.menu.MenuManager;
import wolf.games.mobile.tools.ManageableSprite;
import wolf.games.mobile.tools.MathStuff;
import wolf.games.mobile.tools.SDCardWriter;
import wolf.games.mobile.tools.SplitableString;
import wolf.games.mobile.tools.SpriteManager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

/**
 * (c) 2010 Nicolas Gramlich (c) 2011 Zynga
 * 
 * @author Nicolas Gramlich
 * @since 18:47:08 - 19.03.2010
 */
public class BaseTabViewer extends SimpleBaseGameActivity implements IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	protected static final int width = 1280;
	protected static int height;
	// ===========================================================
	// Fields
	// ===========================================================

	private SpriteManager mSpriteManager;

	private BitmapTextureAtlas mBitmapTextureAtlas;

	private Scene mScene;

	private int yVal = 0;
	private int maxNumWidthCharacters = 79;
	private String mStringDenotation;

	private TabParser mTabParser;

	MenuManager mMenuManager;

	private float downEventY = 0;
	private float lastMoveEvent = 0;
	private float tabTextYBeforeMove = 0;

	private Text commentText;
	private Map<Integer, Text> tabPortionTexts;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public EngineOptions onCreateEngineOptions() {
		tabPortionTexts = new HashMap<Integer, Text>(40);
		// Toast.makeText(this, "Touch the screen to add objects.",
		// Toast.LENGTH_LONG).show();

		Display display = getWindowManager().getDefaultDisplay();
		int tempWidth = display.getWidth(); // deprecated
		height = display.getHeight(); // deprecated

		float heightWidthRatio = (float) height / (float) tempWidth;
		height = (int) ((Integer) width * heightWidthRatio);

		final Camera camera = new Camera(0, 0, width, height);
		// Toast.makeText(this, "Touch the screen to add objects.",
		// Toast.LENGTH_LONG).show();

		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
				new RatioResolutionPolicy(width, height), camera);
		engineOptions.getAudioOptions().setNeedsMusic(true);

		return engineOptions;

	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 128, TextureOptions.BILINEAR);
		this.mBitmapTextureAtlas.load();
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(.97f, .97f, .97f));
		this.mScene.setOnSceneTouchListener(this);

		mSpriteManager = new SpriteManager(this, mScene);

		mMenuManager = new MenuManager(mSpriteManager);

		// SplitableString testSplitable = new
		// SplitableString(SDCardWriter.readFile(this.getFilesDir()+"/" +
		// "userInfo"));
		// testSplitable.SplitByChar(',');
		Font mFont = FontFactory.create(getFontManager(), getTextureManager(), 500, 500, TextureOptions.BILINEAR,
				Typeface.create(Typeface.MONOSPACE, Typeface.BOLD), SharedData.defaultFontSize / 3.2f);
		mFont.load();
		if (SharedData.currentTabParser != null) {
			mTabParser = SharedData.currentTabParser;
			commentText = new Text(0, 0, mFont, mTabParser.getTabStartComment(), mTabParser.getTabStartComment()
					.length() + 20, getVertexBufferObjectManager());
			mScene.attachChild(commentText);
		} else {
			mTabParser = new TabParser();
			if (!SharedData.messageToUser.equals("")) {
				Log.e("stuff", "showing message to user");
				mMenuManager.showMessage(SharedData.messageToUser);
			}
			if (SharedData.failedToLoadWebsite) {
				// Log.e("stuff", "showing message to user");
				// mMenuManager.showMessage(SharedData.messageToUser);
				return this.mScene;
			}
			commentText = new Text(0, 0, mFont, mTabParser.getTabStartComment(), mTabParser.getTabStartComment()
					.length() + 20, getVertexBufferObjectManager());
			mScene.attachChild(commentText);
		}

		if (mTabParser.getTabPortion(0) != null) {
			mStringDenotation = mTabParser.getTabPortion(0).findStringDenotation(mTabParser.mostCommonChar);
			Log.e("stuff", "the string denotation is: " + mStringDenotation);
		}
		for (int i = 0; i < mTabParser.numTabPortions(); i++) {
			// Log.e("stuff", "we have: " + i);
			if (mTabParser.getTabPortion(i) != null) {
				mTabParser.getTabPortion(i).replaceDenotationWith(mStringDenotation, mTabParser.mostCommonChar);
			}
		}
		int currentYloc = 0;
		for (int i = 0; i < mTabParser.numTabPortions(); i++) {
			// Log.e("stuff", "we have: " + i);
			if (mTabParser.getTabPortion(i) != null) {
				if (mTabParser.getTabPortion(i - 1) != null) {
					currentYloc += tabPortionTexts.get(i - 1).getHeight() + 25;
				} else if (commentText != null) {
					currentYloc += commentText.getHeight();
				}
				tabPortionTexts.put(i, new Text(0, currentYloc, mFont, mTabParser.getTabPortion(i)
						.getEntirePortionWithNewLineAt(maxNumWidthCharacters), mTabParser.getTabPortion(i)
						.getEntirePortionWithNewLineAt(maxNumWidthCharacters).length() + 10,
						getVertexBufferObjectManager()));
				mScene.attachChild(tabPortionTexts.get(i));
			}
		}

		return this.mScene;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			downEventY = pSceneTouchEvent.getY();
			lastMoveEvent = pSceneTouchEvent.getY();
			tabTextYBeforeMove = commentText.getY();
			return true;
		}
		if (pSceneTouchEvent.isActionMove()) {
			float movementAmount = downEventY - pSceneTouchEvent.getY();
			float newPosition = tabTextYBeforeMove - movementAmount;
			commentText.setPosition(commentText.getX(), newPosition);

			for (int i = 0; i < mTabParser.numTabPortions(); i++) {
				if (tabPortionTexts.get(i) != null) {
					Text textToMove = tabPortionTexts.get(i);
					movementAmount = lastMoveEvent - pSceneTouchEvent.getY();
					newPosition = tabPortionTexts.get(i).getY() - movementAmount;
					textToMove.setPosition(textToMove.getX(), newPosition);

				}
			}

			lastMoveEvent = pSceneTouchEvent.getY();

			return true;
		}
		if (pSceneTouchEvent.isActionUp()) {
			if (Math.abs(pSceneTouchEvent.getY() - downEventY) < 35) {
				for (int i = 0; i < mTabParser.numTabPortions(); i++) {
					if (mTabParser.getTabPortion(i) != null && tabPortionTexts.get(i) != null) {
						float upPosition = pSceneTouchEvent.getY();
						if (upPosition > tabPortionTexts.get(i).getY()
								&& upPosition < tabPortionTexts.get(i).getY() + tabPortionTexts.get(i).getHeight()) {
							TabPortion tabToPlay = mTabParser.getTabPortion(i);
							tabToPlay.playSection();
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
		for (int i = 0; i < mTabParser.numTabPortions(); i++) {
			if (mTabParser.getTabPortion(i) != null) {
				TabPortion tabToPause = mTabParser.getTabPortion(i);
				tabToPause.pause();
			}
		}
		// mTabParser.getTabPortion(1).pause();
		// this.disableAccelerationSensor();
	}

	@Override
	public void onWindowFocusChanged(boolean pHasWindowFocus) {
		super.onWindowFocusChanged(pHasWindowFocus);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void saveAs(String fileName) {
		writeTabToMyTabs(fileName, mStringDenotation, mTabParser);
	}
	
	public static void writeTabToMyTabs(String fileName, String denotation, TabParser tab) {
		String track1 = "";
		SplitableString track1Splitable = new SplitableString("");
		for (int i = 0; i <= tab.getNumStrings(); i++) {
			track1Splitable.replaceString(i, "");
		}
		// we need to then compile all of it into one string.
		for (int i = 0; i < tab.numTabPortions(); i++) {
			SplitableString tmpSplitable = new SplitableString(tab.getTabPortion(i)
					.getSectionWithoutStringDenotation(tab.mostCommonChar));
			tmpSplitable.SplitByChar('\n');
			for (int x = 1; x < tmpSplitable.getNumSplits(); x++) {
				// Log.i("stuff",tmpSplitable.getStringAt(x) + "ends with " +
				// tmpSplitable.getStringAt(x).charAt(tmpSplitable.getStringAt(x).length()
				// - 1));

				if (tmpSplitable.getStringAt(x).endsWith("\r")) {
					Log.i("stuff", "ended with new line");
					tmpSplitable.replaceString(x,
							tmpSplitable.getStringAt(x).substring(0, tmpSplitable.getStringAt(x).length() - 1));
				}
				if (tmpSplitable.getStringAt(x).startsWith("\r")) {
					Log.i("stuff", "started with new line");
					tmpSplitable.replaceString(x,
							tmpSplitable.getStringAt(x).substring(1, tmpSplitable.getStringAt(x).length() - 1));
				}
				track1Splitable.replaceString(x, track1Splitable.getStringAt(x) + tmpSplitable.getStringAt(x) + "N");
			}
			// Log.i("stuff", "the tab is : " + track1Splitable.getStringAt(1));
		}
		/* start of file */
		track1 += "<xml>";
		/* add the tab its self to whats going to be written to the file */
		track1 += "<tab>";
		for (int x = 1; x <= tab.getNumStrings(); x++) {
			track1 += track1Splitable.getStringAt(x) + '\n';
			// Log.e("Stuff", "adding another\n line");
		}
		track1 += "</tab>";
		/* and now add the string denotation */
		track1 += "<denotation>";
		if (denotation != null) {
			/* How many lines is the denotation? It ought to match up with the number of strings */
			SplitableString denotationSplitable = new SplitableString(denotation);
			denotationSplitable.SplitByChar('\n');
			if (denotationSplitable.getNumSplits() == tab.getNumStrings()) {
				/* and the number of strings does match up with the number of string denotations! */
				track1 += denotation;
			} else if (denotationSplitable.getNumSplits() > tab.getNumStrings()) {
				/* too many string denotations. lets cut off the extras */
				for (int i = 1; i <= tab.getNumStrings(); i++) {
					track1 += denotationSplitable.getStringAt(i) + '\n';
				}
			} else {
				/* do nothing, since the string denotation is just too short. */
			}
		}
		track1 += "</denotation>";
		/* end of the file */
		track1 += "</xml>";
		if (!fileName.endsWith(".xml") && !fileName.endsWith(".XML")) {
			fileName += ".xml";
		}
		SDCardWriter.writeFile(Environment.getExternalStorageDirectory() + "/TabMaker/MyTabs/", fileName, track1);
	}
	
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

package wolf.games.mobile.tabmaker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import wolf.games.mobile.shared.SharedData;
import wolf.games.mobile.shared.TabMakerSounds;
import wolf.games.mobile.tabmaker.menu.MenuManager;
import wolf.games.mobile.tools.ManageableSprite;
import wolf.games.mobile.tools.MathStuff;
import wolf.games.mobile.tools.SDCardWriter;
import wolf.games.mobile.tools.SplitableString;
import wolf.games.mobile.tools.SpriteManager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

//import com.levien.synthesizer.R;
import com.levien.synthesizer.android.service.SynthesizerThread;
import com.levien.synthesizer.core.model.composite.MultiChannelSynthesizer;
import com.levien.synthesizer.core.music.Note;
import com.levien.synthesizer.core.soundfont.SoundFontReader;

public class MainMenu extends SimpleBaseGameActivity implements IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int width = 1280;
	private static int height;
	private static int fourthOfHeight;
	// ===========================================================
	// Fields
	// ===========================================================
	private static int numTimesAppOpened = 0;

	private SpriteManager mSpriteManager;

	private BitmapTextureAtlas mBitmapTextureAtlas;

	private Scene mScene;

	public TabMakerSounds mSounds = null;

	private int yVal = 0;
	private int yVal2 = 0;
	private ManageableSprite qrCode;

	MenuManager mMenuManager;
	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	MultiChannelSynthesizer testSynth;
	SynthesizerThread synthesizerThread;

	@Override
	public EngineOptions onCreateEngineOptions() {
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
		// engineOptions.getAudioOptions().setNeedsMusic(true);

		fourthOfHeight = height / 4;

		return engineOptions;

	}

	@Override
	public void onCreateResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 128, TextureOptions.BILINEAR);
		this.mBitmapTextureAtlas.load();

		// GlobalVariables.setKnockSound(mKnockSound);
		// GlobalVariables.setApplauseSound(mApplauseSound);
		// GlobalVariables.setMoanSound(mMoanSound);

		// this next line is what enables the music.
		// GlobalVariables.setSoundTrack(mMusic);
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(.9f, .9f, .9f));
		this.mScene.setOnSceneTouchListener(this);

		mSpriteManager = new SpriteManager(this, mScene);

		mMenuManager = new MenuManager(mSpriteManager);

		mSpriteManager.makeNewSprite("TabMakerArt/guitarBack.png", 640, height / 2, 400, yVal2).attachChild();
		yVal2 += 900;

		// mPuck = mSpriteManager.makeNewSprite("KnockItArt/puck.png", 0, 0, 0,
		// yVal);//134x134, at 0, 0
		// yVal += 134;
		final ManageableSprite newGame = mSpriteManager.makeNewSprite("TabMakerArt/newTab.png", 350, fourthOfHeight
				- fourthOfHeight / 2, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 150;

		newGame.attachChild();
		newGame.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = newGame.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					SharedData.makingNewTab = true;
					Intent moreScreen = new Intent("wolf.games.mobile.tabmaker.TABEDITORACTIVITY");
					startActivityForResult(moreScreen, 0);
					break;
				case TouchEvent.ACTION_MOVE:
					break;
				case TouchEvent.ACTION_UP:
					break;
				}
			}
		});
		final ManageableSprite loadGame = mSpriteManager.makeNewSprite("TabMakerArt/editTab.png", 250, fourthOfHeight
				* 2 - fourthOfHeight / 2, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 150;
		loadGame.attachChild();
		loadGame.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = loadGame.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					SharedData.makingNewTab = false;
					Intent moreScreen = new Intent("wolf.games.mobile.tabmaker.TABEDITORACTIVITY");
					startActivityForResult(moreScreen, 0);
					break;
				case TouchEvent.ACTION_MOVE:
					break;
				case TouchEvent.ACTION_UP:
					break;
				}
			}
		});
		final ManageableSprite startLevelEditor = mSpriteManager.makeNewSprite("TabMakerArt/emailTab.png", 250,
				fourthOfHeight * 3 - fourthOfHeight / 2, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 150;
		startLevelEditor.attachChild();
		final MainMenu mMainMenu = this;
		startLevelEditor.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = startLevelEditor.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					mMenuManager.selectAFileToSend(mMainMenu);
					/*
					 * Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
					 * emailIntent.setType("text/plain"); emailIntent.putExtra(android .content.Intent.EXTRA_SUBJECT,
					 * SharedData.activeFile); emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					 * "I'm sending this tab from Tab Maker for Android."); emailIntent.putExtra(Intent.EXTRA_STREAM,
					 * Uri.fromFile(new File (Environment.getExternalStorageDirectory() + "/TabMaker/MyTabs/"+
					 * SharedData.activeFile))); startActivity(emailIntent);
					 */
					break;
				case TouchEvent.ACTION_MOVE:
					break;
				case TouchEvent.ACTION_UP:
					break;
				}
			}
		});
		final ManageableSprite loadCustomLevel = mSpriteManager.makeNewSprite("TabMakerArt/getTabs.png", 370,
				fourthOfHeight * 4 - fourthOfHeight / 2, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 150;
		loadCustomLevel.attachChild();
		loadCustomLevel.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = loadCustomLevel.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					mMenuManager.HowToGetTabs(getContext());
					break;
				case TouchEvent.ACTION_MOVE:
					break;
				case TouchEvent.ACTION_UP:
					break;
				}
			}
		});
		final ManageableSprite shareApp = mSpriteManager.makeNewSprite("TabMakerArt/shareApp.png", 900, fourthOfHeight
				- fourthOfHeight / 2, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 150;
		shareApp.attachChild();
		shareApp.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = shareApp.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
					emailIntent.setType("text/plain");
					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "WGM Tab Maker");
					emailIntent
							.putExtra(
									android.content.Intent.EXTRA_TEXT,
									"Tab Maker is an Android app I've been using for making tableature. https://play.google.com/store/apps/developer?id=Wolf+Games+Mobile");
					startActivity(emailIntent);
					break;
				case TouchEvent.ACTION_MOVE:
					break;
				case TouchEvent.ACTION_UP:
					break;
				}
			}
		});
		final ManageableSprite follow = mSpriteManager.makeNewSprite("KnockItArt/twitterBtn.png", 1210, height - 45, 0,
				yVal);// ,200
						// x
						// 200
						// at
						// 0,
						// 135
		yVal += 80;
		follow.attachChild();
		follow.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = follow.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					Intent intent = null;
					try {
						// get the Twitter app if possible
						getPackageManager().getPackageInfo("com.twitter.android", 0);
						intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=1368271321"));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					} catch (Exception e) {
						// no Twitter app, revert to browser
						intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/WolfGamesMobile"));
					}
					startActivity(intent);
					break;
				case TouchEvent.ACTION_MOVE:
					break;
				case TouchEvent.ACTION_UP:
					break;
				}
			}
		});

		final ManageableSprite forums = mSpriteManager.makeNewSprite("TabMakerArt/forums.png", 870, height - 70, 0,
				yVal);
		yVal += 120;
		forums.attachChild();
		forums.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = forums.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					mMenuManager.displayForumsMenu(getContext());
					break;
				case TouchEvent.ACTION_MOVE:
					break;
				case TouchEvent.ACTION_UP:
					break;
				}
			}
		});

		/*
		 * final ManageableSprite qrBtn = mSpriteManager.makeNewSprite("KnockItArt/qrBtn.png", 1149, height - 135, 0,
		 * yVal);//,200 x 200 at 0, 135 yVal += 80; qrBtn.attachChild(); qrBtn.registerTouchArea(new Runnable(){
		 * 
		 * @Override public void run() { TouchEvent mTouchEvent = qrBtn.getTouchEvent(); switch(mTouchEvent.getAction())
		 * { case TouchEvent.ACTION_DOWN:
		 * 
		 * qrCode.attachChild(); qrCode.registerTouchArea(new Runnable(){
		 * 
		 * @Override public void run() { TouchEvent mTouchEvent = qrCode.getTouchEvent();
		 * switch(mTouchEvent.getAction()) { case TouchEvent.ACTION_DOWN: qrCode.detachSelf();
		 * qrCode.unregisterTouchArea(); break; case TouchEvent.ACTION_MOVE: break; case TouchEvent.ACTION_UP: break; }
		 * }}); break; case TouchEvent.ACTION_MOVE: break; case TouchEvent.ACTION_UP: break; } }});
		 */

		final ManageableSprite more = mSpriteManager.makeNewSprite("TabMakerArt/settings.png", 1020, fourthOfHeight * 2
				- fourthOfHeight / 2, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 150;
		more.attachChild();
		more.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = more.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					mMenuManager.settings(mMainMenu);
					break;
				case TouchEvent.ACTION_MOVE:
					break;
				case TouchEvent.ACTION_UP:
					break;
				}
			}
		});
		final ManageableSprite tabViewer = mSpriteManager.makeNewSprite("TabMakerArt/tabViewer.png", 1000,
				fourthOfHeight * 3 - fourthOfHeight / 2, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 150;
		tabViewer.attachChild();
		tabViewer.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = tabViewer.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					mMenuManager.selectFileToViewFromExportFolder();
					break;
				case TouchEvent.ACTION_MOVE:
					break;
				case TouchEvent.ACTION_UP:
					break;
				}
			}
		});
		qrCode = mSpriteManager.makeNewSprite("KnockItArt/knockitQR.png", 670, height - fourthOfHeight * 1.5f, 400,
				yVal2);// ,200 x 200 at 0,
						// 135
		yVal2 += qrCode.getSprite().getHeight();

		// SplitableString testSplitable = new
		// SplitableString(SDCardWriter.readFile(this.getFilesDir()+"/" +
		// "userInfo"));
		// testSplitable.SplitByChar(',');

		// SharedData.textClipboard = (android.text.ClipboardManager)
		// getSystemService(CLIPBOARD_SERVICE);

		return this.mScene;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			// this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
			return true;
		}
		return false;
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();
		// yVal += 432;
		if (new File(this.getFilesDir() + "/" + "userInfo").isFile()) {
			SplitableString testSplitable = new SplitableString(SDCardWriter.readFile(this.getFilesDir() + "/"
					+ "userInfo"));
			testSplitable.SplitByChar(',');
			numTimesAppOpened = Integer.valueOf(testSplitable.getStringAt(1));
			numTimesAppOpened++;
			// mMenuManager.askUserIfHeLikesApp(this);
			if (numTimesAppOpened == 10 || numTimesAppOpened == 30 || numTimesAppOpened == 80) {
				// Intent pleaseRateScreen = new
				// Intent("wolf.games.mobile.KnockIt.PLEASERATESCREEN");
				// startActivityForResult(pleaseRateScreen, 0);
				mMenuManager.askUserIfHeLikesApp(this);
			}

			// every fifteen times the app is opened, display an add.
			else if (numTimesAppOpened % 15 == 11) {
				if (!SharedData.isFullVersion) {
					if (numTimesAppOpened % 30 == 11) {
						mMenuManager.askUserToBuyApp(this);
					}
				}
			}
			if (mSounds == null)
				attemptToLoadAudio();

			if (numTimesAppOpened % 2 == 0) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
				if (prefs.getBoolean("OfferTutorial", true))
					mMenuManager.offerTutorial(this);
			}
			Log.i("num times opened: ", numTimesAppOpened + " " + numTimesAppOpened % 15);
		} else {
			mMenuManager.offerTutorial(this);
			attemptToLoadAudio();
			
		}
		// re write the info file, or create a new one if it does not exist.

		SDCardWriter.writeFile(this.getFilesDir() + "/", "userInfo", numTimesAppOpened + ",");

		// synthesizerThread.play();
	}

	private void attemptToLoadAudio() {
		if (mSounds != null)
			return;

		int successOfLoadingSounds = 0;
		Log.e("Tab Maker sounds", "about to attempt loading sounds");
		int androidVersion = android.os.Build.VERSION.SDK_INT;
		int jellyBean = android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;
		int froyo = android.os.Build.VERSION_CODES.FROYO;
		if (androidVersion > jellyBean || androidVersion == froyo) {
			if (new File(this.getFilesDir() + "/" + "enableSounds").isFile()) {
				SplitableString testSplitable = new SplitableString(SDCardWriter.readFile(this.getFilesDir() + "/"
						+ "enableSounds"));
				testSplitable.SplitByChar(',');

				try {
					successOfLoadingSounds = Integer.valueOf(testSplitable.getStringAt(1));
				} catch (Exception e) {
					Log.e("Tab Maker sounds", "error loading sounds");
				}
				// mMenuManager.askUserIfHeLikesApp(this);
				if (successOfLoadingSounds == 1) {
					/* load up the sounds. they were successful last time. */
					Log.e("Tab Maker sounds", "success, loading sounds");
					try {
						// music:
						mSounds = new TabMakerSounds(this, false);
						SharedData.soundsManager = mSounds;
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Log.e("Tab Maker sounds", "disabling loading sounds");
					/* don't load up native. it didn't work last time */
					mSounds = new TabMakerSounds(this, true);
					SharedData.soundsManager = mSounds;
				}

				Log.i("success of sounds ", successOfLoadingSounds + "");
			} else {
				successOfLoadingSounds = 0;
				SDCardWriter.writeFile(this.getFilesDir() + "/", "enableSounds", successOfLoadingSounds + ",");

				mMenuManager.WarnOfAttemptToStartAudio(this);

			}
		} else {
			/* if its not the new version of anroid, just start it up */
			try {
				// music:
				mSounds = new TabMakerSounds(this, false);
				SharedData.soundsManager = mSounds;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// re write the info file, or create a new one if it does not exist.
	}

	private boolean isPackageInstalled(String packagename, Context context) {
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

	@Override
	public void onPauseGame() {
		// testSynth = null;
		// synthesizerThread.stop();
		// synthesizerThread = null;

		super.onPauseGame();
		// this.disableAccelerationSensor();
	}

	@Override
	public void onWindowFocusChanged(boolean pHasWindowFocus) {
		super.onWindowFocusChanged(pHasWindowFocus);
	}

	@Override
	public void onDestroy() {
		if (SharedData.soundsManager != null) {
			if (SharedData.soundsManager.androidGlue_ != null)
				SharedData.soundsManager.androidGlue_.shutdown();

			SharedData.soundsManager.androidGlue_ = null;

		}

		// synthesizerThread.play();
		Runtime.getRuntime().gc();

		super.onDestroy();
	}

	private MainMenu getContext() {
		return this;
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

package wolf.games.mobile.tabViewer;

import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import wolf.games.mobile.tabmaker.EditorActions;
import wolf.games.mobile.tabmaker.menu.MenuManager;
import wolf.games.mobile.tools.ManageableSprite;
import wolf.games.mobile.tools.SpriteManager;

public class TabViewerWithButtons extends BaseTabViewer {
	
	private Scene mScene;
	MenuManager mMenuManager;
	private SpriteManager mSpriteManager;
	private int yVal = 0;
	ManageableSprite dots;
	ManageableSprite export;
	protected EditorActions currentAction = EditorActions.DO_NOTHING;
	
	@Override
	public Scene onCreateScene() {
		mScene = super.onCreateScene();
		mSpriteManager = new SpriteManager(this, mScene);
		mMenuManager = new MenuManager(mSpriteManager);
		dots = mSpriteManager.makeNewSprite("TabMakerArt/dots.png", width - 75,
				height - 50, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 100;
		dots.attachChild();
		dots.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = dots.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					if (currentAction == EditorActions.DOTS)
						minimizeDots();
					else
						expandDots();
					updateColors();
					break;
				default:
					//do nothing
					//onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		export = mSpriteManager.makeNewSprite("TabMakerArt/export.png", width + 225,
				height - 50, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 100;
		export.attachChild();
		final BaseTabViewer mBaseTabViewer = this;
		export.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = export.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					minimizeDots();
					mMenuManager.exportFromViewer(mBaseTabViewer);
					updateColors();
					break;
				default:
					//do nothing
					//onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		return mScene;
	}

	protected void minimizeDots() {
		export.setPosition(width + 225, export.getPositionY());
		currentAction = EditorActions.EDITOR;
	}

	protected void expandDots() {
		export.setPosition(width - 225, export.getPositionY());
		currentAction = EditorActions.DOTS;
	}

	protected void updateColors() {
		// TODO Auto-generated method stub
		
	}

}

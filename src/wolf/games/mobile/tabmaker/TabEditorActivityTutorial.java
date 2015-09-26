package wolf.games.mobile.tabmaker;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;

import wolf.games.mobile.shared.SharedData;
import android.graphics.Typeface;
import android.util.Log;

public class TabEditorActivityTutorial extends TabEditorActivityWithButtons {

	private enum TutorialStage {
		INTRO, SELECT_NUMBER, INSERT_CHORD_A, INSERT_CHORD_B, INSERT_CHORD_C, INSERT_CHORD_D, INSERT_BRAKE, INSERT_NEW_L, MAKE_SELECTION_A, MAKE_SELECTION_B, MAKE_SELECTION_C, MAKE_SELECTION_D, CUT, PASTE, PINCH_OUT, RETURN_TO_EDITOR, PLAY, HIT_DOTS, MAKE_SELECTION_E, SAVE, DONE
	}

	TutorialStage currentStage = TutorialStage.INTRO;
	Rectangle textSpace = null;
	Text tutorialText = null;

	@Override
	public Scene onCreateScene() {
		mScene = super.onCreateScene();
		return mScene;
	}

	@Override
	public void setUpStringTexts() {
		super.setUpStringTexts();

		textSpace = new Rectangle(0, 0, 1800, 55, this.getVertexBufferObjectManager());
		textSpace.setColor(0, 1, 0);
		textSpace.setAlpha(0.8f);
		mScene.attachChild(textSpace);

		Font tutorialFont = FontFactory.create(getFontManager(), getTextureManager(), 500, 500, TextureOptions.BILINEAR,
				Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 50f);
		tutorialFont.load();

		tutorialText = new Text(0, 0, tutorialFont, "Welcome! Tap A Dash below.", 200, getVertexBufferObjectManager());

		mScene.attachChild(tutorialText);

	}

	float pSceneTouchX = 0;

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			switch (currentStage) {
			case INTRO:
				if (currentAction == EditorActions.EDITOR) {
					tutorialText.setText("Next, select a number from below.");
					this.currentStage = TutorialStage.SELECT_NUMBER;
				}
				break;
			case MAKE_SELECTION_B:
				if (currentAction != EditorActions.SELECT) {
					tutorialText.setText("Hit Select.");
					this.currentStage = TutorialStage.MAKE_SELECTION_A;
				}
				break;
			case INSERT_CHORD_B:
				if (currentAction != EditorActions.INSERT) {
					tutorialText.setText("Hit Insert.");
					this.currentStage = TutorialStage.INSERT_CHORD_B;
				}
				break;
			}

		}
		return super.onSceneTouchEvent(pScene, pSceneTouchEvent);
	}

	@Override
	protected void placeSelectionOnScene(float x) {
		if (currentStage == TutorialStage.MAKE_SELECTION_B) {
			tutorialText.setText("Wider! Make A nice wide select, Drag edges to widen.");
			this.currentStage = TutorialStage.MAKE_SELECTION_C;
		}
		super.placeSelectionOnScene(x);
	}

	@Override
	protected void onSelectionMade(float start, float end) {
		if (Math.abs(start - end) > 500 && this.currentStage == TutorialStage.MAKE_SELECTION_C) {
			tutorialText.setText("Perfect! Lets hit 'select' again.");
			this.currentStage = TutorialStage.MAKE_SELECTION_D;
		}
	}

	@Override
	protected void setCA(EditorActions currentAction) {
		switch (currentStage) {
		case MAKE_SELECTION_A:
			if (currentAction == EditorActions.SELECT) {
				tutorialText.setText("Great! Make a swipe across the screen.");
				this.currentStage = TutorialStage.MAKE_SELECTION_B;
			} else {
				tutorialText.setText("Next, lets make a selection. Hit 'Select'.");
			}
			break;
		case MAKE_SELECTION_B:
			if (currentAction != EditorActions.SELECT_READY && currentAction != EditorActions.SELECT_STARTED) {
				tutorialText.setText("Great! Make a swipe across the screen.");
				this.currentStage = TutorialStage.MAKE_SELECTION_A;
			}
			break;
		case MAKE_SELECTION_C:
			if (currentAction != EditorActions.SELECT_READY && currentAction != EditorActions.SELECT_READY_WAITING
					&& currentAction != EditorActions.SELECT_STARTED) {
				tutorialText.setText("Lets try again, hit 'select'");
				this.currentStage = TutorialStage.MAKE_SELECTION_A;
			}
			break;

		case MAKE_SELECTION_D:
			if (currentAction == EditorActions.SELECT_READY_WAITING) {
				tutorialText.setText("Now tap 'Cut'. We're almost done!");
				this.currentStage = TutorialStage.CUT;
			} else if (currentAction != EditorActions.SELECT_READY) {
				tutorialText.setText("Oops! Lets try again. Hit 'Select'.");
				this.currentStage = TutorialStage.MAKE_SELECTION_A;
			}
			break;

		case INSERT_CHORD_A:
			if (currentAction == EditorActions.INSERT) {
				tutorialText.setText("Perfect! Now tap somewhere in the tab.");
				this.currentStage = TutorialStage.INSERT_CHORD_B;
			} else {
				tutorialText.setText("Hit 'Insert'.");
			}
			break;
		case INSERT_CHORD_B:
			if (currentAction == EditorActions.INSERT_READY) {
				tutorialText.setText("Alright, and hit Insert again.");
				this.currentStage = TutorialStage.INSERT_CHORD_C;
			} else {
				tutorialText.setText("Hit 'Insert'.");
				this.currentStage = TutorialStage.INSERT_CHORD_A;
			}
			break;
		case INSERT_CHORD_C:
			if (currentAction == EditorActions.INSERT_READY_WAITING) {
				tutorialText.setText("And tap 'Chord'");
				this.currentStage = TutorialStage.INSERT_CHORD_D;
			} else {
				tutorialText.setText("Hit 'Insert'.");
				this.currentStage = TutorialStage.INSERT_CHORD_A;
			}
			break;
		case INSERT_CHORD_D:
			tutorialText.setText("Hit 'Insert'.");
			this.currentStage = TutorialStage.INSERT_CHORD_A;
			break;
		case SELECT_NUMBER:
			if (currentAction != EditorActions.EDITOR) {
				tutorialText.setText("Oops, looks like you missed. Tap A Dash below.");
				this.currentStage = TutorialStage.INTRO;
			}
			break;
		case PINCH_OUT:
			if (currentAction == EditorActions.ZOOMED_OUT) {
				tutorialText.setText("Notice tabs split in two at 'New L'. Tap a line to edit.");
				this.currentStage = TutorialStage.RETURN_TO_EDITOR;
			}
			break;
		case RETURN_TO_EDITOR:
			if (currentAction == EditorActions.EDITOR) {
				tutorialText.setText("Alright, hit '...'->'Play'");
				this.currentStage = TutorialStage.PLAY;
			}
			break;
		}
		super.currentAction = currentAction;
	}

	@Override
	protected void onInsertChordSelected() {
		if (currentStage == TutorialStage.INSERT_CHORD_D) {
			tutorialText.setText("Now that you how to insert, try an 'Insert'->'Brake'");
			this.currentStage = TutorialStage.INSERT_BRAKE;
			// tutorialText.setText("Next, lets make a selection. Hit 'Select'.");
			// this.currentStage = TutorialStage.MAKE_SELECTION_A;
		}
		super.onInsertChordSelected();
	}

	@Override
	protected void onInsertBrake() {
		if (currentStage == TutorialStage.INSERT_BRAKE) {
			tutorialText.setText("Next, Insert a newline by using 'Insert'->tap->'New L'");
			this.currentStage = TutorialStage.INSERT_NEW_L;
		}
		super.onInsertBrake();
	}

	@Override
	protected void onInsertNewLine() {
		if (currentStage == TutorialStage.INSERT_NEW_L) {
			tutorialText.setText("Perfect! Lets try a selection. Hit 'Select'.");
			this.currentStage = TutorialStage.MAKE_SELECTION_A;
		}
		super.onInsertNewLine();
	}

	@Override
	protected void onCutReleased() {
		if (currentStage == TutorialStage.CUT) {
			tutorialText.setText("Excellent. Now 'Insert->paste', and then something cool.");
			this.currentStage = TutorialStage.PASTE;
		}
	}

	@Override
	protected void onPasteReleased() {
		if (currentStage == TutorialStage.PASTE) {
			tutorialText.setText("Now, two-finger pinch-zoom out to check out the tab.");
			this.currentStage = TutorialStage.PINCH_OUT;
		}
	}

	@Override
	protected void onCharacterSelected(final TouchEvent pSceneTouchEvent, boolean isCanceling) {
		super.onCharacterSelected(pSceneTouchEvent, isCanceling);
		switch (currentStage) {
		case SELECT_NUMBER:
			if (isCanceling) {
				tutorialText.setText("Oops, looks like you missed. Tap A Dash below.");
				this.currentStage = TutorialStage.INTRO;
			} else {
				tutorialText.setText("Next, lets add a chord. Hit 'Insert'.");
				this.currentStage = TutorialStage.INSERT_CHORD_A;
			}
			break;
		}
	}
	
	@Override
	protected void expandDots() {
		super.expandDots();
		if (currentStage == TutorialStage.HIT_DOTS) {
			tutorialText.setText("OK! now, hit 'Save', then 'continue' or 'cancel'");
			this.currentStage = TutorialStage.SAVE;
		}
	}
	
	@Override
	protected void onSaveReleased() {
		super.onSaveReleased();
		if (currentStage == TutorialStage.SAVE) {
			mMenuManager.displayFinalInstructions(getContext());
			tutorialText.setText("Congrats, You're a pro! Hit your back button to exit.");
			this.currentStage = TutorialStage.DONE;
		}
	}

	@Override
	protected void onPlayHit() {
		if (currentStage == TutorialStage.PLAY) {
			tutorialText.setText("Hit 'Play' again to stop. Then, Hit the '...' button.");
			this.currentStage = TutorialStage.HIT_DOTS;
		}
		super.onPlayHit();
	}

}

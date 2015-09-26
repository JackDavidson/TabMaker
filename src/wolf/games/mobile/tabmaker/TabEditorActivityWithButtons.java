package wolf.games.mobile.tabmaker;

import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.modifier.ease.EaseExponentialOut;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import wolf.games.mobile.shared.SharedData;
import wolf.games.mobile.shared.TabPortion;
import wolf.games.mobile.tabmaker.editor.util.FingerFollower;
import wolf.games.mobile.tabmaker.R;
import wolf.games.mobile.tools.ManageableSprite;

public class TabEditorActivityWithButtons extends TabEditorActivity {

	ManageableSprite insert;
	ManageableSprite space;
	ManageableSprite paste;
	ManageableSprite newL;
	ManageableSprite repeat;
	ManageableSprite brake;
	ManageableSprite chord;

	ManageableSprite dots;
	ManageableSprite saveFile;
	ManageableSprite move;
	ManageableSprite play;
	ManageableSprite export;
	ManageableSprite eadgb;
	ManageableSprite upload;
	ManageableSprite settsTune;
	ManageableSprite plusTen;

	ManageableSprite delete;
	ManageableSprite select;
	ManageableSprite copy;
	ManageableSprite cut;
	ManageableSprite playSelection;

	ManageableSprite undo;
	ManageableSprite redo;
	ManageableSprite moveRight;
	ManageableSprite moveLeft;

	ManageableSprite moveSelectionRight;
	ManageableSprite moveSelectionLeft;
	ManageableSprite moveSelectionUp;
	ManageableSprite moveSelectionDown;

	ManageableSprite print;

	private Handler doubleClickHandler = null;
	private Handler holdHandler = null;

	ZoomedoutTab zoomedoutTab;

	@Override
	public Scene onCreateScene() {

		mScene = super.onCreateScene();
		int yVal2 = 0;

		moveLeft = mSpriteManager.makeNewSprite("TabMakerArt/arrow.png", 91, height - 50, 0, yVal);// ,200 x 200
																									// at 0, 135
		yVal += 100;
		moveLeft.attachChild();
		moveLeft.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = moveLeft.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					moveSelectorOffScreen();
					moveUpDownLeftRightOffScreen();
					if (SharedData.soundsManager != null) {
						SharedData.holdTimerRunning = true;
						SharedData.soundsManager.mBaseActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (holdHandler == null) {
									holdHandler = new Handler();
								}
								holdHandler.removeCallbacksAndMessages(null);
								final Runnable r = new Runnable() {
									public void run() {
										SharedData.holdTimerRunning = false;
									}
								};
								holdHandler.postDelayed(r, 500);
							}
						});
					}
					keepMoving = true;
					startMoving(-2);
					break;
				case TouchEvent.ACTION_UP:
					keepMoving = false;
					if (holdMoveHandler != null)
						holdMoveHandler.removeCallbacksAndMessages(null);
					if (SharedData.holdTimerRunning) {
						bigMove.setPosition(bigMove.getPositionX(), height + 500);
						for (int i = 0; i < numStrings; i++) {
							Text textToMove = stringsMap.get(i);
							textToMove.registerEntityModifier(
									new MoveModifier(.5f, textToMove.getX(), textToMove.getX() + 1040,
											textToMove.getY(), textToMove.getY(), EaseExponentialOut.getInstance()));
						}
						if (getCA() == EditorActions.SELECT_READY) {
							mSelectionRectangle.registerEntityModifier(new MoveModifier(.5f, mSelectionRectangle.getX(),
									mSelectionRectangle.getX() + 1040, mSelectionRectangle.getY(),
									mSelectionRectangle.getY(), EaseExponentialOut.getInstance()));
						}
						if (line != null) {
							line.registerEntityModifier(new MoveModifier(.5f, line.getX(), line.getX() + 1040,
									line.getY(), line.getY(), EaseExponentialOut.getInstance()));
						}
					}
					updateColors();
					break;
				case TouchEvent.ACTION_MOVE:
					// do nothing
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
					break;
				}
			}
		});

		moveRight = mSpriteManager.makeNewSprite("TabMakerArt/arrow.png", 1188, height - 50, 0, yVal);// ,200
																										// x
																										// 200
																										// at
																										// 0,
																										// 135
		moveRight.setRotation(180);
		yVal += 100;
		moveRight.attachChild();
		moveRight.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = moveRight.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					moveSelectorOffScreen();
					moveUpDownLeftRightOffScreen();
					if (SharedData.soundsManager != null) {
						SharedData.holdTimerRunning = true;
						SharedData.soundsManager.mBaseActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (holdHandler == null) {
									holdHandler = new Handler();
								}
								holdHandler.removeCallbacksAndMessages(null);
								final Runnable r = new Runnable() {
									public void run() {
										SharedData.holdTimerRunning = false;
									}
								};
								holdHandler.postDelayed(r, 500);
							}
						});
					}
					keepMoving = true;
					startMoving(2);
					break;
				case TouchEvent.ACTION_UP:
					keepMoving = false;
					if (holdMoveHandler != null)
						holdMoveHandler.removeCallbacksAndMessages(null);
					if (SharedData.holdTimerRunning) {
						bigMove.setPosition(bigMove.getPositionX(), height + 500);
						for (int i = 0; i < numStrings; i++) {
							Text textToMove = stringsMap.get(i);
							textToMove.registerEntityModifier(
									new MoveModifier(.5f, textToMove.getX(), textToMove.getX() - 1040,
											textToMove.getY(), textToMove.getY(), EaseExponentialOut.getInstance()));
						}
						if (getCA() == EditorActions.SELECT_READY) {
							mSelectionRectangle.registerEntityModifier(new MoveModifier(.5f, mSelectionRectangle.getX(),
									mSelectionRectangle.getX() - 1040, mSelectionRectangle.getY(),
									mSelectionRectangle.getY(), EaseExponentialOut.getInstance()));
						}
						if (line != null) {
							line.registerEntityModifier(new MoveModifier(.5f, line.getX(), line.getX() - 1040,
									line.getY(), line.getY(), EaseExponentialOut.getInstance()));
						}
					}
					updateColors();
					break;
				case TouchEvent.ACTION_MOVE:
					// do nothing
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
					break;
				}
			}
		});

		saveFile = mSpriteManager.makeNewSprite("TabMakerArt/save.png", 500, height + 100, 0, yVal);// ,200 x 200 at 0,
																									// 135
		yVal += 100;
		saveFile.attachChild();
		saveFile.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = saveFile.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					minimizeDots();
					mTabObject.saveFile(mMenuManager);
					updateColors();
					onSaveReleased();
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		plusTen = mSpriteManager.makeNewSprite("TabMakerArt/plus10.png", 500, height + 100, 300, yVal2);// ,200 x 200 at
																										// 0, 135
		yVal2 += 100;
		plusTen.attachChild();
		plusTen.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = plusTen.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					mTabObject.insertSpace(mTabObject.getStringLength(), 10);
					updateStrings();
					minimizeDots();
					updateColors();
					onPlusTenReleased();
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		move = mSpriteManager.makeNewSprite("TabMakerArt/move.png", 500, height + 100, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 130;
		move.attachChild();
		move.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = move.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					if (getCA() != EditorActions.MOVE) {
						minimizeDots();
						if (mTabObject.lastHistoryIsSingleReplace() || mTabObject.lastHistoryIsMove()) {
							minimizeDots();
							move.setPosition(move.getPositionX(), height - 165);
							setCA(EditorActions.MOVE);
							lastDownX = -1;
							lastDownY = -1;
							updateColors();
						}
					} else {
						minimizeDots();
						updateColors();
					}
					onLittleMovePressed();
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});

		bigMove = mSpriteManager.makeNewSprite("TabMakerArt/bigMove.png", 180, height + 500, 300, yVal2);// ,200 x 200
																											// at 0, 135
		yVal2 += 301;
		bigMove.getSprite().setZIndex(1);
		// yVal += 300;
		bigMove.attachChild();
		bigMove.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = bigMove.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					if (getCA() != EditorActions.MOVE) {
						// minimizeDots();
						if (mTabObject.lastHistoryIsSingleReplace() || mTabObject.lastHistoryIsMove()) {
							// minimizeDots();
							// move.setPosition(move.getPositionX(), height -
							// 165);
							setCA(EditorActions.MOVE);
							lastDownX = -1;
							lastDownY = -1;
							updateColors();
						}
					} else {
						minimizeDots();
						updateColors();
					}
					break;
				case TouchEvent.ACTION_UP:
					if (getCA() == EditorActions.MOVE) {
						bigMove.setPosition(bigMove.getPositionX(), height + 500);
						minimizeDots();
						updateColors();
					}
					onBigMoveReleased();
					break;
				}
			}
		});
		play = mSpriteManager.makeNewSprite("TabMakerArt/play.png", 500, height + 100, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 100;
		play.attachChild();
		play.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = play.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					if (getCA() != EditorActions.PLAY) {
						onPlayHit();
						startFollowingPlayback();
						updateColors();
					} else {
						if (mTabPortion != null) {
							mTabPortion.pause();
						}
						if (line != null) {
							line.detachSelf();
							line = null;
						}
						minimizeDots();
						updateColors();
					}
					break;
				default:
					break;
				// onSceneTouchEvent(null, mTouchEvent);
				}
			}

			Handler handler;
			int currentPlayPosition = 0;

			private int getIntPreference(String prefString, SharedPreferences prefs, String defaultSetting) {
				String regOffsetString = prefs.getString(prefString, defaultSetting);
				int prefResult = 0;
				try {
					prefResult = Integer.valueOf(regOffsetString);
				} catch (Exception e) {
					//Log.e("stuff", e.toString());
					prefResult = 0;
				}

				return prefResult;
			}

			private void startFollowingPlayback() {
				currentPlayPosition = 0;
				SharedData.notesToSkip = 0;
				SharedData.soundsManager.mBaseActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						handler = new Handler();
						final Runnable r = new Runnable() {
							public void run() {

								handler.removeCallbacksAndMessages(null);

								SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
										SharedData.soundsManager.mBaseActivity.getBaseContext());
								int delay = getIntPreference("NPM", prefs, "400");
								delay = 60000 / delay;

								if ((getCA() == EditorActions.PLAY) && (currentPlayPosition < mTabPortion.getLength()))
									handler.postDelayed(this, delay);
								else {
									/*
									 * done playing, time to get rid of that line
									 */
									if (line != null)
										line.setPosition(0, 1000);
									return;
								}
								currentPlayPosition++;

								for (int i = 0; i < numStrings; i++) {
									Text textToMove = stringsMap.get(i);
									// textToMove.setPosition(textToMove.getX()
									// - width,
									// textToMove.getY());
									textToMove.setPosition(
											textToMove.getX() - (pxLetterWidth * (SharedData.notesToSkip + 1)),
											textToMove.getY());

								}
								SharedData.notesToSkip = 0;
							}
						};

						handler.post(r);
					}
				});

			}
		});
		export = mSpriteManager.makeNewSprite("TabMakerArt/export.png", 500, height + 100, 300, yVal2);// ,200 x 200 at
																										// 0, 135
		yVal2 += 100;
		export.attachChild();
		export.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = export.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					if (getCA() == EditorActions.ZOOMED_OUT)
						mMenuManager.askExportFileAs(exportToString());
					else {
						mMenuManager.askExportFileAs(exportToString());
						minimizeDots();
						// stub. should open export menu here
						updateColors();
					}
					break;
				default:
					break;
				// onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		eadgb = mSpriteManager.makeNewSprite("TabMakerArt/eadgb.png", 650, height + 100, 300, yVal2);// ,200 x 200 at 0,
																										// 135
		yVal2 += 100;
		eadgb.attachChild();
		eadgb.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = eadgb.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					mMenuManager.editDenotation(mTabObject, 0);
					minimizeDots();
					// stub. should open export menu here
					updateColors();
					break;
				default:
					break;
				// onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		upload = mSpriteManager.makeNewSprite("TabMakerArt/upload.png", 650, height + 100, 300, yVal2);// ,200 x 200 at
																										// 0, 135
		yVal2 += 100;
		upload.attachChild();
		upload.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = upload.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:

					mTabObject.copyToClipboard("[tab]" + exportToString() + "[/tab]");
					mMenuManager.uploadFile(getContext());
					minimizeDots();
					// stub. should open export menu here
					updateColors();
					break;
				default:
					break;
				// onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		settsTune = mSpriteManager.makeNewSprite("TabMakerArt/settsTune.png", 300, height + 100, 300, yVal2);// ,200 x
																												// 200
																												// at 0,
																												// 135
		yVal2 += 150;
		settsTune.attachChild();
		settsTune.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = settsTune.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					Intent settingsActivity = new Intent("wolf.games.mobile.shared.PREFERENCES");
					startActivity(settingsActivity);
					minimizeDots();
					// stub. should open export menu here
					updateColors();
					break;
				default:
					break;
				// onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		undo = mSpriteManager.makeNewSprite("TabMakerArt/undo.png", 257, height - 50, 0, yVal);// ,200 x 200
																								// at 0, 135
		yVal += 100;
		undo.attachChild();
		undo.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = undo.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					float positionToGoTo = mTabObject.undo() * pxLetterWidth;
					int delay = 0;
					if (positionToGoTo != 0) {
						float leftSide = -stringsMap.get(1).getX();
						float rightSide = -stringsMap.get(1).getX() + width;

						if (positionToGoTo < leftSide) {
							for (int i = 0; i < numStrings; i++) {
								Text textToMove = stringsMap.get(i);
								textToMove.setPosition(-(positionToGoTo - width / 2), textToMove.getY());
							}
							delay = 200;
						} else if (positionToGoTo > rightSide) {
							for (int i = 0; i < numStrings; i++) {
								Text textToMove = stringsMap.get(i);
								textToMove.setPosition(-(positionToGoTo - width / 2), textToMove.getY());
							}
							delay = 200;
						}
					}

					bigMove.setPosition(bigMove.getPositionX(), height + 500);
					updateColors();
					delay(new Runnable() {
						@Override
						public void run() {
							updateStrings();
						}
					}, delay);
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		redo = mSpriteManager.makeNewSprite("TabMakerArt/redo.png", 423, height - 50, 0, yVal);// ,200 x 200
																								// at 0, 135
		yVal += 100;
		redo.attachChild();
		redo.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = redo.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					mTabObject.redo();
					updateStrings();
					updateColors();
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		paste = mSpriteManager.makeNewSprite("TabMakerArt/paste.png", 750, height + 100, 0, yVal);// ,200 x 200 at 0,
																									// 135
		yVal += 100;
		paste.attachChild();
		paste.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				onPasteTouched();
				TouchEvent mTouchEvent = paste.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					onPasteReleased();
					mTabObject.pasteFromClipboard(activeChar);
					updateStrings();
					minimizeInsert();
					updateColors();
					break;
				case TouchEvent.ACTION_DOWN:
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		chord = mSpriteManager.makeNewSprite("TabMakerArt/chord.png", 750, height + 100, 300, yVal2);// ,200 x 200 at 0,
																										// 135
		yVal2 += 100;
		chord.attachChild();
		chord.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = chord.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					onInsertChordSelected();
					break;
				case TouchEvent.ACTION_DOWN:
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}

		});
		newL = mSpriteManager.makeNewSprite("TabMakerArt/newL.png", 750, height + 100, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 100;
		newL.attachChild();
		newL.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = newL.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					onInsertNewLine();
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}

		});
		space = mSpriteManager.makeNewSprite("TabMakerArt/space.png", 750, height + 100, 0, yVal);// ,200 x 200 at 0,
																									// 135
		yVal += 100;
		space.attachChild();
		space.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = space.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					onSpaceReleased();
					mMenuManager.howMuchSpace(getContext());
					minimizeInsert();
					updateColors();
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		brake = mSpriteManager.makeNewSprite("TabMakerArt/brake.png", 750, height + 100, 0, yVal);// ,200 x 200 at 0,
																									// 135
		yVal += 100;
		brake.attachChild();
		brake.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = brake.getTouchEvent();
				onBrakeTouched();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					onInsertBrake();
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}

		});
		dots = mSpriteManager.makeNewSprite("TabMakerArt/dots.png", 1022, height - 50, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 100;
		dots.attachChild();
		dots.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = dots.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					moveSelectorOffScreen();
					moveUpDownLeftRightOffScreen();
					if (getCA() == EditorActions.DOTS)
						minimizeDots();
					else
						expandDots();
					updateColors();
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		insert = mSpriteManager.makeNewSprite("TabMakerArt/insert.png", 614, height - 50, 0, yVal);// ,200 x
																									// 200
																									// at 0,
																									// 135
		yVal += 100;
		insert.attachChild();
		insert.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = insert.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					onInsertPressedDown();
					switch (getCA()) {
					case INSERT:
						if (line != null) {
							line.detachSelf();
							line = null;
						}
						setCA(EditorActions.EDITOR);
						updateColors();
						break;
					case INSERT_READY:
						expandInsert(insert);
						updateColors();
						setCA(EditorActions.INSERT_READY_WAITING);
						break;
					case INSERT_READY_WAITING:
						minimizeInsert();
						updateColors();
						break;
					case EDITOR:
						moveSelectorOffScreen();
						moveUpDownLeftRightOffScreen();
						mMenuManager.makeToast("Tap where you want to insert something, then hold Insert. (disable in settings)",
								getContext());
						bigMove.setPosition(bigMove.getPositionX(), height + 500);
						setCA(EditorActions.INSERT);
						updateColors();
						break;
					}
					break;
				case TouchEvent.ACTION_UP:
					onInsertPressed();
					switch (getCA()) {
					case INSERT:
						// catch the touch event, but do nothing with it. Insert
						// has only been pressed the first time.
						break;
					case INSERT_READY:
						// minimizeInsert();
						// mMenuManager.makeToast(
						// "Insert Cancelled. Remember to hold your finger down while selecting an option.",
						// getContext());
						// updateColors();
						break;
					default:
						onSceneTouchEvent(null, mTouchEvent);
						break;
					}
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		select = mSpriteManager.makeNewSprite("TabMakerArt/select.png", 831, height - 50, 0, yVal);// ,200 x
																									// 200
																									// at 0,
																									// 135
		yVal += 100;
		select.attachChild();
		select.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = select.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					onSelectPressed();
					switch (getCA()) {
					case SELECT:
						setCA(EditorActions.EDITOR);
						updateColors();
						break;
					case SELECT_READY:
						expandSelect(select);
						setCA(EditorActions.SELECT_READY_WAITING);
						updateColors();
						break;
					case SELECT_READY_WAITING:
						minimizeSelect();
						updateColors();
						break;
					case EDITOR:
						moveSelectorOffScreen();
						moveUpDownLeftRightOffScreen();
						bigMove.setPosition(bigMove.getPositionX(), height + 500);
						setCA(EditorActions.SELECT);
						updateColors();
						break;
					}
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		delete = mSpriteManager.makeNewSprite("TabMakerArt/delete.png", 1000, height + 100, 0, yVal);// ,200 x 200 at 0,
																										// 135
		yVal += 100;
		delete.getSprite().setZIndex(1);
		delete.attachChild();
		delete.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = delete.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					onDeleteReleased();
					mTabObject.deleteSection(selectionStartChar, selectionEndChar);
					updateStrings();
					minimizeSelect();
					updateColors();
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		copy = mSpriteManager.makeNewSprite("TabMakerArt/copy.png", 1000, height + 100, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 100;
		copy.getSprite().setZIndex(1);
		copy.attachChild();
		copy.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				onCopyTouched();
				TouchEvent mTouchEvent = copy.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					onCopyReleased();
					mTabObject.copyToClipboard(selectionStartChar, selectionEndChar);
					minimizeSelect();
					updateColors();
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});

		cut = mSpriteManager.makeNewSprite("TabMakerArt/cut.png", 1000, height + 100, 0, yVal);// ,200 x 200 at 0, 135
		yVal += 100;
		cut.getSprite().setZIndex(1);
		cut.attachChild();
		cut.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = cut.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					onCutReleased();
					mTabObject.copyToClipboard(selectionStartChar, selectionEndChar);
					mTabObject.deleteSection(selectionStartChar, selectionEndChar);
					updateStrings();
					minimizeSelect();
					updateColors();
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});

		playSelection = mSpriteManager.makeNewSprite("TabMakerArt/play.png", 500, height + 100, 300, yVal2);// ,200 x
																											// 200 at 0,
																											// 135
		yVal2 += 100;
		playSelection.attachChild();
		playSelection.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = playSelection.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					if (getCA() != EditorActions.PLAY_SELECTION) {
						minimizeSelect();

						if (SharedData.isFullVersion) {
							if (mTabPortion != null) {
								mTabPortion.pause();
								mTabPortion = null;
							}
							playSelection.setPosition(playSelection.getPositionX(), height - 150);
							String tabPortionString = "";
							for (int i = 0; i < mTabObject.getNumStrings(); i++) {
								tabPortionString += mTabObject.getString(i) + '\n';
							}
							mTabPortion = new TabPortion(tabPortionString);
							mTabPortion.playSection(selectionStartChar, selectionEndChar);
							setCA(EditorActions.PLAY_SELECTION);
						} else {
							mMenuManager.tellPlaySelectionIsFullVersion(getContext());
						}

						updateColors();
					} else {
						if (mTabPortion != null) {
							mTabPortion.pause();
						}
						minimizeSelect();
						updateColors();
					}
					break;
				default:
					break;
				// onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		playSelection.getSprite().setZIndex(1);

		moveSelectionLeft = mSpriteManager.makeNewSprite("TabMakerArt/leftArrowSquare.png", -300, height - 400, 0,
				yVal);// ,200
						// x
						// 200
		// at 0, 135
		yVal += 150;
		moveSelectionLeft.attachChild();
		moveSelectionLeft.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = moveSelectionLeft.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					if (activeChar >= 1) {
						activeChar--;
						moveSelectorOnScreen();
					}
					updateColors();
					break;
				default:
					break;
				}
			}
		});
		moveSelectionLeft.getSprite().setZIndex(1);
		moveSelectionRight = mSpriteManager.makeNewSprite(moveSelectionLeft, -300, height - 400);// ,200 x 200
		moveSelectionRight.setRotation(180);
		moveSelectionRight.attachChild();
		moveSelectionRight.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = moveSelectionRight.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					if (activeChar <= (mTabObject.getStringLength() - 2)) {
						activeChar++;
						moveSelectorOnScreen();
					}
					updateColors();
					break;
				default:
					break;
				}
			}
		});
		moveSelectionRight.getSprite().setZIndex(1);
		moveSelectionDown = mSpriteManager.makeNewSprite(moveSelectionLeft, -300, height - 250);// ,200 x 200
		moveSelectionDown.setRotation(-90);
		moveSelectionDown.attachChild();
		moveSelectionDown.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = moveSelectionDown.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					if (activeString <= (mTabObject.getNumStrings() - 2)) {
						activeString++;
						moveSelectorOnScreen();
					}
					updateColors();
					break;
				default:
					break;
				}
			}
		});
		moveSelectionDown.getSprite().setZIndex(1);
		moveSelectionUp = mSpriteManager.makeNewSprite(moveSelectionLeft, -300, height - 550);// ,200 x 200
		moveSelectionUp.setRotation(90);
		moveSelectionUp.attachChild();
		moveSelectionUp.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = moveSelectionUp.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					if (activeString >= 1) {
						activeString--;
						moveSelectorOnScreen();
					}
					updateColors();
					break;
				default:
					break;
				}
			}
		});
		moveSelectionUp.getSprite().setZIndex(1);

		print = mSpriteManager.makeNewSprite("TabMakerArt/print.png", width - 100, height + 100, 300, yVal2);// ,200 x
																												// 200
																												// at 0,
		// 135
		yVal2 += 100;
		print.attachChild();
		print.registerTouchArea(new Runnable() {
			@Override
			public void run() {
				TouchEvent mTouchEvent = print.getTouchEvent();
				switch (mTouchEvent.getAction()) {
				case TouchEvent.ACTION_UP:
					if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
						doWebViewPrint();
					else
						mMenuManager.cantPrint();
					break;
				case TouchEvent.ACTION_DOWN:
					break;
				default:
					onSceneTouchEvent(null, mTouchEvent);
				}
			}
		});
		print.getSprite().setZIndex(1);
		print.getSprite().setColor(0, 1, 0);

		brake.getSprite().setColor(0, 1, 0);
		space.getSprite().setColor(0, 1, 0);
		cut.getSprite().setColor(0, 1, 0);
		copy.getSprite().setColor(0, 1, 0);
		delete.getSprite().setColor(0, 1, 0);
		moveLeft.getSprite().setColor(0, 1, 0);
		moveRight.getSprite().setColor(0, 1, 0);
		dots.getSprite().setColor(0, 1, 0);
		saveFile.getSprite().setColor(0, 1, 0);
		newL.getSprite().setColor(0, 1, 0);
		export.getSprite().setColor(0, 1, 0);
		eadgb.getSprite().setColor(0, 1, 0);
		upload.getSprite().setColor(0, 1, 0);
		settsTune.getSprite().setColor(0, 1, 0);

		updateColors();

		return mScene;
	}

	private WebView mWebView;

	private void doWebViewPrint() {
		SharedData.soundsManager.mBaseActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				// Create a WebView object specifically for printing
				WebView webView = new WebView(getContext());
				webView.setWebViewClient(new WebViewClient() {

					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						return false;
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						//Log.i("TM tabEditorActWBtns", "page finished loading " + url);
						createWebPrintJob(view);
						mWebView = null;
					}
				});

				// Generate an HTML document on the fly:
				String entireTabExported = exportToString();
				entireTabExported = entireTabExported.replace("\n", "<br/>");
				String htmlDocument = "<html><body><p style=\"font-family: monospace; white-space: nowrap;\">"
						+ entireTabExported + "</p></body></html>";
				webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

				// Keep a reference to WebView object until you pass the PrintDocumentAdapter
				// to the PrintManager
				mWebView = webView;
			}
		});
	}

	@TargetApi(19)
	private void createWebPrintJob(WebView webView) {

		// Get a PrintManager instance
		PrintManager printManager = (PrintManager) getContext().getSystemService(Context.PRINT_SERVICE);

		// Get a print adapter instance
		PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

		// Create a print job with name and adapter instance
		String jobName = getString(R.string.app_name) + " Document";
		PrintJob printJob = printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());

		// Save the job object for later status checking (unnecessary)
		// mPrintJobs.add(printJob);
	}

	private void moveUpDownLeftRightOnScreen() {
		if (modifyBackgroundSprite.getPositionX() >= width / 2) {
			moveSelectionLeft.setPosition(75, height - 400);
			moveSelectionRight.setPosition(225, height - 400);
			moveSelectionDown.setPosition(150, height - 250);
			moveSelectionUp.setPosition(150, height - 550);
		} else {
			moveSelectionLeft.setPosition(width - 225, height - 400);
			moveSelectionRight.setPosition(width - 75, height - 400);
			moveSelectionDown.setPosition(width - 150, height - 250);
			moveSelectionUp.setPosition(width - 150, height - 550);
		}
	}

	public void moveUpDownLeftRightOffScreen() {
		moveSelectionLeft.setPosition(-300, height - 400);
		moveSelectionRight.setPosition(-300, height - 400);
		moveSelectionDown.setPosition(-300, height - 250);
		moveSelectionUp.setPosition(-300, height - 550);
	}

	protected void onBigMoveReleased() {
	}

	protected void onLittleMovePressed() {
	}

	protected void onMovementMade() {
	}

	protected void onInsertPressed() {
	}

	protected void onInsertPressedDown() {
	}

	protected void onBrakeTouched() {
	}

	protected void onNewLineReleased() {
	}

	protected void onPasteReleased() {
	}

	protected void onSpaceReleased() {
	}

	protected void onBrakeReleased() {
	}

	protected void onSelectReleased() {
	}

	protected void onSelectPressed() {
	}

	protected void onSelectionMade(float start, float end) {
	}

	protected void onCopyTouched() {
	}

	protected void onCopyReleased() {
	}

	protected void onDeleteReleased() {
	}

	protected void onCutReleased() {
	}

	protected void onPasteTouched() {
	}

	protected void onSaveReleased() {
	}

	protected void onPlusTenReleased() {
	}

	void minimizeDots() {
		saveFile.setPosition(saveFile.getPositionX(), height + 200);
		move.setPosition(move.getPositionX(), height + 200);
		play.setPosition(play.getPositionX(), height + 200);
		plusTen.setPosition(plusTen.getPositionX(), height + 200);
		export.setPosition(export.getPositionX(), height + 200);
		eadgb.setPosition(eadgb.getPositionX(), height + 200);
		upload.setPosition(upload.getPositionX(), height + 200);
		settsTune.setPosition(settsTune.getPositionX(), height + 550);
		setCA(EditorActions.EDITOR);
	}

	public void minimizeInsert() {
		space.setPosition(space.getPositionX(), height + 200);
		paste.setPosition(paste.getPositionX(), height + 200);
		chord.setPosition(chord.getPositionX(), height + 200);
		newL.setPosition(newL.getPositionX(), height + 200);
		brake.setPosition(brake.getPositionX(), height + 200);
		if (line != null) {
			line.detachSelf();
			line = null;
		}
		setCA(EditorActions.EDITOR);
	}

	private void expandInsert(final ManageableSprite insert) {
		space.setPosition(insert.getPositionX(), height - 150);
		space.getSprite().setZIndex(1);
		paste.setPosition(insert.getPositionX(), height - 250);
		paste.getSprite().setZIndex(1);
		chord.setPosition(insert.getPositionX() + 150, height - 200);
		chord.getSprite().setZIndex(1);
		newL.setPosition(insert.getPositionX(), height - 350);
		newL.getSprite().setZIndex(1);
		brake.setPosition(insert.getPositionX(), height - 450);
		brake.getSprite().setZIndex(1);
		mScene.sortChildren();
	}

	@Override
	public void onPause() {
		if (mTabPortion != null) {
			mTabPortion.pause();
		}
		super.onPause();
	}

	public void minimizeSelect() {
		delete.setPosition(delete.getPositionX(), height + 200);
		copy.setPosition(copy.getPositionX(), height + 200);
		cut.setPosition(cut.getPositionX(), height + 200);
		playSelection.setPosition(cut.getPositionX(), height + 200);
		this.mSelectionRectangle.setWidth(0);
		setCA(EditorActions.EDITOR);
	}

	void expandSelect(final ManageableSprite select) {
		delete.setPosition(select.getPositionX(), height - 150);
		copy.setPosition(select.getPositionX(), height - 250);
		cut.setPosition(select.getPositionX(), height - 350);
		playSelection.setPosition(select.getPositionX(), height - 450);
	}

	protected void expandDots() {
		cancelAll();
		saveFile.setPosition(dots.getPositionX(), height - 150);
		saveFile.getSprite().setZIndex(1);
		move.setPosition(dots.getPositionX(), height - 265);
		move.getSprite().setZIndex(1);
		play.setPosition(dots.getPositionX(), height - 380);
		play.getSprite().setZIndex(1);
		plusTen.setPosition(dots.getPositionX() - 220, height - 360);
		plusTen.getSprite().setZIndex(1);
		export.setPosition(dots.getPositionX(), height - 480);
		export.getSprite().setZIndex(1);
		eadgb.setPosition(dots.getPositionX() + 150, height - 265);
		eadgb.getSprite().setZIndex(1);
		upload.setPosition(dots.getPositionX() + 150, height - 365);
		upload.getSprite().setZIndex(1);
		settsTune.setPosition(dots.getPositionX() - 220, height - 200);
		settsTune.getSprite().setZIndex(1);
		mScene.sortChildren();
		setCA(EditorActions.DOTS);

		bigMove.setPosition(bigMove.getPositionX(), height + 500);

		if (mTabPortion != null) {
			mTabPortion.pause();
			mTabPortion = null;
		}
	}

	private void cancelAll() {
		this.minimizeInsert();
		this.minimizeSelect();
	}

	public void updateColors() {
		insert.getSprite().setColor(0, 1, 0);
		select.getSprite().setColor(0, 1, 0);
		dots.getSprite().setColor(0, 1, 0);
		chord.getSprite().setColor(0, 1, 0);
		if (SharedData.textClipboard.getText() != null) {
			paste.getSprite().setColor(0, 1, 0);
			// pasteB.getSprite().setColor(0,1,0);
		}
		if (mTabObject.canUndo())
			undo.getSprite().setColor(0, 1, 0);
		else
			undo.getSprite().setColor(1, 1, 1);
		if (mTabObject.canRedo())
			redo.getSprite().setColor(0, 1, 0);
		else
			redo.getSprite().setColor(1, 1, 1);

		switch (getCA()) {
		case EDITOR:
			select.getSprite().setColor(0, 1, 0);
			insert.getSprite().setColor(0, 1, 0);
			bigMove.getSprite().setColor(0, 1, 0);
			if (activeString >= (numStrings - 1))
				moveSelectionDown.getSprite().setColor(1, 1, 1);
			else
				moveSelectionDown.getSprite().setColor(0, 1, 0);
			if (activeString <= 0)
				moveSelectionUp.getSprite().setColor(1, 1, 1);
			else
				moveSelectionUp.getSprite().setColor(0, 1, 0);
			if (activeChar >= (mTabObject.getStringLength() - 1))
				moveSelectionRight.getSprite().setColor(1, 1, 1);
			else
				moveSelectionRight.getSprite().setColor(0, 1, 0);
			if (activeChar <= 0)
				moveSelectionLeft.getSprite().setColor(1, 1, 1);
			else
				moveSelectionLeft.getSprite().setColor(0, 1, 0);
			break;
		case INSERT:
			select.getSprite().setColor(1, 1, 1);
			insert.getSprite().setColor(1, 1, 0);
			break;
		case INSERT_READY:
			select.getSprite().setColor(1, 1, 1);
			insert.getSprite().setColor(1, 1, 0);
			break;
		case INSERT_READY_WAITING:
			select.getSprite().setColor(1, 1, 1);
			insert.getSprite().setColor(1, 1, 0);
			break;
		case SELECT_READY:
			insert.getSprite().setColor(1, 1, 1);
			select.getSprite().setColor(1, 1, 0);
			break;
		case SELECT_READY_WAITING:
			insert.getSprite().setColor(1, 1, 1);
			select.getSprite().setColor(1, 1, 0);
			break;
		case SELECT:
			insert.getSprite().setColor(1, 1, 1);
			select.getSprite().setColor(1, 1, 0);
			playSelection.getSprite().setColor(0, 1, 0);
			break;
		case SELECT_STARTED:
			insert.getSprite().setColor(1, 1, 1);
			select.getSprite().setColor(1, 1, 0);
			break;
		case DO_NOTHING:
			insert.getSprite().setColor(1, 1, 1);
			select.getSprite().setColor(1, 1, 0);
			break;
		case DOTS:
			dots.getSprite().setColor(1, 1, 0);
			select.getSprite().setColor(1, 1, 1);
			insert.getSprite().setColor(1, 1, 1);
			play.getSprite().setColor(0, 1, 0);
			plusTen.getSprite().setColor(0, 1, 0);
			if (mTabObject.lastHistoryIsSingleReplace() || mTabObject.lastHistoryIsMove()) {
				move.getSprite().setColor(0, 1, 0);
			} else {
				move.getSprite().setColor(1, 1, 1);
			}
			break;
		case MOVE:
			bigMove.getSprite().setColor(1, 1, 0);
			select.getSprite().setColor(1, 1, 1);
			insert.getSprite().setColor(1, 1, 1);
			if (mTabObject.lastHistoryIsSingleReplace() || mTabObject.lastHistoryIsMove()) {
				move.getSprite().setColor(1, 1, 0);
			}
			undo.getSprite().setColor(1, 1, 1);
			redo.getSprite().setColor(1, 1, 1);
			break;
		case PLAY:
			select.getSprite().setColor(1, 1, 1);
			insert.getSprite().setColor(1, 1, 1);
			play.getSprite().setColor(1, 1, 0);
			undo.getSprite().setColor(1, 1, 1);
			redo.getSprite().setColor(1, 1, 1);
			break;
		case PLAY_SELECTION:
			select.getSprite().setColor(0, 1, 0);
			insert.getSprite().setColor(1, 1, 1);
			playSelection.getSprite().setColor(1, 1, 0);
			undo.getSprite().setColor(1, 1, 1);
			redo.getSprite().setColor(1, 1, 1);
			break;
		}
	}

	boolean keepMoving = false;
	Handler holdMoveHandler;

	private void startMoving(final int charactersPerTime) {
		if (SharedData.soundsManager != null) {
			SharedData.soundsManager.mBaseActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (holdMoveHandler == null)
						holdMoveHandler = new Handler();
					final Runnable r = new Runnable() {
						public void run() {

							holdMoveHandler.removeCallbacksAndMessages(null);

							if (keepMoving)
								holdMoveHandler.postDelayed(this, 500);
							else {
								return;
							}
							//Log.e("moving slow", "value of keepmoving: " + keepMoving);

							for (int i = 0; i < numStrings; i++) {
								Text textToMove = stringsMap.get(i);
								// textToMove.setPosition(textToMove.getX()
								// - width,
								// textToMove.getY());
								textToMove.setPosition(textToMove.getX() - (pxLetterWidth * (charactersPerTime)),
										textToMove.getY());

							}
							if (line != null) {
								line.setPosition(line.getX() - (pxLetterWidth * (charactersPerTime)), line.getY());
							}
							if (mSelectionRectangle != null && (getCA() == EditorActions.SELECT_READY)) {
								mSelectionRectangle.setPosition(
										mSelectionRectangle.getX() - (pxLetterWidth * (charactersPerTime)),
										mSelectionRectangle.getY());
							}
						}
					};
					holdMoveHandler.postDelayed(r, 500);
				}
			});
		}
	}

	int lastSingleReplaceString = 0;
	int lastSingleReplaceChar = 0;

	float initialDistanceBetweenFingers = 0;
	float xPosFinger1 = 0;
	float yPosFinger1 = 0;
	boolean multitouchEvent = false;

	private void checkForPinchMotions(final TouchEvent pSceneTouchEvent) {
		/* pich zoom detection */
		if (pSceneTouchEvent.getPointerID() > 0) {
			multitouchEvent = true;
		}
		if (pSceneTouchEvent.isActionMove() && multitouchEvent) {
			if (pSceneTouchEvent.getPointerID() == 1) {
				xPosFinger1 = pSceneTouchEvent.getX();
				yPosFinger1 = pSceneTouchEvent.getY();
			} else {
				float xDifference = Math.abs(pSceneTouchEvent.getX() - xPosFinger1);
				float yDifference = Math.abs(pSceneTouchEvent.getY() - yPosFinger1);
				float distBetweenFingers = (float) Math.sqrt((xDifference * xDifference) + (yDifference * yDifference));
				if (initialDistanceBetweenFingers == 0) {
					initialDistanceBetweenFingers = distBetweenFingers;
				}
				/* has the pinch shrunk by at least 150? */
				if ((initialDistanceBetweenFingers - distBetweenFingers) > 200) {
					/* Its a pinch zoom! its time to display the thing to select where to do to */
					//Log.i("EditorWButtons", "its a pinch zoom!");
					moveAllButtonsOffScreen();
					tabPositionBeforeZoomOut = stringsMap.get(0).getX();
					for (int i = 0; i < numStrings; i++) {
						Text textToMove = stringsMap.get(i);
						textToMove.setPosition(textToMove.getX(), textToMove.getY() + height);
					}

					String entireTabExported = exportToString();
					if (zoomedoutTab == null) {
						zoomedoutTab = new ZoomedoutTab(this, mScene, entireTabExported.length());
					}
					zoomedoutTab.setText(entireTabExported);
					mMenuManager.makeToast("Tap a spot to edit that position. (disable help in settings)", this);
				}
				//Log.i("EditorWButtons",
						//"distance change between fingers is: " + (initialDistanceBetweenFingers - distBetweenFingers));

				/* reset the multiTouchEvent stuff */
				multitouchEvent = false;
			}

			// Log.i("EditorWButtons","Pointer is: " + pSceneTouchEvent.getPointerID() + "X diff is: " + xDifference +
			// "Y diff is: " + yDifference);
		} else if (pSceneTouchEvent.isActionUp()) {
			initialDistanceBetweenFingers = 0;
			if (multitouchEvent) {
				multitouchEvent = false;
			}
		}
	}

	private void moveAllButtonsOffScreen() {
		setCA(EditorActions.ZOOMED_OUT);
		moveLeft.setPosition(moveLeft.getPositionX(), height + 100);
		undo.setPosition(undo.getPositionX(), height + 100);
		redo.setPosition(redo.getPositionX(), height + 100);
		insert.setPosition(insert.getPositionX(), height + 100);
		select.setPosition(select.getPositionX(), height + 100);
		dots.setPosition(dots.getPositionX(), height + 100);
		moveRight.setPosition(moveRight.getPositionX(), height + 100);
		moveSelectorOffScreen();
		moveUpDownLeftRightOffScreen();

		print.setPosition(width - 275, height - 50);
		export.setPosition(width - 90, height - 50);
	}

	private void moveAllButtonsOnScreen() {
		setCA(EditorActions.EDITOR);
		moveLeft.setPosition(moveLeft.getPositionX(), height - 50);
		undo.setPosition(undo.getPositionX(), height - 50);
		redo.setPosition(redo.getPositionX(), height - 50);
		insert.setPosition(insert.getPositionX(), height - 50);
		select.setPosition(select.getPositionX(), height - 50);
		dots.setPosition(dots.getPositionX(), height - 50);
		moveRight.setPosition(moveRight.getPositionX(), height - 50);

		print.setPosition(print.getPositionX(), height + 100);
		export.setPosition(dots.getPositionX(), height + 100);
	}

	FingerFollower selectorFF;
	private static final double X_DIST_FOR_CANCEL_DENOMINATOR = 14;

	private double distanceToSelector(double Xpos) {
		float rectangleXOffset = mRectangle.getWidth() / 2;
		return Math.abs(activeChar * pxLetterWidth + stringsMap.get(0).getX() + rectangleXOffset - Xpos);
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		/* for the left/right movement handler */
		keepMoving = false;
		switch (getCA()) {
		case EDITOR:
			checkForPinchMotions(pSceneTouchEvent);
			double curX = pSceneTouchEvent.getX();
			double curY = pSceneTouchEvent.getY();

			if (pSceneTouchEvent.isActionDown()) {
				if (!selectorOpen) {
					findSelectedChar(pSceneTouchEvent);
					moveSelectorOnScreen();
					moveUpDownLeftRightOnScreen();
					updateColors();

					selectorFF = new FingerFollower(curX, curY, width/30);
				} else {
					onCharacterSelected(pSceneTouchEvent, distanceToSelector(curX) > width/X_DIST_FOR_CANCEL_DENOMINATOR);
				}
				return true;
			} else if (pSceneTouchEvent.isActionMove()) {
				if (selectorOpen) {
					//Log.e("", "finger move");
					if (selectorFF.hastraveledMinDist(curX, curY)) {
						//Log.e("", "finger min dist");
					}
				}
			} else if (pSceneTouchEvent.isActionUp()) {
				if (selectorOpen) {
					//Log.e("", "finger up");
					if (selectorFF.hastraveledMinDist(curX, curY)) {
						//Log.e("", "finger min dist, finger up.");
						onCharacterSelected(pSceneTouchEvent, false);
					}
				}
			}
			break;
		case INSERT:
			if (pSceneTouchEvent.isActionDown()) {
				if (line == null) {
					findSelectedChar(pSceneTouchEvent);
					float x = activeChar * pxLetterWidth + stringsMap.get(0).getX();
					line = new Line(x, 10, x, height - 110, 5, getVertexBufferObjectManager());
					line.setColor(0, 1f, 0);
					mScene.attachChild(line);
					setCA(EditorActions.INSERT_READY);
					updateColors();
				}
			}
			break;
		case INSERT_READY:
			if (pSceneTouchEvent.isActionDown()) {
				line.detachSelf();
				findSelectedChar(pSceneTouchEvent);
				float x = activeChar * pxLetterWidth + stringsMap.get(0).getX();
				line = new Line(x, 10, x, height - 110, 5, getVertexBufferObjectManager());
				line.setColor(0, 1f, 0);
				mScene.attachChild(line);
				updateColors();
			}
			break;
		case SELECT:
			if (pSceneTouchEvent.isActionDown()) {
				findSelectedChar(pSceneTouchEvent);
				float x = activeChar * pxLetterWidth + stringsMap.get(0).getX();
				placeSelectionOnScene(x);
				setCA(EditorActions.SELECT_STARTED);
				updateColors();
			}
			break;

		case SELECT_STARTED:
			if (pSceneTouchEvent.isActionMove()) {
				findSelectedChar(pSceneTouchEvent);
				float x = activeChar * pxLetterWidth + stringsMap.get(0).getX();
				selectionEndChar = activeChar;
				int selectionCharWidth = selectionEndChar - selectionStartChar;
				resizeSelection(selectionCharWidth);
				updateColors();
			}
			if (pSceneTouchEvent.isActionUp()) {
				setCA(EditorActions.SELECT_READY);
				updateColors();
			}

			onSelectionMade(this.mSelectionRectangle.getX(),
					this.mSelectionRectangle.getX() + this.mSelectionRectangle.getWidth());

			break;

		case SELECT_READY:
			if (pSceneTouchEvent.isActionMove()) {
				findSelectedChar(pSceneTouchEvent);
				float x = activeChar * pxLetterWidth + stringsMap.get(0).getX();
				float selectionStart = this.mSelectionRectangle.getX();
				float selectionEnd = this.mSelectionRectangle.getX() + this.mSelectionRectangle.getWidth();
				float distanceToStart = Math.abs(selectionStart - x);
				float distanceToEnd = Math.abs(selectionEnd - x);
				if (distanceToStart < distanceToEnd) {
					float originalStart = mSelectionRectangle.getX();
					float originalEnd = originalStart + mSelectionRectangle.getWidth();
					float newEnd = x;
					mSelectionRectangle.setX(x);
					float newWidth = originalEnd - newEnd;
					mSelectionRectangle.setWidth(newWidth);
					selectionStartChar = activeChar;
				} else {
					float originalStart = mSelectionRectangle.getX();
					// float originalEnd = originalStart +
					// mSelectionRectangle.getWidth();
					float newEnd = x;
					float newWidth = newEnd - originalStart;
					mSelectionRectangle.setWidth(newWidth);
					selectionEndChar = activeChar;
				}
				onSelectionMade(this.mSelectionRectangle.getX(),
						this.mSelectionRectangle.getX() + this.mSelectionRectangle.getWidth());
				updateColors();
			}
			break;
		case MOVE:
			if (pSceneTouchEvent.isActionDown()) {
				lastDownX = pSceneTouchEvent.getX();
				lastDownY = pSceneTouchEvent.getY();
			}
			if (pSceneTouchEvent.isActionUp()) {
				if (lastDownX >= 0) {
					mMenuManager.makeToast("performing move.", getContext());
					float xMovement = pSceneTouchEvent.getX() - lastDownX;
					float yMovement = lastDownY - pSceneTouchEvent.getY();
					//Log.i("tab maker", xMovement + " " + yMovement);
					mTabObject.moveLastItem(xMovement, yMovement);
					updateStrings();
					// minimizeDots();
					updateColors();

					onMovementMade();
				}
			}
			break;
		case ZOOMED_OUT:
			int moveTabResult = zoomedoutTab.handleMoveTab(pSceneTouchEvent, mTabObject.getDenotation(1).length() - 1);
			if (moveTabResult > 0) {
				/* positive result indicates we need to return to the tab editor at that position */
				/* then we need to continue editing the tab */
				//Log.i("TM TabEditorWBtn", "Returning to edit tab at position: " + moveTabResult);
				for (int i = 0; i < numStrings; i++) {
					Text textToMove = stringsMap.get(i);
					textToMove.setPosition(-(pxLetterWidth * (moveTabResult - 14)), textToMove.getY() - height);
				}
				/* and finally, reset the scene */
				zoomedoutTab.setPositionNoChecks(height + 20);
				moveAllButtonsOnScreen();
			} else if (moveTabResult < 0) {
				/* a negative result means edit a comment */
			} /* else, a result of 0 means nothing to do */
			break;
		case DO_NOTHING:
			break;

		}
		return false;
	}

	protected void placeSelectionOnScene(float x) {
		selectionStartChar = activeChar;
		mSelectionRectangle.setHeight(height - 120);
		mSelectionRectangle.setWidth(0);
		this.mSelectionRectangle.setPosition(x, 0);
	}

	protected void resizeSelection(int selectionCharWidth) {
		mSelectionRectangle.setWidth(pxLetterWidth * selectionCharWidth);
	}

	protected void onCharacterSelected(final TouchEvent pSceneTouchEvent, boolean isCanceling) {
		int selectedCharacter = getSelectedCharHeight(pSceneTouchEvent);

		if (!isCanceling) {
			lastSingleReplaceString = activeString;
			lastSingleReplaceChar = activeChar;
			Character c = selectorMap.get(selectedCharacter);
			if (c == '?')
				mMenuManager.insertCustomChar(this, mTabObject, activeString, activeChar);
			else
				mTabObject.modifyChar(activeString, activeChar, selectorMap.get(selectedCharacter));
			updateString(activeString);
		}
		updateColors();
		moveSelectorOffScreen();
		moveUpDownLeftRightOffScreen();
	}

	@Override
	TabEditorActivityWithButtons getContext() {
		return this;
	}

	private void delay(final Runnable r, final int millis) {

		SharedData.soundsManager.mBaseActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Handler handler = new Handler();
				handler.postDelayed(r, millis);
			}
		});

	}

	protected void onInsertChordSelected() {
		mMenuManager.insertAChord(getContext());
		minimizeInsert();
		updateColors();
	}

	protected void onInsertBrake() {
		mTabObject.insertBrake(activeChar);
		updateStrings();
		minimizeInsert();
		updateColors();
		onBrakeReleased();
	}

	protected void onInsertNewLine() {
		mTabObject.insertNewLine(activeChar);
		updateStrings();
		minimizeInsert();
		updateColors();
		onNewLineReleased();
	}

	protected void onPlayHit() {
		minimizeDots();
		if (mTabPortion != null) {
			mTabPortion.pause();
			mTabPortion = null;
		}
		play.setPosition(play.getPositionX(), height - 150);
		String tabPortionString = "";
		for (int i = 0; i < mTabObject.getNumStrings(); i++) {
			tabPortionString += mTabObject.getString(i) + '\n';
		}
		mTabPortion = new TabPortion(tabPortionString);
		mTabPortion.playSection();

		for (int i = 0; i < numStrings; i++) {
			Text textToMove = stringsMap.get(i);
			// textToMove.setPosition(textToMove.getX() - width,
			// textToMove.getY());
			textToMove.setPosition(pxLetterWidth * 17, textToMove.getY());
		}

		/* put a bar to show where we are playing */
		float x = pxLetterWidth * 17;
		if (line != null) {
			line.detachSelf();
			line = null;
		}
		line = new Line(x, 10, x, height - 110, 5, getVertexBufferObjectManager());
		line.setColor(0, 1f, 0);
		mScene.attachChild(line);

		setCA(EditorActions.PLAY);
		/*
		 * now that we're playing, we need to start moving the view around to match
		 */

	}

	float tabPositionBeforeZoomOut;

	@Override
	public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if (pKeyCode == KeyEvent.KEYCODE_BACK && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if (getCA() == EditorActions.ZOOMED_OUT) {
				for (int i = 0; i < numStrings; i++) {
					Text textToMove = stringsMap.get(i);
					textToMove.setPosition(tabPositionBeforeZoomOut, textToMove.getY() - height);
				}
				/* and finally, reset the scene */
				zoomedoutTab.setPositionNoChecks(height + 20);
				moveAllButtonsOnScreen();
				return true;
			}
		}
		return super.onKeyDown(pKeyCode, pEvent);
	}

}

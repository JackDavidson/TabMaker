package wolf.games.mobile.tabmaker.menu;

import java.io.File;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import wolf.games.mobile.shared.SharedData;
import wolf.games.mobile.tabmaker.TabEditorActivity;
import wolf.games.mobile.tabmaker.TabEditorActivityWithButtons;
import wolf.games.mobile.tabmaker.TabObject;
import wolf.games.mobile.tabmaker.TabParser;
import wolf.games.mobile.tools.CustomAlertDialogBuilder;
import wolf.games.mobile.tools.SDCardWriter;
import wolf.games.mobile.tools.SpriteManager;

/**
 * everything in here is intended to be used in the tab editor
 * 
 * @author jack
 * 
 */
public class MenuManagerPartB extends MenuManagerPartA {

	public MenuManagerPartB(SpriteManager mSpriteManager) {
		super(mSpriteManager);
	}
	
	public void tellPlaySelectionIsFullVersion(final TabEditorActivity mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
				alert.setTitle("Donate to Tab Maker");
				alert.setMessage("\nPlaying selected sections of tablature is a donation version feature.\n\n We ask for a donation of $4.00 to support development of Tab Maker and other apps.");
				alert.setNegativeButton("Donate", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						// intent.setData(Uri.parse("market://search?q=pub:Wolf+Games+Mobile"));
						intent.setData(Uri.parse("market://details?id=wolf.games.mobile.tabmakerB"));
						mCallingActivity.startActivity(intent);
						dialog.dismiss();
					}
				});
				alert.setPositiveButton("Not now", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				alert.show();

			}
		});
	}



	public void selectFileToLoad(final int numStrings, final TabObject mTabObject,
			final TabEditorActivity mTabEditorActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				File filePath = new File(Environment.getExternalStorageDirectory() + "/TabMaker/MyTabs/");
				if (filePath.exists() && filePath.list() != null) {
					final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
					alert.setTitle("Please Select a file to load");
					final File[] fileArray = filePath.listFiles();
					Arrays.sort(fileArray);
					String[] fileStringArray = new String[fileArray.length];
					if (fileArray.length > 0) {
						for (int i = 0; i < fileArray.length; i++) {
							fileStringArray[i] = fileArray[i].toString();
						}
					}
					alert.setCancelable(false);
					// Arrays.sort(fileStringArray);
					alert.setSingleChoiceItems(fileStringArray, 0, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String filePath = fileArray[which].getParent();
							String fileName = fileArray[which].getName();
							/* create a new tab parser to read the tab */
							TabParser mTabParser = new TabParser(filePath, fileName, numStrings);
							/* then pass the tab parser (which now has the parsed tab) to the tabObject */
							mTabObject.loadFromParser(mTabParser);
							/* yeah, i know this is a really bad way to give the file name to everything */
							SharedData.activeFile = fileName;
							/* allocate the memory needed for display of the strings */
							mTabEditorActivity.setUpStringTexts();
							/* close this dialog since we got the input we needed */
							dialog.dismiss();
							/* next few lines are to delay finishing up the loading */
							Handler handler = new Handler();
							final Runnable r = new Runnable() {
								public void run() {
									/*
									 * and finally load in the new strings. needs a little delay in case loading the
									 * first time happened too early
									 */
									mTabEditorActivity.updateStrings();
								}
							};
							handler.postDelayed(r, 2000);
							/* and finally load in the new strings. */
							mTabEditorActivity.updateStrings();

							// mLevelDesignerLoader.LoadFromString(SDCardWriter.readFile(fileArray[which].getPath()));
						}
					});
					alert.setNegativeButton("Cancel/Exit", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							dialog.dismiss();
							mSpriteManager.getContext().finish();
						}
					});
					alert.show();
				} else {
					// file does not exist or is empty
					final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
					alert.setTitle("No custom Tabs found");
					alert.setMessage("there were no files found in " + filePath.toString());
					alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// close the activity
							mSpriteManager.getContext().finish();
						}
					});
					alert.show();
				}
			}
		});
	}

	public void enterFileName(final TabEditorActivity mTabEditorActivity, final TabObject mTabObject) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final EditText mEditText = new EditText(mSpriteManager.getContext());
				final View textEntryView = mEditText;
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setCancelable(false);
				alert.setTitle("Please enter a file name");
				alert.setView(textEntryView);
				alert.setMessage("You must enter a valid file name. You may end it with .xml if you wish, but it is not necessary. The file will be saved to /TabMaker/MyTabs/YOUR_FILE_NAME");
				alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mEditText.getText().toString().equals("")) {
							// do nothing, since nothing was entered
							return;
						}
						SharedData.activeFile = mEditText.getText().toString();

						String defaultNumStrings = PreferenceManager.getDefaultSharedPreferences(
								mTabEditorActivity.getBaseContext()).getString("DefaultNumStr", "6");
						try {
							mTabEditorActivity.mTabObject.numStrings = Integer.valueOf(defaultNumStrings);
						} catch (Exception e) {
							Log.e("stuff", e.toString());
							mTabEditorActivity.mTabObject.numStrings = 6;
						}
						mTabEditorActivity.setUpStringTexts();
						mTabEditorActivity.setUpDefaultStringDenotation();

						dialog.dismiss();
					}
				});
				alert.setNeutralButton("Recover", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						int numStrings = 6;
						String filePath = Environment.getExternalStorageDirectory() + "/TabMaker/MyTabs/";
						String fileName = "Recover.xml";
						TabParser mTabParser = new TabParser(filePath, fileName, numStrings);
						mTabObject.loadFromParser(mTabParser);
						SharedData.activeFile = "Recovered.xml";
						mTabEditorActivity.setUpStringTexts();
						dialog.dismiss();
						Handler handler = new Handler();
						final Runnable r = new Runnable() {
							public void run() {
								SharedData.activeFile = "Recovered.xml";
								mTabEditorActivity.updateStrings();
							}
						};
						handler.postDelayed(r, 2000);
						mTabEditorActivity.updateStrings();

						dialog.dismiss();
					}
				});
				alert.setNegativeButton("Cancel/Exit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						mSpriteManager.getContext().finish();
					}
				});
				alert.show();
			}
		});
	}

	/**
	 * same as above function, except no "recover" button. for use in the tutorial
	 * @param mTabEditorActivity
	 * @param mTabObject
	 */
	public void enterFileNameNoRecover(final TabEditorActivity mTabEditorActivity, final TabObject mTabObject) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final EditText mEditText = new EditText(mSpriteManager.getContext());
				final View textEntryView = mEditText;
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setCancelable(false);
				alert.setTitle("Please enter a file name");
				alert.setView(textEntryView);
				alert.setMessage("You must enter a valid file name. You may end it with .xml if you wish, but it is not necessary. The file will be saved to /TabMaker/MyTabs/YOUR_FILE_NAME");
				alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mEditText.getText().toString().equals("")) {
							// do nothing, since nothing was entered
							return;
						}
						SharedData.activeFile = mEditText.getText().toString();

						String defaultNumStrings = PreferenceManager.getDefaultSharedPreferences(
								mTabEditorActivity.getBaseContext()).getString("DefaultNumStr", "6");
						try {
							mTabEditorActivity.mTabObject.numStrings = Integer.valueOf(defaultNumStrings);
						} catch (Exception e) {
							Log.e("stuff", e.toString());
							mTabEditorActivity.mTabObject.numStrings = 6;
						}
						mTabEditorActivity.setUpStringTexts();

						dialog.dismiss();
					}
				});
				alert.setNegativeButton("Cancel/Exit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						mSpriteManager.getContext().finish();
					}
				});
				alert.show();
			}
		});
	}
	
	public void askSaveFileAs(final String fileContents) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final EditText mEditText = new EditText(mSpriteManager.getContext());
				mEditText.setText(SharedData.activeFile);
				final View textEntryView = mEditText;
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setTitle("Please enter a file name");
				alert.setView(textEntryView);
				alert.setMessage("You must enter a valid file name. You may end it with .txt if you wish, but it is not necessary. The file will be saved to /TabMaker/MyTabs/YOUR_FILE_NAME");
				alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mEditText.getText().toString().equals("")) {
							// do nothing, since nothing was entered
							return;
						}
						SharedData.activeFile = mEditText.getText().toString();
						if (!SharedData.activeFile.endsWith(".xml") && !SharedData.activeFile.endsWith(".XML")) {
							SharedData.activeFile += ".xml";
						}
						SDCardWriter.writeFile(Environment.getExternalStorageDirectory() + "/TabMaker/MyTabs/",
								SharedData.activeFile, fileContents);
						fileSaved();
						dialog.dismiss();
					}
				});
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}

	public void askExportFileAs(final String fileContents) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final EditText mEditText = new EditText(mSpriteManager.getContext());
				String activeFile = SharedData.activeFile;
				if (activeFile.endsWith(".xml") || activeFile.endsWith(".XML"))
					activeFile = activeFile.substring(0, activeFile.length() - 4);
				if (!activeFile.endsWith(".txt"))
					activeFile += ".txt";
				mEditText.setText(activeFile);
				final View textEntryView = mEditText;
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setTitle("Please enter a file name");
				alert.setView(textEntryView);
				alert.setMessage("You must enter a valid file name. You may end it with .txt if you wish, but it is not necessary. The file will be exported to /TabMaker/ExportedTabs/YOUR_FILE_NAME");
				alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mEditText.getText().toString().equals("")) {
							// do nothing, since nothing was entered
							return;
						}
						// SharedData.activeFile =
						// mEditText.getText().toString();
						SDCardWriter.writeFile(Environment.getExternalStorageDirectory() + "/TabMaker/ExportedTabs/",
								mEditText.getText().toString(), fileContents);
						fileSaved();
						try {
							String fileName = mEditText.getText().toString();
							fileName = fileName.replaceAll("[^a-zA-Z0-9./-]", "_");
							fileName = fileName.replaceAll("/", "_");
							fileName = fileName.replaceAll(" ", "_");
							SharedData.fileToTextEdit = Environment.getExternalStorageDirectory()
									+ "/TabMaker/ExportedTabs/" + fileName;
							Intent fileEditor = new Intent("org.paulmach.TEXTEDIT");
							Uri uri = Uri.parse("file://" + SharedData.fileToTextEdit);
							fileEditor.setDataAndType(uri, "text/plain");
							mSpriteManager.getContext().startActivity(fileEditor);
						} catch (Exception e) {
							e.printStackTrace();
						}
						dialog.dismiss();
					}
				});
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}

	public void editDenotation(final TabObject tabObj, final int stringNumberReversed) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final EditText mEditText = new EditText(mSpriteManager.getContext());
				mEditText.setText(tabObj.getDenotation(tabObj.getNumStrings() - (stringNumberReversed + 1)));
				final View textEntryView = mEditText;
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setTitle("String Denotation");
				alert.setView(textEntryView);
				alert.setCancelable(false);
				alert.setMessage("Here, you can enter whatever you want as your string denotation. Starts from lowest to highest (EADGBe)\n Notice! the length must be the same as your first string's denotation. (in this case "
						+ tabObj.getDenotation(tabObj.getNumStrings() - 1).length()
						+ " characters)\nEditing string number: " + (tabObj.getNumStrings() - stringNumberReversed));
				alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mEditText.getText().toString().length() != tabObj.getDenotation(tabObj.getNumStrings() - 1)
								.length() && (stringNumberReversed != 0)) {
							/* TODO: display an error message */
							return;
						} else {
							tabObj.modifyDenotation(tabObj.getNumStrings() - (stringNumberReversed + 1), mEditText
									.getText().toString());
						}
						/* only a recursive call if we haven't reached the last string */
						if ((stringNumberReversed + 1) < tabObj.getNumStrings()) {
							editDenotation(tabObj, stringNumberReversed + 1);
						}

						dialog.dismiss();
					}
				});
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						for (int i = 0; i < tabObj.getNumStrings(); i++) {
							if (tabObj.getDenotation((tabObj.getNumStrings() - i) - 1).length() > tabObj.getDenotation(
									tabObj.getNumStrings() - 1).length()) {
								/* somethings out of wack. the length is too long. lets cut off the end to make it work */
								tabObj.modifyDenotation(
										tabObj.getNumStrings() - (i + 1),
										tabObj.getDenotation((tabObj.getNumStrings() - i) - 1).substring(0,
												tabObj.getDenotation(tabObj.getNumStrings() - 1).length()));

							} else if (tabObj.getDenotation((tabObj.getNumStrings() - i) - 1).length() < tabObj
									.getDenotation(tabObj.getNumStrings() - 1).length()) {

								/*
								 * somethings out of wack. the length is too short. lets add dashes on the end to make
								 * it work.
								 */
								String newString = tabObj.getDenotation(tabObj.getNumStrings() - (i + 1));
								for (int x = 0; x < (tabObj.getDenotation(tabObj.getNumStrings() - 1).length() - tabObj
										.getDenotation(tabObj.getNumStrings() - (i + 1)).length()); x++) {
									newString += "-";
								}

								tabObj.modifyDenotation(tabObj.getNumStrings() - (i + 1), newString);
							}
						}
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}

	public void fileSaved() {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
				alert.setTitle("File Saved");
				alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
				alert.show();
			}
		});
	}
	
	public void insertCustomChar(final TabEditorActivityWithButtons context, final TabObject mTabObject,
			final int activeString, final int activeChar) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				final EditText mEditText = new EditText(mSpriteManager.getContext());
				final View textEntryView = mEditText;
				alert.setTitle("Custom character");
				alert.setView(textEntryView);
				alert.setMessage("Enter a character to replace the one you just clicked:");
				alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						context.moveSelectorOffScreen();
						context.moveUpDownLeftRightOffScreen();
						context.updateColors();
						dialog.dismiss();
					}
				});
				alert.setNegativeButton("Done", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mEditText.getText().toString().length() > 0) {
							mTabObject.modifyChar(activeString, activeChar, mEditText.getText().toString().charAt(0));
							context.moveSelectorOffScreen();
							context.moveUpDownLeftRightOffScreen();
							context.updateString(activeString);
							context.updateColors();
							dialog.dismiss();
						}
					}
				});
				alert.show();
			}
		});
	}

	/* TODO: need to finish filling in all the chords */
	String[] chordNames = { "Open Major A", "Open Major B", "Open Major C", "Open Major D", "Open Major E",
			"Open Major F", "Open Major G", "Open Minor A", "Open Minor B", "Open Minor D", "Open Minor E",
			"Open 7th A", "Open 7th B", "Open 7th C", "Open 7th D", "Open 7th E", "Open 7th G", "Repeat Left",
			"Repeat Right" };

	// open majors, then minors, then bar majors, then bar minors
	/* make sure to write from highest string to lowest string */
	String[] chords = { "0\n2\n2\n2\n0\n-\n", "-\n4\n4\n4\n2\n-\n", // OMaA,OMaB
			"0\n1\n0\n2\n3\n-\n", "2\n3\n2\n0\n-\n-\n", "0\n0\n1\n2\n2\n0\n",// OMaC,OMaD,OMaE
			"1\n1\n2\n3\n-\n-\n", "3\n3\n0\n0\n2\n3\n", "0\n1\n2\n2\n0\n-\n", // OmaF, OmaE, OminA
			"2\n3\n4\n4\n2\n-\n", "1\n3\n2\n0\n-\n-\n", "0\n0\n0\n2\n2\n0\n", // OminB, OminD, OminE
			"0\n2\n0\n2\n0\n-\n", "2\n0\n2\n1\n2\n-\n", "0\n1\n3\n2\n3\n-\n", // O7A, O7B, 07C
			"2\n1\n2\n0\n-\n-\n", "0\n0\n1\n0\n2\n0\n", "1\n0\n0\n0\n2\n3\n", // O7D, O7E, 07G
			"|-\n|-\n|*\n|*\n|-\n|-\n", "-|\n-|\n*|\n*|\n-|\n-|\n" };// OMaf,
																		// OMaG!!

	public void insertAChord(final TabEditorActivityWithButtons tabEditorActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
				// Arrays.sort(fileStringArray);
				alert.setSingleChoiceItems(chordNames, 0, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String toInsert = chords[which];
						tabEditorActivity.mTabObject.insertChord(toInsert, tabEditorActivity.activeChar);
						tabEditorActivity.updateStrings();
						tabEditorActivity.updateColors();
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}

	public void cantPrint() {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setTitle("Print Not Supported");
				alert.setMessage("Printing is supported only on android 4.4 or higher! Sorry!");
				alert.setPositiveButton("O.K.", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}
	
	public void onQuit(final TabEditorActivity context) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setTitle("Quit?");
				alert.setMessage("You may want to save first!");
				alert.setPositiveButton("Save & Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						context.mTabObject.saveFileDontAsk(mSpriteManager);
						dialog.dismiss();
					}
				});
				alert.setNeutralButton("Quit", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						mSpriteManager.getContext().finish();
					}
				});
				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}

}

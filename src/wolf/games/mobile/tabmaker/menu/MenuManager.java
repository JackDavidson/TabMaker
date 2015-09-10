package wolf.games.mobile.tabmaker.menu;

import java.io.File;
import java.util.Arrays;

import org.andengine.ui.activity.SimpleBaseGameActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import wolf.games.mobile.shared.SharedData;
import wolf.games.mobile.shared.TabMakerSounds;
import wolf.games.mobile.tabViewer.BaseTabViewer;
import wolf.games.mobile.tabmaker.MainMenu;
import wolf.games.mobile.tabmaker.TabEditorActivity;
import wolf.games.mobile.tabmaker.TabEditorActivityWithButtons;
import wolf.games.mobile.tabmaker.TabObject;
import wolf.games.mobile.tabmaker.TabParser;
import wolf.games.mobile.tools.CustomAlertDialogBuilder;
import wolf.games.mobile.tools.SpriteManager;
import wolf.games.mobile.tools.SDCardWriter;

public class MenuManager extends MenuManagerPartB {


	public MenuManager(SpriteManager mSpriteManager) {
		super(mSpriteManager);
	}



	

	public void exportFromViewer(final BaseTabViewer mTabViewer) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final EditText mEditText = new EditText(mSpriteManager.getContext());
				final View textEntryView = mEditText;
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setCancelable(false);
				alert.setTitle("Please enter a file name");
				alert.setView(textEntryView);
				alert.setMessage("You must enter a valid file name. You may end it with .txt if you wish, but it is not necessary. The file will be saved to /TabMaker/MyTabs/YOUR_FILE_NAME. You can then go to the main menu and select 'Edit Tab' to modify the tab");
				alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mEditText.getText().toString().equals("")) {
							// do nothing, since nothing was entered
							return;
						}
						mTabViewer.saveAs(mEditText.getText().toString());
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



	public void uploadFile(final SimpleBaseGameActivity callingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setCancelable(false);
				alert.setTitle("Upload Tab");
				alert.setMessage("The entire tab has been copied into your clipboard. On the next screen you will need to log into the WGM phpBB3 forums or create an account. Once thats done, simply paste the tab (again, already in your clipboard) in the body of your post.");
				alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = null;
						intent = new Intent(Intent.ACTION_VIEW, Uri
								.parse("http://wolfgamesmobile.com/phpBB3/posting.php?mode=post&f=4"));
						callingActivity.startActivity(intent);
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

	public void howMuchSpace(final TabEditorActivity mTabEditorActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final EditText mEditText = new EditText(mSpriteManager.getContext());
				final View textEntryView = mEditText;
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setCancelable(false);
				alert.setTitle("How much space to add?");
				alert.setView(textEntryView);
				alert.setMessage("Please enter an integer. No decimals or letters.");
				alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// final int spaceAmount;
						if (mEditText.getText().toString().equals("")) {
							// do nothing, since nothing was entered
							return;
						}
						try {
							SharedData.amtSpaceLastAdded = (int) Integer.parseInt(mEditText.getText().toString());
							mTabEditorActivity.addSpace(SharedData.amtSpaceLastAdded);
							// mTabEditorActivity.updateColors();
						} catch (Exception e) {

						}

						// if (stringsMap.get(0).getCharactersMaximum()
						// < stringsMap.get(0).getText().length() +
						// spaceAmount){
						// incrementStringMaxWidth(5);
						// }

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



	public void onSaveScene(final String fileContents) {
		askSaveFileAs(fileContents);
	}

	

	public void makeToast(final String contents, final TabEditorActivity mTabEditorActivity) {
		if (!PreferenceManager.getDefaultSharedPreferences(mTabEditorActivity.getBaseContext()).getBoolean("NoHelp",
				false))
			mSpriteManager.getContext().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mSpriteManager.getContext(), contents, Toast.LENGTH_LONG).show();
				}
			});
	}

	public void showMessage(final String messageToUser) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final EditText mEditText = new EditText(mSpriteManager.getContext());
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setTitle("Message");
				alert.setMessage(messageToUser);
				alert.setPositiveButton("O.K.", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}

	

	public void settings(final MainMenu mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setTitle("Settings");
				alert.setMessage("Here, you can see some instructions on how to use the app, see the legend, or edit your settings\n If you want to see licenses, go to prefferences.");
				alert.setPositiveButton("Legend", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						legend();
						dialog.dismiss();
					}
				});
				alert.setNeutralButton("Instructions", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Instructions();
						dialog.dismiss();
					}
				});
				alert.setNegativeButton("Preferences", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Defaults();
						Intent settingsActivity = new Intent("wolf.games.mobile.shared.PREFERENCES");
						mCallingActivity.startActivity(settingsActivity);
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}

	public void legend() {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setTitle("Legend");
				alert.setMessage("S - slide or staccato\n" + "h - hammer on\n" + "p - pull off\n"
						+ "x - entirely muted string\n" + "b - bend");
				alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}

	public void Instructions() {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setTitle("Instructions");
				alert.setMessage("1. To start, tap on 'New Tab' and enter a file name.\n\n"
						+ "2. To modify a character, press down on the character you want to change. HOLD your finger down. Slide to select what you want to change it to. You can move far right or left to see exactly what is highlighted.\n\n"
						+ "3. To save, press down on '...' then select 'Save'.\n\n"
						+ "4. To copy and paste, first press 'Select' then slide your finger over the selection you want to make. Next, press and HOLD 'Select' then slide up to 'Copy' before releasing your finger. This might take some practice, but trust me, once you get it, the app is FAST.\n\n"
						+ "5. To paste, press Insert, then tap where you want to insert something. Next, press and HOLD insert. Slide up to 'paste' and release.\n\n"
						+ "6. To see further instuctions, check below.");
				alert.setPositiveButton("More", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}

	public void displayFinalInstructions(final TabEditorActivity context) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setTitle("All done! Now check out the video!");
				alert.setMessage("Here are a few more things you can do in tab maker:\n\n"
						+ "1. If the tab is not long enough, insert more via '...'->'+10'\n\n"
						+ "2. To insert custom characters, tap on the '?' when selecting an edit\n\n"
						+ "3. In the full version, selected sections can be played individually via 'select' -> make selection -> 'play'.\n\n"
						+ "4. To separate lines of tablature, use 'Insert' -> 'New L'. The tab will be separated on export and zoom out.\n\n"
						+ "5. To export a tab, press '...' -> 'Export' This will export a .txt file with the lines you separated out.\n\n"
						+ "6. Extra settings are available via '...' -> 'Settings/Tune'\n\n"
						+ "7. There is a tutorial video available by clicking the link below.\n\n");
				alert.setPositiveButton("Video-Suggested", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse("http://www.youtube.com/watch?v=LBnGlMK5bJo")));
						Log.i("Video", "Video Playing....");
						dialog.dismiss();
					}
				});
				alert.setNegativeButton("Done", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}
}

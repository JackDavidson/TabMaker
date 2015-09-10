package wolf.games.mobile.tabmaker.menu;

import java.io.File;
import java.util.Arrays;

import org.andengine.ui.activity.SimpleBaseGameActivity;

import wolf.games.mobile.shared.SharedData;
import wolf.games.mobile.shared.TabMakerSounds;
import wolf.games.mobile.tabmaker.MainMenu;
import wolf.games.mobile.tabmaker.TabParser;
import wolf.games.mobile.tools.CustomAlertDialogBuilder;
import wolf.games.mobile.tools.SDCardWriter;
import wolf.games.mobile.tools.SpriteManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Environment;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * everything in here is intended to be used in MainMenu
 * 
 * @author jack
 * 
 */
public class MenuManagerPartA {

	SpriteManager mSpriteManager;

	public MenuManagerPartA(SpriteManager mSpriteManager) {
		this.mSpriteManager = mSpriteManager;
	}

	public void askUserIfHeLikesApp(final MainMenu mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
				alert.setTitle("Tab Maker");
				alert.setMessage("\nDo you like Tab Maker?\n\n");
				alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						askUserToRateApp(mCallingActivity);

					}
				});
				alert.setNeutralButton("No/Needs improvement", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						offerToSendEmail(mCallingActivity);

					}
				});
				alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				alert.show();

			}
		});
	}

	public void askUserToRateApp(final MainMenu mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
				alert.setTitle("Tab Maker");
				alert.setMessage("\nWould you consider rating 5-star and/or reviewing this app?\n\n");
				alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						// intent.setData(Uri.parse("market://search?q=pub:Wolf+Games+Mobile"));
						if (SharedData.isFullVersion)
							intent.setData(Uri.parse("market://details?id=wolf.games.mobile.tabmakerB"));
						else
							intent.setData(Uri.parse("market://details?id=wolf.games.mobile.tabmaker"));
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

	public void notifyOfOtherInstalledVersion(final MainMenu mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
				alert.setTitle("Free version is still installed");
				alert.setMessage("\nThe free version of this app is installed on your device. For this version to function properly, it must be uninstalled. Would you like to uninstall it now?\n\n");
				alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Uri packageUri = Uri.parse("package:wolf.games.mobile.tabmaker");
						Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
						mCallingActivity.startActivity(uninstallIntent);
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

	public void askUserToBuyApp(final MainMenu mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
				alert.setTitle("Donate to Tab Maker");
				alert.setMessage("\nThis is a free app. It is ONLY supported by few adds and donations from users.\n\n Will you support tab maker by purchasing the add-free version? - $4.00");
				alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
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

	public void offerToSendEmail(final MainMenu mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
				alert.setTitle("Tab Maker");
				alert.setMessage("\nWould you like to Send a complaint and/or feature suggestion?\n\n");
				alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
						emailIntent.setType("message/rfc822");
						emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
								new String[] { "WolfGamesMobile@Gmail.com" });
						emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Complaint/Feature suggestion");
						emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I have something about Tab Maker.");

						mCallingActivity.startActivity(emailIntent);
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

	public void WarnOfAttemptToStartAudio(final MainMenu mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
				alert.setTitle("Tab Maker");
				alert.setMessage("\nTab Maker will now attempt to enable audio. Your"
						+ " phone may throw an exception, in which case the app will"
						+ " close, and audio will be disabled next time you start the app."
						+ " press ok to continue, or cancel to disable audio. In future versions"
						+ ", there will be an option under settings. Pressing cancel will disable"
						+ " audio until reinstall or update of app.\n\n");
				alert.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						try {
							// music:
							mCallingActivity.mSounds = new TabMakerSounds(mCallingActivity, false);
							SharedData.soundsManager = mCallingActivity.mSounds;

						} catch (Exception e) {
							e.printStackTrace();
						}

						int successOfLoadingSounds = 1;

						SDCardWriter.writeFile(mCallingActivity.getFilesDir() + "/", "enableSounds",
								successOfLoadingSounds + ",");
						dialog.dismiss();

					}
				});
				alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mCallingActivity.mSounds = new TabMakerSounds(mCallingActivity, true);
						SharedData.soundsManager = mCallingActivity.mSounds;
						dialog.dismiss();
					}
				});
				alert.setCancelable(false);
				alert.show();
			}
		});
	}

	public void offerTutorial(final MainMenu mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
				alert.setTitle("Tutorials - Checkout both Youtube & in-app!");
				alert.setMessage("\nIt looks like your new to this app! You should check out both the Youtube and in-app tutorials. Would you like to see one of the tutorials?\n\n You can disable this box by going to Settings -> Show Tutorial on startup");
				alert.setNegativeButton("Yes, in-app", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						SharedData.makingNewTab = true;
						Intent tabViewer = new Intent("wolf.games.mobile.tabmaker.TABEDITORACTIVITYTUTORIAL");
						mSpriteManager.getContext().startActivityForResult(tabViewer, 0);
						dialog.dismiss();
					}
				});
				alert.setNeutralButton("Yes, Youtube", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mCallingActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse("http://www.youtube.com/watch?v=LBnGlMK5bJo")));
						Log.i("Video", "Video Playing....");
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

	public void selectAFileToSend(final MainMenu mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				File filePath = new File(Environment.getExternalStorageDirectory() + "/TabMaker/MyTabs/");
				if (filePath.exists() && filePath.list() != null) {
					final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
					alert.setTitle("Where would you like to look for a file to send?");
					alert.setMessage("MyTabs = xml files from tabs you are still editing in the editor"
							+ "\n\nExportedTabs = Tabs that you have finished editing, and have exported from the tab editor. These will have proper formating"
							+ "\n\nDownloadedTabs = Tabs that you have downloaded. These are saved as .txt files, and contain the text taken directly from websites.");
					alert.setPositiveButton("MyTabs", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							selectAFileToSendFromMyTabs(mCallingActivity);
							dialog.dismiss();
						}
					});
					alert.setNeutralButton("ExportedTabs", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							selectAFileToSendFromExportedTabs(mCallingActivity);
							dialog.dismiss();
						}
					});
					alert.setNegativeButton("DownloadedTabs", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							selectAFileToSendFromDownloadedTabs(mCallingActivity);
							dialog.dismiss();
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

	public void selectAFileToSendFromMyTabs(final MainMenu mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				File filePath = new File(Environment.getExternalStorageDirectory() + "/TabMaker/MyTabs/");
				if (filePath.exists() && filePath.list() != null) {
					final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
					alert.setTitle("Please Select a file to send");
					final File[] fileArray = filePath.listFiles();
					Arrays.sort(fileArray);
					String[] fileStringArray = new String[fileArray.length];
					if (fileArray.length > 0) {
						for (int i = 0; i < fileArray.length; i++) {
							fileStringArray[i] = fileArray[i].toString();
						}
					}
					// Arrays.sort(fileStringArray);
					alert.setSingleChoiceItems(fileStringArray, 0, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String filePath = fileArray[which].getParent();
							String fileName = fileArray[which].getName();
							SharedData.activeFile = fileName;
							Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
							emailIntent.setType("message/rfc822");
							emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, SharedData.activeFile);
							emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
									"I'm sending this tab via Tab Maker for Android.");
							emailIntent.putExtra(
									Intent.EXTRA_STREAM,
									Uri.fromFile(new File(Environment.getExternalStorageDirectory()
											+ "/TabMaker/MyTabs/" + SharedData.activeFile)));
							mCallingActivity.startActivity(emailIntent);
							dialog.dismiss();
							// mLevelDesignerLoader.LoadFromString(SDCardWriter.readFile(fileArray[which].getPath()));
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
							// mSpriteManager.getContext().finish();
						}
					});
					alert.show();
				}
			}
		});
	}

	public void selectAFileToSendFromExportedTabs(final MainMenu mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				File filePath = new File(Environment.getExternalStorageDirectory() + "/TabMaker/ExportedTabs/");
				if (filePath.exists() && filePath.list() != null) {
					final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
					alert.setTitle("Please Select a file to send");
					final File[] fileArray = filePath.listFiles();
					Arrays.sort(fileArray);
					String[] fileStringArray = new String[fileArray.length];
					if (fileArray.length > 0) {
						for (int i = 0; i < fileArray.length; i++) {
							fileStringArray[i] = fileArray[i].toString();
						}
					}
					// Arrays.sort(fileStringArray);
					alert.setSingleChoiceItems(fileStringArray, 0, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String filePath = fileArray[which].getParent();
							String fileName = fileArray[which].getName();
							SharedData.activeFile = fileName;
							Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
							emailIntent.setType("message/rfc822");
							emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, SharedData.activeFile);
							emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
									"I'm sending this tab via Tab Maker for Android.");
							emailIntent.putExtra(
									Intent.EXTRA_STREAM,
									Uri.fromFile(new File(Environment.getExternalStorageDirectory()
											+ "/TabMaker/ExportedTabs/" + SharedData.activeFile)));
							mCallingActivity.startActivity(emailIntent);
							dialog.dismiss();
							// mLevelDesignerLoader.LoadFromString(SDCardWriter.readFile(fileArray[which].getPath()));
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
							// mSpriteManager.getContext().finish();
						}
					});
					alert.show();
				}
			}
		});
	}

	public void selectAFileToSendFromDownloadedTabs(final MainMenu mCallingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				File filePath = new File(Environment.getExternalStorageDirectory() + "/TabMaker/DownloadedTabs/");
				if (filePath.exists() && filePath.list() != null) {
					final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
					alert.setTitle("Please Select a file to send");
					final File[] fileArray = filePath.listFiles();
					Arrays.sort(fileArray);
					String[] fileStringArray = new String[fileArray.length];
					if (fileArray.length > 0) {
						for (int i = 0; i < fileArray.length; i++) {
							fileStringArray[i] = fileArray[i].toString();
						}
					}
					// Arrays.sort(fileStringArray);
					alert.setSingleChoiceItems(fileStringArray, 0, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String filePath = fileArray[which].getParent();
							String fileName = fileArray[which].getName();
							SharedData.activeFile = fileName;
							Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
							emailIntent.setType("message/rfc822");
							emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, SharedData.activeFile);
							emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
									"I'm sending this tab via Tab Maker for Android.");
							emailIntent.putExtra(
									Intent.EXTRA_STREAM,
									Uri.fromFile(new File(Environment.getExternalStorageDirectory()
											+ "/TabMaker/DownloadedTabs/" + SharedData.activeFile)));
							mCallingActivity.startActivity(emailIntent);
							dialog.dismiss();
							// mLevelDesignerLoader.LoadFromString(SDCardWriter.readFile(fileArray[which].getPath()));
						}
					});
					alert.show();
				} else {
					// file does not exist or is empty
					final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
					alert.setTitle("No downloaded Tabs found");
					alert.setMessage("there were no files found in "
							+ filePath.toString()
							+ "\n This file contains everything that has been downloaded by TabMaker. Download a file first to make use of this tool.");
					alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// close the activity
							// mSpriteManager.getContext().finish();
						}
					});
					alert.show();
				}
			}
		});
	}

	public void enterURL(final SimpleBaseGameActivity callingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final EditText mEditText = new EditText(mSpriteManager.getContext());
				final View textEntryView = mEditText;
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setCancelable(false);
				alert.setTitle("Please paste or type URL");
				alert.setView(textEntryView);
				alert.setMessage("The website must use text format for this to work. A file will be saved to /TabMaker/DownloadedTabs/THE_TITLE_OF_THIS_PAGE");
				alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mEditText.getText().toString().equals("")) {
							// do nothing, since nothing was entered
							return;
						}
						SharedData.makingNewTab = false;
						SharedData.currentTabParser = null;
						SharedData.currentURL = mEditText.getText().toString();
						Intent tabViewer = new Intent("wolf.games.mobile.tabViewer.TABVIEWERWITHBUTTONS");
						mSpriteManager.getContext().startActivityForResult(tabViewer, 0);
						dialog.dismiss();
					}
				});
				alert.setNeutralButton("Browse", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = null;
						intent = new Intent(Intent.ACTION_VIEW, Uri
								.parse("http://wolfgamesmobile.com/phpBB3/viewforum.php?f=4"));
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

	public void HowToGetTabs(final SimpleBaseGameActivity callingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setTitle("Import Tab");
				alert.setMessage("How would you like to import a tab?"
						+ "\n\nURL = allows you to type or pase a URL (website). The app will go to the website and download the tab. If successful, the tab will be displayed on the next page."
						+ "\n\nClipboard = highlight a tab in your browser, copy to clipboard, then press this button to load up the tab."
						+ "\n\n.txt file = if you have already downloaded a tab and want to view it again, choose '.txt file'. All downloaded tabs are saved to text files here. ");
				alert.setPositiveButton("URL", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						enterURL(callingActivity);
						dialog.dismiss();
					}
				});
				alert.setNeutralButton("Clipboard", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						enterNameForNewTabFromClipboard(callingActivity);
						dialog.dismiss();
					}
				});
				alert.setNegativeButton(".txt file", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						selectFileToImport();
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}

	public void displayForumsMenu(final SimpleBaseGameActivity callingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setTitle("WGM pbpBB3 Forums");
				alert.setMessage("What would you like to do?");
				alert.setPositiveButton("Download", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						enterURL(callingActivity);
						dialog.dismiss();
					}
				});
				alert.setNeutralButton("Browse", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = null;
						intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://wolfgamesmobile.com/phpBB3"));
						callingActivity.startActivity(intent);
						dialog.dismiss();
					}
				});
				alert.setNegativeButton("Get Help", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent intent = null;
						intent = new Intent(Intent.ACTION_VIEW, Uri
								.parse("http://wolfgamesmobile.com/phpBB3/viewforum.php?f=2"));
						callingActivity.startActivity(intent);
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
	}

	public void enterNameForNewTabFromClipboard(final SimpleBaseGameActivity callingActivity) {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final EditText mEditText = new EditText(mSpriteManager.getContext());
				final View textEntryView = mEditText;
				final CustomAlertDialogBuilder alert = new CustomAlertDialogBuilder(mSpriteManager.getContext());
				alert.setCancelable(false);
				alert.setTitle("Please type a name for your new tab");
				alert.setView(textEntryView);
				alert.setMessage("A file will be saved to /TabMaker/DownloadedTabs/THIS_TAB_NAME");
				alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mEditText.getText().toString().equals("")) {
							// do nothing, since nothing was entered
							return;
						}
						SharedData.makingNewTab = false;
						SharedData.currentTabParser = null;
						SharedData.currentURL = null;
						ClipboardManager textClipboard = (android.text.ClipboardManager) callingActivity
								.getSystemService(callingActivity.CLIPBOARD_SERVICE);
						SharedData.entireTabAsString = (String) textClipboard.getText();
						SharedData.activeFile = mEditText.getText().toString();
						Intent tabViewer = new Intent("wolf.games.mobile.tabViewer.TABVIEWERWITHBUTTONS");
						mSpriteManager.getContext().startActivityForResult(tabViewer, 0);
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

	public void selectFileToImport() {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				File filePath = new File(Environment.getExternalStorageDirectory() + "/TabMaker/DownloadedTabs/");
				if (filePath.exists() && filePath.list() != null) {
					final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
					alert.setTitle("Please Select a file to Import");
					final File[] fileArray = filePath.listFiles();
					Arrays.sort(fileArray);
					String[] fileStringArray = new String[fileArray.length];
					if (fileArray.length > 0) {
						for (int i = 0; i < fileArray.length; i++) {
							fileStringArray[i] = fileArray[i].toString();
						}
					}
					// Arrays.sort(fileStringArray);
					alert.setSingleChoiceItems(fileStringArray, 0, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SharedData.activeFile = "Default";
							String filePath = fileArray[which].getParent();
							String fileName = fileArray[which].getName();
							SharedData.currentTabParser = new TabParser(filePath, fileName, 6);
							SharedData.makingNewTab = false;
							Intent tabViewer = new Intent("wolf.games.mobile.tabViewer.TABVIEWERWITHBUTTONS");
							mSpriteManager.getContext().startActivityForResult(tabViewer, 0);
							dialog.dismiss();

							// mLevelDesignerLoader.LoadFromString(SDCardWriter.readFile(fileArray[which].getPath()));
						}
					});
					alert.show();
				} else {
					// file does not exist or is empty
					final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
					alert.setTitle("No downloaded Tabs found");
					alert.setMessage("there were no files found in " + filePath.toString());
					alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					});
					alert.show();
				}
			}
		});
	}

	public void selectFileToViewFromExportFolder() {
		mSpriteManager.getContext().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				File filePath = new File(Environment.getExternalStorageDirectory() + "/TabMaker/ExportedTabs/");
				if (filePath.exists() && filePath.list() != null) {
					final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
					alert.setTitle("Please Select a file to Import");
					final File[] fileArray = filePath.listFiles();
					Arrays.sort(fileArray);
					String[] fileStringArray = new String[fileArray.length];
					if (fileArray.length > 0) {
						for (int i = 0; i < fileArray.length; i++) {
							fileStringArray[i] = fileArray[i].toString();
						}
					}
					// Arrays.sort(fileStringArray);
					alert.setSingleChoiceItems(fileStringArray, 0, new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SharedData.activeFile = "Default";
							String filePath = fileArray[which].getParent();
							String fileName = fileArray[which].getName();
							SharedData.currentTabParser = new TabParser(filePath, fileName, 6);
							SharedData.makingNewTab = false;
							Intent tabViewer = new Intent("wolf.games.mobile.tabViewer.TABVIEWERWITHBUTTONS");
							mSpriteManager.getContext().startActivityForResult(tabViewer, 0);
							dialog.dismiss();

							// mLevelDesignerLoader.LoadFromString(SDCardWriter.readFile(fileArray[which].getPath()));
						}
					});
					alert.show();
				} else {
					// file does not exist or is empty
					final AlertDialog.Builder alert = new AlertDialog.Builder(mSpriteManager.getContext());
					alert.setTitle("No exported tabs found.");
					alert.setMessage("there were no files found in "
							+ filePath.toString()
							+ ". You must first export a tab you have modified to view it in the tab viewer. This is done in the teb editor.");
					alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					});
					alert.show();
				}
			}
		});
	}

}

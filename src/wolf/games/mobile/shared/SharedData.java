package wolf.games.mobile.shared;

import wolf.games.mobile.tabmaker.TabParser;

public class SharedData {
	//the time since an add was last opened
	//file name
	public static String activeFile = "Default";
	public static String fileToTextEdit = "Default";
	public static String currentURL = "Default";
	public static String activeFilePath = "Default";
	public static String messageToUser = "";
	public static Boolean makingNewTab = true;
	public static Boolean modifyingCustomTab = true;
	public static int amtSpaceLastAdded = 0;
	public static String noteType = "Classical";
	public static Boolean playStacato = true;
	public static Boolean isFullVersion = true;
	public static Boolean exitingCorrectly = false;
	
	public static Boolean canWriteRecoveryFile = false;
	
	public static android.text.ClipboardManager textClipboard = null;
	public static TabParser currentTabParser = null;
	public static TabMakerSounds soundsManager = null;
	public static boolean failedToLoadWebsite = false;
	
	public static int CHANNELS;
	
	public static final float defaultFontSize = 84f;
	
	public static boolean doubleClickTimerRunning = false;
	
	public static Integer notesToSkip = 0;
	public static boolean holdTimerRunning = false;
	public static String entireTabAsString = null;
	

}

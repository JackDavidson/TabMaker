package wolf.games.mobile.tools;

import java.io.*;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

/**
 * SD card writer tool. removes spaces from your stuff, makes the directories you need, and does other things in
 * addition to writing a string to a file for you
 * 
 * @author jack
 * 
 */
public class SDCardWriter {
	public static void writeFile(String filePath, String fileName, String content) {
		try {
			fileName = fileName.replaceAll("[^a-zA-Z0-9./-]", "_");
			fileName = fileName.replaceAll("/", "_");
			fileName = fileName.replaceAll(" ", "_");
			File myFile = new File(filePath);
			myFile.mkdirs();
			myFile = new File(filePath + fileName);
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(content);
			myOutWriter.flush();
			myOutWriter.close();
			fOut.close();
		} catch (Exception e1) {
			Log.e("stuff", "failed writing SD file" + e1.getMessage());
		}
	}

	public static String readFile(String file) {
		String aBuffer = "";
		try {
			File myFile = new File(file);
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
			String aDataRow = "";
			while ((aDataRow = myReader.readLine()) != null) {
				aBuffer += aDataRow + "\n";
				// aBuffer += aDataRow;
			}
			fIn.close();
			myReader.close();
		} catch (Exception e) {
			Log.e("stuff", "Failed reading SD file" + e.getMessage());
		}

		// Log.e("stuff", aBuffer);
		return aBuffer;
	}

	public static String readFile(InputStream fIn) {
		String aBuffer = "";
		try {
			// File myFile = new File(file);
			// FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
			String aDataRow = "";
			while ((aDataRow = myReader.readLine()) != null) {
				// aBuffer += aDataRow + "\n";
				aBuffer += aDataRow;
			}
			fIn.close();
			myReader.close();
		} catch (Exception e) {
			Log.e("stuff", "Failed reading SD file" + e.getMessage());
		}
		return aBuffer;
	}
}
package wolf.games.mobile.shared;


import wolf.games.mobile.tabmaker.R;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;



import com.levien.synthesizer.android.AndroidGlue;
import com.levien.synthesizer.android.service.SynthesizerThread;
import com.levien.synthesizer.core.model.composite.MultiChannelSynthesizer;
import com.levien.synthesizer.core.music.Note;
import com.levien.synthesizer.core.soundfont.SoundFontReader;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;

public class TabMakerSounds {

	public Music mKnockSound;
	public Music mApplauseSound;
	public Music mMoanSound;
	public Music entireScale;
	public Map<Integer, Music> guitarNotes;
	public Map<Integer, Music> classicalNotes;
	public SimpleBaseGameActivity mBaseActivity;

	private static final int FINGERS = 5;
	private static final int CHANNELS = 8;

	public AndroidGlue androidGlue_;
	private static List<String> patchNames;

	public TabMakerSounds(SimpleBaseGameActivity mBaseActivity,
			Boolean disableAudio) {
		MusicFactory.setAssetBasePath("mfx/");
		SoundFactory.setAssetBasePath("mfx/");
		this.mBaseActivity = mBaseActivity;
		guitarNotes = new HashMap<Integer, Music>(45);
		classicalNotes = new HashMap<Integer, Music>(45);

		Log.i("Tab Maker", " starting to load sounds");

		SharedData.CHANNELS = CHANNELS;

		if (!disableAudio) {

			AudioParams params = new AudioParams(44100, 64);
			// TODO: for pre-JB-MR1 devices, do some matching against known
			// devices to
			// get best audio parameters.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				getJbMr1Params(params);
			}
			// Empirical testing shows better performance with small buffer size
			// than actually matching the media server's reported buffer size.
			params.bufferSize = 64;

			androidGlue_ = new AndroidGlue();
			androidGlue_.start(params.sampleRate, params.bufferSize);
			InputStream patchIs = mBaseActivity.getResources().openRawResource(
					R.raw.rom1a);
			byte[] patchData = new byte[4104];
			try {
				patchIs.read(patchData);
				androidGlue_.sendMidi(patchData);
				patchNames = new ArrayList<String>();
				for (int i = 0; i < 32; i++) {
					patchNames.add(new String(patchData, 124 + 128 * i, 10,
							"ISO-8859-1"));
				}
			} catch (IOException e) {
				Log.e(getClass().getName(), "loading patches failed");
			}

			int presetNum = 11;
			androidGlue_.sendMidi(new byte[] { (byte) 0xc0, (byte) presetNum });
		}

		Log.i("Tab Maker", "loaded sounds");
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	void getJbMr1Params(AudioParams params) {
		AudioManager audioManager = (AudioManager) mBaseActivity
				.getSystemService(Context.AUDIO_SERVICE);
		String sr = audioManager
				.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
		String bs = audioManager
				.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
		params.confident = true;
		params.sampleRate = Integer.parseInt(sr);
		params.bufferSize = Integer.parseInt(bs);
		// log("from platform: " + params);
	}

	class AudioParams {
		AudioParams(int sr, int bs) {
			confident = false;
			sampleRate = sr;
			bufferSize = bs;
		}

		public String toString() {
			return "sampleRate=" + sampleRate + " bufferSize=" + bufferSize;
		}

		boolean confident;
		int sampleRate;
		int bufferSize;
	}

}

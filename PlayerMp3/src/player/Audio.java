package player;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


/**
 * @author Kacper Ratajczak
 */
public class Audio {
	/**
	 * @see #clip
	 * @see #Audio(String)
	 * @see #play(float, int)
	 * @see #close()
	 * @see #stop()
	 * @see #changeVolume(float)
	 * @see #playClip(float, int)
	 * @see #volumeControl(float)
	 */
	private Clip clip;

	public Audio(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {

		AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
		AudioFormat baseFormat = ais.getFormat();
		AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16,
				baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);


		AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
		clip = AudioSystem.getClip();
		clip.open(dais);
	}


	public void play(float volume, int framePos) {
		playClip(volume, framePos);
		clip.start();
	}


	public void stop() {
		if (clip.isRunning())
			clip.stop();
	}


	public void close() {
		stop();
		clip.close();
	}

	public void playAfterPause(int frame) {
		clip.setFramePosition(frame);
		clip.start();
	}

	public int getFramePosition() {
		return clip.getFramePosition();
	}

	public boolean getIsRunning() {
		return clip.isRunning();
	}


	public void changeVolume(float volume) {
		volumeControl(volume);
	}


	private void playClip(float volume, int framePos) {
		if (clip == null)
			return;

		stop();
		volumeControl(volume);

		clip.setFramePosition(framePos);
	}

	private void volumeControl(float volume) {
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		try {
			gainControl.setValue(volume);
			System.out.println(volume);
		} catch (IllegalArgumentException e) {

			if (volume > gainControl.getMaximum())
				volume = gainControl.getMaximum();
			else if (volume < gainControl.getMinimum())
				volume = gainControl.getMinimum();
		}
	}
}

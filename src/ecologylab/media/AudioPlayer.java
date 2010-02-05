/**
 * 
 */
package ecologylab.media;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import ecologylab.generic.Debug;
import ecologylab.xml.library.audiometadata.AudioFileMetadata;

/**
 * This class performs simple sound playback.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public class AudioPlayer extends Debug
{
    /** The sound file that this sound player will play when the playback method is invoked. */
    protected AudioFileMetadata audioFile           = null;

    /** The mixer that will provide us with lines to work with. */
    protected Mixer             mixer               = null;

    /** The source data line: the place to which we will write the output; yes, it seems backwards. */
    protected SourceDataLine    sourceDataLine      = null;

    /** Listeners for AudioEvents. */
    List<AudioEventListener>    audioEventListeners = new LinkedList<AudioEventListener>();

    /**
     * Factory method for producing AudioPlayers. Invoke this method to get a AudioPlayer Object.
     * 
     * The standard constructor is not made public because it may be impossible to create an instance. This method
     * encapuslates such an event.
     * 
     * This method attempts to automatically capture the correct Mixer and SourceDataLine objects for the AudioPlayer.
     * 
     * @throws LineUnavailableException
     */
    public static final AudioPlayer getAudioPlayerInstance() throws LineUnavailableException
    {
        Mixer.Info[] availableMixers = AudioSystem.getMixerInfo();

        Mixer selectedMixer = AudioSystem.getMixer(availableMixers[0]);

        Line.Info[] availableSourceLines = selectedMixer.getSourceLineInfo();

        SourceDataLine selectedLine = (SourceDataLine) selectedMixer.getLine(availableSourceLines[0]);

        return new AudioPlayer(selectedMixer, selectedLine);
    }

    /**
     * Factory method for producing AudioPlayers. Invoke this method to get a AudioPlayer Object.
     * 
     * The standard constructor is not made public because it may be impossible to create an instance. This method
     * encapuslates such an event.
     * 
     * This method must be supplied with the correct Mixer and SourceDataLine objects for the AudioPlayer.
     */
    public static final AudioPlayer getAudioPlayerInstance(Mixer mixer, SourceDataLine sourceDataLine)
    {
        return new AudioPlayer(mixer, sourceDataLine);
    }

    protected AudioPlayer(Mixer mixer, SourceDataLine sourceDataLine)
    {
        this.mixer = mixer;
        this.sourceDataLine = sourceDataLine;
    }

    /**
     * Sets the current sound file that will be played back using the playback method.
     * 
     * @param audioFile
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public void setAudioFile(AudioFileMetadata audioFile)
    {
        this.unsetAudioFile();

        this.audioFile = audioFile;
    }

    /**
     * Unsets the current sound file so that no file may be played back using the playback method.
     */
    public void unsetAudioFile()
    {
        this.audioFile = null;
    }

    /**
     * Plays back the current sound file and fires appropriate events at the beginning and end.
     * 
     * @throws LineUnavailableException
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public void playbackAudioFile() throws UnsupportedAudioFileException, IOException
    {
        if (this.audioFile == null)
        {
            this.debug("There is no sound file currently set; cannot playback.");
            return;
        }
        
				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile.getFile().file());

				AudioFormat baseFormat = audioInputStream.getFormat();

				// attempt to get these settings automatically, if they cannot be gotten automatically, we must try the
				// properties map; if that does not work, then we're hosed.
				AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 
				        baseFormat.getSampleRate(),
				        16,
				        baseFormat.getChannels(), 
				        baseFormat.getChannels() * 2,
				        baseFormat.getSampleRate(), 
				        false);
				
				debug("orig: "+baseFormat.toString());
				debug("deco: "+decodedFormat.toString());

				AudioInputStream din = AudioSystem.getAudioInputStream(decodedFormat, audioInputStream);

				int frameSizeInBytes = decodedFormat.getFrameSize();
				int bufferLengthInFrames = sourceDataLine.getBufferSize() / 8;
				int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
				byte[] data = new byte[bufferLengthInBytes];
				int numBytesRead = 0;

				this.fireAudioEvent(AudioEvent.PLAY);

				try
				{
				    this.sourceDataLine.open(decodedFormat);
				}
				catch (LineUnavailableException e)
				{
				    e.printStackTrace();
				    
				    debug("valid formats:");
				    
				    Line.Info lineInfo = sourceDataLine.getLineInfo();
				    for (AudioFormat a : ((DataLine.Info)lineInfo).getFormats())
				    {
				        debug(a.toString());
				    }
				    
				    return;
				}

				sourceDataLine.start();

				while ((numBytesRead = din.read(data)) != -1)
				{
				    int numBytesRemaining = numBytesRead;

				    while (numBytesRemaining > 0)
				    {
				        numBytesRemaining -= sourceDataLine.write(data, 0, numBytesRemaining);
				    }
				}
				
				sourceDataLine.drain();

				sourceDataLine.stop();
				sourceDataLine.close();

				this.fireAudioEvent(AudioEvent.FINISHED);
    }

    /**
     * Convienence method: same as calling setSoundFile(audioFile) then playbackSoundFile().
     * 
     * @param audioFile1
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws LineUnavailableException
     */
    public void playbackAudioFile(AudioFileMetadata audioFile1) throws UnsupportedAudioFileException, IOException,
            LineUnavailableException
    {
        this.setAudioFile(audioFile1);
        this.playbackAudioFile();
    }

    public void addAudioEventListener(AudioEventListener l)
    {
        this.audioEventListeners.add(l);
    }

    public void removeAudioEventListener(AudioEventListener l)
    {
        this.audioEventListeners.remove(l);
    }

    protected void fireAudioEvent(int eventType)
    {
        AudioEvent e = new AudioEvent(eventType);

        for (AudioEventListener l : audioEventListeners)
        {
            l.soundEventOccurred(e);
        }
    }
}

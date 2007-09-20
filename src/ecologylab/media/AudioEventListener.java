/**
 * 
 */
package ecologylab.media;

/**
 * Classes implementing this interface can listen for sound playback events, such as the end of a track or the beginning
 * of playback.
 * 
 * @author Zach
 * 
 */
public interface AudioEventListener
{
    public void soundEventOccurred(AudioEvent s);
}

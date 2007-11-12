/**
 * 
 */
package ecologylab.media;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public class AudioEvent
{
    /**
     * Indicates that a sound has started playing; may be the beginning of the sound or may be restarting after a PAUSE.
     */
    public static final int PLAY     = 1;

    /**
     * Indicates that a sound has stopped playing, but that the playback position has not moved (so that it may be
     * resumed later).
     */
    public static final int PAUSE    = 2;

    /**
     * Indicates that a sound has stopped playing and that the playback position has returned to the beginning.
     */
    public static final int STOPPED  = 4;

    /**
     * Indicates that the sound has finished playing.
     */
    public static final int FINISHED = 8;

    private int eventType;
    
    public AudioEvent(int eventType)
    {
        this.eventType = eventType;
    }

    /**
     * @return the eventType
     */
    public int getEventType()
    {
        return eventType;
    }
}

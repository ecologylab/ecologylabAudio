package ecologylab.media;

/**
 * Locates the end of an audio stream for use in synchronizing audio tracks.
 * 
 * @author William Hamilton (bill@ecologylab.net)
 */
public class EndSyncFinder implements SyncFinder
{
	/** total length of a TTeCLoG game log */
	protected long	microDuration;

	public EndSyncFinder(long microsecondDuration)
	{
		this.microDuration = microsecondDuration;
	}

	/**
	 * Finds the frame at which the start of the playback should be: the frame corresponding to the
	 * beginning of the game.
	 * 
	 * @return an integer representing the index of the first sample that should be played back.
	 */
	public int findSyncFrame(AudioBufferPlayer p, int syncChannel)
	{
		return p.getBufferLength() - (int) (microDuration / 1000000.0 * p.getFormat().getFrameRate());
	}
}
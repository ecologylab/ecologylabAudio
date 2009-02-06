package ecologylab.media;
import java.util.ArrayList;

public class EndSyncFinder implements SyncFinder
{
	private long microDuration;
	
	public EndSyncFinder(long microsecondDuration)
	{
		this.microDuration = microsecondDuration;
	}
		
	public int findSyncFrame(AudioBufferPlayer p, int syncChannel)
	{
		return p.getFrames() - (int) (microDuration / 1000000.0 * p.getFormat().getFrameRate());
	}
	
}
/**
 * 
 */
package ecologylab.media;

import ecologylab.generic.Debug;

/**
 * For use with synchronization tracks. Locates the beginning of a synchronized period.
 * findSyncFrame(...) identifies the end of the sync track, then moves forward until a zero-crossing
 * occurs. The zero-crossing is taken to be the beginning of the synchronized period.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
public class BeginSyncFinder extends Debug
{
	/**
	 * Assumes that sync track is decreasing while sync'ing; scans from the end of the file until a
	 * point where the sync track decreases instead of increases, this is reported as the beginning of
	 * the sync track.
	 * 
	 * @see ecologylab.media.SyncFinder#findSyncFrame(ecologylab.media.AudioBufferPlayer, int)
	 * 
	 * @return The index of the sample immediately after the last zero crossing of p.
	 */
	public static int findStartSyncFrame(AudioBufferPlayer p, int syncChannel)
	{
		int endFrame = p.getBufferLength()-1;

		int lastValue = Integer.MIN_VALUE;

		for (int i = endFrame; i > 0; i--)
		{
//			Debug.println("current value: "+p.getValueAt(i, syncChannel)+"; last value: "+lastValue);

			int currentValue = p.getValueAt(i, syncChannel);

			if (currentValue < lastValue)
				return i + 1;
			else
				lastValue = currentValue;
		}

		return 0;
	}
	
	/**
	 * Searches in an AudioBufferPlayer on the given syncChannel; starts from startFrame and returns
	 * the next frame where getValue() != lastValue.
	 */
	public static int findNextSyncFrameChange(AudioBufferPlayer p, int syncChannel, int startFrame)
	{
		int endFrame = p.getBufferLength() - 1;
		int lastVal = p.getValueAt(startFrame, syncChannel);

		for (int i = startFrame+1; i < endFrame; i++)
		{
			int syncVal = p.getValueAt(i, syncChannel);
			
			if (syncVal != lastVal)
				return i;
		}

		return endFrame;
	}

	public static void checkSyncTrack(AudioBufferPlayer p, int syncChannel)
	{
		int[] syncChannelValues = p.getValuesInRange(0, p.getBufferLength(), syncChannel);
		
		int lastValue = syncChannelValues[0];
		int counter = 1;
		
		for (int i = 1; i < p.getBufferLength(); i++)
		{
			int syncVal = syncChannelValues[i];
			if (syncVal != lastValue)
			{
				Debug.println(counter+" : "+lastValue);
				lastValue = syncVal;
				counter = 1;
			}
			else
			{
				counter++;
			}
		}
		
		Debug.println(counter+" : "+lastValue);
	}
}

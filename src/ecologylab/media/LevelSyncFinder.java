package ecologylab.media;

import java.util.ArrayList;

/**
 * Finds the location, in an audio stream
 * 
 * @author William Hamilton (bill@ecologylab.net)
 */
public class LevelSyncFinder implements SyncFinder
{
	public enum CheckPoint
	{
		HIGH, LOW;
	}

	/**
	 * a float value indicating how close to the extreme value of the line a
	 * checkpoint must be to register as a checkpoint
	 */
	private float						epsilon		= 0.5f;

	private ArrayList<CheckPoint>	checkPoints	= new ArrayList<CheckPoint>();

	public LevelSyncFinder(float epsilon)
	{
		this.epsilon = epsilon;
	}

	public LevelSyncFinder()
	{
		this(0.5f);
	}

	public void addCheckPoint(CheckPoint p)
	{
		checkPoints.add(p);
	}

	public int findSyncFrame(AudioBufferPlayer p, int syncChannel)
	{
		int high = (int) (p.getMaxAmplitude() * epsilon);
		int low = (int) (p.getMinAmplitude() * epsilon);

		int x, checkpoint;
		for (x = 0, checkpoint = 0; x < p.getBufferLength()
				&& checkpoint < checkPoints.size(); x++)
		{
			int val = p.getValueAt(x, syncChannel);
			if (checkPoints.get(checkpoint) == CheckPoint.HIGH && val >= high)
			{
				checkpoint++;
			}
			else if (checkPoints.get(checkpoint) == CheckPoint.LOW && val <= low)
			{
				checkpoint++;
			}
		}

		return x;
	}

}

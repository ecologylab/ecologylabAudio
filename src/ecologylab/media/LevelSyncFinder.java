package ecologylab.media;
import java.util.ArrayList;

public class LevelSyncFinder implements SyncFinder
{
	public enum CheckPoint
	{
		HIGH, LOW;
	}
	
	private float epsilon = 0.5f;
	private ArrayList<CheckPoint> checkPoints = new ArrayList<CheckPoint>();
	
	//epsilon is an float value indicating how close to the extreme value of the line
	//does a checkpoint have to be to register as a checkpoint
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
		for(x = 0, checkpoint = 0; x < p.getFrames() && checkpoint < checkPoints.size(); x++)
		{
			int val = p.getValueAt(x,syncChannel);
			if(checkPoints.get(checkpoint) == CheckPoint.HIGH && val >= high)
			{
				checkpoint++;
			}
			else if(checkPoints.get(checkpoint) == CheckPoint.LOW && val <= low)
			{
				checkpoint++;
			}
		}
		
		return x;
	}
	
}

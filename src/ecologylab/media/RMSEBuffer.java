package ecologylab.media;

import java.util.ArrayList;

/**
 * Stores a set of root mean square energy values for a set of frames, based on a log and an
 * AudioBufferPlayer.
 * 
 * @author Zachary O. Toups (zach@ecologylab.net)
 */
public class RMSEBuffer
{
	private final ArrayList<Double>	rmseValues	= new ArrayList<Double>();

	private final AudioBufferPlayer	audioBuf;

	private double									minRMSE			= Double.MAX_VALUE;

	private double									maxRMSE			= Double.MIN_VALUE;

	private int											channel;

	public RMSEBuffer(AudioBufferPlayer audioBuf, int channel)
	{
		this.audioBuf = audioBuf;

		this.channel = channel;
	}

	public void addRMSEValue(long startTime, long endTime)
	{
		Double rmseVal = new Double(audioBuf.getRMSE(channel, audioBuf
				.getSampleIndexFromTime(startTime), audioBuf.getSampleIndexFromTime(endTime)));
		
		this.rmseValues.add(rmseVal);

		this.minRMSE = Math.min(minRMSE, rmseVal);
		this.maxRMSE = Math.max(maxRMSE, rmseVal);
	}

	public double getRMSEValue(int index)
	{
		try
		{
			return this.rmseValues.get(index);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * @return the minRMSE
	 */
	public double getMinRMSE()
	{
		return minRMSE;
	}

	/**
	 * @return the maxRMSE
	 */
	public double getMaxRMSE()
	{
		return maxRMSE;
	}
}

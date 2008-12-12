package ecologylab.media;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.*;

public class AudioBufferPlayer
{
	private byte[][] rawData;
	private ByteBuffer[] dataBuffers;
	
	private AudioFormat unsplitFormat;
	private AudioFormat splitFormat;
	private Mixer mixer;
	private SourceDataLine[] lines;
	
	private Integer playIndex = 0;
	private int syncIndex = 0;
		
	private boolean playing;
	private Object playSync = new Object();
		
	private ChannelPlayer[] players;
	public static final int frame_interval = 1000;
	private Thread playerThread;	
	
	private long lastFramePosition = 0;
	private int[] maxAmplitudeAbs;
	
	private int numChannels = 0;
	
	private boolean muteAll = false;
	private boolean[] lineMutes;
	private BooleanControl[] muteControls;
	
	
	
	public AudioBufferPlayer(File f, Mixer.Info mixerInfo) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		AudioInputStream stream = AudioSystem.getAudioInputStream(f);
		unsplitFormat = stream.getFormat();
		splitFormat = new AudioFormat(unsplitFormat.getEncoding(),unsplitFormat.getSampleRate(),unsplitFormat.getSampleSizeInBits(),
												1,unsplitFormat.getFrameSize()/unsplitFormat.getChannels(),unsplitFormat.getFrameRate(),unsplitFormat.isBigEndian()); 
		
		
		System.out.println(unsplitFormat.toString());
		numChannels = unsplitFormat.getChannels();
		maxAmplitudeAbs = new int[numChannels];
				
		byte[] unsplitData = new byte[(int) (stream.getFrameLength() * unsplitFormat.getFrameSize())];
		playing = false;

		int readIndex = 0;
		
		while(readIndex < unsplitData.length)
		{
			readIndex += stream.read(unsplitData, readIndex, unsplitData.length - readIndex);
		}
		
		rawData = new byte[unsplitFormat.getChannels()][unsplitData.length/unsplitFormat.getChannels()];
		
		for(int x = 0; x < unsplitData.length; x += unsplitFormat.getFrameSize())
		{
			for(int y = 0; y < unsplitFormat.getChannels(); y++)
			{
				for(int z = 0; z < splitFormat.getFrameSize(); z++)
				{
					rawData[y][x/unsplitFormat.getChannels()+z] = unsplitData[x+y*splitFormat.getFrameSize()+z]; 
				}
			}
		}
		
		dataBuffers = new ByteBuffer[unsplitFormat.getChannels()];
		
		mixer = AudioSystem.getMixer(mixerInfo);
		
		lines = new SourceDataLine[unsplitFormat.getChannels()];
		for(int x = 0; x < unsplitFormat.getChannels(); x++)
		{
			dataBuffers[x] = ByteBuffer.wrap(rawData[x]);
			if(!unsplitFormat.isBigEndian())
				dataBuffers[x].order(ByteOrder.LITTLE_ENDIAN);
		
			lines[x] = (SourceDataLine)mixer.getLine(new DataLine.Info(SourceDataLine.class,splitFormat));
			lines[x].open();
			lines[x].start();
		}
		
		if(mixer.isSynchronizationSupported(lines, true))
		{
			System.err.println("Synchronized!");
			mixer.synchronize(lines, true);
		}
				
		for(Control c : mixer.getControls())
		{
			System.err.println(c.toString());
		}
		
		for(Line l : mixer.getTargetLines())
		{
			System.err.println(l.toString());
		}
		
		for(int channel = 0; channel < numChannels; channel++)
		{
			for(int frame = 0; frame < stream.getFrameLength();frame++)
			{
				maxAmplitudeAbs[channel] = Math.max(Math.abs(this.getValueAt(frame, channel)), maxAmplitudeAbs[channel]);
			}
		}
		
		players = new ChannelPlayer[unsplitFormat.getChannels()];
		for(int channel = 0; channel < unsplitFormat.getChannels(); channel++)
		{
			players[channel] = new ChannelPlayer(channel);
			
			Thread t = new Thread(players[channel]);
			t.start();
		}
		
		lineMutes = new boolean[unsplitFormat.getChannels()];
		
		muteControls = new BooleanControl[unsplitFormat.getChannels()];
		
		Control[][] controls = getControls();
		
		for(int x = 0; x < controls.length; x++)
		{
			for(int y = 0; y < controls[x].length; y++)
			{
				if(controls[x][y].getType().equals(BooleanControl.Type.MUTE))
				{
					muteControls[x] = (BooleanControl) controls[x][y];
					break;
				}
			}
		}
		
	}
	
	public AudioBufferPlayer(File f, Mixer.Info mixerInfo, SyncFinder finder, int syncChannel) throws UnsupportedAudioFileException, IOException, LineUnavailableException
	{
		this(f,mixerInfo);
		
		if(getChannels() <= syncChannel)
		{
			System.err.println("Don't have channel: " + syncChannel);
			return;
		}
		
		this.syncIndex = finder.findSyncFrame(this,syncChannel);
		
		for(ChannelPlayer p : players)
		{
			p.setPlayFrame(syncIndex);
		}
		
		this.muteChannel(syncChannel,true);
	}
	
	public int getChannels()
	{
		return numChannels;
	}
	
	public int getMaxAbsAmplitudeForChannel(int channel)
	{
		return maxAmplitudeAbs[channel];
	}
	
	public int getMaxAmplitude()
	{
		if(unsplitFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
		{
			if(unsplitFormat.getSampleSizeInBits() == 16)
			{
				return Short.MAX_VALUE;
			}
			else if(unsplitFormat.getSampleSizeInBits() == 32)
			{
				return Integer.MAX_VALUE;
			}
		}
		else if(unsplitFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))
		{
			if(unsplitFormat.getSampleSizeInBits() == 16)
			{
				return Short.MAX_VALUE;
			}
			else if(unsplitFormat.getSampleSizeInBits() == 32)
			{
				return Integer.MAX_VALUE;
			}
		}
		return AudioSystem.NOT_SPECIFIED;
	}
	
	public int getMinAmplitude()
	{
		if(unsplitFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
		{
			if(unsplitFormat.getSampleSizeInBits() == 16)
			{
				return Short.MIN_VALUE;
			}
			else if(unsplitFormat.getSampleSizeInBits() == 32)
			{
				return Integer.MIN_VALUE;
			}
		}
		else if(unsplitFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))
		{
			if(unsplitFormat.getSampleSizeInBits() == 16)
			{
				return 0;
			}
			else if(unsplitFormat.getSampleSizeInBits() == 32)
			{
				return Integer.MIN_VALUE;
			}
		}
		return AudioSystem.NOT_SPECIFIED;
	}
	
	public int getValueAt(int frame, int channel)
	{
		if(frame < 0 || frame >= rawData[channel].length / splitFormat.getFrameSize())
		{
			return 0;
		}
		
		int index = frame*splitFormat.getFrameSize();
		
		
		if(splitFormat.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED))
		{
			if(splitFormat.getSampleSizeInBits() == 16)
			{
				return this.dataBuffers[channel].getShort(index);
			}
			else if(splitFormat.getSampleSizeInBits() == 32)
			{
				return this.dataBuffers[channel].getInt(index);
			}
		}
		else if(splitFormat.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))
		{
			if(splitFormat.getSampleSizeInBits() == 16)
			{
				return (splitFormat.isBigEndian())? ((int)(rawData[channel][index]<<8)) | rawData[channel][index+1]:
					((int)(rawData[channel][index+1]<<8)) | rawData[channel][index];
			}
			else if(splitFormat.getSampleSizeInBits() == 32)
			{
				long tmp = 0;
				if(splitFormat.isBigEndian())
				{
					for(int i = index; i < index + 4; i++)
					{
						tmp <<= 8;
						tmp |= rawData[channel][i];
					}
				}
				else
				{
					for(int i = index + 3; i >= index; i--)
					{
						tmp <<= 8;
						tmp |= rawData[channel][i];
					}
				}
				return (int)(Integer.MIN_VALUE + tmp);
			}
		}
		return AudioSystem.NOT_SPECIFIED;
	}
	
	public AudioFormat getFormat()
	{
		return unsplitFormat;
	}
	
	public long getLength()
	{
		return (long)(rawData[0].length / splitFormat.getFrameSize() / splitFormat.getFrameRate() * 1000);
	}
	
	public int getFrames()
	{
		return rawData[0].length / splitFormat.getFrameSize();
	}
	
	/*
	 * TODO: Change for multichannels
	 */
	public void setMicrosecondPosition(long microseconds)
	{
		boolean wasPlaying = this.playing;
		
		stop();
		int frame = (int) (unsplitFormat.getFrameRate() * microseconds / 1000000.0) + this.syncIndex;
		for(ChannelPlayer player : this.players)
		{
			player.setPlayFrame(frame);
		}
		
		if(wasPlaying)
			play();
	}
	
	public int getMaxAbsOfEnvelope(int channel, int frameIndex, int envelopeSize, int samplingDensity)
	{
		int max = this.getMinAmplitude();
		
		int start = Math.max(0,frameIndex - envelopeSize / 2);
		int end = Math.min(frameIndex + envelopeSize / 2, getFrames() - 1);
		
		int step = Math.max(1,(start - end)/samplingDensity); 
		
		for(int x = start ; x <= end; x+= step)
		{
			max = Math.max(max, Math.abs(this.getValueAt(x, channel)));
		}
		return max;
	}
	
	public int getMaxOfEnvelope(int channel, int frameIndex, int envelopeSize, int samplingDensity)
	{
		int max = this.getMinAmplitude();
		
		int start = Math.max(0,frameIndex - envelopeSize / 2);
		int end = Math.min(frameIndex + envelopeSize / 2, getFrames() - 1);
		
		int step = Math.max(1,(start - end)/samplingDensity); 
		
		for(int x = start ; x <= end; x+= step)
		{
			max = Math.max(max, (this.getValueAt(x, channel)));
		}
		return max;
	}
	
	public int getMinOfEnvelope(int channel, int frameIndex, int envelopeSize, int samplingDensity)
	{
		int min = this.getMaxAmplitude();
		
		int start = Math.max(0,frameIndex - envelopeSize / 2);
		int end = Math.min(frameIndex + envelopeSize / 2, getFrames() - 1);
		
		int step = Math.max(1,(start - end)/samplingDensity); 
		
		for(int x = start ; x <= end; x+= step)
		{
			min = Math.min(min, this.getValueAt(x, channel));
		}
		return min;
	}
	
	public int getMinOfEnvelope(int channel, int envelopeSize, int samplingDensity)
	{
		return getMinOfEnvelope(channel, this.getCurrentFrame(),
										envelopeSize, samplingDensity);
	}
	
	public int getMaxOfEnvelope(int channel, int envelopeSize, int samplingDensity)
	{
		return getMaxOfEnvelope(channel, this.getCurrentFrame(),
										envelopeSize, samplingDensity);
	}
	
	public int getMaxAbsOfEnvelope(int channel, int envelopeSize, int samplingDensity)
	{
		return getMaxAbsOfEnvelope(channel, this.getCurrentFrame(),
										envelopeSize, samplingDensity);
	}
	
	public int getCurrentFrame()
	{
		return players[0].getCurrentFrame();
	}
	
	public int getCurrentPlaybackTime()
	{
		return (int) ((getCurrentFrame() - syncIndex) / splitFormat.getFrameRate() * 1000);
	}
	
	private class ChannelPlayer implements Runnable
	{
		private int channel;
		
		private int playIndex;
		private long lastFramePosition = 0;
		private Object indexSync = new Object();
		
		public ChannelPlayer(int channel)
		{
			this.channel = channel;
			playIndex = syncIndex;
		}
		
		public int getCurrentFrame()
		{
			synchronized(indexSync)
			{
				return (playIndex - lines[channel].getBufferSize()) / splitFormat.getFrameSize() + 
						 (int)(lines[channel].getLongFramePosition() - lastFramePosition);
			}
		}
		
		public void setPlayFrame(int frame)
		{
			synchronized(indexSync)
			{
				playIndex = frame * splitFormat.getFrameSize();
			}
			synchronized(playSync)
			{
				playSync.notifyAll();
			}
		}
		
		public void run()
		{
			while(true)
			{
				synchronized(playSync)
				{
					while(!playing || playIndex >= rawData[channel].length)
					{
						try
						{
							playSync.wait();
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
				
				synchronized(indexSync)
				{
					lastFramePosition = lines[channel].getLongFramePosition();
				}
				try{
					lines[channel].write(rawData[channel], this.playIndex, splitFormat.getFrameSize() * frame_interval);
				} catch (ArrayIndexOutOfBoundsException e)
				{
					//Reached end of buffer
				}
				synchronized(indexSync)
				{
					playIndex += splitFormat.getFrameSize() * frame_interval;
				}
			}
		}
	}
	
	public void stop()
	{
		synchronized(playSync)
		{
			playing = false;
		}
			
		for(SourceDataLine line: lines)
		{
			line.stop();
			line.flush();
		}	
	}
	
	public void play()
	{
		synchronized(playSync)
		{
			for(SourceDataLine line: lines)
			{
				line.start();
			}
			
			playing = true;
			playSync.notifyAll();
		}
	}
	
	public boolean isPlaying()
	{
		return this.playing;
	}
	
	public Control[][] getControls()
	{
		Control[][] controls = new Control[lines.length][];
		for(int x = 0; x < controls.length; x++)
		{
			controls[x] = lines[x].getControls();
		}
		return controls;
	}
	
	public void muteAll(boolean muteAll)
	{
		this.muteAll = muteAll;
		muteUpdate();
	}
	
	public void muteChannel(int channel, boolean mute)
	{
		this.lineMutes[channel] = mute;
		muteUpdate();
	}
	
	public void muteUpdate()
	{
		for(int x = 0; x < getChannels(); x++)
		{
			if(muteControls[x] != null && muteControls[x].getValue() != lineMutes[x] || muteAll)
			{
				muteControls[x].setValue(lineMutes[x] || muteAll);
			}
		}
	}
	
	public boolean allMuted()
	{
		return muteAll;
	}
	
	public boolean lineMuted(int channel)
	{
		return lineMutes[channel] || muteAll;
	}

	public int getSyncIndex()
	{
		return syncIndex;
	}
	
	public static Mixer.Info getJavaAudioEngineMixer()
	{
		for(Mixer.Info info : AudioSystem.getMixerInfo())
		{
			if(info.getName().toLowerCase().contains("java"))
				return info;
		}
		return null;
	}

}

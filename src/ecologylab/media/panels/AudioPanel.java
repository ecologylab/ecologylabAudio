package ecologylab.media.panels;
import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

import ecologylab.media.AudioBufferPlayer;


public class AudioPanel extends JPanel
{
	private AudioWaveformPanel waveform;
	
	public AudioPanel(AudioBufferPlayer buffer, SoloListener s)
	{
		this(buffer,s,null,null);
	}
	
	public AudioPanel(AudioBufferPlayer buffer, SoloListener s, ArrayList<Integer> channels)
	{
		this(buffer,s,channels,null);
	}
	
	public AudioPanel(AudioBufferPlayer buffer, SoloListener s, ArrayList<Integer> channels, ArrayList<String> channelNames)
	{
		super();
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		this.setLayout(gridbag);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridheight = 1;
		c.gridwidth = 1;
				
		JPanel controllerPane = new ChannelControllerPanel(buffer, s, channels, channelNames);
		controllerPane.setMinimumSize(new Dimension(120,120));
		
		gridbag.setConstraints(controllerPane, c);
		this.add(controllerPane);
		
		
		waveform = new AudioWaveformPanel(buffer, channels);
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 40.0;
		c.weighty = 1.0;
		c.gridheight = 1;
		c.gridwidth = 1;
		gridbag.setConstraints(waveform, c);
		this.add(waveform);
		
		this.setVisible(true);
	}
	
	public AudioPanel(AudioBufferPlayer buffer)
	{
		this(buffer, null);
	}
	
	public AudioWaveformPanel getWaveform()
	{
		return waveform;
	}
}

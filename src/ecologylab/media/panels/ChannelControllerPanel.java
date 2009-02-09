package ecologylab.media.panels;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.sound.sampled.*;

import ecologylab.media.AudioBufferPlayer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class ChannelControllerPanel extends JPanel implements ActionListener, ChangeListener
{
	private AudioBufferPlayer buffer;
	private ArrayList<Integer> channels;
	private boolean soloing = false;
	
	private JSlider volumeSlider;
	private JSlider panSlider;
	private JComboBox channelSelector;
	private JCheckBox muteAllBox;
	private JCheckBox soloBox;
	
	private FloatControl[] gainControls;
	private FloatControl[] panControls;
	private SoloListener soloer;
	
	private HashMap<Object, Integer> selectionMap = new HashMap<Object, Integer>();
	
	public static final String CHANNEL_SELECTION_COMMAND = "channel";
	public static final String MUTE_CHANGED_COMMAND = "mute_changed";
	public static final String SOLO_COMMAND = "solo";
	
	public ChannelControllerPanel(AudioBufferPlayer b, SoloListener s)
	{
		this(b,s,null,null);
	}
	
	public ChannelControllerPanel(AudioBufferPlayer b, SoloListener s, ArrayList<Integer> c)
	{
		this(b,s,c,null);
	}
	
	public ChannelControllerPanel(AudioBufferPlayer b, SoloListener s, ArrayList<Integer> ch, ArrayList<String> channelNames)
	{
		this.buffer = b;
		this.soloer = s;
		if(ch != null)
		{
			this.channels = ch;
		} else {
			this.channels = new ArrayList<Integer>();
			for(int x = 0; x < b.getChannels(); x++)
			{
				channels.add(x);
			}
		}
		gainControls = new FloatControl[channels.size()];
		panControls = new FloatControl[channels.size()];			
		
		Control[][] controls = b.getControls();
		for(int x : channels)
		{
			System.out.println("Line: " + x);
			for(Control control : controls[x])
			{
				System.out.println(control.toString());
				Control.Type type = control.getType();
				if(type.equals(FloatControl.Type.MASTER_GAIN) || 
					type.equals(FloatControl.Type.VOLUME) ||
					type.equals(FloatControl.Type.AUX_SEND))
				{
					gainControls[x] = (FloatControl)control;
				}
				else if(type.equals(FloatControl.Type.PAN))
				{
					panControls[x] = (FloatControl) control;
				}
			}
		}
		
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		this.setLayout(gridbag);
		
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.gridwidth = 1;
		
		channelSelector = new JComboBox();
		channelSelector.setEditable(false);
		for(int x = 0; x < channels.size(); x++)
		{
			String label = (channelNames != null && x <= channelNames.size())?
										channelNames.get(x):"Channel " + x;
			channelSelector.addItem(label);
			this.selectionMap.put(label, x);
		}
		
		
		
		
		muteAllBox = new JCheckBox("Mute all:");
		muteAllBox.setHorizontalTextPosition(SwingConstants.LEFT);
		muteAllBox.addActionListener(this);
		muteAllBox.setActionCommand(MUTE_CHANGED_COMMAND);
		gridbag.setConstraints(muteAllBox, c);
		add(muteAllBox);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		soloBox = new JCheckBox("Solo:");
		soloBox.setHorizontalTextPosition(SwingConstants.LEFT);
		soloBox.setActionCommand(SOLO_COMMAND);
		
		if(soloer != null)
		{
			soloBox.addActionListener(soloer);
			soloBox.setSelected(false);
			soloer.addChannel(this);
		}
		else
		{
			soloBox.setEnabled(false);
		}
		
		gridbag.setConstraints(soloBox,c);
		add(soloBox);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		channelSelector.addActionListener(this);
		channelSelector.setActionCommand(CHANNEL_SELECTION_COMMAND);
		gridbag.setConstraints(channelSelector,c);
		add(channelSelector);
				
		JLabel gainLabel = new JLabel("Gain:",JLabel.CENTER);
		gridbag.setConstraints(gainLabel, c);
		add(gainLabel);
		
		volumeSlider = new JSlider(0,100);
		volumeSlider.addChangeListener(this);
		gridbag.setConstraints(volumeSlider, c);
		add(volumeSlider);
		
		JLabel panLabel = new JLabel("Pan:",JLabel.CENTER);
		gridbag.setConstraints(panLabel, c);
		add(panLabel);
		
		panSlider = new JSlider(-100,100);
		panSlider.addChangeListener(this);
	
		gridbag.setConstraints(panSlider, c);
		add(panSlider);
							
		if(gainControls != null)
		{
			volumeSlider.setEnabled(true);
		} else {
			volumeSlider.setEnabled(false);
		}
		
		this.setBorder(new BevelBorder(BevelBorder.RAISED));
		
		this.setVisible(true);
		updateControls();
	}

	private int channelSelected()
	{
		return selectionMap.get(channelSelector.getSelectedItem());
	}
	
	public void updateControls()
	{
		int channel = channelSelected();
		if(panControls[channel] != null)
		{
			panSlider.removeChangeListener(this);
			panSlider.setValue((int)(panControls[channel].getValue()*100));
			panSlider.addChangeListener(this);
			
			panSlider.setEnabled(true);
		}
		else
		{
			panSlider.setEnabled(false);
		}
		if(gainControls[channel] != null)
		{
			int volumeValue = (int) (((gainControls[channel].getValue() - gainControls[channel].getMinimum())/(gainControls[channel].getMaximum()-gainControls[channel].getMinimum())) * 100);
			
			volumeSlider.removeChangeListener(this);
			volumeSlider.setValue(volumeValue);
			volumeSlider.addChangeListener(this);
			
			volumeSlider.setEnabled(true);
		}
		else
		{
			volumeSlider.setEnabled(false);
		}
		
	}
	
	public void actionPerformed(ActionEvent arg0)
	{
		if(arg0.getActionCommand().equals(CHANNEL_SELECTION_COMMAND))
		{
			updateControls();
		} else if(arg0.getActionCommand().equals(MUTE_CHANGED_COMMAND))
		{
			buffer.muteAll(this.muteAllBox.isSelected() || (soloer != null && soloer.anySoloed() && !isSoloed()));
		}
	}

	public void stateChanged(ChangeEvent arg0)
	{
		int channel = channelSelected();
		if(arg0.getSource().equals(this.volumeSlider))
		{
			float value = gainControls[channel].getMinimum() + (gainControls[channel].getMaximum()-gainControls[channel].getMinimum()) * 
																	((float)volumeSlider.getValue()/
																	(volumeSlider.getMaximum()) - volumeSlider.getMinimum());
			this.gainControls[channel].setValue(value);
		}
		if(arg0.getSource().equals(this.panSlider))
		{
			this.panControls[channel].setValue(panSlider.getValue()/100.0f);
		}
	}
	
	public boolean isSoloed()
	{
		return this.soloBox.isSelected();
	}
	
}

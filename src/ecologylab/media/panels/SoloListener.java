package ecologylab.media.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SoloListener implements ActionListener
	{
		private ArrayList<ChannelControllerPanel> channels = new ArrayList<ChannelControllerPanel>();		
		private boolean anySoloed = false;
		
		public void addChannel(ChannelControllerPanel ccp)
		{
			channels.add(ccp);
		}
		
		public void actionPerformed(ActionEvent arg0)
		{
			if(arg0.getActionCommand().equals(ChannelControllerPanel.SOLO_COMMAND))
			{
				anySoloed = false;
				for(ChannelControllerPanel channel : channels)
				{
					anySoloed |= channel.isSoloed();
					if(anySoloed)
						break;
				}
				
				ActionEvent e = new ActionEvent(this, 0, ChannelControllerPanel.MUTE_CHANGED_COMMAND);
				
				for(ChannelControllerPanel channel : channels)
				{
					channel.actionPerformed(e);
				}
			}
		}
		
		public boolean anySoloed()
		{
			return anySoloed;
		}
		
	}
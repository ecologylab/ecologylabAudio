package ecologylab.media.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ecologylab.media.AudioBufferPlayer;
import ecologylab.rendering.layout.OverlapAllChildrenLayout;

public class AudioWaveformPanel extends JPanel implements AdjustmentListener, MouseListener,
		MouseWheelListener, ChangeListener
{
	private static final long			serialVersionUID	= 7858299102057847022L;

	private int										envelopeLength		= 100000;

	private int										maxEnvelopeLength;

	private static final int			samplingDensity		= 6;

	// private long pixelTimeInterval;
	private AudioBufferPlayer			buffer;

	private int										currentStartFrame	= 0;

	private Waveform							waveformPanel			= new Waveform();

	private Scrollbar							scrollBar					= new Scrollbar();

	private boolean								isAdjusting				= false;

	private static final int			timeMarkings			= 5;

	private ArrayList<JCheckBox>	checkBoxes				= new ArrayList<JCheckBox>();

	private JLayeredPane					layeredPane;

	private Color									waveformColor			= Color.LIGHT_GRAY;

	private ArrayList<Integer>		channels;

	public final int							tickHeight				= 3;

	public final int							barHeight					= tickHeight + 17;

	public AudioWaveformPanel(AudioBufferPlayer b)
	{
		this(b, null);
	}

	public AudioWaveformPanel(AudioBufferPlayer b, ArrayList<Integer> c)
	{
		super();
		buffer = b;
		if (c != null)
		{
			this.channels = c;
		}
		else
		{
			this.channels = new ArrayList<Integer>();
			for (int x = 0; x < b.getChannels(); x++)
			{
				channels.add(x);
			}
		}

		maxEnvelopeLength = buffer.getBufferLength();

		scrollBar = new Scrollbar(Scrollbar.HORIZONTAL, 0, envelopeLength, 0, (int) buffer
				.getBufferLength());
		updateScrollBounds();
		scrollBar.addMouseListener(this);

		this.setLayout(new BorderLayout());
		this.add(scrollBar, BorderLayout.SOUTH);
		scrollBar.addAdjustmentListener(this);

		layeredPane = new JLayeredPane();
		layeredPane.setLayout(new OverlapAllChildrenLayout());
		layeredPane.add(waveformPanel, new Integer(100));
		layeredPane.add(new SelectionPane(), new Integer(200));

		this.add(layeredPane, BorderLayout.CENTER);
		layeredPane.addMouseWheelListener(this);

		JPanel checkPanel = new JPanel();
		MuteListener muteListener = new MuteListener();

		checkPanel.setLayout(new GridLayout(channels.size(), 1));

		for (int x : channels)
		{
			JCheckBox box = new JCheckBox();
			box.setSelected(!buffer.lineMuted(x));
			box.addActionListener(muteListener);

			checkPanel.add(box);
			checkBoxes.add(box);
		}

		BevelBorder border1 = new BevelBorder(BevelBorder.RAISED);
		EmptyBorder border2 = new EmptyBorder(barHeight, 0, 0, 0);
		checkPanel.setBorder(new CompoundBorder(border1, border2));

		this.add(checkPanel, BorderLayout.WEST);

		Timer repaintTimer = new Timer(41, new RepaintListener());
		repaintTimer.setRepeats(true);
		repaintTimer.start();
	}

	private class RepaintListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			layeredPane.repaint();
		}
	}

	private class MuteListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			for (int x = 0; x < checkBoxes.size(); x++)
			{
				buffer.muteChannel(channels.get(x), !checkBoxes.get(x).isSelected());
			}
		}
	}

	public void updateMuteChecks()
	{
		for (int x = 0; x < checkBoxes.size(); x++)
		{
			checkBoxes.get(x).setSelected(!buffer.lineMuted(x));
		}
	}

	public void updateScrollBounds()
	{
		scrollBar.setVisibleAmount(envelopeLength);
		scrollBar.setBlockIncrement(envelopeLength / 4);
	}

	public boolean isChannelSelected(int channel)
	{
		return checkBoxes.get(channel).isSelected();
	}

	public void setColor(Color c)
	{
		this.waveformColor = c;
	}

	private class SelectionPane extends JPanel
	{
		private static final long	serialVersionUID	= 1420653930962109124L;

		Color	transparency	= new Color(255, 255, 255, 0);

		Color	disabled			= new Color(255, 255, 255, 128);

		public SelectionPane()
		{
			this.setOpaque(false);
		}

		public void paintComponent(Graphics g)
		{

			int channelHeight = (this.getHeight() - barHeight) / channels.size();

			for (int c = 0; c < channels.size(); c++)
			{
				if (buffer.lineMuted(c))
				{
					g.setColor(disabled);

					g.fillRect(0, barHeight + c * channelHeight, this.getWidth(), channelHeight);
				}
			}

		}
	}

	private class Waveform extends JPanel implements ActionListener
	{
		private static final long	serialVersionUID	= -7188326762593254179L;

		private BufferedImage	waveform;

		private VolatileImage	volWaveform		= null;

		private int						barPosition		= 0;

		public final Font			font					= new Font("Default", Font.PLAIN, 10);

		public void paintComponent(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;

			int currentFrame = buffer.getCurrentFrame();

			if (waveform == null
					|| ((currentFrame < currentStartFrame || currentStartFrame + (envelopeLength * 0.8) < currentFrame)
							&& !isAdjusting && buffer.isPlaying()))
			{
				setStartFrame((int) (currentFrame - (envelopeLength * 0.1)));
			}

			drawVolatileImage(g2, volWaveform, 0, 0, waveform);

			barPosition = (int) ((currentFrame - currentStartFrame) / (double) (envelopeLength) * this
					.getWidth());

			g2.setColor(Color.BLACK);
			g2.drawLine(barPosition, 0, barPosition, this.getHeight());
		}

		public void resetBufferedWaveform()
		{
			waveform = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);

			Graphics2D g2 = waveform.createGraphics();

			g2.setColor(waveformColor);
			g2.fillRect(0, barHeight, waveform.getWidth(), waveform.getHeight() - barHeight);

			g2.setColor(waveformColor.darker());
			g2.fillRect(0, 0, waveform.getWidth(), barHeight);

			AffineTransform trans = g2.getTransform();
			Font oldFont = g2.getFont();

			g2.setFont(this.font);

			FontMetrics metrics = g2.getFontMetrics();

			g2.setColor(Color.BLACK);
			for (int x = 0; x < timeMarkings; x++)
			{
				int xOffset = (int) ((x + 0.5f) * waveform.getWidth() / (timeMarkings));
				float timeSeconds = (((float) xOffset / waveform.getWidth() * envelopeLength)
						+ currentStartFrame - buffer.getSyncIndex())
						/ buffer.getFormat().getFrameRate();

				String time = (((Math.signum(timeSeconds) == -1) ? "-" : "") + (int) Math.abs(timeSeconds) / 60)
						+ ":" + (Math.abs(timeSeconds) % 60);

				if (time.lastIndexOf('.') > 0 && time.lastIndexOf('.') < time.length() - 3)
				{
					time = time.substring(0, time.lastIndexOf('.') + 3);
				}

				g2.drawLine(xOffset, 0, xOffset, tickHeight);
				g2.drawString(time, xOffset - metrics.stringWidth(time) / 2, barHeight
						- metrics.getMaxDescent());
			}

			for (int c = 0; c < channels.size(); c++)
			{
				g2.translate(0, (waveform.getHeight() - barHeight) / channels.size() * (0.5f + c)
						+ barHeight);
				g2.scale(1, (waveform.getHeight() - barHeight) / channels.size()
						/ (2 * (double) buffer.getMaxAbsAmplitudeForChannel(c)));

				int pixelEnvWidth = (int) (envelopeLength / waveform.getWidth());
				Point low = new Point(0, 0);
				low.setLocation(-1, buffer.getValueAt(currentStartFrame - pixelEnvWidth, channels.get(c)));

				Point high = new Point(0, 0);

				g2.setColor(Color.DARK_GRAY);

				for (int x = 0; x < waveform.getWidth(); x++)
				{
					for (int y = 0; y < samplingDensity; y++)
					{
						high.setLocation(x, -buffer.getValueAt(currentStartFrame + x * pixelEnvWidth + y
								* pixelEnvWidth / samplingDensity, channels.get(c)));
						g2.drawLine(low.x, low.y, high.x, high.y);
						low.setLocation(high);
					}
				}
				g2.setTransform(trans);
			}

			g2.setFont(oldFont);

			if (this.volWaveform != null)
				this.volWaveform.flush();
		}

		public void setBounds(int x, int y, int width, int heigth)
		{
			super.setBounds(x, y, width, heigth);
			this.resetBufferedWaveform();
		}

		public void actionPerformed(ActionEvent arg0)
		{
			this.repaint();
		}

		// This method draws a volatile image and returns it or possibly a
		// newly created volatile image object. Subsequent calls to this method
		// should always use the returned volatile image.
		// If the contents of the image is lost, it is recreated using orig.
		// img may be null, in which case a new volatile image is created.
		public VolatileImage drawVolatileImage(Graphics2D g, VolatileImage img, int x, int y, Image orig)
		{
			final int MAX_TRIES = 100;
			for (int i = 0; i < MAX_TRIES; i++)
			{
				if (img != null)
				{
					// Draw the volatile image
					g.drawImage(img, x, y, null);

					// Check if it is still valid
					if (!img.contentsLost())
					{
						return img;
					}
				}
				else
				{
					// Create the volatile image
					img = g.getDeviceConfiguration().createCompatibleVolatileImage(orig.getWidth(null),
							orig.getHeight(null));
				}

				// Determine how to fix the volatile image
				switch (img.validate(g.getDeviceConfiguration()))
				{
				case VolatileImage.IMAGE_OK:
					// This should not happen
					break;
				case VolatileImage.IMAGE_INCOMPATIBLE:
					// Create a new volatile image object;
					// this could happen if the component was moved to another device
					img.flush();
					img = g.getDeviceConfiguration().createCompatibleVolatileImage(orig.getWidth(null),
							orig.getHeight(null));
				case VolatileImage.IMAGE_RESTORED:
					// Copy the original image to accelerated image memory
					Graphics2D gc = (Graphics2D) img.createGraphics();
					gc.drawImage(orig, 0, 0, null);
					gc.dispose();
					break;
				}
			}

			// The image failed to be drawn after MAX_TRIES;
			// draw with the non-accelerated image
			g.drawImage(orig, x, y, null);
			return img;
		}

	}

	private void setStartFrame(int frame)
	{
		currentStartFrame = frame;
		scrollBar.setValue(currentStartFrame);
		waveformPanel.resetBufferedWaveform();
	}

	public void adjustmentValueChanged(AdjustmentEvent arg0)
	{
		if (!arg0.getValueIsAdjusting()
				|| Math.abs(arg0.getValue() - currentStartFrame) >= this.envelopeLength / 5)
		{
			currentStartFrame = arg0.getValue();
			waveformPanel.resetBufferedWaveform();
		}
	}

	public void mouseClicked(MouseEvent arg0)
	{
	}

	public void mouseEntered(MouseEvent arg0)
	{
	}

	public void mouseExited(MouseEvent arg0)
	{
	}

	public void mousePressed(MouseEvent arg0)
	{
		isAdjusting = true;
	}

	public void mouseReleased(MouseEvent arg0)
	{
		isAdjusting = false;
	}

	public void mouseWheelMoved(MouseWheelEvent arg0)
	{
		int pointPos = arg0.getPoint().x;
		int timeZoomed = currentStartFrame
				+ (int) ((double) pointPos / waveformPanel.getWidth() * this.envelopeLength);
		int oldEnvelopeLength = envelopeLength;

		envelopeLength *= Math.pow(2, arg0.getWheelRotation());
		envelopeLength = (int) Math.min(maxEnvelopeLength, envelopeLength);

		if (envelopeLength == oldEnvelopeLength)
		{
			// nothing to do already viewing this
			return;
		}

		currentStartFrame = timeZoomed
				- (int) ((double) pointPos / waveformPanel.getWidth() * this.envelopeLength);
		currentStartFrame = Math.max(0, currentStartFrame);

		waveformPanel.resetBufferedWaveform();
		updateScrollBounds();
	}

	public void stateChanged(ChangeEvent arg0)
	{
		this.updateMuteChecks();
	}

}

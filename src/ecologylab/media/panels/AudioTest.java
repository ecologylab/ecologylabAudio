package ecologylab.media.panels;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import ecologylab.media.AudioBufferPlayer;
import ecologylab.media.SyncFinder;

public class AudioTest
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        //Mixer mixer = AudioSystem.getMixer(mixerInfo[0]);
        AudioBufferPlayer buf = null;
        JFileChooser chooser = new JFileChooser();
		    // Note: source for ExampleFileFilter can be found in FileChooserDemo,
		    // under the demo/jfc directory in the JDK.
        
        SyncFinder myFinder = new SyncFinder();
        myFinder.addCheckPoint(SyncFinder.CheckPoint.LOW);
        myFinder.addCheckPoint(SyncFinder.CheckPoint.HIGH);
        
		  int retVal = chooser.showOpenDialog(null);
		  if(retVal == JFileChooser.APPROVE_OPTION)
		  {
			  AudioInputStream myStream1 = null, myStream2 = null;
			  	try
				{
			  		buf = new AudioBufferPlayer(chooser.getSelectedFile(),mixerInfo[5],myFinder,3);
				}
				catch (UnsupportedAudioFileException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (LineUnavailableException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
							
				JFrame myFrame = new JFrame();
								
				AudioPanel view = new AudioPanel(buf);
				
				
				/*myFrame.add(new AudioControllerPanel("Audio Control", myClip1));
				*/
				myFrame.setSize(200,300);
				myFrame.add(view);
				myFrame.setVisible(true);
				myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				buf.play();
				
				
				//myClip1.start();
				//myClip2.start();
				
				/*DataInputStream dStream = new DataInputStream(myStream2);
				
				while(true)
				{
					byte[] bytes = new byte[2];
					ByteBuffer myBuf = ByteBuffer.wrap(bytes);
					myBuf.order(ByteOrder.LITTLE_ENDIAN);
					try
					{
						while(myStream2.available() < 2);
						myStream2.read(bytes);
						System.out.println("Next Short: " + (((double)myBuf.getShort()) / Short.MAX_VALUE) * 10000);
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}*/
				
			}
				
				while(true)
				{
					//System.out.println("Clip 1: " + myClip1.getLevel());
					//System.out.println("Clip 2: " + myClip2.getLevel()+"\n");
					
					try
					{
						Thread.sleep(1000);
					}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
    }
        /*System.out.println("Available mixers:");
        int i = 0;
        for (Mixer.Info info : mixerInfo)
        {
            System.out.println("*** " + (i++) + " " + info.toString());

            Line.Info[] lines = AudioSystem.getMixer(info).getSourceLineInfo();

            System.out.println("source lines: " + lines.length);

            for (Line.Info line : lines)
            {
                System.out.println("-" + line.toString());

                if (line instanceof DataLine.Info)
                {
                    System.out.println("valid formats:");

                    for (AudioFormat a : ((DataLine.Info) line).getFormats())
                    {
                        System.out.println(a.toString());
                    }
                }
                
            }

            lines = AudioSystem.getMixer(info).getTargetLineInfo();

            System.out.println("target lines: " + lines.length);

            for (Line.Info line : lines)
            {
                System.out.println("-" + line.toString());
            }

            System.out.println();
        }*/
   }



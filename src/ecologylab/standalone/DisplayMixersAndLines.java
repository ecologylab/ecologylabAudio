package ecologylab.standalone;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;

public class DisplayMixersAndLines
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        System.out.println("Available mixers:");
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
        }
    }

}

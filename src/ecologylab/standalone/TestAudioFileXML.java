package ecologylab.standalone;


import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import ecologylab.xml.XmlTranslationException;
import ecologylab.xml.library.audiometadata.AudioFileMetadata;

/**
 * @author Zach
 *
 */
public class TestAudioFileXML
{

    /**
     * @param args
     * @throws InvalidAudioFrameException 
     * @throws ReadOnlyFileException 
     * @throws TagException 
     * @throws IOException 
     * @throws CannotReadException 
     * @throws XmlTranslationException 
     * @throws CannotWriteException 
     */
    public static void main(String[] args) throws CannotReadException, IOException, TagException, ReadOnlyFileException, InvalidAudioFrameException, XmlTranslationException, CannotWriteException
    {
        File file = new File("C:\\01 Kryptonite.mp3");
        
        AudioFileMetadata md = new AudioFileMetadata(file);
        
        System.out.println(md.translateToXML());
        
        md.setAlbum("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        System.out.println(md.translateToXML());

        md.commitMetadata();
        
        System.out.println("committed?");
        
        md = new AudioFileMetadata(file);
        
        System.out.println(md.translateToXML());

    }

}

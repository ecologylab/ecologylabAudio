/**
 * 
 */
package ecologylab.xml.library.audiometadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import ecologylab.net.ParsedURL;
import ecologylab.xml.ElementState;
import ecologylab.xml.types.element.Mappable;

/**
 * Library for creating and manipulating XML based upon an audio file's metadata
 * tags and characteristics, such as artist, album, and duration.
 * 
 * Note that when translating FROM an AudioFileMetadata object, the METADATA
 * will NOT be refereshed from the original file, while CHARACTERISTICS of the
 * file (such as duration) will be refreshed. This allows you to store your own,
 * different (modified) metadata and utilize it as cannon, rather than replacing
 * it with the data stored in the file. You may use the refreshMetadataFromFile
 * method to ensure that this object contains the data from the original file,
 * if so desired.
 * 
 * This class can also expose the underlying properties map for the associated
 * file. Note that this map ALWAYS contains the data based upon the file and
 * should not be changed. Also note that since it contains data based on the
 * file, such data may be different from what is stored in the AudioFileMetadata
 * object if that object has been modified after reading from the file.
 * 
 * This class does not yet support writing metadata back to the original audio
 * file.
 * 
 * @author Zach
 * 
 */
public class AudioFileMetadata extends ElementState implements Mappable<String>
{
    @xml_attribute protected String    title;

    @xml_attribute protected String    artist;

    @xml_attribute protected String    album;

    @xml_attribute protected String    year;

    @xml_attribute protected String    comment;

    @xml_attribute protected String    genre;

    @xml_attribute protected String    track;

    @xml_attribute protected long      duration;

    @xml_attribute protected ParsedURL file;

    /** The unique identifier for this object; time when created + track title when created. */
    @xml_attribute String id;
    
    protected Map<String, Object>      propertiesMap;

    /**
     * No-arg constructor for XML conversion.
     * 
     * THIS CONSTRUCTOR SHOULD NOT BE USED! USING THIS CONSTRUCTOR WILL RESULT
     * IN AN INVALID STATE FOR THIS OBJECT!
     */
    @Deprecated public AudioFileMetadata()
    {
    }

    public AudioFileMetadata(File audioFile) throws IOException,
            ClassCastException, UnsupportedAudioFileException
    {
        this.populateMetadataFromFile(audioFile);
        
        this.id = String.valueOf(System.currentTimeMillis())+this.title;
    }

    public AudioFileMetadata(ParsedURL audioFileURL) throws ClassCastException,
            IOException, UnsupportedAudioFileException
    {
        this(audioFileURL.file());
    }

    @SuppressWarnings("unchecked") protected void populateMetadataFromFile(
            File audioFile) throws ClassCastException, IOException,
            UnsupportedAudioFileException
    {
        this.file = new ParsedURL(audioFile);

        this.verifyFileAndLoadProperties();

        this.refreshMetadataFromFile();
    }

    public void refreshMetadataFromFile()
    {
        // metadata
        this.album = (String) propertiesMap.get("album");
        this.artist = (String) propertiesMap.get("author");
        this.comment = (String) propertiesMap.get("comment");
        this.genre = (String) propertiesMap.get("mp3.id3tag.genre");
        this.title = (String) propertiesMap.get("title");
        this.year = (String) propertiesMap.get("date");
        this.track = (String) propertiesMap.get("mp3.id3tag.track");

        // characteristics
        this.duration = (Long) propertiesMap.get("duration");
    }

    /**
     * This method is called after the file attribute has been set; it verifies
     * that the file exists and is a file, then attempts to load it as an audio
     * file and cache its properties map.
     * 
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * 
     */
    private void verifyFileAndLoadProperties()
            throws UnsupportedAudioFileException, IOException
    {
        if (this.file == null || !this.file.file().exists())
        {
            throw new FileNotFoundException();
        }
        else
        {
            File audioFile = this.file.file();
            AudioFileFormat baseFileFormat = AudioSystem
                    .getAudioFileFormat(audioFile);

            propertiesMap = (baseFileFormat).properties();
        }
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the artist
     */
    public String getArtist()
    {
        return artist;
    }

    /**
     * @param artist
     *            the artist to set
     */
    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    /**
     * @return the album
     */
    public String getAlbum()
    {
        return album;
    }

    /**
     * @param album
     *            the album to set
     */
    public void setAlbum(String album)
    {
        this.album = album;
    }

    /**
     * @return the year
     */
    public String getYear()
    {
        return year;
    }

    /**
     * @param year
     *            the year to set
     */
    public void setYear(String year)
    {
        this.year = year;
    }

    /**
     * @return the comment
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * @param comment
     *            the comment to set
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    /**
     * @return the genre
     */
    public String getGenre()
    {
        return genre;
    }

    /**
     * @param genre
     *            the genre to set
     */
    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    /**
     * @return the file
     */
    public ParsedURL getFile()
    {
        return file;
    }

    public long getDuration()
    {
        return duration;
    }

    /**
     * Checks the validity of the associated file and re-loads any
     * characteristics (duration, etc.), but not metadata, from the associated
     * file.
     */
    @Override protected void postTranslationProcessingHook()
    {
        try
        {
            this.verifyFileAndLoadProperties();
        }
        catch (UnsupportedAudioFileException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        super.preTranslationProcessingHook();
    }

    public String getTrack()
    {
        return track;
    }

    public void setTrack(String track)
    {
        this.track = track;
    }

    public Map<String, Object> getPropertiesMap()
    {
        return propertiesMap;
    }

    /**
     * @see ecologylab.xml.types.element.Mappable#key()
     */
    public String key()
    {
        return id;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

}
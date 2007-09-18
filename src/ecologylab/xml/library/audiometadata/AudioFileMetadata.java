/**
 * 
 */
package ecologylab.xml.library.audiometadata;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import ecologylab.net.ParsedURL;
import ecologylab.xml.ElementState;

/**
 * Library for creating and manipulating XML based upon an audio file's metadata tags.
 * 
 * @author Zach
 * 
 */
public class AudioFileMetadata extends ElementState
{
    @xml_attribute String    title;

    @xml_attribute String    artist;

    @xml_attribute String    album;

    @xml_attribute String    year;

    @xml_attribute String    comment;

    @xml_attribute String    genre;

    @xml_attribute ParsedURL file;

    private AudioFile        cachedFile = null;

    /**
     * No-arg constructor for XML conversion.
     * 
     * THIS CONSTRUCTOR SHOULD NOT BE USED! USING THIS CONSTRUCTOR WILL RESULT IN AN INVALID STATE FOR THIS OBJECT!
     */
    @Deprecated public AudioFileMetadata()
    {
    }

    public AudioFileMetadata(File audioFile) throws CannotReadException, IOException, TagException,
            ReadOnlyFileException, InvalidAudioFrameException
    {
        this.populateMetadataFromFile(audioFile);
    }

    public AudioFileMetadata(ParsedURL audioFileURL) throws CannotReadException, IOException, TagException,
            ReadOnlyFileException, InvalidAudioFrameException
    {
        this(audioFileURL.file());
    }

    public void populateMetadataFromFile(File audioFile) throws CannotReadException, IOException, TagException,
            ReadOnlyFileException, InvalidAudioFrameException
    {
        AudioFile newAudioFile = AudioFileIO.read(audioFile);

        this.cachedFile = newAudioFile;

        Tag tag = newAudioFile.getTag();

        this.file = new ParsedURL(audioFile);

        this.album = tag.getFirstAlbum();
        this.artist = tag.getFirstArtist();
        this.comment = tag.getFirstComment();
        this.genre = tag.getFirstGenre();
        this.title = tag.getFirstTitle();
        this.year = tag.getFirstYear();
    }

    /**
     * Writes the current state of this back to the backing audio file's tags.
     * 
     * @throws CannotWriteException
     */
    public void commitMetadata() throws CannotWriteException
    {
        Tag tag = null;

        try
        {
            tag = cachedFile.getTag();
        }
        catch (NullPointerException e)
        {
            this.debug("No backing audio file; fatal error. Do not use the no argument constructor!");
            return;
        }

        tag.setAlbum(this.album);
        tag.setArtist(this.artist);
        tag.setComment(this.comment);
        tag.setYear(this.year);
        tag.setGenre(this.genre);
        tag.setTitle(this.title);
        
        cachedFile.commit();
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

    /**
     * Attempts to cache the backing file for this object after it is created.
     * 
     * @see ecologylab.xml.ElementState#postTranslationProcessingHook()
     */
    @Override protected void postTranslationProcessingHook()
    {
        try
        {
            this.cachedFile = AudioFileIO.read(this.file.file());
        }
        catch (CannotReadException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (TagException e)
        {
            e.printStackTrace();
        }
        catch (ReadOnlyFileException e)
        {
            e.printStackTrace();
        }
        catch (InvalidAudioFrameException e)
        {
            e.printStackTrace();
        }
    }
}

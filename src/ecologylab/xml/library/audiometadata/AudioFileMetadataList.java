/**
 * 
 */
package ecologylab.xml.library.audiometadata;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.types.element.ArrayListState;
import ecologylab.xml.types.element.Mappable;
import ecologylab.xml.types.element.StringState;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 *
 */
public @xml_inherit class AudioFileMetadataList extends ArrayListState<StringState> implements Mappable<String>
{
    @xml_attribute private String title;

    /**
     * 
     */
    public AudioFileMetadataList()
    {
    }
    
    public AudioFileMetadataList(String title)
    {
        this.title = title;
    }

    /**
     * @see ecologylab.xml.types.element.ArrayListState#add(ecologylab.xml.ElementState)
     */
    public boolean add(AudioFileMetadata elementState)
    {
        return super.add(new StringState(elementState.getId()));
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @see ecologylab.xml.types.element.Mappable#key()
     */
    public String key()
    {
        return title;
    }

}

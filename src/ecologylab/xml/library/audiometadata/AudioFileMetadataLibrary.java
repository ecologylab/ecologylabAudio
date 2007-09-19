/**
 * 
 */
package ecologylab.xml.library.audiometadata;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.types.element.ArrayListState;

/**
 * @author Zach
 *
 */
public @xml_inherit class AudioFileMetadataLibrary extends ArrayListState<AudioFileMetadata>
{
    @xml_attribute private String libraryName;

    public String getLibraryName()
    {
        return libraryName;
    }

    public void setLibraryName(String libraryName)
    {
        this.libraryName = libraryName;
    }

    /**
     * 
     */
    public AudioFileMetadataLibrary()
    {
    }

}

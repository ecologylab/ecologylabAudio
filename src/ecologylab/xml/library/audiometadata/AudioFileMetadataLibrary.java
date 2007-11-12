/**
 * 
 */
package ecologylab.xml.library.audiometadata;

import ecologylab.xml.xml_inherit;
import ecologylab.xml.types.element.ArrayListState;
import ecologylab.xml.types.element.HashMapState;

/**
 * @author Zachary O. Toups (toupsz@cs.tamu.edu)
 * 
 */
public @xml_inherit class AudioFileMetadataLibrary extends HashMapState<String, AudioFileMetadata>
{
    @xml_nested private HashMapState<String, AudioFileMetadataList> lists = new HashMapState<String, AudioFileMetadataList>();

    public AudioFileMetadataLibrary()
    {
    }

    /**
     * Adds the given list to the set of stored lists. If there is a title collision, returns the previous list with the
     * same title and removes it.
     * 
     * @param newList
     * @return
     */
    public AudioFileMetadataList addList(AudioFileMetadataList newList)
    {
        return this.lists.put(newList.key(), newList);
    }
    
    public AudioFileMetadataList removeList(String listName)
    {
        return this.lists.remove(listName);
    }
    
    public AudioFileMetadataList getList(String listName)
    {
        return this.lists.get(listName);
    }
}

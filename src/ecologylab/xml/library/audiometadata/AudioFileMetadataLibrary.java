/**
 * 
 */
package ecologylab.xml.library.audiometadata;

import java.util.HashMap;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.annotations.simpl_composite;
import ecologylab.serialization.annotations.simpl_map;
import ecologylab.serialization.annotations.simpl_nowrap;

/**
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public class AudioFileMetadataLibrary extends ElementState
{
	@simpl_map @simpl_nowrap
	protected HashMap<String, AudioFileMetadata>		map		= new HashMap<String, AudioFileMetadata>();

	@simpl_composite
	private HashMap<String, AudioFileMetadataList>	lists	= new HashMap<String, AudioFileMetadataList>();

	public AudioFileMetadataLibrary()
	{
	}

	/**
	 * Adds the given list to the set of stored lists. If there is a title collision, returns the
	 * previous list with the same title and removes it.
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

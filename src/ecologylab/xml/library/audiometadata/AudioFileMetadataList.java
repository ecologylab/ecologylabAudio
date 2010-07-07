/**
 * 
 */
package ecologylab.xml.library.audiometadata;

import java.util.ArrayList;

import ecologylab.xml.ElementState;
import ecologylab.xml.simpl_inherit;
import ecologylab.xml.types.element.Mappable;
import ecologylab.xml.types.element.StringState;

/**
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public @simpl_inherit
class AudioFileMetadataList extends ElementState implements Mappable<String>
{
	@simpl_collection
	@simpl_nowrap
	ArrayList<StringState>	list	= new ArrayList<StringState>();

	@simpl_scalar
	private String					title;

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
		return list.add(new StringState(elementState.getId()));
	}

	/**
	 * @param title
	 *          the title to set
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

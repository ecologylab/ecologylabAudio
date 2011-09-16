/**
 * 
 */
package ecologylab.xml.library.audiometadata;

import java.util.ArrayList;

import ecologylab.serialization.ElementState;
import ecologylab.serialization.annotations.simpl_collection;
import ecologylab.serialization.annotations.simpl_inherit;
import ecologylab.serialization.annotations.simpl_nowrap;
import ecologylab.serialization.annotations.simpl_scalar;
import ecologylab.serialization.types.element.IMappable;
import ecologylab.serialization.types.element.StringState;

/**
 * @author Zachary O. Toups (zach@ecologylab.net)
 * 
 */
public @simpl_inherit
class AudioFileMetadataList extends ElementState implements IMappable<String>
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
	 * @see ecologylab.serialization.types.element.ArrayListState#add(ecologylab.serialization.ElementState)
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
	 * @see ecologylab.serialization.types.element.Mappable#key()
	 */
	public String key()
	{
		return title;
	}

}

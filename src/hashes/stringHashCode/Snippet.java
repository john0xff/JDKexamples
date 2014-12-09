package hashes.stringHashCode;

/**
 * Part of string class
 * 
 * @author BartBien
 *
 */
public final class Snippet
{
	/** The value is used for character storage. */
	private final char value[];

	/** Cache the hash code for the string */
	private int hash; // Default to 0

	public Snippet()
	{
		this.value = new char[0];
	}

	public int hashCode()
	{
		int h = hash;
		if (h == 0 && value.length > 0)
		{
			char val[] = value;

			for (int i = 0; i < value.length; i++)
			{
				h = 31 * h + val[i];
			}
			hash = h;
		}
		return h;
	}
}

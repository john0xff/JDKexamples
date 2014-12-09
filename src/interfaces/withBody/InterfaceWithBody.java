package interfaces.withBody;

import java.util.stream.IntStream;

public class InterfaceWithBody
{
	// public interface CharSequence
	// with default method with body..
	// public default IntStream codePoints() {

	// public final class StringBuilder
	// extends AbstractStringBuilder
	// implements java.io.Serializable, CharSequence

	public InterfaceWithBody()
	{
		CharSequence charSequence = new StringBuilder();
		charSequence.chars();
	}
}

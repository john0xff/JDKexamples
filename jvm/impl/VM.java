package impl;
public class VM
{


	/**
	 * ObjectInputStream <-
	 * 
	 * Returns the first non-null class loader (not counting class loaders of generated reflection implementation
	 * classes) up the execution stack, or null if only code from the null class loader is on the stack. This method is
	 * also called via reflection by the following RMI-IIOP class:
	 *
	 * com.sun.corba.se.internal.util.JDKClassLoader
	 *
	 * This method should not be removed or its signature changed without corresponding modifications to the above
	 * class.
	 */
	private static ClassLoader latestUserDefinedLoader()
	{
		return sun.misc.VM.latestUserDefinedLoader();
	}
}

package reflection.exampleOfUsage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

public class SwingEventListenerList
{
	EventListenerList ev;
	
	private final static Object[] NULL_ARRAY = new Object[0];
	protected transient Object[] listenerList = NULL_ARRAY;

	// Serialization support.
	private void writeObject(ObjectOutputStream s) throws IOException
	{
		Object[] lList = listenerList;
		s.defaultWriteObject();

		// Save the non-null event listeners:
		for (int i = 0; i < lList.length; i += 2)
		{
			Class<?> t = (Class) lList[i];
			EventListener l = (EventListener) lList[i + 1];
			if ((l != null) && (l instanceof Serializable))
			{
				s.writeObject(t.getName());
				s.writeObject(l);
			}
		}

		s.writeObject(null);
	}

	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException
	{
		listenerList = NULL_ARRAY;
		s.defaultReadObject();
		Object listenerTypeOrNull;

		while (null != (listenerTypeOrNull = s.readObject()))
		{
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			EventListener l = (EventListener) s.readObject();
			String name = (String) listenerTypeOrNull;
			// ReflectUtil.checkPackageAccess(name);
			// add((Class<EventListener>) Class.forName(name, true, cl), l);
		}
	}
}

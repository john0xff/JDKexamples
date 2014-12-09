package reflection.oracle;

import java.lang.reflect.Field;



/**
 * http://www.tutorialspoint.com/java/lang/class_getfields.htm
 * 
 * 
 * @author BartBien
 *
 */
public class GetClass
{
	
	
	public static void main(String[] args)
	{
		String a = new String("test");
		
		Class c = a.getClass();
		
		System.out.println(c);
		
		
		//Person p = new Person(10, "John", "Long Street");
		
		Class cls;
		try
		{
			cls = Class.forName("oracle.Person");
			System.out.println(cls);
			
			Field[] fields = cls.getFields();
			
			
			for(int i = 0; i < fields.length; i++)
			{
				System.out.println(fields[i]);
			}
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

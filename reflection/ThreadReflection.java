import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;


public class ThreadReflection
{
// code from Thread class.	
//	 @CallerSensitive
//	    public ClassLoader getContextClassLoader() {
//	        if (contextClassLoader == null)
//	            return null;
//	        SecurityManager sm = System.getSecurityManager();
//	        if (sm != null) {
//	            ClassLoader.checkClassLoaderPermission(contextClassLoader,
//	                                                   Reflection.getCallerClass());
//	        }
//	        return contextClassLoader;
//	    }

	public static void main(String[] args)
	{
		//ClassLoader.
		
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		
		System.out.println(cl);
	}
}
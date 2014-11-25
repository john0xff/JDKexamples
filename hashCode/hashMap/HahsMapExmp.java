package hashMap;

import java.util.HashMap;

public class HahsMapExmp
{

	public static void main(String[] args)
	{
		HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
		
		hashMap.put(new Integer(1), "test");
		
		hashMap.get(1);
		hashMap.values();
	}

}

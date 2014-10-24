package bigData;

import java.util.TreeMap;

public class TrieSample
{
	class Counter
	{
		private int count = 0;

		public int getCount()
		{
			return count;
		}

		public void setCount(int count)
		{
			this.count = count;
		}

		public void addCount()
		{
			this.count++;
		}
	}

	public static void main(String[] args)
	{
		String[] strings =
		{ "ben", "john", "merry", "ammy" };

		TreeMap<String, Counter> treeMap = new TreeMap<String, Counter>();

		for (int i = 0; i < strings.length; i++)
		{
			
			treeMap.values();
			
//			if (strings[i] == treeMap.get("john"))
//			{
//				
//			}
		}

		System.out.println();

	}

}

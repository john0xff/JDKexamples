package examples2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexSearch
{
	public void readFile()
	{
		File file = new File("regex/examples2/regext.txt");
		BufferedReader bufferedReader = null;
		try
		{
			bufferedReader = new BufferedReader(new FileReader(file));
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try
		{
			int lines = Integer.parseInt(bufferedReader.readLine());

			for (int i = 0; i < lines; i++)
			{
				System.out.println(bufferedReader.readLine());
			}

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		Integer counter = new Integer(0);
		
		TreeMap<Integer, String> map = new TreeMap<Integer, String>();
		
		map.put(counter++, "a");
		map.put(counter++, "b");
		map.put(counter++, "a");
		map.put(counter++, "b");
		
		for (int i = 0; i < map.size(); i++)
		{
			System.out.println(map.get(i));
		}
		
		Set<Integer> set = map.keySet();
		System.out.println(set.size());
		

//		Set<String> set = new TreeSet<String>();
//		set.add("a");
//		set.add("a");
//		set.add("b");
//		
//		for (String string : set)
//		{
//			System.out.println(string);
//		}

//		String regex = "";
//		Pattern pattern = Pattern.compile(regex);
	}
}

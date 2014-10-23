package bigData;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

import jdk.nashorn.internal.runtime.regexp.joni.Regex;

/**
 * http://stackoverflow.com/questions/12190326/parsing-one-terabyte-of-text-and-efficiently-counting-the-number-of-occurrences 
 * 
 * http://en.wikipedia.org/wiki/Trie
 * 
 * @author Bart88
 *
 */
public class FewTBData
{
	private static class Counter
	{
		private int count = 0;

		public int getCount()
		{
			return this.count;
		}

		public void reset()
		{
			this.count = 0;
		}

		public void signalize()
		{
			this.count += 1;
		}
	}

	public static void main(String[] args) throws IOException
	{
		TreeMap<String, Counter> words = new TreeMap<String, Counter>();

		FileInputStream stream = null;
		Scanner scanner = null;

		Pattern pattern = Pattern.compile("\\S+");
		
		try
		{
			stream = new FileInputStream("regex/bigData/regext.txt");
			//stream = new FileInputStream("C:/Users/Bart88/Desktop/bigData - Copy.txt");
			scanner = new Scanner(stream);
			
//			String word = scanner.next(pattern);
//			String word2 = scanner.next(pattern);
//			String word3 = scanner.next(pattern);
//			String word4 = scanner.next(pattern);
			
			
			while(scanner.hasNext())
			{
				String tmp = scanner.next(pattern);
				String word = tmp.replaceAll("[\\.,:!?\\[\\]\\{\\}]+", "");
				
				Counter counter = words.get(word);
				
				if(counter == null)
					words.put(word, counter = new Counter());
				
				counter.signalize();
			}
			
			for(String key : words.keySet())
			{
				Counter value = words.get(key);
				System.out.println(key + ": " + value.getCount());
			}
		}
		finally
		{
			if(scanner != null)
				scanner.close();
			
			if (stream != null)
				stream.close();
		}
	}

}

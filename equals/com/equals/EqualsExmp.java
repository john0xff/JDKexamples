package com.equals;

import java.util.ArrayList;

/**
 * Immutable objects are simply objects whose state (the object's data) cannot change after construction. Examples of
 * immutable objects from the JDK include String and Integer.
 * 
 * == tests for reference equality //// .equals() tests for value equality.
 * 
 * @author BartBien
 *
 */
public class EqualsExmp
{

	public static void main(String[] args)
	{
//		equalsString();
//		equalsString2();

		// equalsInteger();
		// equalsInteger2();

		// equalsArrayList();
		
		equalsNull();
	}

	private static void equalsString()
	{
		String a = "aaaaaaaaaaaaaaaaaaaa";
		String b = "aaaaaaaaaaaaaaaaaaaa";

		if (a == b)
			System.out.println("a == b"); // here we feel the difference
		else
			System.out.println("a != b");

		if (a.equals(b))
			System.out.println("a equals b");
		else
			System.out.println("a !equals b");
	}

	private static void equalsString2()
	{
		String a = new String("aaaaaaaaaaaaaaaaaaaa");
		String b = new String("aaaaaaaaaaaaaaaaaaaa");

		if (a == b)
			System.out.println("a == b"); // here we feel the difference
		else
			System.out.println("a != b");

		if (a.equals(b))
			System.out.println("a equals b");
		else
			System.out.println("a !equals b");
	}

	private static void equalsInteger()
	{
		Integer a = 1;
		Integer b = 1;

		if (a == b)
			System.out.println("a == b"); // here we feel the difference
		else
			System.out.println("a != b");

		if (a.equals(b))
			System.out.println("a equals b");
		else
			System.out.println("a !equals b");
	}

	private static void equalsInteger2()
	{
		Integer a = new Integer(1);
		Integer b = new Integer(1);

		if (a == b)
			System.out.println("a == b"); // here we feel the difference
		else
			System.out.println("a != b");

		if (a.equals(b))
			System.out.println("a equals b");
		else
			System.out.println("a !equals b");
	}

	private static void equalsArrayList()
	{
		ArrayList<String> list1 = new ArrayList<String>();
		list1.add("a");
		ArrayList<String> list2 = new ArrayList<String>();
		list2.add("a");

		// list2 = list1; assign the reference - after this list1 == list2

		if (list1 == list2)
			System.out.println("list1 == list2");
		else
			System.out.println("list1 != list2"); // here we feel the difference

		if (list1.equals(list2))
			System.out.println("list1 equals list2");
		else
			System.out.println("list1 !equals list2");
	}

	private static void equalsNull()
	{
		ArrayList<String> list1 = null;

		ArrayList<String> list2 = null;

		// list2 = list1; assign the reference - after this list1 == list2

		if (list1 == list2)
			System.out.println("list1 == list2");
		else
			System.out.println("list1 != list2");

		if (list1.equals(list2)) // java.lang.NullPointerException
			System.out.println("list1 equals list2");
		else
			System.out.println("list1 !equals list2");
	}

}

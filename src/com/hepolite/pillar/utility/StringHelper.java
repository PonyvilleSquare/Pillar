package com.hepolite.pillar.utility;

public class StringHelper
{
	/** Converts the string to a string where the first letter in every word is capitalized */
	public static String toTitleCase(String string)
	{
		String[] arr = string.toLowerCase().split(" ");
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < arr.length; i++)
			sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
		return sb.toString().trim();
	}
}

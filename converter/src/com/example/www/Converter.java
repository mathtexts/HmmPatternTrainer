package com.example.www;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {

	private static final Pattern structurePattern = Pattern
			.compile("(\\w+)\\t+([^\\t~]+)(\\t~\\t(.+))?");

	private static final String nl = System.getProperty("line.separator");

	private void convert(String inFile, String outFile) 
	{    	
		try {
			BufferedReader dataReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
			BufferedWriter dataWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
			
			String line;
			
			while ((line = dataReader.readLine()) != null) {

				String[] words = line.split(" ");
				
				if (words[0].equals("#")) {
					
					for (int i=1; i<words.length; i++) {
						
						BufferedReader patternsReader = new BufferedReader(new InputStreamReader(new FileInputStream("/home/nicolas/Desktop/patterns")));
						String w = words[i];
						String rawPattern;
						
						dataWriter.write(w);
						
						Boolean matches = false;
						
						while ((rawPattern = patternsReader.readLine()) != null) 
						{
							Matcher structureMatcher = structurePattern.matcher(rawPattern);
							
							// work only with correct type patterns
							if (structureMatcher.matches()) 
							{
								Pattern typePattern = Pattern.compile(structureMatcher.group(2));
								Matcher typeMatcher = typePattern.matcher(w);
								
								matches = typeMatcher.matches() || matches;
								
								if (typeMatcher.matches()) {
									dataWriter.write(" " + structureMatcher.group(1));
									dataWriter.write(" " + structureMatcher.group(1).toLowerCase());
								}
							}	
							else {
								System.out.println("Pattern '" + rawPattern + "' is incorrect");
							}
						}
						
						if (!matches) {
							dataWriter.write(" OTHER");	
							dataWriter.write(" other");	
						}
						
						dataWriter.write(nl);
						patternsReader.close();
					}

					dataWriter.write(nl);
				}
			}
			dataWriter.flush();

			dataReader.close();
			dataWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void main (String[] args) {
		Converter c = new Converter();
//		c.convert(args[0], args[1]);
		
		String in = "/home/nicolas/Desktop/in.txt";
		String out = "/home/nicolas/Desktop/out.txt";
		c.convert(in, out);
	}
}
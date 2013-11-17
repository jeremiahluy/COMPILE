import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.EmptyStackException;

import objects.VisualNovel;

public class Driver {
	public static void main(String args[]){
		/*
		 * CG
		 * Additional Commands
		 * Bottom Up Parser
		 * */
		
		String fileName = "proto";
		String fileExtension = "str";
		try {
			//Read the story file written by the user
			BufferedReader reader = new BufferedReader(new FileReader(fileName + "." + fileExtension));
			String line = null;
			
			LineNumberReader  lnr = new LineNumberReader(new FileReader(fileName + "." + fileExtension));
			lnr.skip(Long.MAX_VALUE);
			
			LexicalAnalyzer lA = new LexicalAnalyzer();
			
			int[] lineNumbers = new int[lnr.getLineNumber() + 2];
			int currLine = 0;
			lineNumbers[currLine] = 1;
			
			while ((line = reader.readLine()) != null) {
				//Give out each line to the Lexical Analyzer to print the tokens.
				lA.analyze(line.toCharArray());
				currLine++;
				
				lineNumbers[currLine] = lA.getTokenList().size();
			}
			
			lA.end();
			
			BottomUpParser a = new BottomUpParser(lineNumbers);
			
			try {
				a.s0(lA.getTokenList(), 0);
			} catch (IndexOutOfBoundsException e) {
			} catch (EmptyStackException e) {
			} finally {
				a.end();
				
				VisualNovel v = new VisualNovel();
				
				v.setBgList(a.getBGList());
				v.setCharList(a.getCharList());
				v.setChapList(a.getChapterList());
				v.setCgList(a.getCGList());
				
				System.out.println(v.generateJS());
				reader.close();
				lnr.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

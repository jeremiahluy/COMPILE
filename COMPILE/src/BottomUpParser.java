import java.util.ArrayList;
import java.util.Stack;

import objects.Background;
import objects.Chapter;
import objects.ChapterChoice;
import objects.Emotion;
import objects.Graphics;
import objects.StoryCharacter;
import action.Action;
import action.AssignmentAction;
import action.BranchAction;
import action.ChangeBGAction;
import action.CharacterEnterAction;
import action.CharacterExitAction;
import action.EmotionAction;
import action.MessageAction;
import action.MultiAction;


public class BottomUpParser {

	private Stack<String> stack = new Stack<String>();
	private Stack<String> errorList = new Stack<String>();
	private ArrayList<Background> bgList = new ArrayList<Background>();
	private ArrayList<Graphics> cgList = new ArrayList<Graphics>();
	private ArrayList<StoryCharacter> charList = new ArrayList<StoryCharacter>();
	private ArrayList<Chapter> chapterList = new ArrayList<Chapter>();
	private StoryCharacter currentChar;
	private Chapter currentChapter;
	private String currentVariable, currentURL;
	private int[] lineNumbers;

	//--------------------------------

	private final boolean DEBUG = false;
	private Stack<Object> parserStack = new Stack<Object>();
	private int currentState = 0;

	private final int BACKGROUND_STATE = 0,
			CHAPTER_STATE = 2,
			CG_STATE = 3,
			EMO_STATE = 4;

	public BottomUpParser(int[] ln) {
		lineNumbers = ln;
	}

	//Start
	public void s0(ArrayList<Token> tokenList, int index){
		if(DEBUG)

			System.out.println("Current State: s0");
		Object o = null;
		if(!parserStack.isEmpty()){
			o = parserStack.pop();
			if(o instanceof Token && ((Token) o).getType() == Token.END_TOKEN)
				o = parserStack.pop();
			//Reduce to Start
			if(o.getClass().equals(ArrayList.class)) {
				ArrayList list = (ArrayList) o;
				if(list.size() > 0)
					//.getClass().equals(
					if(list.get(0) instanceof Background){
						for(int i = 0; i < list.size(); i++) {
							Background b = (Background) list.get(i);
							saveBackground(b.getBackgroundName(), b.getBackgroundURL());
							//							System.out.println(b.toString());
						}
					} else if(list.get(0) instanceof Graphics){
						for(int i = 0; i < list.size(); i++) {
							Graphics g = (Graphics) list.get(i);
							saveGraphics(g.getGraphicsName(), g.getGraphicsURL());
							//							System.out.println(g.toString());
						}
					} else if(list.get(0) instanceof StoryCharacter){
						for(int i = 0; i < list.size(); i++) {
							StoryCharacter s = (StoryCharacter) list.get(i);
							saveChar(s.getCharName(), s.getEmotionList());
							//							System.out.println(s.toString());
						}
					} else if(list.get(0) instanceof ChapterChoice){
						for(int i = 0; i < list.size(); i++) {
							ChapterChoice c = (ChapterChoice) list.get(i);
						}
					} 
				s0(tokenList, index);
			} else if(o instanceof Action){
				ArrayList<Action> actionList = new ArrayList<Action>();
				while(o instanceof Action){
					actionList.add(0, (Action) o);
					o = parserStack.pop();
				}
				parserStack.push(o);
				//Pop Colon
				parserStack.pop();
				String chapName = ((Token) parserStack.pop()).getToken();

				Chapter c = saveChapter(chapName);
				for(Action a : actionList)
					c.addAction(a);
			} else parserStack.push(o);
		}

		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Start -> @ BG
		if(currToken.getType() == Token.BACKGROUND_DEFINITION) {
			currentState = BACKGROUND_STATE;
			s1(tokenList, index + 1);
		} else if(currToken.getType() == Token.CG_DEFINITION) {
			currentState = CG_STATE;
			s7(tokenList, index + 1);
		} else if(currToken.getType() == Token.CHARACTER_DEFINITION) {
			s8(tokenList, index + 1);
		} else if(currToken.getType() == Token.STORY_START || currToken.getType() == Token.VARIABLE) {
			s14(tokenList,index + 1);
		} else if(currToken.getType() == Token.END_TOKEN){
			s0(tokenList, index + 1);
		}

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Erase, Convert, Add
		if(nextToken.getType() == Token.L_BRACE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s0");
			s1(tokenList, index + 1);

		} else if(nextToken.getType() == Token.END_OF_FUNCTION) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s0");
			s14(tokenList, index + 1);

		} else if(currToken.getType() == Token.L_BRACE) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s0");
			s1(tokenList, index);

		} else if(currToken.getType() == Token.END_OF_FUNCTION) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s0");
			s14(tokenList, index);

		} else if(nextToken.getType() == Token.BACKGROUND_DEFINITION ||
				nextToken.getType() == Token.CG_DEFINITION || 
				nextToken.getType() == Token.CHARACTER_DEFINITION || 
				nextToken.getType() == Token.STORY_START ||
				nextToken.getType() == Token.VARIABLE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s0");
			s0(tokenList, index + 1);

		} 

	}

	//Background: Background @ { Ass }
	public void s1(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s1");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Background: Background @ { Ass }		
		if(currToken.getType() == Token.L_BRACE)
			s2(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Erase, Convert, Add
		if(nextToken.getType() == Token.VARIABLE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s1");
			s2(tokenList, index + 1);

		} else if(currToken.getType() == Token.VARIABLE) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s1");
			s2(tokenList, index);

		} else if(nextToken.getType() == Token.L_BRACE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s1");
			s1(tokenList, index + 1);

		} 
	}

	//Background: Background { @ Ass }
	//Ass: @ VAR = URL;
	public void s2(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s2");
		Token currToken = tokenList.get(index);

		//Background: Background { @ Ass }		
		if(parserStack.get(parserStack.size() - 1) instanceof AssignmentAction)
			s3(tokenList, index);

		parserStack.push(currToken);

		//Ass: @ VAR = URL;
		if(currToken.getType() == Token.VARIABLE)
			s4(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.ASSIGNMENT_OPERATION) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s2");
			s4(tokenList, index + 1);

		} else if(currToken.getType() == Token.ASSIGNMENT_OPERATION) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s2");
			s4(tokenList, index);

		} else if(nextToken.getType() == Token.VARIABLE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s2");
			s2(tokenList, index + 1);

		} 
	}

	//Background: Background { Ass @ }
	//Background: Background { @ Ass }
	//CG: CG { Ass @ }
	//CG { @ Ass }
	//Character: VAR { Emotion @ }
	//Character: VAR { @ Emotion }
	public void s3(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s3");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		ArrayList<Background> bg = new ArrayList<Background>();
		ArrayList<Graphics> cg = new ArrayList<Graphics>();
		ArrayList<Emotion> emo = new ArrayList<Emotion>();

		//Background: Background { Ass @ }
		if(currToken.getType() == Token.R_BRACE){
			//Reduce to BG
			//Pop Right Brace
			parserStack.pop();
			Object o = null;
			//Pop all Background Assignments
			do {
				o = parserStack.pop();
				if(o instanceof AssignmentAction){
					AssignmentAction a = (AssignmentAction) o;
					if(currentState == BACKGROUND_STATE)
						bg.add(new Background(a.getVarName(), a.getURL(), 0));
					else if(currentState == CG_STATE)
						cg.add(new Graphics(a.getVarName(), a.getURL(), 0));
					else if(currentState == EMO_STATE)
						emo.add(new Emotion(a.getVarName(), a.getURL(), ""));


				} else parserStack.push(o);
			} while(o instanceof AssignmentAction);
			if(currentState == BACKGROUND_STATE) {
				//Pop Left Brace
				parserStack.pop();
				//Pop Background Keyword
				parserStack.pop();
				parserStack.push(bg);
			} else if(currentState == CG_STATE) {
				//Pop Left Brace
				parserStack.pop();
				//Pop CG Keyword
				parserStack.pop();
				parserStack.push(cg);
			} else if(currentState == EMO_STATE) {
				parserStack.push(emo);
				s12(tokenList, index);
			}
			s0(tokenList, index + 1);
		} else if(currToken.getType() == Token.VARIABLE) {
			s4(tokenList, index + 1);
		}

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.ASSIGNMENT_OPERATION) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s3");
			s4(tokenList, index + 1);

		} else if(currToken.getType() == Token.ASSIGNMENT_OPERATION) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s3");
			s4(tokenList, index);

		} else if(nextToken.getType() == Token.VARIABLE ||
				nextToken.getType() == Token.R_BRACE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s3");
			s3(tokenList, index + 1);

		}
	}

	//Ass: VAR @ = URL;
	public void s4(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s4");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Ass: VAR @ = URL;		
		if(currToken.getType() == Token.ASSIGNMENT_OPERATION)
			s5(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.URL) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s4");
			s5(tokenList, index + 1);

		} else if(currToken.getType() == Token.URL) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s4");
			s5(tokenList, index);

		} else if(nextToken.getType() == Token.ASSIGNMENT_OPERATION){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s4");
			s4(tokenList, index + 1);

		} 
	}

	//Ass: VAR = @ URL;
	public void s5(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s5");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Ass: VAR = @ URL;		
		if(currToken.getType() == Token.URL)
			s6(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.END_OF_STATEMENT) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s5");
			s6(tokenList, index + 1);

		} else if(currToken.getType() == Token.END_OF_STATEMENT) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s5");
			s6(tokenList, index);

		} else if(nextToken.getType() == Token.URL){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s5");
			s5(tokenList, index + 1);

		} 
	}

	//Ass: VAR = URL @ ;
	public void s6(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s6");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Ass: VAR = URL; @
		if(currToken.getType() == Token.END_OF_STATEMENT) {
			//Reduce to Ass
			//Pop Semicolon
			parserStack.pop();
			//Pop URL
			String URL = ((Token) parserStack.pop()).getToken();
			//Pop Equal Sign
			parserStack.pop();
			//Pop Variable Name
			String varName = ((Token) parserStack.pop()).getToken();

			parserStack.push(new AssignmentAction(varName, URL));
			s2(tokenList, index + 1);
		}

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.VARIABLE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s6");
			s2(tokenList, index + 1);

		} else if(currToken.getType() == Token.VARIABLE) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s6");
			s2(tokenList, index);

		} else if(nextToken.getType() == Token.END_OF_STATEMENT){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s6");
			s6(tokenList, index + 1);

		} 
	}

	//CG: CG @ { Ass }
	public void s7(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s7");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Background: Background @ { Ass }		
		if(currToken.getType() == Token.L_BRACE)
			s2(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Erase, Convert, Add
		if(nextToken.getType() == Token.VARIABLE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s7");
			s2(tokenList, index + 1);

		} else if(currToken.getType() == Token.VARIABLE) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s7");
			s2(tokenList, index);

		} else if(nextToken.getType() == Token.L_BRACE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s7");
			s7(tokenList, index + 1);

		} 
	}

	//Character_Block: Character @ { Character_Assignment }
	public void s8(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s8");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Character_Block: Character @ { Character_Assignment }		
		if(currToken.getType() == Token.L_BRACE)
			s9(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.VARIABLE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s8");
			s9(tokenList, index + 1);

		} else if(currToken.getType() == Token.VARIABLE) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s8");
			s9(tokenList, index);

		} else if(nextToken.getType() == Token.L_BRACE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s8");
			s8(tokenList, index + 1);

		} 
	}

	//Character_Block: Character { @ Character_Assignment }
	//Character_Assignment: @ VAR { Emotion }
	public void s9(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s9");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Character_Assignment: @ VAR { Emotion }		
		if(currToken.getType() == Token.VARIABLE)
			s10(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.L_BRACE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s9");
			s10(tokenList, index + 1);

		} else if(currToken.getType() == Token.L_BRACE) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s9");
			s10(tokenList, index);

		} else if(nextToken.getType() == Token.VARIABLE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s9");
			s9(tokenList, index + 1);

		} 
	}

	//Character_Assignment: VAR @ { Emotion }
	public void s10(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s10");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Character_Assignment: VAR @ { Emotion }		
		if(currToken.getType() == Token.L_BRACE)
			s11(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.VARIABLE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s10");
			s11(tokenList, index + 1);

		} else if(currToken.getType() == Token.VARIABLE) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s10");
			s11(tokenList, index);

		} else if(nextToken.getType() == Token.L_BRACE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s10");
			s10(tokenList, index + 1);

		} 
	}

	//Character_Assignment: VAR { @ Emotion }
	public void s11(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s11");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		currentState = EMO_STATE;

		//Character_Assignment: VAR { @ Emotion }		
		if(currToken.getType() == Token.VARIABLE)
			s4(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.ASSIGNMENT_OPERATION) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s11");
			s4(tokenList, index + 1);

		} else if(currToken.getType() == Token.ASSIGNMENT_OPERATION) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s11");
			s4(tokenList, index);

		} else if(nextToken.getType() == Token.VARIABLE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s11");
			s11(tokenList, index + 1);

		} 
	}

	//Character_Assignment: VAR { Emotion @ }
	public void s12(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s12");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Character_Block: Character { @ Character_Assignment }
		if(currToken.getType() == Token.R_BRACE) {
			//Reduce to Character_Assignment
			//Pop Right Brace
			parserStack.pop();
			//Pop Emotions
			ArrayList<Emotion> emoList = (ArrayList<Emotion>) parserStack.pop();
			//Pop Left Brace
			parserStack.pop();
			Token charToken = (Token) parserStack.pop();

			StoryCharacter c = new StoryCharacter(charToken.getToken(), 0);
			for(Emotion e : emoList)
				saveEmotion(c, e.getEmoName(), e.getEmoURL());

			parserStack.push(c);
			s13(tokenList, index + 1);
		}

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.R_BRACE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s12");
			s13(tokenList, index + 1);

		} else if(currToken.getType() == Token.R_BRACE) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s12");
			s13(tokenList, index);

		} 
	}

	//Character_Block: Character { Character_Assignment @ }
	//Character_Block: Character { @ Character_Assignment }
	public void s13(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s13");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Character_Block: Character { Character_Assignment @ }
		if(currToken.getType() == Token.R_BRACE) {
			ArrayList<StoryCharacter> charList = new ArrayList<StoryCharacter>();
			//Pop Right Brace
			parserStack.pop();
			//Pop Characters
			Object o = null;
			do {
				o = parserStack.pop();
				if(o instanceof StoryCharacter)
					charList.add((StoryCharacter) o);
				else parserStack.push(o);
			} while(o instanceof StoryCharacter);
			//Pop Left Brace
			parserStack.pop();
			//Pop Character Keyword
			parserStack.pop();

			parserStack.push(charList);
			s0(tokenList, index + 1);

			//Character_Block: Character { @ Character_Assignment }
		} else if(currToken.getType() == Token.VARIABLE) {
			s10(tokenList, index + 1);
		}

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.L_BRACE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s13");
			s10(tokenList, index + 1);

		} else if(currToken.getType() == Token.L_BRACE) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s13");
			s10(tokenList, index);

		} else if(nextToken.getType() == Token.VARIABLE ||
				nextToken.getType() == Token.R_BRACE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s13");
			s13(tokenList, index + 1);

		}
	}

	/*
	//Story: @ Story: Actions [Choice]
	public void s14(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s14");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Story: @ Story: Actions [Choice]		
		if(currToken.getType() == Token.STORY_START || currToken.getType() == Token.VARIABLE)
			s15(tokenList, index + 1);
	}
	 */

	//Story: Story @ : Actions [Choice]
	public void s14(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s14");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Story: Story @ : Actions [Choice]		
		if(currToken.getType() == Token.END_OF_FUNCTION)
			s15(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.VARIABLE ||
				nextToken.getType() == Token.END_TOKEN ||
				nextToken.getType() == Token.CHAPTER_TRANSITION) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s14");
			s15(tokenList, index + 1);

		} else if(currToken.getType() == Token.VARIABLE ||
				currToken.getType() == Token.END_TOKEN ||
				currToken.getType() == Token.CHAPTER_TRANSITION) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s14");
			s15(tokenList, index);

		} else if(nextToken.getType() == Token.END_OF_FUNCTION){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s14");
			s14(tokenList, index + 1);

		}
	}

	//Story: Story : @ Actions [Choice]
	//Action: @ VAR keyword VAR
	//Action: @ VAR keyword MESSAGE
	//Action: @ VAR keyword
	//Action: @ keyword
	//Choice: @ Choice: (VAR) MESSAGE; [or ChapChoice]
	public void s15(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s15");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Story: Story : @ Actions [Choice]		
		if(currToken.getType() == Token.VARIABLE)
			s16(tokenList, index + 1);
		else if(currToken.getType() == Token.END_TOKEN)
			s0(tokenList, index + 1);
		else if(currToken.getType() == Token.CHAPTER_TRANSITION)
			s20(tokenList, index + 1);
		else if(!parserStack.isEmpty() && 
				(currToken.getType() == Token.BACKGROUND_DEFINITION ||
				currToken.getType() == Token.CG_DEFINITION || 
				currToken.getType() == Token.CHARACTER_DEFINITION || 
				currToken.getType() == Token.STORY_START ||
				currToken.getType() == Token.VARIABLE)){
			parserStack.pop();
			s0(tokenList, index);
		}
		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.VARIABLE ||
				nextToken.getType() == Token.END_TOKEN ||
				nextToken.getType() == Token.CHAPTER_TRANSITION) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s14");
			s15(tokenList, index + 1);

		} else if(currToken.getType() == Token.VARIABLE ||
				currToken.getType() == Token.END_TOKEN ||
				currToken.getType() == Token.CHAPTER_TRANSITION) {
			System.out.println("Missing Keyword on Line " + getLineNumber(index) + ".");
			reject("s14");
			s15(tokenList, index);

		} else if(nextToken.getType() == Token.VARIABLE ||
				nextToken.getType() == Token.END_TOKEN ||
				nextToken.getType() == Token.CHAPTER_TRANSITION){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s14");
			s14(tokenList, index + 1);

		}
	}

	//Action: VAR @ keyword VAR
	//Action: VAR @ keyword MESSAGE
	//Action: VAR @ keyword
	public void s16(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s16");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Action: VAR @ says MESSAGE		
		if(currToken.getType() == Token.MESSAGE_FUNCTION)
			s17(tokenList, index + 1);
		//Action: VAR @ feels VAR	
		else if(currToken.getType() == Token.EMOTION_FUNCTION)
			s19(tokenList, index + 1);
		else if(currToken.getType() == Token.ENTRANCE_FUNCTION || currToken.getType() == Token.EXIT_FUNCTION)
			s18(tokenList, index + 1);
		else if(currToken.getType() == Token.BACKGROUND_FUNCTION)
			s18(tokenList, index + 1);
		else if(currToken.getType() == Token.END_OF_FUNCTION) {
			if(!parserStack.isEmpty()) {
				parserStack.pop();
				parserStack.pop();
				/*
					Object o = parserStack.pop();
					ArrayList<Action> actionList = new ArrayList<Action>();
					while(o instanceof Action){
						actionList.add(0, (Action) o);
						o = parserStack.pop();
					}
					parserStack.push(o);
					parserStack.push(actionList);
				 */
				s0(tokenList, index - 1);
			}
		}

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(currToken.getType() == Token.MESSAGE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s16");
			s17(tokenList, index);

		} else if(currToken.getType() == Token.END_OF_STATEMENT) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s16");
			s18(tokenList, index);

		} else if(currToken.getType() == Token.VARIABLE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s16");
			s19(tokenList, index);

		} else if(nextToken.getType() == Token.MESSAGE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s16");
			s17(tokenList, index + 1);

		} else if(nextToken.getType() == Token.END_OF_STATEMENT) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s16");
			s18(tokenList, index + 1);

		} else if(nextToken.getType() == Token.VARIABLE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s16");
			s19(tokenList, index + 1);

		} else if(nextToken.getType() == Token.MESSAGE_FUNCTION ||
				nextToken.getType() == Token.EMOTION_FUNCTION ||
				nextToken.getType() == Token.ENTRANCE_FUNCTION ||
				nextToken.getType() == Token.EXIT_FUNCTION ||
				nextToken.getType() == Token.BACKGROUND_FUNCTION ||
				nextToken.getType() == Token.END_OF_FUNCTION){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s16");
			s16(tokenList, index + 1);

		}

	}

	//Action: VAR keyword @ MESSAGE
	public void s17(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s17");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Action: VAR keyword @ MESSAGE
		if(currToken.getType() == Token.MESSAGE)
			s18(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.END_OF_STATEMENT ||
				nextToken.getType() == Token.MULTIACTION) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s17");
			s18(tokenList, index + 1);

		} else if(currToken.getType() == Token.END_OF_STATEMENT ||
				currToken.getType() == Token.MULTIACTION) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s17");
			s18(tokenList, index);

		} else if(nextToken.getType() == Token.MESSAGE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s17");
			s17(tokenList, index + 1);

		}
	}

	//Action: VAR keyword VAR @ ;
	//Action: VAR keyword MESSAGE @ ;
	//Action: VAR keyword @ ;
	public void s18(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s18");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Action: VAR keyword MESSAGE @ ;
		if(currToken.getType() == Token.END_OF_STATEMENT) {
			//Reduce to Action
			Object o = null;
			int numActions = 0;
			MultiAction m = new MultiAction();
			do {
				//Pop Semicolon
				o = parserStack.pop();
				if(!(o instanceof Token) || !(((Token) o).getType() == Token.MULTIACTION || ((Token) o).getType() == Token.END_OF_STATEMENT)) {
					parserStack.push(o);
					break;
				} 
				o = parserStack.pop();
				numActions++;
				if(o instanceof Token && ((Token) o).getType() != Token.END_OF_STATEMENT && ((Token) o).getType() != Token.END_OF_FUNCTION) {
					Token t = (Token) o;
					if(t.getType() == Token.MESSAGE) {
						//Pop says Keyword
						parserStack.pop();
						String charName = ((Token) parserStack.pop()).getToken();
						StoryCharacter c = new StoryCharacter(charName, 0);
						m.addAction(new MessageAction(charName, t.getToken()));
					} else if(t.getType() == Token.VARIABLE) {
						//Pop feels Keyword
						parserStack.pop();
						String charName = ((Token) parserStack.pop()).getToken();
						StoryCharacter c = new StoryCharacter(charName, 0);
						String emoName = t.getToken();
						Emotion e = new Emotion(emoName, "", emoName + "_" + charName.toLowerCase());

						m.addAction(new EmotionAction(c, e));
					} else if(t.getType() == Token.ENTRANCE_FUNCTION) {
						String charName = ((Token) parserStack.pop()).getToken();
						StoryCharacter c = new StoryCharacter(charName, 0);

						m.addAction(new CharacterEnterAction(c));
					} else if(t.getType() == Token.EXIT_FUNCTION) {
						String charName = ((Token) parserStack.pop()).getToken();
						StoryCharacter c = new StoryCharacter(charName, 0);

						m.addAction(new CharacterExitAction(c));
					} else if(t.getType() == Token.BACKGROUND_FUNCTION) {
						String bgName = ((Token) parserStack.pop()).getToken();
						Background bg = new Background(bgName, "", 0);

						m.addAction(new ChangeBGAction(bg));
					}
				} else {
					parserStack.push(o);
					numActions--;
				}
			} while(o instanceof Token && ((Token) o).getType() != Token.END_OF_STATEMENT && ((Token) o).getType() != Token.END_OF_FUNCTION);
			if(numActions == 1)
				parserStack.push(m.getActionList().get(0));
			else parserStack.push(m);
			s15(tokenList, index + 1);
		} else if(currToken.getType() == Token.MULTIACTION) {
			s15(tokenList, index + 1);
		}

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.VARIABLE ||
				nextToken.getType() == Token.END_TOKEN ||
				nextToken.getType() == Token.CHAPTER_TRANSITION) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s18");
			s15(tokenList, index + 1);

		} else if(currToken.getType() == Token.VARIABLE ||
				currToken.getType() == Token.END_TOKEN ||
				currToken.getType() == Token.CHAPTER_TRANSITION) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s18");
			s15(tokenList, index);

		} else if(nextToken.getType() == Token.MULTIACTION ||
				nextToken.getType() == Token.END_OF_STATEMENT){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s18");
			s18(tokenList, index + 1);

		}
	}

	//Action: VAR keyword @ VAR
	public void s19(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s19");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Action: VAR feels @ VAR		
		if(currToken.getType() == Token.VARIABLE)
			s18(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.MULTIACTION ||
				nextToken.getType() == Token.END_OF_STATEMENT) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s19");
			s18(tokenList, index + 1);

		} else if(currToken.getType() == Token.MULTIACTION ||
				currToken.getType() == Token.END_OF_STATEMENT) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s19");
			s18(tokenList, index);

		} else if(nextToken.getType() == Token.VARIABLE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s19");
			s19(tokenList, index + 1);

		}
	}

	//Choice: Choice @ : (VAR) MESSAGE ; [or ChapChoice]
	public void s20(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s20");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Choice: Choice @ : (VAR) MESSAGE ; [or ChapChoice]	
		if(currToken.getType() == Token.END_OF_FUNCTION)
			s21(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.OPEN_PARENTHESIS) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s20");
			s21(tokenList, index + 1);

		} else if(currToken.getType() == Token.OPEN_PARENTHESIS) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s20");
			s21(tokenList, index);

		} else if(nextToken.getType() == Token.END_OF_FUNCTION){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s20");
			s20(tokenList, index + 1);

		}
	}

	//Choice: Choice : @ (VAR) MESSAGE ; [or ChapChoice]
	//ChapChoice: @ (VAR) MESSAGE;
	public void s21(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s21");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Choice: Choice @ : (VAR) MESSAGE ; [or ChapChoice]	
		if(currToken.getType() == Token.OPEN_PARENTHESIS)
			s22(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.VARIABLE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s21");
			s22(tokenList, index + 1);

		} else if(currToken.getType() == Token.VARIABLE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s21");
			s22(tokenList, index);

		} else if(nextToken.getType() == Token.OPEN_PARENTHESIS){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s21");
			s20(tokenList, index + 1);

		}
	}

	//Choice: Choice : ( @ VAR ) MESSAGE ; [or ChapChoice]
	//ChapChoice: ( @ VAR) MESSAGE;
	public void s22(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s22");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Choice: Choice : ( @ VAR ) MESSAGE ; [or ChapChoice]	
		if(currToken.getType() == Token.VARIABLE)
			s23(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.CLOSE_PARENTHESIS) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s22");
			s23(tokenList, index + 1);

		} else if(currToken.getType() == Token.CLOSE_PARENTHESIS) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s22");
			s23(tokenList, index);

		} else if(nextToken.getType() == Token.VARIABLE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s22");
			s22(tokenList, index + 1);

		}
	}

	//Choice: Choice : ( VAR @ ) MESSAGE ; [or ChapChoice]
	//ChapChoice: ( VAR @ ) MESSAGE;
	public void s23(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s23");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Choice: Choice : ( VAR @ ) MESSAGE ; [or ChapChoice]	
		if(currToken.getType() == Token.CLOSE_PARENTHESIS)
			s24(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.MESSAGE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s23");
			s24(tokenList, index + 1);

		} else if(currToken.getType() == Token.MESSAGE) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s23");
			s24(tokenList, index);

		} else if(nextToken.getType() == Token.CLOSE_PARENTHESIS){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s23");
			s23(tokenList, index + 1);

		}
	}

	//Choice: Choice : ( VAR ) @ MESSAGE ; [or ChapChoice]
	//ChapChoice: ( VAR ) @ MESSAGE;
	public void s24(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s24");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Choice: Choice : ( VAR ) @ MESSAGE ; [or ChapChoice]	
		if(currToken.getType() == Token.MESSAGE)
			s25(tokenList, index + 1);

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.END_OF_STATEMENT) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s24");
			s25(tokenList, index + 1);

		} else if(currToken.getType() == Token.END_OF_STATEMENT) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s24");
			s25(tokenList, index);

		} else if(nextToken.getType() == Token.MESSAGE){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s24");
			s24(tokenList, index + 1);

		}
	}

	//Choice: Choice : ( VAR ) MESSAGE @ ; [or ChapChoice]
	//ChapChoice: ( VAR ) MESSAGE @ ;
	public void s25(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s25");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//ChapChoice: ( VAR ) MESSAGE ; @	
		if(currToken.getType() == Token.END_OF_STATEMENT) {
			//Reduce to ChapChoice
			//Pop semicolon
			parserStack.pop();
			Object o = parserStack.pop();
			//Pop Message
			String message = ((Token) o).getToken();
			//Pop Close Parenthesis
			parserStack.pop();
			//Pop Chapter Name
			o = parserStack.pop();
			String chapName = ((Token) o).getToken();
			//Pop Open Parenthesis
			parserStack.pop();

			parserStack.push(new ChapterChoice(chapName, message));

			s26(tokenList, index + 1);
		}

		Token nextToken = tokenList.get(index + 1);

		//Error Handling: Convert, Add, Erase
		if(nextToken.getType() == Token.CHOICES_SEPARATOR) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s25");
			s26(tokenList, index + 1);

		} else if(currToken.getType() == Token.CHOICES_SEPARATOR) {
			System.out.println("Wrong Keyword Used on Line " + getLineNumber(index) + ".");
			reject("s25");
			s26(tokenList, index);

		} else if(nextToken.getType() == Token.END_OF_STATEMENT){
			System.out.println("Extra Keyword on Line " + getLineNumber(index) + ".");
			reject("s25");
			s25(tokenList, index + 1);

		}
	}

	//Choice: Choice : ( VAR ) MESSAGE ; @ or ChapChoice
	public void s26(ArrayList<Token> tokenList, int index){
		if(DEBUG)
			System.out.println("Current State: s26");
		Token currToken = tokenList.get(index);
		parserStack.push(currToken);

		//Choice: Choice : ( VAR ) MESSAGE ; @ or ChapChoice	
		if(currToken.getType() == Token.CHOICES_SEPARATOR) {
			parserStack.pop();
			s21(tokenList, index + 1);
		} else {
			//Pop previously read token
			parserStack.pop();

			//Reduce to Branch Action
			ArrayList<ChapterChoice> chapChoices = new ArrayList<ChapterChoice>();
			//Pop Choices
			Object o = parserStack.pop();
			while(o instanceof ChapterChoice){
				chapChoices.add((ChapterChoice) o);
				o = parserStack.pop();
			}
			parserStack.push(o);

			//Pop Colon
			parserStack.pop();
			//Pop Choice Keyword
			parserStack.pop();

			if(chapChoices.size() > 1) {
				MultiAction ma = new MultiAction();
				for(ChapterChoice c : chapChoices)
					ma.addAction(new BranchAction(c));
				parserStack.add(ma);
			} else if(chapChoices.size() == 1) {
				parserStack.push(new BranchAction(chapChoices.get(0)));
			}


			s0(tokenList, index);
		}
	}







	public void saveAction(Chapter c, Action a){
		c.addAction(a);
	}

	public ArrayList<Chapter> getChapterList() {
		return chapterList;
	}

	public ArrayList<Background> getBGList(){
		return bgList;
	}

	public ArrayList<StoryCharacter> getCharList(){
		return charList;
	}

	public ArrayList<Graphics> getCGList() {
		return cgList;
	}

	public StoryCharacter getCharacter(String charName){
		for(StoryCharacter c : charList){
			if(c.getCharName().equals(charName))
				return c;
		}
		return null;
	}

	public Chapter getChapter(String chapter){
		for(Chapter c : chapterList){
			if(c.getChapName().equals(chapter))
				return c;
		}
		return null;
	}

	public Background getBackground(String bgName){
		for(Background b : bgList){
			if(b.getBackgroundName().equals(bgName))
				return b;
		}
		return null;
	}

	private boolean code_state = true;

	public Chapter saveChapter(String chapName){
		Chapter c = new Chapter(chapName);
		chapterList.add(c);
		//		System.out.println("Creating Chapter: " + c.getChapName());
		return c;
	}

	public void saveBackground(String bgName, String bgURL){
		Background b = new Background(bgName, bgURL, bgList.size());
		bgList.add(b);
		//		System.out.println("Saving Background: " + b.getBackgroundName() + " = " + b.getBackgroundURL());
	}

	public void saveGraphics(String cgName, String cgURL){
		Graphics g = new Graphics(cgName, cgURL, cgList.size());
		cgList.add(g);
		//		System.out.println("Saving Background: " + b.getBackgroundName() + " = " + b.getBackgroundURL());
	}

	public StoryCharacter saveChar(String charName){
		StoryCharacter c = new StoryCharacter(charName, charList.size());
		charList.add(c);
		//		System.out.println("Saving Character: " + c.getCharName());
		return c;
	}

	public StoryCharacter saveChar(String charName, ArrayList<Emotion> emotionList){
		StoryCharacter c = new StoryCharacter(charName, charList.size());
		c.setEmotionList(emotionList);
		charList.add(c);
		//		System.out.println("Saving Character: " + c.getCharName());
		return c;
	}

	public void saveEmotion(StoryCharacter c, String emoName, String emoURL){
		Emotion e = new Emotion(emoName, emoURL, emoName + "_" + c.getCharName().toLowerCase());
		c.addEmotion(e);
		//		System.out.println("Saving Emotion: " + c.getCharName() + " Emotion: " + e.getEmoName() + " = " + e.getEmoURL());
	}

	public void displayErrors() {
		for(int i = 0; i < errorList.size(); i++)
			System.out.println(errorList.get(i));
	}

	public int getLineNumber(int index){
		for(int i = 0; i < lineNumbers.length; i++){
			if(lineNumbers[i] <= index && lineNumbers[i + 1] > index) 
				return i + 1;
		}
		return 0;
	}

	//BACKGROUND START
	public void q0(ArrayList<Token> tokenList, int index){
		//@ Background { bg } Character { char } Story
		if(tokenList.get(index).getType() == Token.BACKGROUND_DEFINITION) {
			//System.out.println("@ Background { bg } Character { char } ");

			stack.push("q0");

			q1(tokenList, index + 1);

		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Background Definition Keyword");
			reject("q0");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.BACKGROUND_DEFINITION)
				q0(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.L_BRACE)
				q1(tokenList, index + 1);
			//ADD
			else q1(tokenList, index);
		}
	}

	public void q1(ArrayList<Token> tokenList, int index){
		//Background @ { bg } Character { char } Story
		if(tokenList.get(index).getType() == Token.L_BRACE) {
			//System.out.println("Background @ { bg } Character { char } Story");

			stack.push(tokenList.get(index).getToken());
			stack.push("q1");

			q2(tokenList, index + 1);

		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Open Brace");
			reject("q1");
			//ADD
			if(tokenList.get(index + 1).getType() == Token.L_BRACE)
				q1(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.VARIABLE)
				q2(tokenList, index + 1);
			//ADD
			else q2(tokenList, index);
		}
	}
	//BACKGROUND VAR START
	public void q2(ArrayList<Token> tokenList, int index){
		//Background { @ bg } Character { char } Story || @ bgvar = <URL>;
		if(tokenList.get(index).getType() == Token.VARIABLE) { 
			//			//System.out.println("Background { @ bg } Character { char } Story || @ bgvar = <URL>;");

			stack.push(tokenList.get(index).getToken());
			stack.push("q2");

			currentVariable = tokenList.get(index).getToken();

			q3(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No BG Variable");
			reject("q2");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.VARIABLE)
				q2(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.ASSIGNMENT_OPERATION)
				q3(tokenList, index + 1);
			//ADD
			else q3(tokenList, index);
		}
	}

	public void q3(ArrayList<Token> tokenList, int index){
		//bgvar @ = <URL>;
		if(tokenList.get(index).getType() == Token.ASSIGNMENT_OPERATION) {
			//System.out.println("bgvar @ = <URL>;");

			stack.push(tokenList.get(index).getToken());
			stack.push("q3");

			q4(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Assignment Operation");
			reject("q3");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.ASSIGNMENT_OPERATION)
				q3(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.URL)
				q4(tokenList, index + 1);
			//ADD
			else q4(tokenList, index);
		}
	}
	//BACKGROUND URL
	public void q4(ArrayList<Token> tokenList, int index){
		//bgvar = @ <URL>;
		if(tokenList.get(index).getType() == Token.URL) {
			//System.out.println("bgvar = @ <URL>;");

			stack.push(tokenList.get(index).getToken());
			stack.push("q4");

			currentURL = tokenList.get(index).getToken();
			saveBackground(currentVariable, currentURL.substring(1, currentURL.length() - 1));
			q45(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No URL");
			reject("q4");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.URL)
				q4(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.END_OF_STATEMENT)
				q45(tokenList, index + 1);
			//ADD
			else q45(tokenList, index);
		}
	}

	public void q45(ArrayList<Token> tokenList, int index){
		//bgvar = <URL> @ ; 
		if(tokenList.get(index).getType() == Token.END_OF_STATEMENT) {
			//System.out.println("bgvar = <URL> @ ;");

			stack.push(tokenList.get(index).getToken());
			stack.push("q45");

			q5(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No End of Statement");
			reject("q45");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.END_OF_STATEMENT)
				q45(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.R_BRACE || tokenList.get(index + 1).getType() == Token.VARIABLE)
				q5(tokenList, index + 1);
			//ADD
			else q5(tokenList, index);
		}
	}
	//BACKGROUND REPEAT
	public void q5(ArrayList<Token> tokenList, int index){
		//BG @ BG || Background { BG @ } 
		if(tokenList.get(index).getType() == Token.R_BRACE) {
			//System.out.println("BG @ BG || Background { BG @ } ");

			stack.push(tokenList.get(index).getToken());
			stack.push("q5");

			q6(tokenList, index + 1);
		} else if(tokenList.get(index).getType() == Token.VARIABLE) {
			//System.out.println("BG @ BG || Background { BG @ } ");

			currentVariable = tokenList.get(index).getToken();

			stack.push(tokenList.get(index).getToken());
			stack.push("q5");

			q3(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Close Brace");
			reject("q5");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.R_BRACE)
				q5(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.CHARACTER_DEFINITION)
				q6(tokenList, index + 1);
			//ADD
			else q6(tokenList, index);
		}
	}
	//CHARACTER START
	public void q6(ArrayList<Token> tokenList, int index){
		//Background { BG } @ Character { CHAR } Story
		if(tokenList.get(index).getType() == Token.CHARACTER_DEFINITION) {
			//System.out.println("Background { BG } @ Character { CHAR } Story");

			stack.push(tokenList.get(index).getToken());
			stack.push("q6");

			q7(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Character Definition Keyword");
			reject("q6");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.CHARACTER_DEFINITION)
				q6(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.L_BRACE)
				q7(tokenList, index + 1);
			//ADD
			else q7(tokenList, index);
		}
	}

	public void q7(ArrayList<Token> tokenList, int index){
		//Background { BG } Character @ { CHAR } Story
		if(tokenList.get(index).getType() == Token.L_BRACE) {
			//System.out.println("Background { BG } Character @ { CHAR } Story");

			stack.push(tokenList.get(index).getToken());
			stack.push("q7");

			q8(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Character Open Brace");
			reject("q7");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.L_BRACE)
				q7(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.VARIABLE)
				q8(tokenList, index + 1);
			//ADD
			else q8(tokenList, index);
		}
	}
	//CHARACTER VAR START
	public void q8(ArrayList<Token> tokenList, int index){
		//Background { BG } Character { @ CHAR } Story || @ var { CHAR2 } 
		if(tokenList.get(index).getType() == Token.VARIABLE) {
			//System.out.println("Background { BG } Character { @ CHAR } Story || @ var { CHAR2 }");

			stack.push(tokenList.get(index).getToken());
			stack.push("q8");

			currentVariable = tokenList.get(index).getToken();

			currentChar = saveChar(currentVariable);

			q9(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Character Variable");
			reject("q8");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.VARIABLE)
				q8(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.L_BRACE)
				q9(tokenList, index + 1);
			//ADD
			else q9(tokenList, index);
		}
	}

	public void q9(ArrayList<Token> tokenList, int index){
		//CHAR @ { CHAR2 } 
		if(tokenList.get(index).getType() == Token.L_BRACE) {
			//System.out.println("CHAR @ { CHAR2 }");

			stack.push(tokenList.get(index).getToken());
			stack.push("q9");

			q10(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Open Brace");
			reject("q9");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.L_BRACE)
				q9(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.VARIABLE)
				q10(tokenList, index + 1);
			//ADD
			else q10(tokenList, index);
		}
	}
	//CHARACTER EMOTION START
	public void q10(ArrayList<Token> tokenList, int index){
		//CHAR { @ CHAR2 } || @ CHAR2 = <URL>; 
		if(tokenList.get(index).getType() == Token.VARIABLE) {
			//System.out.println("CHAR { @ CHAR2 } || @ CHAR2 = <URL>; ");

			stack.push(tokenList.get(index).getToken());
			stack.push("q10");

			currentVariable = tokenList.get(index).getToken(); 

			q11(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Emotion Variable");
			reject("q10");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.VARIABLE)
				q10(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.ASSIGNMENT_OPERATION)
				q11(tokenList, index + 1);
			//ADD
			else q11(tokenList, index);
		}
	}

	public void q11(ArrayList<Token> tokenList, int index){
		//CHAR2 @ = <URL>;
		if(tokenList.get(index).getType() == Token.ASSIGNMENT_OPERATION) {
			//System.out.println("CHAR2 @ = <URL>;");

			stack.push(tokenList.get(index).getToken());
			stack.push("q11");

			q12(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Assignment Operation");
			reject("q11");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.ASSIGNMENT_OPERATION)
				q11(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.URL)
				q12(tokenList, index + 1);
			//ADD
			else q12(tokenList, index);
		}
	}
	//CHARACTER EMOTION URL
	public void q12(ArrayList<Token> tokenList, int index){
		//CHAR2 = @ <URL>;
		if(tokenList.get(index).getType() == Token.URL) { 
			//System.out.println("CHAR2 = @ <URL>;");

			stack.push(tokenList.get(index).getToken());
			stack.push("q12");

			currentURL = tokenList.get(index).getToken();

			saveEmotion(currentChar, currentVariable, currentURL.substring(1, currentURL.length() - 1));

			q125(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No URL");
			reject("q12");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.URL)
				q12(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.END_OF_STATEMENT)
				q125(tokenList, index + 1);
			//ADD
			else q125(tokenList, index);
		}
	}

	public void q125(ArrayList<Token> tokenList, int index){
		//CHAR2 = @ <URL>;
		if(tokenList.get(index).getType() == Token.END_OF_STATEMENT) { 
			//System.out.println("CHAR2 = @ <URL>;");

			stack.push(tokenList.get(index).getToken());
			stack.push("q125");

			q13(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No End of Statement");
			reject("q12");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.END_OF_STATEMENT)
				q125(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.R_BRACE || tokenList.get(index + 1).getType() == Token.VARIABLE)
				q13(tokenList, index + 1);
			//ADD
			else q13(tokenList, index);
		}
	}
	//CHARACTER EMOTION REPEAT
	public void q13(ArrayList<Token> tokenList, int index){
		//CHAR2 @ CHAR2 || CHAR { CHAR2 @ }
		if(tokenList.get(index).getType() == Token.R_BRACE) { 
			//System.out.println("CHAR2 @ CHAR2 || CHAR { CHAR2 @ }");

			stack.push(tokenList.get(index).getToken());
			stack.push("q13");

			q14(tokenList, index + 1);
		} else if(tokenList.get(index).getType() == Token.VARIABLE) {
			//System.out.println("CHAR2 @ CHAR2 || CHAR { CHAR2 @ }");

			stack.push(tokenList.get(index).getToken());
			stack.push("q13");

			currentVariable = tokenList.get(index).getToken();

			q11(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Close Brace");
			reject("q13");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.R_BRACE)
				q13(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.VARIABLE)
				q14(tokenList, index + 1);
			//ADD
			else q14(tokenList, index);
		}
	}
	//CHARACTER REPEAT
	public void q14(ArrayList<Token> tokenList, int index){
		//Background { BG } Character { CHAR @ }
		if(tokenList.get(index).getType() == Token.R_BRACE) {
			//System.out.println("Background { BG } Character { CHAR @ }");

			stack.push(tokenList.get(index).getToken());
			stack.push("q14");

			q15(tokenList, index + 1);
		} else if(tokenList.get(index).getType() == Token.VARIABLE) {
			//System.out.println("Background { BG } Character { CHAR @ }");

			stack.push(tokenList.get(index).getToken());
			stack.push("q14");

			currentVariable = tokenList.get(index).getToken();

			currentChar = saveChar(currentVariable);

			q9(tokenList, index + 1);
		} else {
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Close Brace");
			reject("q14");
			//ERASE
			if(tokenList.get(index + 1).getType() == Token.R_BRACE)
				q14(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.STORY_START)
				q15(tokenList, index + 1);
			//ADD
			else q15(tokenList, index);
		}
	}
	//STORY START
	public void q15(ArrayList<Token> tokenList, int index){
		//@ Start : STATEMENTS Choices: ( CHAPTER_NAME ) "MESSAGE" OR ( CHAPTER_NAME ) "MESSAGE"
		//CHAPTER_NAME: STATEMENTS

		if(tokenList.get(index).getType() == Token.STORY_START) {
			//System.out.println("@ Start : STATEMENTS");

			currentVariable = tokenList.get(index).getToken();

			currentChapter = saveChapter(currentVariable);

			stack.push("q15");
			q16(tokenList, index + 1);

		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Start Keyword");
			reject("q15");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.STORY_START)
				q15(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.END_OF_FUNCTION)
				q16(tokenList, index + 1);
			//ADD
			else q16(tokenList, index);
		}
	}

	private void q16(ArrayList<Token> tokenList, int index) {
		//Start @ : STATEMENTS Choices: ( CHAPTER_NAME ) "MESSAGE" OR ( CHAPTER_NAME ) "MESSAGE"
		//CHAPTER_NAME: STATEMENTS

		if(tokenList.get(index).getType() == Token.END_OF_FUNCTION) {
			//System.out.println("Start @ : STATEMENTS");

			stack.push("q16");
			q17(tokenList, index + 1);

		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No End of Function");
			reject("q16");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.END_OF_FUNCTION)
				q16(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index).getType() == Token.VARIABLE || tokenList.get(index).getType() == Token.CHAPTER_TRANSITION)
				q17(tokenList, index + 1);
			//ADD
			else q17(tokenList, index);
		}
	}

	//STATEMENT START
	private void q17(ArrayList<Token> tokenList, int index) {
		//Start : @ STATEMENTS Choices: ( CHAPTER_NAME ) "MESSAGE" OR ( CHAPTER_NAME ) "MESSAGE"
		//@ CHAPTER_NAME: STATEMENTS

		if(tokenList.get(index).getType() == Token.VARIABLE) {
			//System.out.println("Start : @ STATEMENTS / @ CHAPTER :");

			currentVariable = tokenList.get(index).getToken();

			stack.push("q17");
			q18(tokenList, index + 1);

		} else if(tokenList.get(index).getType() == Token.CHAPTER_TRANSITION) {
			//System.out.println("Start : @ Choices : ...");
			//			BranchAction ba = new BranchAction(currentChapter);
			//			currentChapter.addAction(ba);

			stack.push("q17");
			q23(tokenList, index + 1);
		} else {
			//No Background Definition
			reject("q17");

			//ADD
			if(tokenList.get(index).getType() == Token.END_OF_FUNCTION) {
				errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Chapter Name");
				q18(tokenList, index);
			} //ERASE
			else if(tokenList.get(index + 1).getType() == Token.VARIABLE){
				errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": Extra Symbol");
				q17(tokenList, index + 1);
			} //CONVERT
			else if(tokenList.get(index + 1).getType() == Token.CHAPTER_TRANSITION){
				errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": Extra Symbol");
				q18(tokenList, index + 1);
			} //ADD
			else if(tokenList.get(index).getType() == Token.MESSAGE_FUNCTION ||
					tokenList.get(index).getType() == Token.BACKGROUND_FUNCTION ||
					tokenList.get(index).getType() == Token.ENTRANCE_FUNCTION ||
					tokenList.get(index).getType() == Token.EXIT_FUNCTION ||
					tokenList.get(index).getType() == Token.EMOTION_FUNCTION) {
				errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Variable");
				q18(tokenList, index);
			}
		}

	}
	//STATEMENT END / MIDDLE KEYWORDS
	private void q18(ArrayList<Token> tokenList, int index) {
		// VAR @ says "Message" || VAR @ background; || VAR @ feels VAR; || VAR @ enters; || VAR @ leaves;
		if(tokenList.get(index).getType() == Token.BACKGROUND_FUNCTION ||
				tokenList.get(index).getType() == Token.ENTRANCE_FUNCTION ||
				tokenList.get(index).getType() == Token.EXIT_FUNCTION) {
			//System.out.println("VAR @ says Message || VAR @ background; || VAR @ feels VAR; || VAR @ enters; || VAR @ leaves;");

			if(tokenList.get(index).getType() == Token.BACKGROUND_FUNCTION)
				saveAction(currentChapter, new ChangeBGAction(getBackground(currentVariable)));
			else if(tokenList.get(index).getType() == Token.ENTRANCE_FUNCTION)
				saveAction(currentChapter, new CharacterEnterAction(getCharacter(currentVariable)));
			else if(tokenList.get(index).getType() == Token.EXIT_FUNCTION)
				saveAction(currentChapter, new CharacterExitAction(getCharacter(currentVariable)));
			stack.push("q18");
			q22(tokenList, index + 1);
		} else if(tokenList.get(index).getType() == Token.MESSAGE_FUNCTION) {
			q20(tokenList, index + 1);
		} else if(tokenList.get(index).getType() == Token.EMOTION_FUNCTION) {
			q21(tokenList, index + 1);
		} else if(tokenList.get(index).getType() == Token.END_OF_FUNCTION) {

			currentChapter = saveChapter(currentVariable);

			q29(tokenList, index);
		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Keyword");
			reject("q18");

			//ADD
			if(tokenList.get(index).getType() == Token.END_OF_STATEMENT)
				q22(tokenList, index);
			//ERASE
			else if(tokenList.get(index + 1).getType() == Token.MESSAGE_FUNCTION ||
					tokenList.get(index + 1).getType() == Token.BACKGROUND_FUNCTION ||
					tokenList.get(index + 1).getType() == Token.ENTRANCE_FUNCTION ||
					tokenList.get(index + 1).getType() == Token.EXIT_FUNCTION ||
					tokenList.get(index + 1).getType() == Token.EMOTION_FUNCTION)
				q18(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.MESSAGE)
				q20(tokenList, index + 1);
			else if(tokenList.get(index + 1).getType() == Token.END_OF_STATEMENT)
				q22(tokenList, index + 1);
			else if(tokenList.get(index + 1).getType() == Token.VARIABLE)
				q21(tokenList, index + 1);
		}
	}
	//MESSAGE
	private void q20(ArrayList<Token> tokenList, int index) {
		// VAR says @ "Message";
		if(tokenList.get(index).getType() == Token.MESSAGE) {
			currentChar = getCharacter(currentVariable);
			currentVariable = tokenList.get(index).getToken();
			saveAction(currentChapter, new MessageAction(currentChar, currentVariable.substring(1, currentVariable.length() - 1)));

			q22(tokenList, index + 1);
		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Message");
			reject("q20");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.MESSAGE)
				q20(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.END_OF_STATEMENT)
				q22(tokenList, index + 1);
			//ADD
			else q22(tokenList, index);
		}
	}
	//EMOTION
	private void q21(ArrayList<Token> tokenList, int index) {
		// VAR feels @ VAR;
		if(tokenList.get(index).getType() == Token.VARIABLE) {
			currentChar = getCharacter(currentVariable);
			currentVariable = tokenList.get(index).getToken();
			if(currentChar.getEmotion(currentVariable) != null)
				saveAction(currentChapter, new EmotionAction(currentChar, currentChar.getEmotion(currentVariable)));
			else { 
				errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Emotion exists for the character");
				reject("q21");
			}
			q22(tokenList, index + 1);
		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Emotion Variable");
			reject("q21");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.VARIABLE)
				q21(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.END_OF_STATEMENT)
				q22(tokenList, index + 1);
			//ADD
			else q22(tokenList, index);
		}
	}

	private void q22(ArrayList<Token> tokenList, int index) {
		if(tokenList.get(index).getType() == Token.END_OF_STATEMENT) {
			q17(tokenList, index + 1);
		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No End of Statement");
			reject("q22");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.END_OF_STATEMENT)
				q21(tokenList, index + 1);
			//			//CONVERT
			//			else if(tokenList.get(index + 1).getType() == Token.END_OF_STATEMENT)
			//				q22(tokenList, index + 1);
			//ADD
			else q17(tokenList, index);
		}
	}

	private void q23(ArrayList<Token> tokenList, int index) {
		//CHOICES @ : (CHAPTER) MESSAGE OR (CHAPTER) MESSAGE
		if(tokenList.get(index).getType() == Token.END_OF_FUNCTION) {
			//System.out.println("CHOICES @ : (CHAPTER) MESSAGE OR (CHAPTER) MESSAGE");

			stack.push("q23");
			q24(tokenList, index + 1);

		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No End of Function");
			reject("q23");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.END_OF_FUNCTION)
				q23(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.OPEN_PARENTHESIS)
				q24(tokenList, index + 1);
			//ADD
			else q24(tokenList, index);
		}
	}
	//CHOICES START
	private void q24(ArrayList<Token> tokenList, int index) {
		//CHOICES : @ (CHAPTER) MESSAGE OR (CHAPTER) MESSAGE
		if(tokenList.get(index).getType() == Token.OPEN_PARENTHESIS) {
			//System.out.println("CHOICES : @ (CHAPTER) MESSAGE OR (CHAPTER) MESSAGE");

			stack.push("q24");
			q25(tokenList, index + 1);

		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Open Parenthesis");
			reject("q24");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.OPEN_PARENTHESIS)
				q24(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.VARIABLE)
				q25(tokenList, index + 1);
			//ADD
			else q25(tokenList, index);
		}
	}

	private void q25(ArrayList<Token> tokenList, int index) {
		//CHOICES : ( @ CHAPTER ) MESSAGE OR ( CHAPTER ) MESSAGE
		if(tokenList.get(index).getType() == Token.VARIABLE) {
			//System.out.println("CHOICES : ( @ CHAPTER ) MESSAGE OR ( CHAPTER ) MESSAGE");

			currentVariable = tokenList.get(index).getToken();

			stack.push("q25");
			q26(tokenList, index + 1);

		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Chapter Name");
			reject("q25");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.VARIABLE)
				q25(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.CLOSE_PARENTHESIS)
				q26(tokenList, index + 1);
			//ADD
			else q26(tokenList, index);
		}
	}

	private void q26(ArrayList<Token> tokenList, int index) {
		//CHOICES : ( CHAPTER @ ) MESSAGE OR ( CHAPTER ) MESSAGE
		if(tokenList.get(index).getType() == Token.CLOSE_PARENTHESIS) {
			//System.out.println("CHOICES : ( CHAPTER @ ) MESSAGE OR ( CHAPTER ) MESSAGE");

			stack.push("q26");
			q27(tokenList, index + 1);

		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No Close Parenthesis");
			reject("q26");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.CLOSE_PARENTHESIS)
				q26(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.MESSAGE)
				q27(tokenList, index + 1);
			//ADD
			else q27(tokenList, index);
		}
	}
	//CHOICES MESSAGE
	private void q27(ArrayList<Token> tokenList, int index) {
		//CHOICES : ( CHAPTER ) @ MESSAGE OR ( CHAPTER ) MESSAGE
		if(tokenList.get(index).getType() == Token.MESSAGE) {
			//System.out.println("CHOICES : ( CHAPTER ) @ MESSAGE OR ( CHAPTER ) MESSAGE CHAPTERNAME: ...");
			String message = tokenList.get(index).getToken();
			ChapterChoice cc = new ChapterChoice(currentVariable, message.substring(1, message.length() - 1));
			currentChapter.addChapterChoice(cc);

			stack.push("q27");
			q275(tokenList, index + 1);

		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No MESSAGE");
			reject("q27");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.MESSAGE)
				q27(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.END_OF_STATEMENT)
				q275(tokenList, index + 1);
			//ADD
			else q275(tokenList, index);
		}
	}

	private void q275(ArrayList<Token> tokenList, int index) {
		//CHOICES : ( CHAPTER ) @ MESSAGE OR ( CHAPTER ) MESSAGE
		if(tokenList.get(index).getType() == Token.END_OF_STATEMENT) {
			//System.out.println("CHOICES : ( CHAPTER ) MESSAGE @ ; OR ( CHAPTER ) MESSAGE CHAPTERNAME: ...");

			stack.push("q275");
			q28(tokenList, index + 1);

		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No End of Statement");
			reject("q27");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.END_OF_STATEMENT)
				q275(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index + 1).getType() == Token.CHOICES_SEPARATOR || tokenList.get(index + 1).getType() == Token.VARIABLE)
				q28(tokenList, index + 1);
			//ADD
			else q28(tokenList, index);
		}
	}

	private void q28(ArrayList<Token> tokenList, int index) {
		//CHOICES : ( CHAPTER ) MESSAGE @ OR ( CHAPTER ) MESSAGE @ CHAPTERNAME: ...
		if(tokenList.get(index).getType() == Token.CHOICES_SEPARATOR) {
			//System.out.println("CHOICES : ( CHAPTER ) MESSAGE @ OR ( CHAPTER ) MESSAGE CHAPTERNAME: ...");



			stack.push("q28");
			q24(tokenList, index + 1);

		} else if(tokenList.get(index).getType() == Token.VARIABLE) {
			//System.out.println("CHOICES : ( CHAPTER ) MESSAGE @ CHAPTERNAME: ...");

			currentVariable = tokenList.get(index).getToken();

			currentChapter = saveChapter(currentVariable);

			stack.push("q28");
			q29(tokenList, index + 1);
		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No OR/CHAPTERNAME");
			reject("q27");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.CHOICES_SEPARATOR)
				q28(tokenList, index + 1);
			else if(tokenList.get(index + 1).getType() == Token.VARIABLE)
				q28(tokenList, index + 1);
			//CONVERT
			//			else if(tokenList.get(index + 1).getType() == Token.CHOICES_SEPARATOR || tokenList.get(index + 1).getType() == Token.VARIABLE)
			//				q28(tokenList, index + 1);
			//ADD
			//			else q28(tokenList, index);
		}
	}
	//STORY REPEAT
	private void q29(ArrayList<Token> tokenList, int index) {
		//CHAPTERNAME @ : STATEMENTS
		if(tokenList.get(index).getType() == Token.END_OF_FUNCTION) {
			//System.out.println("CHAPTERNAME @ : STATEMENTS");

			stack.push("q29");
			q17(tokenList, index + 1);

		} else {
			//No Background Definition
			errorList.push(tokenList.get(index - 1).getToken() + " " + tokenList.get(index).getToken() + " " + tokenList.get(index + 1).getToken() + "\nLine " + getLineNumber(index) + ": No End of Function");
			reject("q29");

			//ERASE
			if(tokenList.get(index + 1).getType() == Token.END_OF_FUNCTION)
				q29(tokenList, index + 1);
			//CONVERT
			else if(tokenList.get(index).getType() == Token.VARIABLE || tokenList.get(index).getType() == Token.CHAPTER_TRANSITION)
				q17(tokenList, index + 1);
			//ADD
			else q17(tokenList, index);
		}
	}

	public void end(){	
		if(code_state == true);
//			System.out.println("//ACCEPT");
		else {
			displayErrors();
			System.out.println("//REJECT");
		}
	}

	public void reject(String segment){
		code_state = false;
		System.out.println("Reject " + segment);
	}



}

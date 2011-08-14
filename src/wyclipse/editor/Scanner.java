package wyclipse.editor;

import java.io.*;
import java.util.*;
import wyc.stages.WhileyLexer;
import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;

public class Scanner implements ITokenScanner {
	private ColorManager manager;
	private List<WhileyLexer.Token> tokens;
	private int pos;
	
	public Scanner(ColorManager manager) {
		this.manager = manager;						
	}
	
	public int getTokenLength() {
		WhileyLexer.Token token = tokens.get(pos-1);
		return token.text.length();
	}
	
	public int getTokenOffset() {
		WhileyLexer.Token token = tokens.get(pos-1);
		return token.start;
	}
	
	public IToken nextToken() {
		if(pos == tokens.size()) {
			return Token.EOF;
		} else {
			WhileyLexer.Token token = tokens.get(pos++);
			if (token instanceof WhileyLexer.Keyword) {
				return new Token(new TextAttribute(
						manager.getColor(ColorManager.KEYWORD_COLOR)));
			} else if(token instanceof WhileyLexer.LineComment || token instanceof WhileyLexer.BlockComment) {
				return new Token(new TextAttribute(
						manager.getColor(ColorManager.COMMENT_COLOR)));
			} else if(token instanceof WhileyLexer.Strung) {
				return new Token(new TextAttribute(
						manager.getColor(ColorManager.STRING_COLOR)));
			} else {
				return new Token(new TextAttribute(
						manager.getColor(ColorManager.DEFAULT_COLOR)));
			}
		}
	}
	
	public void setRange(IDocument document, int offset, int length) {
		// At this point, we could be a little smarter and actually update the
		// token list incrementally.
		try {
			String text = document.get();
			System.out.println("DOCUMENT TEXT: " + text.length());
			WhileyLexer lexer = new WhileyLexer(new StringReader(text));
			tokens = lexer.scan();
			pos = 0;
		} catch(IOException e) {
			// this is probably dead-code
		}
	}	
}

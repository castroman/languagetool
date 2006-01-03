/* LanguageTool, a natural language style checker 
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package de.danielnaber.languagetool.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import de.danielnaber.languagetool.AnalyzedSentence;
import de.danielnaber.languagetool.AnalyzedToken;
import de.danielnaber.languagetool.Language;

/**
 * A rule that matches commas and closing parenthesis preceeded by whitespace
 * and opening parenthesis followed by whitespace.
 * 
 * @author Daniel Naber
 */
public class CommaWhitespaceRule extends Rule {

  public CommaWhitespaceRule(ResourceBundle messages) {
    super(messages);
  }
  
  public String getId() {
    return "COMMA_PARENTHESIS_WHITESPACE";
  }

  public String getDescription() {
    return messages.getString("desc_comma_whitespace");
  }

  public Language[] getLanguages() {
    return new Language[] { Language.ENGLISH, Language.GERMAN };
  }

  public RuleMatch[] match(AnalyzedSentence text) {
    List ruleMatches = new ArrayList();
    AnalyzedToken[] tokens = text.getTokens();
    String prevToken = "";
    int pos = 0;
    int prevPos = 0;
    for (int i = 0; i < tokens.length; i++) {
      String token = tokens[i].getToken();
      pos += token.length();
      String msg = null;
      int fixPos = 0;
      int fixLen = 0;
      if (token.trim().equals("") && prevToken.trim().equals("(")) {
        msg = messages.getString("no_space_after");
      } else if (token.trim().equals(")") && prevToken.trim().equals("")) {
        msg = messages.getString("no_space_before");
        if (prevPos > 0)
          fixPos = -1;
      } else if (prevToken.trim().equals(",") && !token.trim().equals("") &&
          !token.equals("'") && !token.equals("\"") && !token.matches(".*\\d.*")) {
        msg = messages.getString("missing_space_after_comma");
        fixLen = - prevToken.length() + 1;
      } else if (token.trim().equals(",") && prevToken.trim().equals("")) {
        msg = messages.getString("space_after_comma");
        fixLen = 1;
        if (prevPos > 0)
          fixPos = -1;
      }
      if (msg != null) {
        int fromPos = prevPos + fixPos;
        int toPos = prevPos + fixPos + fixLen + prevToken.length();
        RuleMatch ruleMatch = new RuleMatch(this, fromPos, toPos, msg);
        ruleMatches.add(ruleMatch);
      }
      prevToken = token;
      prevPos = pos;
    }
    return toRuleMatchArray(ruleMatches);
  }

  public void reset() {
    // nothing
  }

}

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

public class CoffeeLexer {

    private JSONArray lexicalRulesJSON;
    private ArrayList<LexicalRule> lexicalRules;
    private BufferedReader reader;
    private static final String MAIN_DELIMITER = " ";
    private static final String EMPTY_STRING = "";
    private Token head, pointer;

    public Token analyze(String programPath) throws Exception {

        reader = new BufferedReader(new FileReader(programPath));

        int rowNumber = 0;

        pointer = new Token();
        head = pointer;

        String programLine = reader.readLine();

        while (programLine != null) {

            rowNumber++;

            String expectingTokenValue = EMPTY_STRING;
            LexicalRule lastMatchedRule = null;

            char[] lineChars = programLine.toCharArray();

            for (int charIndex = 0; charIndex < lineChars.length; charIndex++) {

                char currChar = lineChars[charIndex];

                expectingTokenValue += currChar;

                if (expectingTokenValue.equals(MAIN_DELIMITER)) {
                    expectingTokenValue = EMPTY_STRING;
                    continue;
                }

                for (LexicalRule lexicalRule : lexicalRules) {
                    if (lexicalRule.matches(expectingTokenValue)) {
                        lastMatchedRule = lexicalRule;
                        break;
                    }
                }

                if (lastMatchedRule != null && !lastMatchedRule.matches(expectingTokenValue)) {
                    int valueLength = expectingTokenValue.length();
                    String value = expectingTokenValue.substring(0, valueLength - 1);
                    makeToken(value, lastMatchedRule.getType(), rowNumber, charIndex - valueLength + 2);
                    charIndex--;
                    lastMatchedRule = null;
                    expectingTokenValue = EMPTY_STRING;
                }

                if (charIndex == programLine.length() - 1 && lastMatchedRule != null && lastMatchedRule.matches(expectingTokenValue)) {
                    int valueLength = expectingTokenValue.length();
                    makeToken(expectingTokenValue, lastMatchedRule.getType(), rowNumber, charIndex - valueLength + 1);
                    expectingTokenValue = EMPTY_STRING;
                }
            }
            if (!expectingTokenValue.equals(EMPTY_STRING)) {
                throw new CoffeeException("Error at row " + rowNumber + "\nCould not recognize Token: " + expectingTokenValue);
            }
            programLine = reader.readLine();
        }
        return head.getNextToken();
    }

    private void makeToken(String value, String type, int rowNumber, int charIndex) {
        Token newToken = new Token(value, type, rowNumber, charIndex);
        pointer.setNextToken(newToken);
        pointer = pointer.getNextToken();
    }

    CoffeeLexer(String path) throws IOException, CoffeeException {
        this.lexicalRulesJSON = getRulesFromFile(path);
        lexicalRules = new ArrayList<>();
        for (int i = 0; i < lexicalRulesJSON.length(); i++) {
            JSONObject priorRules = lexicalRulesJSON.getJSONObject(i);
            Iterator iterator = priorRules.keys();
            while (iterator.hasNext()) {
                String type = iterator.next().toString();
                try {
                    lexicalRules.add(new LexicalRule(Pattern.compile(priorRules.getString(type)), type));
                } catch (Exception e) {
                    throw new CoffeeException("Could not pars format: " + type);
                }
            }
        }
    }

    private JSONArray getRulesFromFile(String rulesPath) throws IOException {
        reader = new BufferedReader(new FileReader(rulesPath));
        StringBuilder fileContent = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            fileContent.append(line);
            line = reader.readLine();
        }
        return new JSONArray(fileContent.toString());
    }
}
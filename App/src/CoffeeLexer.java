/*
Developed By Reza Manosuri
std_reza_mansouri@khu.ac.ir
Hosted On GitHub at https://github.com/rezmansouri/CoffeeLEX
 */
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CoffeeLexer {

    private final String
            SYMBOLS_KEY = "symbols",
            KEYWORDS_KEY = "keywords",
            OPERATORS_KEY = "operators",
            REGEX_KEY = "regex",
            DELIMITERS_KEY = "delimiters",
            NUMBER_KEY = "number",
            IDENTIFIER_KEY = "identifier",
            STRING_KEY = "string",
            COMMENT_KEY = "comment",
            MAIN_DELIMITER_KEY = "mainDelimiter",
            GENERAL_DELIMITERS_KEY = "generalDelimiters";

    private JSONObject rules;
    private List keywords;
    private List operators;
    private String mainDelimiter;
    private List generalDelimiters;

    private Pattern numberDefinition,
            stringDefinition,
            commentDefinition,
            identifierDefinition;

    private BufferedReader reader;

    CoffeeLexer(String path) throws IOException {
        this.rules = getRulesFromFile(path);
        JSONObject regex = rules.getJSONObject(REGEX_KEY);
        this.numberDefinition = Pattern.compile(regex.getString(NUMBER_KEY));
        this.identifierDefinition = Pattern.compile(regex.getString(IDENTIFIER_KEY));
        this.stringDefinition = Pattern.compile(regex.getString(STRING_KEY));
        this.commentDefinition = Pattern.compile(regex.getString(COMMENT_KEY));
        JSONObject symbols = rules.getJSONObject(SYMBOLS_KEY);
        this.keywords = symbols.getJSONArray(KEYWORDS_KEY).toList();
        this.operators = symbols.getJSONArray(OPERATORS_KEY).toList();
        JSONObject delimiters = rules.getJSONObject(DELIMITERS_KEY);
        this.mainDelimiter = delimiters.getString(MAIN_DELIMITER_KEY);
        this.generalDelimiters = delimiters.getJSONArray(GENERAL_DELIMITERS_KEY).toList();
    }

    Token analyze(String programPath) throws Exception {
        reader = new BufferedReader(new FileReader(programPath));
        int rowNumber = 0;
        int columnNumber = 0;
        Token head = new Token();
        Token pointer = new Token();
        head.setNextToken(pointer);
        String programLine = reader.readLine();
        while (programLine != null) {
            rowNumber++;
            columnNumber = 0;
            String expectingTokenValue = "";
            for (int columnIndex = 0; columnIndex < programLine.length(); columnIndex++) {
                char currentProgramCharacter = programLine.charAt(columnIndex);
                expectingTokenValue += currentProgramCharacter;
                if (expectingTokenValue.equals(mainDelimiter)) {
                    expectingTokenValue = "";
                    columnNumber++;
                    continue;
                }
                if (isKeyWord(expectingTokenValue)) {
                    pointer.setNextToken(new Token(expectingTokenValue, "keyword", rowNumber, columnNumber + 1));
                    pointer = pointer.getNextToken();
                    expectingTokenValue = "";
                    columnNumber = columnIndex;
                } else if (isOperator(expectingTokenValue)) {
                    int tempColumnIndex = columnIndex + 1;
                    String tempExpectingTokenValue = "";
                    boolean outOfBound = false, presentDelim = false;
                    if (tempColumnIndex != programLine.length()) {
                        tempExpectingTokenValue = expectingTokenValue + programLine.charAt(tempColumnIndex);
                        Matcher numberMatcher = numberDefinition.matcher(tempExpectingTokenValue);
                        if (isOperator(tempExpectingTokenValue)) {
                            while (isOperator(tempExpectingTokenValue)) {
                                if (tempExpectingTokenValue.contains(mainDelimiter)) {
                                    presentDelim = true;
                                    break;
                                }
                                tempColumnIndex++;
                                if (tempColumnIndex == programLine.length()) {
                                    outOfBound = true;
                                    break;
                                }
                                tempExpectingTokenValue = tempExpectingTokenValue + programLine.charAt(tempColumnIndex);
                            }
                            expectingTokenValue = tempExpectingTokenValue;
                            if (outOfBound) {
                                pointer.setNextToken(new Token(expectingTokenValue, "operator", rowNumber, columnNumber + 1));
                                pointer = pointer.getNextToken();
                                expectingTokenValue = "";
                                columnNumber = columnIndex;
                                columnIndex = tempColumnIndex;
                            } else if (presentDelim) {
                                expectingTokenValue = expectingTokenValue.substring(0, expectingTokenValue.length() - mainDelimiter.length());
                                pointer.setNextToken(new Token(expectingTokenValue, "operator", rowNumber, columnNumber + 1));
                                pointer = pointer.getNextToken();
                                expectingTokenValue = "";
                                columnNumber = columnIndex;
                                columnIndex = tempColumnIndex;
                            } else {
                                expectingTokenValue = expectingTokenValue.substring(0, expectingTokenValue.length() - 1);
                                pointer.setNextToken(new Token(expectingTokenValue, "operator", rowNumber, columnNumber + 1));
                                pointer = pointer.getNextToken();
                                expectingTokenValue = "";
                                columnNumber = columnIndex;
                                columnIndex = tempColumnIndex - 1;
                            }
                        } else if (numberMatcher.matches()) {
                            while (numberMatcher.matches()) {
                                if (tempExpectingTokenValue.contains(mainDelimiter)) {
                                    presentDelim = true;
                                    break;
                                }
                                tempColumnIndex++;
                                if (tempColumnIndex == programLine.length()) {
                                    outOfBound = true;
                                    break;
                                }
                                tempExpectingTokenValue = tempExpectingTokenValue + programLine.charAt(tempColumnIndex);
                                numberMatcher = numberDefinition.matcher(tempExpectingTokenValue);
                            }
                            expectingTokenValue = tempExpectingTokenValue;
                            if (outOfBound) {
                                pointer.setNextToken(new Token(expectingTokenValue, "number", rowNumber, columnNumber + 1));
                                pointer = pointer.getNextToken();
                                expectingTokenValue = "";
                                columnNumber = columnIndex;
                                columnIndex = tempColumnIndex;
                            } else if (presentDelim) {
                                expectingTokenValue = expectingTokenValue.substring(0, expectingTokenValue.length() - mainDelimiter.length());
                                pointer.setNextToken(new Token(expectingTokenValue, "number", rowNumber, columnNumber + 1));
                                pointer = pointer.getNextToken();
                                expectingTokenValue = "";
                                columnNumber = columnIndex;
                                columnIndex = tempColumnIndex;
                            } else {
                                expectingTokenValue = expectingTokenValue.substring(0, expectingTokenValue.length() - 1);
                                pointer.setNextToken(new Token(expectingTokenValue, "number", rowNumber, columnNumber + 1));
                                pointer = pointer.getNextToken();
                                expectingTokenValue = "";
                                columnNumber = columnIndex;
                                columnIndex = tempColumnIndex - 1;
                            }
                        } else {
                            pointer.setNextToken(new Token(expectingTokenValue, "operator", rowNumber, columnNumber + 1));
                            pointer = pointer.getNextToken();
                            expectingTokenValue = "";
                            columnNumber = columnIndex;
                        }
                    } else {
                        pointer.setNextToken(new Token(expectingTokenValue, "operator", rowNumber, columnNumber + 1));
                        pointer = pointer.getNextToken();
                        expectingTokenValue = "";
                        columnNumber = columnIndex;
                    }
                } else if (isGeneralDelimiter(expectingTokenValue)) {
                    expectingTokenValue = "";
                    columnNumber = columnIndex;
                } else {
                    Matcher numberMatcher = numberDefinition.matcher(expectingTokenValue),
                            identifierMatcher = identifierDefinition.matcher(expectingTokenValue),
                            stringMatcher = stringDefinition.matcher(expectingTokenValue),
                            commentMatcher = commentDefinition.matcher(expectingTokenValue);
                    if (numberMatcher.matches()) {
                        int tempColumnIndex = columnIndex + 1;
                        String tempExpectingTokenValue = "";
                        boolean numberIsContinued = false, outOfBound = false, presentDelim = false;
                        if (tempColumnIndex != programLine.length()) {
                            tempExpectingTokenValue = expectingTokenValue + programLine.charAt(tempColumnIndex);
                            numberMatcher = numberDefinition.matcher(tempExpectingTokenValue);
                            while (numberMatcher.matches()) {
                                if (tempExpectingTokenValue.contains(mainDelimiter)) {
                                    presentDelim = true;
                                    break;
                                }
                                numberIsContinued = true;
                                tempColumnIndex++;
                                if (tempColumnIndex == programLine.length()) {
                                    outOfBound = true;
                                    break;
                                }
                                tempExpectingTokenValue += programLine.charAt(tempColumnIndex);
                                numberMatcher = numberDefinition.matcher(tempExpectingTokenValue);
                            }
                        }
                        if (numberIsContinued) {
                            if (presentDelim) {
                                expectingTokenValue = tempExpectingTokenValue.substring(0, tempExpectingTokenValue.length() - mainDelimiter.length());
                            } else if (outOfBound) {
                                columnIndex = tempColumnIndex - 1;
                                expectingTokenValue = tempExpectingTokenValue;
                            } else {
                                columnIndex = tempColumnIndex - 1;
                                expectingTokenValue = tempExpectingTokenValue.substring(0, tempExpectingTokenValue.length() - 1);
                            }
                        }
                        pointer.setNextToken(new Token(expectingTokenValue, "number", rowNumber, columnNumber + 1));
                        pointer = pointer.getNextToken();
                        expectingTokenValue = "";
                        columnNumber = columnIndex;
                    } else if (identifierMatcher.matches()) {
                        int tempColumnIndex = columnIndex + 1;
                        String tempExpectingTokenValue = "";
                        boolean identifierIsContinued = false, outOfBound = false, presentDelim = false;
                        if (tempColumnIndex != programLine.length()) {
                            tempExpectingTokenValue = expectingTokenValue + programLine.charAt(tempColumnIndex);
                            presentDelim = tempExpectingTokenValue.contains(mainDelimiter);
                            identifierMatcher = identifierDefinition.matcher(tempExpectingTokenValue);
                            while (identifierMatcher.matches()) {
                                identifierIsContinued = true;
                                tempColumnIndex++;
                                if (tempColumnIndex == programLine.length()) {
                                    outOfBound = true;
                                    break;
                                }
                                tempExpectingTokenValue += programLine.charAt(tempColumnIndex);
                                if (tempExpectingTokenValue.contains(mainDelimiter)) {
                                    presentDelim = true;
                                    break;
                                }
                                identifierMatcher = identifierDefinition.matcher(tempExpectingTokenValue);
                            }
                        }
                        if (identifierIsContinued) {
                            if (presentDelim) {
                                expectingTokenValue = tempExpectingTokenValue.substring(0, tempExpectingTokenValue.length() - mainDelimiter.length());
                                if (isKeyWord(expectingTokenValue)) {
                                    pointer.setNextToken(new Token(expectingTokenValue, "keyword", rowNumber, columnNumber + 1));
                                    pointer = pointer.getNextToken();
                                    expectingTokenValue = "";
                                    columnNumber = tempColumnIndex - mainDelimiter.length();
                                    columnIndex = tempColumnIndex;
                                } else {
                                    pointer.setNextToken(new Token(expectingTokenValue, "identifier", rowNumber, columnNumber + 1));
                                    pointer = pointer.getNextToken();
                                    expectingTokenValue = "";
                                    columnIndex = tempColumnIndex;
                                    columnNumber = tempColumnIndex - mainDelimiter.length();
                                }
                            } else if (outOfBound) {
                                if (isKeyWord(tempExpectingTokenValue)) {
                                    pointer.setNextToken(new Token(tempExpectingTokenValue, "keyword", rowNumber, columnNumber + 1));
                                    pointer = pointer.getNextToken();
                                    expectingTokenValue = "";
                                    columnNumber = tempColumnIndex - 1;
                                    columnIndex = tempColumnIndex;
                                } else {
                                    columnIndex = tempColumnIndex - 1;
                                    columnNumber = tempColumnIndex - 1;
                                    expectingTokenValue = tempExpectingTokenValue;
                                }
                            } else {
                                expectingTokenValue = tempExpectingTokenValue.substring(0, tempExpectingTokenValue.length() - 1);
                                if (isKeyWord(expectingTokenValue)) {
                                    pointer.setNextToken(new Token(expectingTokenValue, "keyword", rowNumber, columnNumber + 1));
                                    expectingTokenValue = "";
                                    pointer = pointer.getNextToken();
                                    columnNumber = tempColumnIndex - 1;
                                } else {
                                    pointer.setNextToken(new Token(expectingTokenValue, "identifier", rowNumber, columnNumber + 1));
                                    expectingTokenValue = "";
                                    pointer = pointer.getNextToken();
                                    columnNumber = tempColumnIndex - 1;
                                }
                                columnIndex = tempColumnIndex - 1;
                            }
                        } else {
                            pointer.setNextToken(new Token(expectingTokenValue, "identifier", rowNumber, columnNumber + 1));
                            pointer = pointer.getNextToken();
                            expectingTokenValue = "";
                            columnIndex = tempColumnIndex - 1;
                            columnNumber = tempColumnIndex - mainDelimiter.length();
                            columnIndex = presentDelim ? tempColumnIndex : tempColumnIndex - 1;
                        }
                    } else if (stringMatcher.matches()) {
                        int tempColumnIndex = columnIndex + 1;
                        String tempExpectingTokenValue = "";
                        boolean stringIsContinued = false, outOfBound = false, presentDelim = false;
                        if (tempColumnIndex != programLine.length()) {
                            tempExpectingTokenValue = expectingTokenValue + programLine.charAt(tempColumnIndex);
                            stringMatcher = stringDefinition.matcher(tempExpectingTokenValue);
                            while (stringMatcher.matches()) {
                                stringIsContinued = true;
                                if (tempExpectingTokenValue.contains(mainDelimiter)) {
                                    presentDelim = true;
                                    break;
                                }
                                tempColumnIndex++;
                                if (tempColumnIndex == programLine.length()) {
                                    outOfBound = true;
                                    break;
                                }
                                tempExpectingTokenValue += programLine.charAt(tempColumnIndex);
                                stringMatcher = stringDefinition.matcher(tempExpectingTokenValue);
                            }
                        }
                        if (stringIsContinued) {
                            if (presentDelim) {
                                expectingTokenValue = tempExpectingTokenValue.substring(0, tempExpectingTokenValue.length() - mainDelimiter.length());
                            } else if (outOfBound) {
                                columnIndex = tempColumnIndex - 1;
                                expectingTokenValue = tempExpectingTokenValue;
                            } else {
                                columnIndex = tempColumnIndex - 1;
                                expectingTokenValue = tempExpectingTokenValue.substring(0, tempExpectingTokenValue.length() - 1);
                            }
                        }
                        pointer.setNextToken(new Token(expectingTokenValue, "string", rowNumber, columnNumber + 1));
                        pointer = pointer.getNextToken();
                        expectingTokenValue = "";
                        columnNumber = columnIndex;
                    } else if (commentMatcher.matches()) {
                        int tempColumnIndex = columnIndex + 1;
                        if (tempColumnIndex != programLine.length()) {
                            String tempExpectingTokenValue = expectingTokenValue + programLine.charAt(tempColumnIndex);
                            commentMatcher = commentDefinition.matcher(tempExpectingTokenValue);
                            while (commentMatcher.matches()) {
                                tempColumnIndex++;
                                if (tempColumnIndex == programLine.length()) break;
                                tempExpectingTokenValue += programLine.charAt(tempColumnIndex);
                                commentMatcher = commentDefinition.matcher(tempExpectingTokenValue);
                            }
                            columnIndex = tempColumnIndex;
                        }
                        expectingTokenValue = "";
                        columnNumber = columnIndex;
                    }
                }
            }
            if (!expectingTokenValue.equals("")) {
                throw new CoffeeException("Error at row " + rowNumber + " column " + columnNumber + "\t*" + expectingTokenValue + "*\nCould not recognize Token.");
            }
            programLine = reader.readLine();
        }
        return head.getNextToken().getNextToken();
    }

    private JSONObject getRulesFromFile(String rulesPath) throws IOException {
        reader = new BufferedReader(new FileReader(rulesPath));
        StringBuilder fileContent = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            fileContent.append(line);
            line = reader.readLine();
        }
        return new JSONObject(fileContent.toString());
    }

    private boolean isKeyWord(String value) {
        return keywords.contains(value);
    }

    private boolean isOperator(String value) {
        return operators.contains(value);
    }

    private boolean isGeneralDelimiter(String value) {
        return generalDelimiters.contains(value);
    }

}

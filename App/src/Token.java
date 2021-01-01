/*
Developed By Reza Manosuri
std_reza_mansouri@khu.ac.ir
Hosted On GitHub at https://github.com/rezmansouri/CoffeeLEX
 */
class Token {
    private String value;
    private String type;
    private Token nextToken;
    private int rowNumber;
    private int columnNumber;

    Token(String value, String type, int rowNumber, int columnNumber) {
        this.value = value;
        this.type = type;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
    }

    Token() {
    }

    void setNextToken(Token t) {
        this.nextToken = t;
    }

    boolean hasNext() {
        return nextToken != null;
    }

    Token getNextToken() {
        return nextToken;
    }

    String getValue() {
        return value;
    }

    String getType() {
        return type;
    }

    String getColumnNumber() {
        return "" + columnNumber;
    }

    String getRowNumber() {
        return "" + rowNumber;
    }

    public String toString() {
        return "value: *" + value + "*\t\ttype: " + type + "\t\trow: " + rowNumber + "\t\tcolumn: " + columnNumber;
    }
}
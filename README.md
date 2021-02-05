# CoffeeLEX ☕
## CoffeeLEX is a Lexical Analyzer for programming languages.
It takes the Lexical Rules of a language and a program as inputs and checks for errors.

The output of CoffeeLEX is a linked list of the tokens of the program.


***Current Version of CoffeeLEX accepts a more dynamic representation of the Lexical Rules Input compared to the Version 1.0***

***It was improved to work with SynTAXI Syntax Analyzer hosted at https://github.com/rezmansouri/SynTAXI***

## Input Format

The rules need to be given in a JSON Format File.

It must consist of a JSON Array of JSON Objects.

Each JSON Object  holds token signatures of tokens with similar priorities to be analyzed.

For example in the following input :

```
[
  {
    "Int Keyword": "int",
    "Character Input Keyword": "cin",
    "Character Output keyword": "cout",
    "For Keyword": "for",
    "Return": "return"
  },
  {
    "Identifier": "[A-Z_a-z]+",
    "Comma": ",",
    "Semicolon": ";",
    "Parentheses Open": "\\(",
    "Parentheses Close": "\\)",
    "Increment Operator": "\\+\\+",
    "Assignment": "=",
    "Multiply": "\\*",
    "Accolade Open": "\\{",
    "Accolade Close": "\\}",
    "Get Char Operator": ">>",
    "Put Char Operator": "<<",
    "Smaller Equal": "<=",
    "Number":"[+-]?([0-9]*[.])?[0-9]+",
    "String":"\".*\""
  }
]
```

The first JSON Object is declaring the signature of the reserved keywords for the language which have the highest priority to be analyzed.

For example for the token value of "cout" if there is no priority set, it will be analyzed as an identifier instead of its own kind.
## Process

CoffeeLEX gets the address of your rules JSON File and the address of your Program source code file.

Using an implicit automata in the CoffeeLEX.analyze() the source code is checked and the tokens are generated.

***Note: CoffeeLEX does not generate tokens for comments and delimiters as it should not***

The token structure is implemented as follows:

* value:         The value of the token.
* type:          The type of the token wether it's a keyword or and identifier or an operator or a string.
* nextToken:     A reference to the next token which is used in the linkedList that the CoffeeLEX is generating.
* rowNumber:     An integer declaring the line of the program in which the token is at.
* columnNumber:  An integer declaring the column of the program in which the token is at.

## Output Format

The head of the generated Linked List is stored at headOfLinkedList token Object in the Main class:

```
public class Main {
    public static void main(String[] args) throws Exception {
        String[] paths = brewCoffee();
        CoffeeLexer coffeeLexer = new CoffeeLexer(paths[0]);
        Token headOfLinkedList = coffeeLexer.analyze(paths[1]);
        ...
```
The Main class also prints out all the nodes of the generated LinkedList in the pourCoffee() method.
`
 pourCoffee(headOfLinkedList);
`

If any Error/Exceptions occur during the analysis, the CoffeeException class is used to throw Exceptions with Custom messages.

`
Exception in thread "main" CoffeeException: Error at row 11 column 8	*&*
Could not recognize Token.
`

### Output Example

For Example The output of CoffeeLEX for the previously mentioned rules and the following program
```
int main()
{
 int i,fact=1,number;
 cout << "Enter a number: ";
  cin >> number;
    for(i=1;i<=number;i++){
      fact=fact*i;
  }
  cout << fact;
return 0;
}
```
is as follows:
```
Value               Type                      Row  Column  
-----               ----                      ---  -----   
int                 Int Keyword               1    1       
main                Identifier                1    5       
(                   Parentheses Open          1    9       
)                   Parentheses Close         1    9       
{                   Accolade Open             2    0       
int                 Int Keyword               3    2       
i                   Identifier                3    6       
,                   Comma                     3    7       
fact                Identifier                3    8       
=                   Assignment                3    12      
1                   Number                    3    13      
,                   Comma                     3    14      
number              Identifier                3    15      
;                   Semicolon                 3    20      
cout                Character Output keyword  4    2       
<<                  Put Char Operator         4    7       
"Enter a number: "  String                    4    10      
;                   Semicolon                 4    27      
cin                 Character Input Keyword   5    3       
>>                  Get Char Operator         5    7       
number              Identifier                5    10      
;                   Semicolon                 5    15      
for                 For Keyword               6    5       
(                   Parentheses Open          6    8       
i                   Identifier                6    9       
=                   Assignment                6    10      
1                   Number                    6    11      
;                   Semicolon                 6    12      
i                   Identifier                6    13      
<=                  Smaller Equal             6    14      
number              Identifier                6    16      
;                   Semicolon                 6    22      
i                   Identifier                6    23      
++                  Increment Operator        6    24      
)                   Parentheses Close         6    26      
{                   Accolade Open             6    26      
fact                Identifier                7    7       
=                   Assignment                7    11      
fact                Identifier                7    12      
*                   Multiply                  7    16      
i                   Identifier                7    17      
;                   Semicolon                 7    17      
}                   Accolade Close            8    2       
cout                Character Output keyword  9    3       
<<                  Put Char Operator         9    8       
fact                Identifier                9    11      
;                   Semicolon                 9    14      
return              Return                    10   1       
0                   Number                    10   8       
;                   Semicolon                 10   8       
}                   Accolade Close            11   0       
CoffeeLex © - 2021
Developed By Reza Mansouri
```

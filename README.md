# CoffeeLEX©
## CoffeeLEX is a Lexical Analyzer for programming languages.
It takes the Lexical Rules of a language and a program as inputs and checks for errors.

The output of CoffeeLEX is a linked list of the tokens of the program.
## Input Format
The rules need to be given in a JSON Format File.

The file must consist of a JSON object with the Following Keys:

1. **symbols**

   A JSON object with the following keys:

   1. **keywords**:
   
        A JSON array of the preserved keywords of the programming language as strings.
   
   2. **operators**:
   
        A JSON array of the operators of the programming language such as *; ? || ++ / .* as strings.
   
2. **regex**

   1. **number**:
   
        The regular expression in which the numbers in your language must match as a string.
   
   2. **identifier**
   
        The regular expression in which the identifiers (such as variable names or function names) in your language must match as a string.
   
   3. **string**
   
        The regular expression in which the numbers in your language must match as a string.
   
   4. **comment**
   
        The regular expression in which the comments in your language must match as a string. 
   
   ***Note: Only Single Line Comments are Covered By CoffeeLEX***
   
3. **delimiters**

     1. **mainDelimiter**
     
          The main delimiter of your language which is usually *Space* as a string.
          
     2. **generalDelimiters**
     
          The general delimiters of your language such as *\n \r \t* as strings.
          
### Input Example:
```
{
 "symbols": {
    "keywords": [
      "main",
      "public",
      "static",
      "void",
      "class",
      "int",
      "for",
      "while",
      "String",
      "System"
    ],
    "operators": [
      "=",
      ".",
      "+",
      "-",
      ";",
      "<",
      "<=",
      "++",
      "{",
      "}",
      "(",
      ")",
      "[",
      "]"
    ]
  },
  "regex": {
    "number": "[+-]?([0-9]*[.])?[0-9]+",
    "identifier": "[A-Z_a-z]+",
    "string": "[\"][\.]*[\"]",
    "comment": "//[\.| ]*"
 },
  "delimiters": {
    "mainDelimiter": " ",
    "generalDelimiters": [
      "\n",
      "\t",
      "\r"
    ]
  }
}
```

## Process

CoffeeLEX gets the address of your rules JSON File and the address of your Program source code file.

Using an implicit automata in the CoffeeLEX.analyze() the source code is checked and the tokens are generated.

***Note: CoffeeLEX does not generate tokens for comments and delimiters as it should not***

The token structure is implemented as follows:

* value:         The value of the token.
* type:          The type of the token whether it's a keyword or an identifier or an operator or a string.
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
class Example {
    public static void main(String[] args) {
        int integerVariable = 100;
        int minusIntegerVariable = -100;
        int result = integerVariable - minusIntegerVariable;
        for (int i=0; i<result; i++) {
            System.out.println(i);
        }
        String x = "hello";
        //This is a Comment
    }
}

```
is as follows:
```
Value                 Type        Row  Column  
-----                 ----        ---  -----   
class                 keyword     1    1       
Example               identifier  1    5       
{                     operator    1    13      
public                keyword     2    5       
static                keyword     2    10      
void                  keyword     2    17      
main                  keyword     2    22      
(                     operator    2    27      
String                keyword     2    28      
[                     operator    2    34      
]                     operator    2    35      
args                  identifier  2    37      
)                     operator    2    41      
{                     operator    2    43      
int                   keyword     3    9       
integerVariable       identifier  3    11      
=                     operator    3    27      
100                   number      3    30      
;                     operator    3    33      
int                   keyword     4    9       
minusIntegerVariable  identifier  4    11      
=                     operator    4    32      
-100                  number      4    35      
;                     operator    4    36      
int                   keyword     5    9       
result                identifier  5    11      
=                     operator    5    18      
integerVariable       identifier  5    21      
-                     operator    5    36      
minusIntegerVariable  identifier  5    39      
;                     operator    5    59      
for                   keyword     6    9       
(                     operator    6    11      
int                   keyword     6    13      
i                     identifier  6    16      
=                     operator    6    18      
0                     number      6    19      
;                     operator    6    20      
i                     identifier  6    22      
<                     operator    6    23      
result                identifier  6    24      
;                     operator    6    30      
i                     identifier  6    32      
++                    operator    6    33      
)                     operator    6    34      
{                     operator    6    37      
System                keyword     7    13      
.                     operator    7    18      
out                   identifier  7    19      
.                     operator    7    22      
println               identifier  7    23      
(                     operator    7    30      
i                     identifier  7    31      
)                     operator    7    32      
;                     operator    7    33      
}                     operator    8    9       
String                keyword     9    9       
x                     identifier  9    14      
=                     operator    9    16      
"hello"               string      9    19      
;                     operator    9    26      
}                     operator    11   5       
}                     operator    12   1       
```

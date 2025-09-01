Grammar for the *Mica* language:

```
symbol                  = [a-zA-Z_] [a-zA-Z0-9_]*
type                    = int | real | char | string | bool
                            | intRange | realRange | any
                            | ( '[' type ']' ) | ( '{' type '}' )

boolLiteral             = 'true' | 'false'
charLiteral             = '\'' . '\''
stringLiteral           = '"' .* '"'
intLiteral              = [0-9] [0-9_]*
realLiteral             = intLiteral '.' [0-9] [0-9_]*
hexLiteral              = '0x' [0-9a-fA-F]+
binaryLiteral           = '0b' [0-1]+
exponentLiteral         = ( intLiteral | realLiteral ) 'e' '-'? intLiteral
intRangeLiteral         = intLiteral '..' intLiteral
realRangeLiteral        = realLiteral '..' realLiteral
arrayLiteral            = '[' ( expression ( ',' expression )* )? ']'
setLiteral              = '{' ( expression ( ',' expression )* )? '}'

functionCall            = symbol '(' ( expression ( ',' expression )* )? ')'

expression              = boolLiteral | charLiteral | stringLiteral
                            | intLiteral | realLiteral | hexLiteral
                            | binaryLiteral | exponentLiteral | intRangeLiteral
                            | realRangeLiteral | arrayLiteral | setLiteral
                            | functionCall
                            | ( expression '[' expression ']' )
                            | ( '(' expression ')' )
                            | ( '-' | '+' | '!' ) expression
                            | expression ( '+' | '-' | '*' | '/' | '^' | '&' | '|' ) expression

declarationStatement    = symbol ( ':' type )? '=' expression
assignmentStatement     = symbol ( '=' | '+=' | '-=' ) expression
affixationStatement     = symbol ( '++' | '--' ) | ( '++' | '--' ) symbol
returnStatement         = 'return' expression?
breakStatement          = 'break'

blockBody               = statement | ( '{' statement* '}' )

ifStatement             = 'if' expression blockBody ( 'else if' blockBody )? ( 'else' blockBody )?
loopIfStatement         = 'loop' ( 'if' expression )? blockBody ( 'else' blockBody )?
loopInStatement         = 'loop' symbol 'in' expression blockBody
expressionStatement     = expression
userInputStatement      = '<' symbol
userOutputStatement     = '>' expression

statement               = declaration | assignment | returnStatement | breakStatement | ifStatement

functionDeclaration     = symbol '(' ( symbol ':' type ( ',' symbol ':' type )* )? ')' ( ':' type )? '{' statement* '}'

propertyDeclaration     = symbol ':' type
typeDeclaration         = type symbol '{' propertyDeclaration* functionDeclaration* '}'

rootStatement           = statement | functionDeclaration
```

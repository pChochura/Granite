Grammar for the *Mica* language:

```
symbol                  = [a-zA-Z_] [a-zA-Z0-9_]*
type                    = 'int' | 'real' | 'char' | 'string' | 'bool'
                            | 'intRange' | 'realRange' | 'any'
                            | ( '[' type ']' ) | ( '{' type '}' )

boolLiteral             = 'true' | 'false'
charLiteral             = '\'' . '\''
stringLiteral           = '"' .* '"'
intLiteral              = [0-9] [0-9_]*
realLiteral             = intLiteral '.' intLiteral
hexLiteral              = '0x' [0-9a-fA-F]+
binaryLiteral           = '0b' [0-1]+
exponentLiteral         = ( intLiteral | realLiteral ) 'e' '-'? intLiteral
intRangeLiteral         = intLiteral '..' intLiteral
realRangeLiteral        = realLiteral '..' realLiteral
arrayLiteral            = '[' ( expression ( ',' expression )* ','? )? ']'
setLiteral              = '{' ( expression ( ',' expression )* ','? )? '}'

functionCallExpression  = symbol '(' ( expression ( ',' expression )* ','? )? ')'

expressionBlockBody     = expressionStatement | ( '{' statement* expressionStatement '}' )
ifConditionExpression   = 'if' expression expressionBlockBody ( 'else if' expressionBlockBody )? 'else' expressionBlockBody

expression              = boolLiteral | charLiteral | stringLiteral
                            | intLiteral | realLiteral | hexLiteral
                            | binaryLiteral | exponentLiteral | intRangeLiteral
                            | realRangeLiteral | arrayLiteral | setLiteral
                            | functionCallExpression
                            | ifConditionExpression
                            | ( expression '[' expression ']' )
                            | ( '(' expression ')' )
                            | ( ( '-' | '+' | '!' ) expression )
                            | ( expression ( '+' | '-' | '*' | '/' | '^' | '&' | '|' ) expression )

declarationStatement    = symbol ( ':' type )? '=' expression
assignmentStatement     = symbol ( '=' | '+=' | '-=' ) expression
affixationStatement     = ( symbol ( '++' | '--' ) ) | ( ( '++' | '--' ) symbol )
returnStatement         = 'return' expression?
breakStatement          = 'break'

blockBody               = statement | ( '{' statement* '}' )

ifConditionStatement    = 'if' expression blockBody ( 'else if' blockBody )? ( 'else' blockBody )?
loopIfStatement         = 'loop' ( 'if' expression )? blockBody ( 'else' blockBody )?
loopInStatement         = 'loop' symbol ( ',' symbol )? 'in' expression blockBody
expressionStatement     = expression
userInputStatement      = '<' symbol
userOutputStatement     = '>' expression

statement               = declarationStatement | assignmentStatement | affixationStatement
                            | returnStatement | breakStatement | ifConditionStatement
                            | loopIfStatement | loopInStatement | expressionStatement
                            | userInputStatement | userOutputStatement

functionDeclaration     = symbol '(' ( symbol ':' type ( '=' expression )? ( ',' symbol ':' type ( '=' expression )? ','? )* )? ')' ( ':' type )? '{' statement* '}'
typeDeclaration         = type symbol '{' ( symbol ':' type )* functionDeclaration* '}'

rootLevelStatement      = statement | functionDeclaration | typeDeclaration
```

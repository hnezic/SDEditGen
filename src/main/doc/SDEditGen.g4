grammar SDEditGen;

program
   : WHITE title? objects statements
   ;

objects
   : 'objects' '{' objectDecl+ '}'
   ;

objectDecl
   : ident ident flag*
   ;

flag
   : 'named' | 'existing'
   ;

// Statements

statements
   : statement*
   ;

statementsInBraces
   : '{' statement* '}'
   ;

statement
   : constructor | method | code | objectCreation | methodCall | diagramLink | aFragment | loop | alt
   ;

// Method call, object creation

objectCreation
   : 'create' ident arguments? result? optStatements
   ;

methodCall
   : 'call' ident methodName arguments? result? optStatements
   ;

methodName
   : ident | string
   ;

optStatements
   : statementsInBraces
   | optEnd
   ;

arguments
   : '(' ( argument (comma argument)* )? ')'
   ;

argument
   : ident
   ;

result
   : 'return' ident
   ;

// Method, constructor, call

constructor
   : 'constructor' ident statementsInBraces
   ;

method
   : ident methodName statementsInBraces
   ;

code
   : 'code' ident statementsInBraces
   ;

// Fragments

aFragment
   : 'fragment' ident? fragmentCommon
   ;

loop
   : 'loop' fragmentCommon
   ;

alt
   : 'alt' fragmentCommon
   ;

fragmentCommon
   : string? fragmentItems
   ;

fragmentItems
   : '{' fragmentItem+ '}'
   ;

fragmentItem
   : statement | sectionDelimiter
   ;

sectionDelimiter
   : 'section' string?
   ;

// Notes, comments

diagramLink
   : 'diagramLink' string ident number optEnd
   ;

title
   : 'title' string optEnd
   ;

// Tokens

optEnd
   : ';' WHITE
   ;

ident
   : IDENT WHITE
   ;

string
   : STRING WHITE
   ;

comma
   : ',' WHITE
   ;

number
   : NUMBER WHITE
   ;

IDENT
   : [a-zA-Z_] [a-zA-Z0-9_]*
   ;

STRING
   : '"' ( ~ ["] )* '"'
   ;

NUMBER
   : [0-9]+
   ;

WHITE
   : [ \t\n\r] +
   ;

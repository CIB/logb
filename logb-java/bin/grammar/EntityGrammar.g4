grammar EntityGrammar;

parse
 : entity EOF
 ;

entity
 : ID OPEN assignments? CLOSE
 ;
 
assignments
 : assignment (COMMA assignment)*
 ;

assignment
 : ID EQUALS entity
 ;


OPEN 	: '(' ;
CLOSE 	: ')' ;
COMMA 	: ',' ;
EQUALS 	: '=' ;

WHITESPACE : [ \t\r\n] -> skip;

ID
 : [a-zA-Z_] [a-zA-Z_0-9]*
 ;

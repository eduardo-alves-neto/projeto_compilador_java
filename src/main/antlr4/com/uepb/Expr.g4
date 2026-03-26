grammar Expr;

prog: NEWLINE* (stmt NEWLINE*)* EOF;

stmt:
	declVar
	| atribuicao
	| ifStmt
	| whileStmt
	| printStmt
	| inputStmt;

declVar: VAR ID ('=' expr)? ';'; 
atribuicao: ID '=' expr ';';

ifStmt: IF '(' condicao ')' '{' NEWLINE* (stmt NEWLINE*)* '}';
whileStmt: WHILE '(' condicao ')' '{' NEWLINE* (stmt NEWLINE*)* '}';

printStmt: PRINT '(' (expr | STRING) ')' ';';
inputStmt: INPUT '(' ID ')' ';';

condicao: condicaoOu;
condicaoOu: condicaoE (OR condicaoE)*;
condicaoE: condicaoPrim (AND condicaoPrim)*;
condicaoPrim: exprRelacional | TRUE | FALSE | '(' condicao ')';
exprRelacional: expr OP_REL expr;

/*

if(3 == 3 and 3 > 9 or 3 > 9) {

}

 */

expr: termo (OP1 termo)*;
termo: fator (OP2 fator)*;
fator: unario (POT fator)?;
unario: (OP1)? atom;
atom: INT | ID | '(' expr ')';

/*

  -6 ^ 2 * 2 


 */

VAR: 'var';
IF: 'if';
WHILE: 'while';
PRINT: 'print';
INPUT: 'input';
AND: 'and';
OR: 'or';
TRUE: 'true';
FALSE: 'false';

OP1: '+' | '-';
OP2: '*' | '/';
POT: '^';
OP_REL: '==' | '!=' | '>' | '>=' | '<' | '<=';

ID: [a-zA-Z_][a-zA-Z0-9_]*;
INT: [0-9]+ ('.' [0-9]+)?;
STRING: '"' (~["\r\n] | '\\' .)*? '"';

LINE_COMMENT: '//' ~[\r\n]* -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;
NEWLINE: [\r\n]+;
WS: [ \t]+ -> skip;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.token;

/**
 *
 * @author Gabriel
 */
public enum TokenType {


    ID,
    NUM,
    LITERAL,
    CARACTER,

    //OPERADORES
    ADICAO,
    SUB,
    MULT,
    DIV,
    IGUAL,
    DIF,
    MAIOR,
    MAIORIGUAL,
    MENOR,
    MENORIGUAL,
    E,
    OU,
    ATRIB,
    INCR,
    DECR,
    PONTO,

    //DELIMITADORES
    PONTOVIRGULA,
    VIRGULA,
    ABREPAR,
    FECHAPAR,
    ABRECHAVE,
    FECHACHAVE,
    ABRECOLCH,
    FECHACOLCH,

    //COMENTARIOS
    COMENTBLOCO,
    COMENTLINHA,

    //PALAVRAS RESERVADAS
    VARIAVEIS,
    METODOS,
    CONSTANTES,
    CLASSE,
    RETORNO,
    VAZIO,
    PRINCIPAL,
    SE,
    ENTAO,
    SENAO,
    ENQUANTO,
    PARA,
    LEIA,
    ESCREVA,
    INTEIRO,
    REAL,
    LOGICO,
    CARACTERE,
    CADEIA,
    VERDADEIRO,
    FALSO,
    HERDA_DE,

    //OUTROS
    ERRO,
    EOF

}

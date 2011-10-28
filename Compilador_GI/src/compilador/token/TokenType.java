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

    //DELIMITADORES
    PONTOVIRGULA,
    ABREPAR,
    FECHAPAR,
    ABRECHAVE,
    FECHACHAVE,
    ABRECOLCH,
    FECHACOLCH,

    //COMENTARIOS
    COMENTBLOCO,
    COMENTLINHA,

    ERRO,
    EOF

}

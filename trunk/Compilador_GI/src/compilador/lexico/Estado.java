/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.lexico;

/**
 *
 * @author Gabriel
 */
public enum Estado {

    START,
    EM_ID,
    EM_NUM,
    EM_OPERADOR,
    EM_DELIMITADOR,
    EM_CADEIACONSTANTE,
    EM_CARACTER,
    FIM


}

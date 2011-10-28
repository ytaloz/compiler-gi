/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.token;

/**
 *
 * @author Gabriel
 */
public enum TokenCategory {

    PALAVRA_RESERVADA,
    IDENTIFICADOR,
    CADEIA_CONSTANTE,
    NUMERO,
    OPERADOR,
    DELIMITADOR,
    COMENTARIO,
    ERRO,
    EOF
            
}

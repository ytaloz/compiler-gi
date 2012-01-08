/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.sintatico;

import compilador.token.TokenType;

/**
 *
 * @author Gabriel
 */
public class ErroSintaticoException extends RuntimeException {
    
    public TokenType tokenEsperado;

    public ErroSintaticoException( TokenType tokenEsperado)
    {
        this.tokenEsperado = tokenEsperado;
    }

}

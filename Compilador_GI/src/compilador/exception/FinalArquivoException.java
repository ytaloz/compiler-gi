/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.exception;

import compilador.token.TokenType;

/**
 *
 * @author Gabriel
 */
public class FinalArquivoException extends RuntimeException {

    public TokenType tokenEsperado;

    public FinalArquivoException( TokenType tokenEsperado )
    {
        this.tokenEsperado = tokenEsperado;
    }

    @Override
    public String getMessage() {
        return "final inesperado de arquivo! Esperava " + tokenEsperado;
    }
}

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
public class ErroSintaticoException extends RuntimeException {
    
    public TokenType tokenEsperado;
    public String mensagem = "";
    public String mensagemContexto = "";

    public ErroSintaticoException( TokenType tokenEsperado )
    {
        this.tokenEsperado = tokenEsperado;
    }

    public ErroSintaticoException( String mensagem )
    {
        this.mensagem = mensagem;
    }

    public ErroSintaticoException()
    {

    }
 
}

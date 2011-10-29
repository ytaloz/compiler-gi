/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.token;

/**
 *
 * @author Gabriel
 */
public class TokenErro extends Token {

    String mensagem;

    public TokenErro(String lexema, int linha, String mensagem)
    {
        super(TokenType.ERRO, TokenCategory.ERRO, lexema, linha);
        this.mensagem = mensagem;
    }

    public String getMensagem()
    {
        return this.mensagem;
    }

}

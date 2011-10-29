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
    int posFinal;
    int posInicial;

    public TokenErro(String lexema, int linha, String mensagem, int posFinal)
    {
        super(TokenType.ERRO, TokenCategory.ERRO, lexema, linha);
        this.mensagem = mensagem;
        this.posFinal = posFinal;
        this.posInicial = posFinal - lexema.length();
    }

    public String getMensagem()
    {
        return this.mensagem;
    }

    public int getPosFinal() {
        return posFinal;
    }

    public int getPosInicial() {
        return posInicial;
    }
}

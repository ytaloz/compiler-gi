/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.token;

/**
 *
 * @author Gabriel
 */
public class Token {

    private TokenType tipo;
    private TokenCategory categoria;
    private String lexema;

    public Token(TokenType tipo, TokenCategory categoria, String lexema)
    {
        this.tipo = tipo;
        this.categoria = categoria;
        this.lexema = lexema;
    }

    public TokenCategory getCategoria() {
        return categoria;
    }

    public void setCategoria(TokenCategory categoria) {
        this.categoria = categoria;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public TokenType getTipo() {
        return tipo;
    }

    public void setTipo(TokenType tipo) {
        this.tipo = tipo;
    }

}

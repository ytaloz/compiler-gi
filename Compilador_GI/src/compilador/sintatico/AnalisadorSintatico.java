/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.sintatico;

import compilador.gui.Janela;
import compilador.token.Token;
import java.util.List;

/**
 *
 * @author Gabriel
 */
public class AnalisadorSintatico {

    Janela janela;
    List<Token> tokens;

    public AnalisadorSintatico(Janela janela)
    {
        this.janela = janela;
    }

    public void analisar(List<Token> tokens)
    {
        this.tokens = tokens;
    }

}

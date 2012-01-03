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

    private int erros = 0;

    public AnalisadorSintatico(Janela janela)
    {
        this.janela = janela;
    }

    public void analisar(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    public void imprimirSaida()
    {
        if (temErros()) janela.imprimirCabecalhoErrosSintaticos();
        if (temErros()) janela.imprimirTotalDeErrosSintaticos(erros);
    }

    public boolean temErros()
    {
        return erros > 0;
    }

}

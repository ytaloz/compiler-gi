/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador;

import compilador.gui.Janela;
import compilador.lexico.AnalisadorLexico;
import compilador.token.Token;
import java.util.List;

/**
 *
 * @author Gabriel
 */
public class Compilador implements Runnable {

    Janela janela;
    List<Token> tokens;
    AnalisadorLexico analisadorLexico;

    public Compilador()
    {
        janela = new Janela(this);
        analisadorLexico = new AnalisadorLexico(janela);
    }

    public void analisar(String codigoFonte)
    {
        tokens = analisadorLexico.analisarTokens();
    }

    public void run() {
        analisar(janela.getCodigoFonte());
    }

}

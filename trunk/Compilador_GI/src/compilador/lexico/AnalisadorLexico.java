/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.lexico;

import compilador.TabelaDeSimbolos;
import compilador.gui.Janela;
import compilador.token.Token;
import compilador.token.TokenType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gabriel
 */
public class AnalisadorLexico implements Runnable{

    Janela janela;

    List<Token> tokens = new ArrayList<Token>();
    TabelaDeSimbolos tabelaDeSimbolos = new TabelaDeSimbolos();

    public AnalisadorLexico()
    {
        janela = new Janela(this);
        janela.setVisible(true);
    }

    public void analisarTokens()
    {
        Automato automato = new Automato(janela.getCodigoFonte(), tabelaDeSimbolos);
        Token token;

        do {
            token = automato.getToken();
            tokens.add(token);
            janela.imprimirToken(token);
        } while (token.getTipo() != TokenType.EOF);

        janela.pararAnalise();
    }

    public void run() {
        analisarTokens();
    }

    


}

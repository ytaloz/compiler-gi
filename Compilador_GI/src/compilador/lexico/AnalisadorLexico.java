/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.lexico;

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

    //Automato automato;
    List<Token> tokens = new ArrayList<Token>();

    public AnalisadorLexico()
    {
        janela = new Janela(this);
        janela.setVisible(true);
    }

    public void analisarTokens()
    {
        Automato automato = new Automato(janela.getCodigoFonte());
        Token token;

        do {
            token = automato.getToken();
            tokens.add(token);
            janela.imprimirToken(token);
        } while (token.getTipo() != TokenType.EOF);

        janela.pararAnálise();
    }

    private void testeImpressao()
    {
        janela.imprimirToken("teste", "teste", "teste", 1);
        janela.imprimirToken("teste", "teste", "teste", 2);
        janela.imprimirToken("teste", "teste", "teste", 3);
    }

    public void run() {
        analisarTokens();
        //testeImpressao();
    }

    


}

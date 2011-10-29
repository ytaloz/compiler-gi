/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.lexico;

import compilador.TabelaDeSimbolos;
import compilador.gui.Janela;
import compilador.token.Token;
import compilador.token.TokenErro;
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

    private int erros = 0;

    public AnalisadorLexico()
    {
        janela = new Janela(this);
        janela.setVisible(true);
    }

    public void analisarTokens()
    {
        erros = 0;
        Automato automato = new Automato(janela.getCodigoFonte(), tabelaDeSimbolos);
        Token token;

        do {
            token = automato.getToken();
            tokens.add(token);
            janela.imprimirToken(token);

            if(token instanceof TokenErro) {
                erros++;
                janela.imprimirErro((TokenErro)token);
            }
            
        } while (token.getTipo() != TokenType.EOF);


        if (erros == 0) {
            janela.imprimirMensagemSucesso();
        }
        janela.pararAnalise();
        
    }

    public void run() {
        analisarTokens();
    }

    


}

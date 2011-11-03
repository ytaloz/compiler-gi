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
        tokens = new ArrayList<Token>();
        Token token;

        do {
            token = automato.getProxToken();
            tokens.add(token);
            janela.imprimirToken(token);
            //if (tokens.size()==1) janela.setCaretColor();

            if (token instanceof TokenErro) {
                erros++;
                if (erros == 1) {
                    janela.imprimirCabecalhoErros();
                }
                janela.imprimirErro((TokenErro) token);
            }
            
        } while (token.getTipo() != TokenType.EOF);


        if (erros == 0) {
            janela.imprimirMensagemSucesso();
        } else {
            janela.imprimirTotalDeErros(erros);
        }
        janela.imprimirTotalDeTokens(tokens.size());
        janela.pararAnalise();
        
    }

    public void run() {
        analisarTokens();
    }

    


}

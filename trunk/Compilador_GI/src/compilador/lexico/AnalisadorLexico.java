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
public class AnalisadorLexico {

    Janela janela;

    List<Token> tokens = new ArrayList<Token>();
    TabelaDeSimbolos tabelaDeSimbolos = new TabelaDeSimbolos();

    private int erros = 0;

    public AnalisadorLexico(Janela janela)
    {
        this.janela = janela;
    }

    public List<Token> analisarTokens()
    {
        erros = 0;
        Automato automato = new Automato(janela.getCodigoFonte(), tabelaDeSimbolos);
        tokens = new ArrayList<Token>();
        Token token;

        do {
            token = automato.getProxToken();
            tokens.add(token);
            //janela.imprimirToken(token);

            if (token instanceof TokenErro) {
                erros++;
                if (erros == 1) {
                    //janela.imprimirCabecalhoErros();
                }
                //janela.imprimirErro((TokenErro) token);
            }
            
        } while (token.getTipo() != TokenType.EOF);


//        if (erros == 0) {
//            janela.imprimirMensagemSucesso();
//        } else {
//            janela.imprimirTotalDeErrosLexicos(erros);
//        }
        //janela.imprimirTotalDeTokens(tokens.size());


        //janela.pararAnalise();

        return tokens;
        
    }

    public void imprimirSaida()
    {
        if (temErros()) janela.imprimirCabecalhoErrosLexicos();

        for (Token token : tokens) {
            janela.imprimirToken(token);
            if (token instanceof TokenErro) {
                janela.imprimirErro((TokenErro) token);
            }
        }

        if (temErros()) janela.imprimirTotalDeErrosLexicos(erros);
    }

    public boolean temErros()
    {
        return erros > 0;
    }

}

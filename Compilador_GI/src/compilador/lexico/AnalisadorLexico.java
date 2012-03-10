/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.lexico;

import compilador.tabeladesimbolos.TabelaDeSimbolos;
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

    //total de erros computados
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

            if (token instanceof TokenErro) {
                erros++;
            }
            
        } while (token.getTipo() != TokenType.EOF);

        return tokens;        
    }

    public void imprimirSaida()
    {
        if (temErros()) janela.imprimirCabecalhoErrosLexicos();

        for (Token token : tokens) {
            janela.imprimirToken(token);
            if (token instanceof TokenErro) {
                janela.imprimirErroLexico((TokenErro) token);
            }
        }
    }

    public boolean temErros()
    {
        return erros > 0;
    }

    public int getErros()
    {
        return erros;
    }

}

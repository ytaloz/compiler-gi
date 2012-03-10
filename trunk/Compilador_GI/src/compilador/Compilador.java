/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador;

import compilador.gui.Janela;
import compilador.lexico.AnalisadorLexico;
import compilador.semantico.AnalisadorSemantico;
import compilador.sintatico.AnalisadorSintatico;
import compilador.tabeladesimbolos.TabelaDeSimbolos;
import compilador.token.Token;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author Gabriel
 */
public class Compilador implements Runnable {

    Janela janela;
    List<Token> tokens;
    TabelaDeSimbolos tabelaDeSimbolos = new TabelaDeSimbolos();

    AnalisadorLexico analisadorLexico;
    AnalisadorSintatico analisadorSintatico;
    AnalisadorSemantico analisadorSemantico;

    public Compilador()
    {
        janela = new Janela(this);
        analisadorLexico = new AnalisadorLexico(janela, tabelaDeSimbolos);
        analisadorSintatico = new AnalisadorSintatico(janela);
        analisadorSemantico = new AnalisadorSemantico(janela, tabelaDeSimbolos);
    }

    public void analisar(String codigoFonte)
    {
        analiseLexica();
        analiseSintatica();
        analiseSemantica();
        pararAnalise();
        imprimirSaida();
    }

    private void analiseLexica()
    {
        tokens = analisadorLexico.analisarTokens();
    }

    private void analiseSintatica()
    {
        analisadorSintatico.analisar(tokens);
    }

    private void analiseSemantica()
    {
        analisadorSemantico.analisar(tokens);
    }

    private void imprimirSaida() 
    {
        Runnable runnable = new Runnable() {
            public void run() {

                if (existemErros()) {
                    janela.imprimirCabecalhoErros();
                } else {
                    janela.imprimirMensagemSucesso();
                }

                analisadorLexico.imprimirSaida();
                if(analisadorLexico.getErros()>0) janela.pularLinhaSaida();
                analisadorSintatico.imprimirSaida();
                if(analisadorSintatico.getErros()>0) janela.pularLinhaSaida();
                analisadorSemantico.imprimirSaida();
       
                janela.imprimirTotalDeErrosLexicos(analisadorLexico.getErros());
                janela.imprimirTotalDeErrosSintaticos(analisadorSintatico.getErros());
                janela.imprimirTotalDeErrosSemanticos(analisadorSemantico.getErros());
                janela.imprimirTotalDeTokens(tokens.size());
            }
        };
        SwingUtilities.invokeLater(runnable);

    }

    private void pararAnalise()
    {
        janela.pararAnalise();
    }

    private boolean existemErros()
    {
        return ( analisadorLexico.temErros() || analisadorSintatico.temErros() || analisadorSemantico.temErros() );
    }

    public void run() {
        analisar(janela.getCodigoFonte());
    }

}

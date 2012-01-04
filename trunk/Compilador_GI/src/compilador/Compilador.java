/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador;

import compilador.gui.Janela;
import compilador.lexico.AnalisadorLexico;
import compilador.sintatico.AnalisadorSintatico;
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

    AnalisadorLexico analisadorLexico;
    AnalisadorSintatico analisadorSintatico;

    public Compilador()
    {
        janela = new Janela(this);
        
        analisadorLexico = new AnalisadorLexico(janela);
        analisadorSintatico = new AnalisadorSintatico(janela);
    }

    public void analisar(String codigoFonte)
    {
        analiseLexica();
        analiseSintatica();
        
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
                janela.imprimirLinha();
                analisadorSintatico.imprimirSaida();
       
                janela.imprimirTotalDeErrosLexicos(analisadorLexico.getErros());
                if(analisadorLexico.getErros()>0) janela.imprimirTotalDeErrosSintaticos(analisadorSintatico.getErros());
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
        return ( analisadorLexico.temErros() || analisadorSintatico.temErros() );
    }

    public void run() {
        analisar(janela.getCodigoFonte());
    }

}

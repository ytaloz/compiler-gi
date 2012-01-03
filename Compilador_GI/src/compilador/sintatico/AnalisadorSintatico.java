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

    private Janela janela;

    //lista de tokens representando o codigo fonte
    private List<Token> tokens;

    //token atual da análise
    private Token tokenAtual;

    //ponteiro do token atual sendo analisado
    private int ponteiro = -1;

    //lista de erros sintaticos
    private List<ErroSintatico> erros;


    public AnalisadorSintatico(Janela janela)
    {
        this.janela = janela;
    }


    //método principal - inicia a análise sintática, dada uma lista de tokens
    
    public void analisar(List<Token> tokens)
    {
        this.tokens = tokens;
        proxToken();
    }

    //MÉTODOS CORRESPONDENTES AO NÃO-TERMINAIS DA GRAMÁTICA

    private void programa()
    {
        
    }


    //MÉTODOS AUXILIARES

    private void proxToken()
    {
        ponteiro++;
        tokenAtual = tokens.get(ponteiro);
    }

    private void match(Token esperado)
    {
        if(tokenAtual.getTipo() == esperado.getTipo()) proxToken();
        else erroSintatico("Token inesperado: " + tokenAtual.getTipo(), tokenAtual.getLinha());
    }

    private void erroSintatico(String msg, int linha)
    {
        erros.add(new ErroSintatico(msg, linha));
    }

    public void imprimirSaida()
    {
        if (temErros()) janela.imprimirCabecalhoErrosSintaticos();
        if (temErros()) janela.imprimirTotalDeErrosSintaticos(erros.size());
    }

    public boolean temErros()
    {
        if( erros==null ) return false;
        return erros.size() > 0;
    }

}

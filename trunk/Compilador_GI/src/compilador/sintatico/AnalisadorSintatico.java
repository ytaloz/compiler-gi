/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.sintatico;

import compilador.gui.Janela;
import compilador.token.Token;
import compilador.token.TokenType;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Gabriel
 */
public class AnalisadorSintatico {

    //constantes correspondentes aos nomes dos não terminais, para buscar o conjunto primeiro
    
    public static final String BLOCO_CONSTANTES = "bloco_constantes";
    public static final String BLOCO_VARIAVEIS = "bloco_variaveis";
    public static final String CLASSES = "classes";

    private Janela janela;

    //lista de tokens representando o codigo fonte
    private List<Token> tokens;

    //token atual da análise
    private Token tokenAtual;

    //ponteiro do token atual sendo analisado
    private int ponteiro = -1;

    //lista de erros sintaticos
    private List<ErroSintatico> erros;

    //classe que armazena os conjuntos primeiros das produções da gramática
    private ConjuntoPrimeiro conjuntoPrimeiro = new ConjuntoPrimeiro();


    public AnalisadorSintatico(Janela janela)
    {
        this.janela = janela;
    }


    //método principal - inicia a análise sintática, dada uma lista de tokens
    
    public void analisar(List<Token> tokens)
    {
        this.tokens = tokens;
        proxToken();
        programa();
    }

    //MÉTODOS CORRESPONDENTES AOS NÃO-TERMINAIS DA GRAMÁTICA

    private void programa()
    {
        if (primeiro(BLOCO_CONSTANTES).contains(tokenAtual.getTipo())) {
            bloco_constantes();
            outros_blocos_programa();
        }
        if (primeiro(BLOCO_VARIAVEIS).contains(tokenAtual.getTipo())) {
            bloco_variaveis();
            classes();
        }
        if (primeiro(CLASSES).contains(tokenAtual.getTipo())) {
            classes();
        }
    }

    private void bloco_constantes()
    {
        switch( tokenAtual.getTipo() )
        {
            case CONSTANTES: {
                match(TokenType.CONSTANTES);
                match(TokenType.ABRECHAVE);
                declaracao_constantes();
                match(TokenType.FECHACHAVE);
                break;
            }
            default : erroSintatico("Erro sintático no bloco constantes!", tokenAtual.getLinha());
        }

    }

    private void declaracao_constantes()
    {
        
    }

    private void bloco_variaveis()
    {
        
    }

    private void classes()
    {
        
    }

    private void outros_blocos_programa()
    {

    }

    //MÉTODO QUE RETORNA O CONJUNTO PRIMEIRO DE UMA DADA PRODUÇÃO

    private Set<TokenType> primeiro(String producao)
    {
        return conjuntoPrimeiro.getConjunto(producao);
    }

    //MÉTODOS AUXILIARES

    private void proxToken()
    {
        ponteiro++;
        tokenAtual = tokens.get(ponteiro);
    }

    private void match(TokenType esperado)
    {
        if(tokenAtual.getTipo() == esperado) proxToken();
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

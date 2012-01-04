/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.sintatico;

import compilador.gui.Janela;
import compilador.token.Token;
import compilador.token.TokenType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Gabriel
 */
public class AnalisadorSintatico {

    //constantes correspondentes aos nomes dos não terminais que necessitam de conjunto primeiro na análise
    
    public static final String BLOCO_CONSTANTES = "bloco_constantes";
    public static final String BLOCO_VARIAVEIS = "bloco_variaveis";
    public static final String CLASSES = "classes";
    public static final String DECL_CONSTANTES_MESMO_TIPO = "decl_constantes_mesmo_tipo";
    public static final String DECL_VARIAVEIS_MESMO_TIPO = "decl_variaveis_mesmo_tipo";
    public static final String TIPO_VARIAVEL = "tipo_variavel";
    public static final String PARAMETRO_REAL = "parametro_real";

    private Janela janela;

    //lista de tokens representando o codigo fonte
    private List<Token> tokens;

    //token atual da análise
    private Token tokenAtual;

    //ponteiro do token atual sendo analisado
    private int ponteiro = -1;

    //lista de erros sintaticos
    private List<ErroSintatico> erros = new ArrayList<ErroSintatico>();

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
        inicializarVariaveis();
        
        proxToken();
        //programa();
        //bloco_constantes();
        //bloco_variaveis();
        //instanciar_obj();
        classes();

        if (! (tokenAtual.getTipo() == TokenType.EOF) ) {
            erroSintatico("Token inesperado: " + tokenAtual.getTipo(), tokenAtual.getLinha());
        }
    }

    private void inicializarVariaveis()
    {
        this.ponteiro = -1;
        erros.clear();
    }


//-------- MÉTODOS CORRESPONDENTES AOS NÃO-TERMINAIS DA GRAMÁTICA --------------

//    private void programa()
//    {
//        if (primeiro(BLOCO_CONSTANTES).contains(tokenAtual.getTipo())) {
//            bloco_constantes();
//            //outros_blocos_programa();
//        }
//        else if (primeiro(BLOCO_VARIAVEIS).contains(tokenAtual.getTipo())) {
//            bloco_variaveis();
//            classes();
//        }
//        else if (primeiro(CLASSES).contains(tokenAtual.getTipo())) {
//            classes();
//        }
//    }


    // DECLARAÇÃO DE CONSTANTES

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
        if (primeiro(DECL_CONSTANTES_MESMO_TIPO).contains(tokenAtual.getTipo())) {
            decl_constantes_mesmo_tipo();
            declaracao_constantes();
        }
    }

    private void decl_constantes_mesmo_tipo()
    {
        tipo_variavel();
        lista_decl_constantes();
        match(TokenType.PONTOVIRGULA);
    }

    private void tipo_variavel()
    {
        if ( tokenAtual.getTipo() == TokenType.INTEIRO ||
             tokenAtual.getTipo() == TokenType.REAL ||
             tokenAtual.getTipo() == TokenType.LOGICO ||
             tokenAtual.getTipo() == TokenType.CARACTERE ||
             tokenAtual.getTipo() == TokenType.CADEIA  ) proxToken();
            
        else erroSintatico("<tipo_variavel> esperado; ", tokenAtual.getLinha());
    }

    private void lista_decl_constantes()
    {
        atribuicao();
        loop_lista_decl_constantes();
    }

    private void atribuicao()
    {

    }

    private void segundo_membro_atribuicao()
    {

    }

    private void loop_lista_decl_constantes()
    {
        if (tokenAtual.getTipo() == TokenType.VIRGULA) {
            match(TokenType.VIRGULA);
            lista_decl_constantes();
        }
    }

    // DECLARAÇÃO DE VARIÁVEIS

    private void bloco_variaveis()
    {
        switch( tokenAtual.getTipo() )
        {
            case VARIAVEIS: {
                match(TokenType.VARIAVEIS);
                match(TokenType.ABRECHAVE);
                declaracao_variaveis();
                match(TokenType.FECHACHAVE);
                break;
            }
            default : erroSintatico("Erro sintático no bloco variaveis!", tokenAtual.getLinha());
        }
    }

    private void declaracao_variaveis()
    {
        if (primeiro(DECL_VARIAVEIS_MESMO_TIPO).contains(tokenAtual.getTipo())) {
            decl_variaveis_mesmo_tipo();
            declaracao_variaveis();
        }
    }

    private void decl_variaveis_mesmo_tipo()
    {
        if(primeiro(TIPO_VARIAVEL).contains(tokenAtual.getTipo())) {
            tipo_variavel();
            lista_decl_variaveis();
            match(TokenType.PONTOVIRGULA);
        }
        else if(tokenAtual.getTipo() == TokenType.ID) {
            match(TokenType.ID);
            match(TokenType.ID);
            complemento_variavel_instanciar_obj();
        }
    }

    private void lista_decl_variaveis()
    {
        match(TokenType.ID);
        complemento_decl_variavel();
    }

    private void complemento_decl_variavel()
    {
        switch( tokenAtual.getTipo() )
        {
            case VIRGULA: {
                loop_lista_decl_variaveis();
            }
            case ATRIB: {
                match(TokenType.ATRIB);
                segundo_membro_atribuicao();
                loop_lista_decl_variaveis();
            }
            case ABRECOLCH: {
                match(TokenType.ABRECOLCH);
                expressao_aritmetica();
                match(TokenType.FECHACOLCH);
            }
            default : erroSintatico("Erro sintático no complemento_decl_variavel!", tokenAtual.getLinha());
        }
    }

    private void loop_lista_decl_variaveis()
    {
        if(tokenAtual.getTipo() == TokenType.VIRGULA) {
            match(TokenType.VIRGULA);
           lista_decl_variaveis();
        }
    }

   

    // DECLARAÇÃO DE CLASSES

    private void classes()
    {
        if(tokenAtual.getTipo() == TokenType.CLASSE) {
           classe();
           classes();
        }
    }

    private void classe()
    {
        match(TokenType.CLASSE);
        match(TokenType.ID);
        complemento_decl_classe();
    }

    private void complemento_decl_classe()
    {
         if(tokenAtual.getTipo() == TokenType.ABRECHAVE) {
           match(TokenType.ABRECHAVE);
           blocos_classe();
           match(TokenType.FECHACHAVE);
        }
         else if(tokenAtual.getTipo() == TokenType.HERDA_DE)
         {
             match(TokenType.HERDA_DE);
             match(TokenType.ID);
             match(TokenType.ABRECHAVE);
             blocos_classe();
             match(TokenType.FECHACHAVE);
         }
         else erroSintatico("Token inesperado: " + tokenAtual.getTipo(), tokenAtual.getLinha());
    }

    private void blocos_classe()
    {
         if(tokenAtual.getTipo() == TokenType.CONSTANTES) {
          bloco_constantes();
          bloco_variaveis();
          bloco_metodos();
        }
    }

    // METODOS

    private void bloco_metodos()
    {
        match(TokenType.METODOS);
        match(TokenType.ABRECHAVE);
        declaracao_metodos();
        metodo_principal();
    }

    private void declaracao_metodos()
    {

    }

    private void metodo_principal()
    {
        
    }

    private void outros_blocos_programa()
    {

    }

    // OBJETOS

    private void instanciar_obj()
    {
        match(TokenType.ID);
        match(TokenType.ID);
        complemento_variavel_instanciar_obj();
    }

    private void complemento_variavel_instanciar_obj()
    {
        if(tokenAtual.getTipo() == TokenType.ABREPAR) {
            match(TokenType.ABREPAR);
           parametros_reais();
           match(TokenType.FECHAPAR);
        }
    }

    private void parametros_reais()
    {
        if(primeiro(PARAMETRO_REAL).contains(tokenAtual.getTipo())) {
            parametro_real();
            loop_parametros_reais();
        }
    }

    private void parametro_real()
    {
        if ( tokenAtual.getTipo() == TokenType.ID ||
             tokenAtual.getTipo() == TokenType.NUM ||
             tokenAtual.getTipo() == TokenType.CADEIA ||
             tokenAtual.getTipo() == TokenType.CARACTER ||
             tokenAtual.getTipo() == TokenType.VERDADEIRO ||
             tokenAtual.getTipo() == TokenType.FALSO  ) proxToken();

        else erroSintatico("<parametro_real> esperado; ", tokenAtual.getLinha());
    }

    private void loop_parametros_reais()
    {
        if(tokenAtual.getTipo() == TokenType.VIRGULA) {
            match(TokenType.VIRGULA);
            parametros_reais();
        }
    }

    // EXPRESSOES

    private void expressao_aritmetica()
    {
        
    }


//-------- MÉTODO QUE RETORNA O CONJUNTO PRIMEIRO DE UMA DADA PRODUÇÃO ---------

    private Set<TokenType> primeiro(String producao)
    {
        return conjuntoPrimeiro.getConjunto(producao);
    }

//--------------------------- MÉTODOS AUXILIARES -------------------------------

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

        for (ErroSintatico erro : erros) {
            janela.imprimirErroSintatico(erro);
        }

        if (temErros()) janela.imprimirTotalDeErrosSintaticos(erros.size());
    }

    public boolean temErros()
    {
        if( erros==null ) return false;
        return erros.size() > 0;
    }

}

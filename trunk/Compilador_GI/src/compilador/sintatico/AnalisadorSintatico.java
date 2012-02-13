/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.sintatico;

import compilador.exception.ErroSintaticoException;
import compilador.gui.Janela;
import compilador.token.Token;
import compilador.token.TokenType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Gabriel
 */
public class AnalisadorSintatico {

    //constantes correspondentes aos nomes dos não terminais que necessitam de conjunto primeiro e/ou sequencia na análise
    
    public static final String BLOCO_CONSTANTES = "bloco_constantes";
    public static final String BLOCO_VARIAVEIS = "bloco_variaveis";
    public static final String BLOCO_METODOS = "bloco_metodos";
    public static final String DECLARACAO_METODO = "declaracao_metodo";
    public static final String CLASSES = "classes";
    public static final String CLASSE = "classe";
    public static final String DECL_CONSTANTES_MESMO_TIPO = "decl_constantes_mesmo_tipo";
    public static final String DECL_VARIAVEIS_MESMO_TIPO = "decl_variaveis_mesmo_tipo";
    public static final String TIPO_VARIAVEL = "tipo_variavel";
    public static final String PARAMETRO_REAL = "parametro_real";
    public static final String PARAMETROS_MESMO_TIPO = "parametros_mesmo_tipo";
    public static final String EXPRESSAO = "expressao";
    public static final String EXPRESSAO_PARENTESE = "expressao_parentese";
    public static final String EXPRESSAO_ARITMETICA = "expressao_aritmetica";
    public static final String PROX_TRECHO_SOMA = "prox_trecho_soma";
    public static final String COMANDO_GERAL = "comando_geral";
    public static final String COMANDO_LINHA = "comando_linha";
    public static final String COMANDO_BLOCO = "comando_bloco";

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

    //classe que armazena os conjuntos sequencia das produções da gramática
    private ConjuntoSequencia conjuntoSequencia = new ConjuntoSequencia();


    public AnalisadorSintatico(Janela janela)
    {
        this.janela = janela;
    }


//----- MÉTODO PRINCIPAL - INICIA A ANÁLISE ------------------------------------
    
    public void analisar(List<Token> tokens)
    {
        this.tokens = removerErrosEComentarios((ArrayList)tokens);
        inicializarVariaveis();
        
        proxToken();

        try {
        programa();
        //bloco_constantes();
        //bloco_variaveis();
        //instanciar_obj();
        //classes();
        //expressao();
        //atribuicao();
        //comandos();
        // bloco_metodos();
        }
        catch (RuntimeException e) {
            erroSintatico(e.getMessage(), tokenAtual.getLinha());
        }

    }

    private List<Token> removerErrosEComentarios(ArrayList<Token> tokens)
    {
        List<Token> resultado = (List) tokens.clone();

        for (Iterator<Token> i = resultado.iterator(); i.hasNext();) {
            Token token = i.next();
            if (token.getTipo()==TokenType.ERRO || token.getTipo()==TokenType.COMENTBLOCO || token.getTipo()==TokenType.COMENTLINHA) {
                i.remove();
            }
        }

        return resultado;
    }

    private void inicializarVariaveis()
    {
        this.ponteiro = -1;
        erros.clear();
    }

/******************************************************************************
 ************* MÉTODOS DE RECONHECIMENTO - NÃO TERMINAIS **********************
/******************************************************************************/


//-------- INÍCIO DO PROGRAMA --------------------------------------------------

    private void programa()
    {
        try {
            if (primeiro(BLOCO_CONSTANTES).contains(tokenAtual.getTipo())) {
                bloco_constantes();
                outros_blocos_programa();
            }
            else if (primeiro(BLOCO_VARIAVEIS).contains(tokenAtual.getTipo())) {
                bloco_variaveis();
                classes();
            }
            else if (primeiro(CLASSES).contains(tokenAtual.getTipo())) {
                classes();
            }
            else if(tokenAtual.getTipo()!= TokenType.EOF) throw new ErroSintaticoException("esperava bloco de constantes, variáveis ou declaração de classe: ");
        }
        catch( ErroSintaticoException ex ) {
                erroSintatico(ex);
            }
    }

    private void outros_blocos_programa()
    {
        if(tokenAtual.getTipo() == TokenType.VARIAVEIS) {
            bloco_variaveis();
            classes();
        }
        else if(tokenAtual.getTipo() == TokenType.CLASSE) {
            classes();
        }
        else if(tokenAtual.getTipo()!=TokenType.EOF) throw new ErroSintaticoException("esperava bloco de variaveis ou declaração de classe: ");
    }


// ------------------ DECLARAÇÃO DE CONSTANTES ---------------------------------

    private void bloco_constantes()
    {
        try {
            match(TokenType.CONSTANTES);
            match(TokenType.ABRECHAVE);
            declaracao_constantes();
            match(TokenType.FECHACHAVE);
        }
        catch( ErroSintaticoException ex ) {
            ex.mensagemContexto = "Erro na declaração do bloco de constantes - ";
            erroSintatico(ex);
            panico(conjuntoSequencia.getConjunto(BLOCO_CONSTANTES));
        }
    }

    private void declaracao_constantes()
    {
        if (primeiro(DECL_CONSTANTES_MESMO_TIPO).contains(tokenAtual.getTipo())) {
            decl_constantes_mesmo_tipo();
            declaracao_constantes();
        }
        else if (tokenAtual.getTipo() != TokenType.FECHACHAVE && tokenAtual.getTipo() != TokenType.EOF) throw new ErroSintaticoException("Esperava uma declaração de constante: ");
    }

    private void decl_constantes_mesmo_tipo()
    {
        try {
            tipo_variavel();
            lista_decl_constantes();
            match(TokenType.PONTOVIRGULA);
        }
        catch (ErroSintaticoException ex) {
            erroSintatico(ex);
            panico(conjuntoSequencia.getConjunto(DECL_CONSTANTES_MESMO_TIPO));
        }
    }

    private void tipo_variavel()
    {
        if ( tokenAtual.getTipo() == TokenType.INTEIRO ||
             tokenAtual.getTipo() == TokenType.REAL ||
             tokenAtual.getTipo() == TokenType.LOGICO ||
             tokenAtual.getTipo() == TokenType.CARACTERE ||
             tokenAtual.getTipo() == TokenType.CADEIA  ) proxToken();
            
        else throw new ErroSintaticoException("Faltando declaraçao de tipo");
    }

    private void lista_decl_constantes()
    {
        atribuicao();
        loop_lista_decl_constantes();
    }

    private void loop_lista_decl_constantes()
    {
        if (tokenAtual.getTipo() == TokenType.VIRGULA) {
            match(TokenType.VIRGULA);
            lista_decl_constantes();
        }
    }

// --------------- DECLARAÇÃO DE VARIÁVEIS -------------------------------------

    private void bloco_variaveis()
    {
        try {
            match(TokenType.VARIAVEIS);
            match(TokenType.ABRECHAVE);
            declaracao_variaveis();
            match(TokenType.FECHACHAVE);
        } 
        catch (ErroSintaticoException ex) {
            ex.mensagemContexto = "Erro na declaração do bloco de variáveis - ";
            erroSintatico(ex);
            panico(conjuntoSequencia.getConjunto(BLOCO_VARIAVEIS));
        }
    }

    private void declaracao_variaveis()
    {
        if (primeiro(DECL_VARIAVEIS_MESMO_TIPO).contains(tokenAtual.getTipo())) {
            decl_variaveis_mesmo_tipo();
            declaracao_variaveis();
        }
        else if (tokenAtual.getTipo() != TokenType.FECHACHAVE && tokenAtual.getTipo() != TokenType.EOF) throw new ErroSintaticoException("Esperava uma declaração de variavel: ");
    }

    private void decl_variaveis_mesmo_tipo()
    {
        try {
            if(primeiro(TIPO_VARIAVEL).contains(tokenAtual.getTipo())) {
                tipo_variavel();
                lista_decl_variaveis();
                match(TokenType.PONTOVIRGULA);
            }
            else if(tokenAtual.getTipo() == TokenType.ID) {
                match(TokenType.ID);
                match(TokenType.ID);
                complemento_variavel_instanciar_obj();
                match(TokenType.PONTOVIRGULA);
            }
            else throw new ErroSintaticoException("esperando declaração de tipo (primitivo ou classe): ");
        }
        catch (ErroSintaticoException ex) {
            ex.mensagemContexto = "Erro na declaração de variável - ";
            erroSintatico(ex);
            panico(conjuntoSequencia.getConjunto(DECL_VARIAVEIS_MESMO_TIPO));
        }
    }

    private void lista_decl_variaveis()
    {
        if (tokenAtual.getTipo() == TokenType.ID) {
            match(TokenType.ID);
            complemento_decl_variavel();
        }
        else throw new ErroSintaticoException("Esperava um identificador na declaração da variável: ");
    }

    private void complemento_decl_variavel()
    {
        switch( tokenAtual.getTipo() )
        {
            case VIRGULA: {
                prox_trecho_lista_decl_variaveis();
                break;
            }
            case PONTOVIRGULA: {
                prox_trecho_lista_decl_variaveis();
                break;
            }
            case ATRIB: {
                match(TokenType.ATRIB);
                segundo_membro_atribuicao();
                prox_trecho_lista_decl_variaveis();
                break;
            }
            case ABRECOLCH: {
                match(TokenType.ABRECOLCH);
                expressao_aritmetica();
                match(TokenType.FECHACOLCH);
                prox_trecho_lista_decl_variaveis();
                break;
            }
        }
    }

    private void prox_trecho_lista_decl_variaveis()
    {
        if( tokenAtual.getTipo() == TokenType.VIRGULA ) {
            match(TokenType.VIRGULA);
            lista_decl_variaveis();
        }
    }


// --------- DECLARAÇÃO DE CLASSES ---------------------------------------------

    private void classes()
    {
        if(tokenAtual.getTipo() == TokenType.CLASSE) {
           classe();
           classes();
        }
        else if(tokenAtual.getTipo()!=TokenType.EOF) throw new ErroSintaticoException("esperava declaração de classe: ");
    }

    private void classe()
    {
        try{
            match(TokenType.CLASSE);
            match(TokenType.ID);
            complemento_decl_classe();
        }
        catch (ErroSintaticoException ex) {
            erroSintatico(ex);
            panico(conjuntoSequencia.getConjunto(CLASSE));
        }
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
         else throw new ErroSintaticoException("esperava a chave de abertura do corpo da classe ou a declaração de herança herda_de :");
    }

    private void blocos_classe()
    {
         if(tokenAtual.getTipo() == TokenType.CONSTANTES) {
             bloco_constantes();
             outros_blocos_classe();
        }
         else if(tokenAtual.getTipo() == TokenType.VARIAVEIS) {
             bloco_variaveis();
             bloco_metodos();
         }
         else if(tokenAtual.getTipo() == TokenType.METODOS) {
             bloco_metodos();
         }
    }

    private void outros_blocos_classe() {
         if(tokenAtual.getTipo() == TokenType.VARIAVEIS) {
             bloco_variaveis();
             bloco_metodos();
         }
         else if(tokenAtual.getTipo() == TokenType.METODOS) {
             bloco_metodos();
         }
    }

// -------------- METODOS ------------------------------------------------------

    private void bloco_metodos()
    {
        try {
            if (tokenAtual.getTipo() == TokenType.METODOS) {
                match(TokenType.METODOS);
                match(TokenType.ABRECHAVE);
                declaracao_metodos();
                match(TokenType.FECHACHAVE);
            }
        }
        catch(ErroSintaticoException ex) {
            erroSintatico(ex);
            panico(conjuntoSequencia.getConjunto(BLOCO_METODOS));
        }
    }

    private void declaracao_metodos()
    {
        if ( tokenAtual.getTipo() == TokenType.INTEIRO ||
             tokenAtual.getTipo() == TokenType.REAL ||
             tokenAtual.getTipo() == TokenType.LOGICO ||
             tokenAtual.getTipo() == TokenType.CARACTERE ||
             tokenAtual.getTipo() == TokenType.CADEIA ) {

           tipo_metodo_menos_vazio();
           declaracao_metodo();
           declaracao_metodos();
        }
        else if( tokenAtual.getTipo() == TokenType.VAZIO ) {
            match(TokenType.VAZIO);
            declaracao_metodo_vazio();
        }
    }


    private void tipo_metodo_menos_vazio()
    {
        if ( tokenAtual.getTipo() == TokenType.INTEIRO ||
             tokenAtual.getTipo() == TokenType.REAL ||
             tokenAtual.getTipo() == TokenType.LOGICO ||
             tokenAtual.getTipo() == TokenType.CARACTERE ||
             tokenAtual.getTipo() == TokenType.CADEIA ) proxToken();
        else throw new ErroSintaticoException("esperava uma declaração de tipo: ");
    }

    private void declaracao_metodo()
    {
        try {
            match(TokenType.ID);
            match(TokenType.ABREPAR);
            parametros_formais();
            match(TokenType.FECHAPAR);
            match(TokenType.ABRECHAVE);
            dec_var_metodo();
            comandos();
            match(TokenType.FECHACHAVE);
        }
         catch(ErroSintaticoException ex) {
            erroSintatico(ex);
            panico(conjuntoSequencia.getConjunto(DECLARACAO_METODO));
        }
    }

    private void dec_var_metodo()
    {
        if(tokenAtual.getTipo() == TokenType.VARIAVEIS) {
            bloco_variaveis();
        }
    }

    private void declaracao_metodo_vazio()
    {
        try {
            if (tokenAtual.getTipo() == TokenType.ID) {
                declaracao_metodo();
                declaracao_metodos();
            } else if (tokenAtual.getTipo() == TokenType.PRINCIPAL) {
                match(TokenType.PRINCIPAL);
                match(TokenType.ABREPAR);
                match(TokenType.VAZIO);
                match(TokenType.FECHAPAR);
                match(TokenType.ABRECHAVE);
                dec_var_metodo();
                comandos();
                match(TokenType.FECHACHAVE);
            }
        }
        catch(ErroSintaticoException ex) {
            erroSintatico(ex);
            panico(conjuntoSequencia.getConjunto(DECLARACAO_METODO));
        }
    }

    private void parametros_formais()
    {
        if ( tokenAtual.getTipo() == TokenType.INTEIRO ||
             tokenAtual.getTipo() == TokenType.REAL ||
             tokenAtual.getTipo() == TokenType.LOGICO ||
             tokenAtual.getTipo() == TokenType.CARACTERE ||
             tokenAtual.getTipo() == TokenType.CADEIA ) {

            parametros_mesmo_tipo();
            parametros_formais();
        }
        else if (tokenAtual.getTipo() == TokenType.VAZIO) {
             match(TokenType.VAZIO);
        }
    }

    private void parametros_mesmo_tipo()
    {
        try {
            tipo_variavel();
            lista_parametros();
            match(TokenType.PONTOVIRGULA);
        }
        catch(ErroSintaticoException ex) {
            erroSintatico(ex);
            panico(conjuntoSequencia.getConjunto(PARAMETROS_MESMO_TIPO));
        }

    }

    private void lista_parametros()
    {
        match(TokenType.ID);
        loop_lista_parametros();
    }

    private void loop_lista_parametros()
    {
        if (tokenAtual.getTipo() == TokenType.VIRGULA) {
            match(TokenType.VIRGULA);
            lista_parametros();
        }
    }

 // ------------------- OBJETOS ------------------------------------------------

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
           parametros_reais_instanciar_obj();
           match(TokenType.FECHAPAR);
        }
    }

    private void parametros_reais_instanciar_obj()
    {
        parametro_real();
        loop_parametros_reais_instanciar_obj();
    }

    private void parametro_real()
    {
        if ( tokenAtual.getTipo() == TokenType.ID ||
             tokenAtual.getTipo() == TokenType.NUM ||
             tokenAtual.getTipo() == TokenType.LITERAL ||
             tokenAtual.getTipo() == TokenType.CARACTER ||
             tokenAtual.getTipo() == TokenType.VERDADEIRO ||
             tokenAtual.getTipo() == TokenType.FALSO  ) proxToken();

        else throw new ErroSintaticoException("esperava uma variável ou valor como parâmetro: ");
    }

    private void loop_parametros_reais_instanciar_obj()
    {
        if(tokenAtual.getTipo() == TokenType.VIRGULA) {
            match(TokenType.VIRGULA);
            parametros_reais_instanciar_obj();
        }
    }

 // ------------------- EXPRESSÕES ---------------------------------------------

//    private void expressao()
//    {
//        try {
//            if (tokenAtual.getTipo() == TokenType.ABREPAR) {
//                match(TokenType.ABREPAR);
//                expressao_parentese();
//            }
//            else if (tokenAtual.getTipo() == TokenType.ID || tokenAtual.getTipo() == TokenType.NUM) {
//                termo();
//                prox_trecho_soma();
//                prox_trecho_relacional();
//            }
//            else if (tokenAtual.getTipo() == TokenType.VERDADEIRO || tokenAtual.getTipo() == TokenType.FALSO) {
//                expressao_booleana();
//                prox_trecho_expl();
//            }
//            else {
//                throw new ErroSintaticoException("esperava uma expressão, variável ou valor: ");
//            }
//        }
//        catch(ErroSintaticoException ex) {
//            erroSintatico(ex);
//            panico(conjuntoSequencia.getConjunto(EXPRESSAO));
//        }
//
//    }
//
//    private void expressao_parentese()
//    {
//        try {
//            if (tokenAtual.getTipo() == TokenType.ABREPAR) {
//                match(TokenType.ABREPAR);
//                complemento_expressao_parentese();
//            }
//            else if (tokenAtual.getTipo() == TokenType.ID || tokenAtual.getTipo() == TokenType.NUM) {
//                termo();
//                prox_trecho_soma();
//                prox_trecho_geral();
//            }
//            else if (tokenAtual.getTipo() == TokenType.VERDADEIRO || tokenAtual.getTipo() == TokenType.FALSO) {
//                expressao_booleana();
//                prox_trecho_expl();
//                match(TokenType.FECHAPAR);
//            }
//            else {
//                throw new ErroSintaticoException("esperava uma expressão, variável ou valor: ");
//            }
//        }
//        catch(ErroSintaticoException ex) {
//            erroSintatico(ex);
//            panico(conjuntoSequencia.getConjunto(EXPRESSAO_PARENTESE));
//        }
//    }
//
//    private void termo()
//    {
//        fator();
//        prox_trecho_multiplicacao();
//    }
//
//    private void fator()
//    {
//         if(tokenAtual.getTipo() == TokenType.ID) {
//            match(TokenType.ID);
//            complemento_fator_variavel();
//        }
//        else if(tokenAtual.getTipo() == TokenType.NUM ) {
//            match(TokenType.NUM);
//        }
//        else throw new ErroSintaticoException("esperava um identificador ou número: ");
//    }
//
//    private void complemento_fator_variavel()
//    {
//        if(tokenAtual.getTipo() == TokenType.ABREPAR || tokenAtual.getTipo() == TokenType.PONTO || tokenAtual.getTipo() == TokenType.ATRIB) {
//            loop_acesso_atributo_obj();
//        }
//        else if(tokenAtual.getTipo() == TokenType.ABRECOLCH) {
//            match(TokenType.ABRECOLCH);
//            expressao_aritmetica();
//            match(TokenType.FECHACOLCH);
//        }
//    }
//
//    private void prox_trecho_multiplicacao()
//    {
//        if(tokenAtual.getTipo() == TokenType.MULT || tokenAtual.getTipo() == TokenType.DIV) {
//            operador_multiplicacao();
//            expressao_aritmetica();
//            prox_trecho_multiplicacao();
//        }
//    }
//
//    private void operador_multiplicacao()
//    {
//        if ( tokenAtual.getTipo() == TokenType.MULT ||
//             tokenAtual.getTipo() == TokenType.DIV  ) proxToken();
//
//        else throw new ErroSintaticoException("operador de multiplicação ou divisão esperado: ");
//    }
//
//    private void prox_trecho_soma()
//    {
//        try {
//            if (tokenAtual.getTipo() == TokenType.ADICAO || tokenAtual.getTipo() == TokenType.SUB) {
//                operador_soma();
//                expressao_aritmetica();
//                prox_trecho_multiplicacao();
//            }
//        }
//        catch(ErroSintaticoException ex) {
//            erroSintatico(ex);
//            panico(conjuntoSequencia.getConjunto(PROX_TRECHO_SOMA));
//        }
//    }
//
//    private void operador_soma()
//    {
//        if ( tokenAtual.getTipo() == TokenType.ADICAO ||
//             tokenAtual.getTipo() == TokenType.SUB  ) proxToken();
//
//        else throw new ErroSintaticoException("operador de soma ou subtração esperado: ");
//    }
//
//    private void prox_trecho_relacional()
//    {
//        if ( tokenAtual.getTipo() == TokenType.MAIOR ||
//             tokenAtual.getTipo() == TokenType.MENOR ||
//             tokenAtual.getTipo() == TokenType.MAIORIGUAL   ||
//             tokenAtual.getTipo() == TokenType.MENORIGUAL ||
//             tokenAtual.getTipo() == TokenType.IGUAL ||
//             tokenAtual.getTipo() == TokenType.DIF  ) {
//
//                operador_relacional();
//                expressao_aritmetica();
//                prox_trecho_expl();
//        }
//    }
//
//    private void operador_relacional()
//    {
//        if ( tokenAtual.getTipo() == TokenType.MAIOR ||
//             tokenAtual.getTipo() == TokenType.MENOR ||
//             tokenAtual.getTipo() == TokenType.MAIORIGUAL   ||
//             tokenAtual.getTipo() == TokenType.MENORIGUAL ||
//             tokenAtual.getTipo() == TokenType.IGUAL ||
//             tokenAtual.getTipo() == TokenType.DIF  ) proxToken();
//
//        else throw new ErroSintaticoException("operador relacional esperado: ");
//    }
//
//    private void prox_trecho_expl()
//    {
//         if ( tokenAtual.getTipo() == TokenType.OU || tokenAtual.getTipo() == TokenType.E  ) {
//             operador_logico();
//             expressao_logica();
//         }
//    }
//
//    private void operador_logico()
//    {
//        if ( tokenAtual.getTipo() == TokenType.OU ||
//             tokenAtual.getTipo() == TokenType.E  ) proxToken();
//    }
//
//    private void expressao_logica()
//    {
//        termo_l();
//        prox_trecho_expl();
//    }
//
//    private void termo_l()
//    {
//        if ( tokenAtual.getTipo() == TokenType.VERDADEIRO || tokenAtual.getTipo() == TokenType.FALSO  ) {
//             expressao_booleana();
//         }
//        else if ( tokenAtual.getTipo() == TokenType.ABREPAR ) {
//             match(TokenType.ABREPAR);
//             termo_l_parentesis();
//         }
//        else if ( tokenAtual.getTipo() == TokenType.ID || tokenAtual.getTipo() == TokenType.NUM  ) {
//             termo();
//             prox_trecho_soma();
//             operador_relacional();
//             expressao_aritmetica();
//         }
//        else throw new ErroSintaticoException("esperava variavel, valor ou termo lógico: ");
//    }
//
//    private void termo_l_parentesis()
//    {
//        expressao_aritmetica();
//        complemento_termo_l_parentesis();
//    }
//
//    private void complemento_termo_l_parentesis()
//    {
//        if ( tokenAtual.getTipo() == TokenType.ABREPAR ) {
//            match(TokenType.ABREPAR);
//            operador_relacional();
//            expressao_aritmetica();
//        }
//        else if (tokenAtual.getTipo() == TokenType.E || tokenAtual.getTipo() == TokenType.OU) {
//            prox_trecho_expl();
//            match(TokenType.FECHAPAR);
//        }
//        else if ( tokenAtual.getTipo() == TokenType.MAIOR ||
//             tokenAtual.getTipo() == TokenType.MENOR ||
//             tokenAtual.getTipo() == TokenType.MAIORIGUAL   ||
//             tokenAtual.getTipo() == TokenType.MENORIGUAL ||
//             tokenAtual.getTipo() == TokenType.IGUAL ||
//             tokenAtual.getTipo() == TokenType.DIF  ) {
//
//            prox_trecho_relacional();
//            match(TokenType.FECHAPAR);
//        }
//    }
//
//
//
//    private void complemento_expressao_parentese()
//    {
//        termo_l_parentesis();
//        prox_trecho_expl();
//        match(TokenType.FECHAPAR);
//        prox_trecho_expl();
//    }
//
//    private void prox_trecho_geral()
//    {
//        if ( tokenAtual.getTipo() == TokenType.MAIOR ||
//             tokenAtual.getTipo() == TokenType.MENOR ||
//             tokenAtual.getTipo() == TokenType.MAIORIGUAL   ||
//             tokenAtual.getTipo() == TokenType.MENORIGUAL ||
//             tokenAtual.getTipo() == TokenType.IGUAL ||
//             tokenAtual.getTipo() == TokenType.DIF  ) {
//            operador_relacional();
//            expressao_aritmetica();
//            prox_trecho_expl();
//            match(TokenType.FECHAPAR);
//            prox_trecho_expl();
//        }
//        else if( tokenAtual.getTipo() == TokenType.FECHAPAR ) {
//            match(TokenType.FECHAPAR);
//            complemento_exp_aritm_parentese();
//        }
//        else throw new ErroSintaticoException("operador relacional ou fim de expressão esperado: ");
//    }
//
//    private void complemento_exp_aritm_parentese_atrib()
//    {
//        if ( tokenAtual.getTipo() == TokenType.ADICAO ||
//             tokenAtual.getTipo() == TokenType.SUB ||
//             tokenAtual.getTipo() == TokenType.MULT   ||
//             tokenAtual.getTipo() == TokenType.DIV  ) {
//
//            operador_aritmetico();
//            expressao_aritmetica();
//        }
//    }
//
//    private void operador_aritmetico()
//    {
//        if ( tokenAtual.getTipo() == TokenType.ADICAO || tokenAtual.getTipo() == TokenType.SUB ) {
//            operador_soma();
//        }
//        else if ( tokenAtual.getTipo() == TokenType.MULT || tokenAtual.getTipo() == TokenType.DIV  ) {
//             operador_multiplicacao();
//        }
//        else throw new ErroSintaticoException("esperava um operador aritmético: ");
//    }
//
//    private void complemento_exp_aritm_parentese()
//    {
//        if ( tokenAtual.getTipo() == TokenType.MAIOR ||
//             tokenAtual.getTipo() == TokenType.MENOR ||
//             tokenAtual.getTipo() == TokenType.MAIORIGUAL   ||
//             tokenAtual.getTipo() == TokenType.MENORIGUAL ||
//             tokenAtual.getTipo() == TokenType.IGUAL ||
//             tokenAtual.getTipo() == TokenType.DIF  ) {
//            operador_relacional();
//            expressao_aritmetica();
//            prox_trecho_expl();
//        }
//        else if ( tokenAtual.getTipo() == TokenType.ADICAO ||
//             tokenAtual.getTipo() == TokenType.SUB ||
//             tokenAtual.getTipo() == TokenType.MULT   ||
//             tokenAtual.getTipo() == TokenType.DIV ) {
//
//            operador_aritmetico();
//            expressao_aritmetica();
//            complemento_exp_aritm_parentese();
//        }
//
//    }
//
//    private void expressao_aritmetica()
//    {
//        if ( tokenAtual.getTipo() == TokenType.ID || tokenAtual.getTipo() == TokenType.NUM  ) {
//             termo();
//             prox_trecho_soma();
//         }
//        else if( tokenAtual.getTipo() == TokenType.ABREPAR) {
//            match( TokenType.ABREPAR );
//            expressao_aritmetica();
//            match( TokenType.FECHAPAR );
//        }
//        else throw new ErroSintaticoException("esperava o início de uma expressão aritmética: ");
//    }
//
//    private void expressao_relacional()
//    {
//        expressao_aritmetica();
//        operador_relacional();
//        expressao_aritmetica();
//    }
//
//    private void expressao_booleana()
//    {
//        if ( tokenAtual.getTipo() == TokenType.VERDADEIRO || tokenAtual.getTipo() == TokenType.FALSO  ) {
//             proxToken();
//         }
//    }

// ------------------- EXPRESSÕES NOVO ---------------------------------------------

    private void expressao()
    {
        if (primeiro(EXPRESSAO_ARITMETICA).contains(tokenAtual.getTipo())) {
            expressao_aritmetica();
            complemento_exp_aritmetica();
        }
        else if (tokenAtual.getTipo() == TokenType.VERDADEIRO || tokenAtual.getTipo() == TokenType.FALSO) {
            expressao_booleana();
            prox_trecho_expl();
        }
        //if(tokenAtual.getTipo()!=TokenType.EOF) erroSintatico("token inesperado: " + tokenAtual.getTipo(), tokenAtual.getLinha());
    }

    private void complemento_exp_aritmetica()
    {
        if (tokenAtual.getTipo() == TokenType.MAIOR ||
            tokenAtual.getTipo() == TokenType.MENOR ||
            tokenAtual.getTipo() == TokenType.MAIORIGUAL ||
            tokenAtual.getTipo() == TokenType.MENORIGUAL ||
            tokenAtual.getTipo() == TokenType.IGUAL ||
            tokenAtual.getTipo() == TokenType.DIF) {
            operador_relacional();
            expressao_aritmetica();
            prox_trecho_expl();
        }
    }

    private void prox_trecho_expl()
    {
        if ( tokenAtual.getTipo() == TokenType.OU || tokenAtual.getTipo() == TokenType.E  ) {
             operador_logico();
             termo_l();
        }
    }

    private void operador_logico()
    {
        if ( tokenAtual.getTipo() == TokenType.OU ||
             tokenAtual.getTipo() == TokenType.E  ) proxToken();
    }

    private void termo_l()
    {
         if (primeiro(EXPRESSAO_ARITMETICA).contains(tokenAtual.getTipo())) {
            expressao_relacional();
            prox_trecho_expl();
        }
        else if (tokenAtual.getTipo() == TokenType.VERDADEIRO || tokenAtual.getTipo() == TokenType.FALSO) {
            expressao_booleana();
            prox_trecho_expl();
        }
        else throw new ErroSintaticoException("esperava uma expressão relacional ou um valor lógico: ");
    }

    private void expressao_relacional()
    {
        expressao_aritmetica();
        operador_relacional();
        expressao_aritmetica();
    }

    private void operador_relacional()
    {
        if ( tokenAtual.getTipo() == TokenType.MAIOR ||
             tokenAtual.getTipo() == TokenType.MENOR ||
             tokenAtual.getTipo() == TokenType.MAIORIGUAL   ||
             tokenAtual.getTipo() == TokenType.MENORIGUAL ||
             tokenAtual.getTipo() == TokenType.IGUAL ||
             tokenAtual.getTipo() == TokenType.DIF  ) proxToken();

        else throw new ErroSintaticoException("operador relacional esperado: ");
    }

    private void expressao_aritmetica()
    {
        termo_aritm();
        prox_trecho_exp_aritm();
    }

    private void termo_aritm()
    {
        if ( tokenAtual.getTipo() == TokenType.ID || tokenAtual.getTipo() == TokenType.NUM || tokenAtual.getTipo() == TokenType.ABREPAR ) {
            fator();
            complemento_fator();
        }
        else throw new ErroSintaticoException("esperava inicio de uma expressao aritmética - numero ou identificador: ");
    }

    private void fator()
    {
        if ( tokenAtual.getTipo() == TokenType.ID ) {
            match( TokenType.ID );
            complemento_referencia_variavel();
        }
        else if ( tokenAtual.getTipo() == TokenType.NUM ) {
            match( TokenType.NUM );
        }
        else if ( tokenAtual.getTipo() == TokenType.ABREPAR ) {
            match( TokenType.ABREPAR );
            expressao_aritmetica();
            match( TokenType.FECHAPAR );
        }
        else throw new ErroSintaticoException("esperava identificador ou numero: ");
    }

    private void complemento_fator()
    {
        if ( tokenAtual.getTipo() == TokenType.MULT || tokenAtual.getTipo() == TokenType.DIV ) {
            operador_multiplicacao();
            termo_aritm();
        }
    }

    private void operador_multiplicacao()
    {
        if ( tokenAtual.getTipo() == TokenType.MULT ||
             tokenAtual.getTipo() == TokenType.DIV  ) proxToken();

        else throw new ErroSintaticoException("operador de multiplicação ou divisão esperado: ");
    }

    private void prox_trecho_exp_aritm()
    {
        if ( tokenAtual.getTipo() == TokenType.ADICAO || tokenAtual.getTipo() == TokenType.SUB ) {
            operador_soma();
            expressao_aritmetica();
        }
    }

    private void operador_soma()
    {
        if ( tokenAtual.getTipo() == TokenType.ADICAO ||
             tokenAtual.getTipo() == TokenType.SUB  ) proxToken();

        else throw new ErroSintaticoException("operador de soma ou subtração esperado: ");
    }


    private void expressao_booleana()
    {
        if ( tokenAtual.getTipo() == TokenType.VERDADEIRO || tokenAtual.getTipo() == TokenType.FALSO  ) {
             proxToken();
         }
    }


// --------------CHAMADA DE MÉTODOS ---------------------------------------------

    private void chamada_metodo()
    {
        match( TokenType.ID );
        complemento_chamada_metodo();
    }

    private void complemento_chamada_metodo()
    {
        if (tokenAtual.getTipo() == TokenType.ABREPAR) {
            match(TokenType.ABREPAR);
            parametros_reais();
            match(TokenType.FECHAPAR);
        }
        else if(tokenAtual.getTipo() == TokenType.PONTO) {
            match(TokenType.PONTO);
            chamada_metodo();
        }
        //else throw new ErroSintaticoException("esperava parentese ou ponto para chamada de método: ");

    }

// -------------------- COMANDOS -----------------------------------------------

    private void comandos()
    {
        if(primeiro(COMANDO_GERAL).contains(tokenAtual.getTipo())) {
            comando_geral();
            comandos();
        }
    }

    private void comando_geral()
    {
        try {
            if (primeiro(COMANDO_LINHA).contains(tokenAtual.getTipo())) {
                comando_linha();
                match(TokenType.PONTOVIRGULA);
            } else if (primeiro(COMANDO_BLOCO).contains(tokenAtual.getTipo())) {
                comando_bloco();
            } else {
                throw new ErroSintaticoException("esperava o início de um comando de linha ou comando de bloco: ");
            }
        }
        catch(ErroSintaticoException ex) {
            erroSintatico(ex);
            panico(conjuntoSequencia.getConjunto(COMANDO_GERAL));
        }
    }

    private void comando_linha()
    {
        switch(tokenAtual.getTipo())
        {
            case ESCREVA: {
                comando_escreva();
                break;
            }
            case LEIA: {
                comando_leia();
                break;
            }
            case RETORNO: {
                retorno();
                break;
            }
            case INCR: {
                incremento_decremento();
                match(TokenType.ID);
                break;
            }
            case DECR: {
                incremento_decremento();
                match(TokenType.ID);
                break;
            }
            case ID: {
                match(TokenType.ID);
                complemento_variavel_comando();
                break;
            }
            default: erroSintatico(tokenAtual);;
        }
    }

    private void comando_bloco()
    {
        switch(tokenAtual.getTipo())
        {
             case SE: {
                comando_se();
                break;
            }
            case PARA: {
                comando_para();
                break;
            }
            case ENQUANTO: {
                comando_enquanto();
                break;
            }
         }
    }
    
    private void comando_se()
    {
        match(TokenType.SE);
        match(TokenType.ABREPAR);
        //expressao_logica();
        expressao();
        match(TokenType.FECHAPAR);
        match(TokenType.ENTAO);
        match(TokenType.ABRECHAVE);
        comandos();
        match(TokenType.FECHACHAVE);
        complemento_comando_se();
    }
    
    private void complemento_comando_se()
    {
        if(tokenAtual.getTipo() == TokenType.SENAO) {
            match(TokenType.SENAO);
            match(TokenType.ABRECHAVE);
            comandos();
            match(TokenType.FECHACHAVE);
        }
    }
    
    private void comando_para()
    {
        match(TokenType.PARA);
        match(TokenType.ABREPAR);
        atribuicao();
        match(TokenType.PONTOVIRGULA);
        expressao_relacional();
        match(TokenType.PONTOVIRGULA);
        atribuicao();
        match(TokenType.FECHAPAR);
        match(TokenType.ABRECHAVE);
        comandos();
        match(TokenType.FECHACHAVE);
    }
    
    private void comando_enquanto()
    {
        match(TokenType.ENQUANTO);
        match(TokenType.ABREPAR);
        //expressao_logica();
        expressao();
        match(TokenType.FECHAPAR);
        match(TokenType.ABRECHAVE);
        comandos();
        match(TokenType.FECHACHAVE);
    }

    private void comando_escreva()
    {
        match(TokenType.ESCREVA);
        match(TokenType.ABREPAR);
        params_escreva();
        match(TokenType.FECHAPAR);
    }

    private void params_escreva()
    {
        param_escreva();
        loop_params_escreva();
    }

    private void param_escreva()
    {
        if ( tokenAtual.getTipo() == TokenType.ABREPAR ||
             tokenAtual.getTipo() == TokenType.VERDADEIRO ||
             tokenAtual.getTipo() == TokenType.FALSO   ||
             tokenAtual.getTipo() == TokenType.ID ||
             tokenAtual.getTipo() == TokenType.NUM  ) {

            expressao();
        }
        else if ( tokenAtual.getTipo() == TokenType.LITERAL ) {
            match(TokenType.LITERAL);
        }
        else throw new ErroSintaticoException("esperava um valor ou expressão como argumento: ");
    }

    private void loop_params_escreva()
    {
        if ( tokenAtual.getTipo() == TokenType.VIRGULA ) {
            match(TokenType.VIRGULA);
            params_escreva();
        }
    }

    private void comando_leia()
    {
        match(TokenType.LEIA);
        match(TokenType.ABREPAR);
        params_leia();
        match(TokenType.FECHAPAR);
    }

    private void params_leia()
    {
        match(TokenType.ID);
        loop_params_leia();
    }

    private void loop_params_leia()
    {
        if ( tokenAtual.getTipo() == TokenType.VIRGULA ) {
            match(TokenType.VIRGULA);
            params_leia();
        }
    }

    private void retorno()
    {
        match(TokenType.RETORNO);
        match(TokenType.ABREPAR);
        expressao();
        match(TokenType.FECHAPAR);
    }

    private void complemento_variavel_comando()
    {
        switch(tokenAtual.getTipo())
        {
            case PONTO: {
                complemento_chamada_metodo();
                break;
            }
            case ABRECOLCH: {
                match(TokenType.ABRECOLCH);
                expressao_aritmetica();
                match(TokenType.FECHACOLCH);
                match(TokenType.ATRIB);
                segundo_membro_atribuicao();
                break;
            }
            case ATRIB: {
                match(TokenType.ATRIB);
                segundo_membro_atribuicao();
                break;
            }
            case ABREPAR: {
                match(TokenType.ABREPAR);
                parametros_reais();
                match(TokenType.FECHAPAR);
                break;
            }
            case INCR: {
                incremento_decremento();
                break;
            }
            case DECR: {
                incremento_decremento();
                break;
            }
            default: throw new ErroSintaticoException("esperava uma atribuição ou chamada de método: ");
        }
    }

    private void complemento_ponto_comando()
    {
        match(TokenType.ID);
        loop_acesso_atributo_obj();
    }

    private void parametros_reais()
    {
        if (primeiro(PARAMETRO_REAL).contains(tokenAtual.getTipo())) {
            parametro_real();
            loop_parametros_reais();
        }
    }

    private void loop_parametros_reais()
    {
        if ( tokenAtual.getTipo() == TokenType.VIRGULA ) {
            match(TokenType.VIRGULA);
            parametros_reais();
        }
    }

    private void loop_acesso_atributo_obj()
    {
        if ( tokenAtual.getTipo() == TokenType.PONTO  ) {
            match(TokenType.PONTO);
            complemento_ponto_comando();
        }

    }

// ------------------- ATRIBUIÇÃO ----------------------------------------------

    private void atribuicao()
    {
        if ( tokenAtual.getTipo() == TokenType.ID  ) {
            match( TokenType.ID );
            complemento_id_atribuicao();
         }
        else if( tokenAtual.getTipo() == TokenType.INCR || tokenAtual.getTipo() == TokenType.DECR ) {
            incremento_decremento();
            match( TokenType.ID );
        }
        else throw new ErroSintaticoException("Esperando início de atribuição ou operador de incremento/decremento: ");
    }

    private void complemento_id_atribuicao()
    {
        if(tokenAtual.getTipo() == TokenType.PONTO ||
           tokenAtual.getTipo() == TokenType.ATRIB ||
           tokenAtual.getTipo() == TokenType.ABRECOLCH)  {

            complemento_referencia_variavel_atrib();
        }
        else if(tokenAtual.getTipo() == TokenType.INCR ||
                tokenAtual.getTipo() == TokenType.DECR) {

            incremento_decremento();
        }
    }

    private void complemento_referencia_variavel_atrib()
    {
        if( tokenAtual.getTipo() == TokenType.PONTO ) {
            loop_acesso_atributo_obj();

            complemento_referencia_variavel();

            match(TokenType.ATRIB);
            segundo_membro_atribuicao();
        }
        else if(tokenAtual.getTipo() == TokenType.ABRECOLCH) {
            match(TokenType.ABRECOLCH);
            expressao_aritmetica();
            match(TokenType.FECHACOLCH);
            match(TokenType.ATRIB);
            segundo_membro_atribuicao();
        }
        else if(tokenAtual.getTipo() == TokenType.ATRIB) {
            match(TokenType.ATRIB);
            segundo_membro_atribuicao();
        }
    }

    private void complemento_referencia_variavel()
    {
        if( tokenAtual.getTipo() == TokenType.PONTO ) {
            loop_acesso_atributo_obj();
            //complemento_chamada_metodo();
        }
        else if( tokenAtual.getTipo() == TokenType.ABRECOLCH ) {
            match(TokenType.ABRECOLCH);
            expressao_aritmetica();
            match(TokenType.FECHACOLCH);
        }
    }

     private void segundo_membro_atribuicao()
     {
          if(tokenAtual.getTipo() == TokenType.ABREPAR ||
           tokenAtual.getTipo() == TokenType.ID ||
           tokenAtual.getTipo() == TokenType.NUM ||
           tokenAtual.getTipo() == TokenType.VERDADEIRO ||
           tokenAtual.getTipo() == TokenType.FALSO)  {
              expressao();
          }
          else if( tokenAtual.getTipo() == TokenType.CARACTER || tokenAtual.getTipo() == TokenType.LITERAL ) {
              proxToken();
          }
          else if(tokenAtual.getTipo() == TokenType.INCR || tokenAtual.getTipo() == TokenType.DECR) {
              incremento_decremento();
              match(TokenType.ID);
          }
          else throw new ErroSintaticoException("esperando uma expressão, variável ou valor para realizar a atribuição: ");
     }

    private void incremento_decremento()
    {
        if(tokenAtual.getTipo() == TokenType.INCR || tokenAtual.getTipo() == TokenType.DECR) proxToken();
        else throw new ErroSintaticoException("esperando operador de incremento ou decremento: ");
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
        else throw new ErroSintaticoException(esperado);
    }

    private void erroSintatico(ErroSintaticoException ex)
    {
        if(ex.tokenEsperado != null) erroSintatico(ex.tokenEsperado, tokenAtual, ex.mensagemContexto);
        else erroSintatico(ex.mensagem, tokenAtual.getLinha());
    }

    private void panico(Set<TokenType> conjuntoSincronizacao)
    {
        while(!conjuntoSincronizacao.contains(tokenAtual.getTipo())) proxToken();
    }

    //exibe token esperado na mensagem de erro
    private void erroSintatico(TokenType esperado, Token obtido, String mensagemContexto)
    {
        String msg = "Token inesperado: " + obtido.getTipo() + " ";

        if(obtido.getTipo() == TokenType.ID || obtido.getTipo() == TokenType.NUM) {
            msg += "'" + obtido.getLexema() + "'";
        }

        msg += ", esperava: " + esperado;
        msg = mensagemContexto + msg;
        erros.add(new ErroSintatico(msg, obtido.getLinha()));
    }

    //não era esperado um unico token, portanto só exibe o token inesperado obtido
    private void erroSintatico(Token obtido)
    {
        String msg = "Token inesperado: " + obtido.getTipo() + " ";
        
        if(obtido.getTipo() == TokenType.ID || obtido.getTipo() == TokenType.NUM) {
            msg += "'" + obtido.getLexema() + "'";
        }
        erros.add(new ErroSintatico(msg, obtido.getLinha()));
    }

    //exibe uma mensagem livre de erro
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
    }

    public boolean temErros()
    {
        if( erros==null ) return false;
        return erros.size() > 0;
    }

    public int getErros()
    {
        return erros.size();
    }

}

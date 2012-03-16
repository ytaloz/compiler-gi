/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.semantico;

import static compilador.sintatico.AnalisadorSintatico.*;
import compilador.exception.ErroSintaticoException;
import compilador.exception.FinalArquivoException;
import compilador.gui.Janela;
import compilador.sintatico.ConjuntoPrimeiro;
import compilador.sintatico.ConjuntoSequencia;
import compilador.tabeladesimbolos.ArvoreDeEscopo;
import compilador.tabeladesimbolos.TabelaDeSimbolos;
import compilador.tabeladesimbolos.simbolos.Classe;
import compilador.tabeladesimbolos.simbolos.Constante;
import compilador.tabeladesimbolos.simbolos.Metodo;
import compilador.tabeladesimbolos.simbolos.Simbolo;
import compilador.tabeladesimbolos.simbolos.Variavel;
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
public class AnalisadorSemantico {

    private Janela janela;

    TabelaDeSimbolos tabelaDeSimbolos;

    //lista de tokens representando o codigo fonte
    private List<Token> tokens;

    //token atual da análise
    private Token tokenAtual;

    //ponteiro do token atual sendo analisado
    private int ponteiro = -1;

    //lista de erros sintaticos
    private List<ErroSemantico> erros = new ArrayList<ErroSemantico>();

    //classe que armazena os conjuntos primeiros das produções da gramática
    private ConjuntoPrimeiro conjuntoPrimeiro = new ConjuntoPrimeiro();

    //classe que armazena os conjuntos sequencia das produções da gramática
    private ConjuntoSequencia conjuntoSequencia = new ConjuntoSequencia();

    

    public AnalisadorSemantico(Janela janela, TabelaDeSimbolos tabela)
    {
        this.janela = janela;
        this.tabelaDeSimbolos = tabela;
    }

    public void analisar(List<Token> tokens)
    {
        this.tokens = removerErrosEComentarios((ArrayList)tokens);
        inicializarVariaveis();

        proxToken();

        try {
           programa();
        }
        catch (FinalArquivoException e) {
            //não faz nada
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
        tabelaDeSimbolos = new TabelaDeSimbolos();
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
            else if(tokenAtual.getTipo()!= TokenType.EOF) throw new ErroSintaticoException();
        }
        catch( ErroSintaticoException ex ) {
                
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
        else if(tokenAtual.getTipo()!=TokenType.EOF) throw new ErroSintaticoException();
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
            panico(conjuntoSequencia.getConjunto(BLOCO_CONSTANTES));
        }
    }

    private void declaracao_constantes()
    {
        if (primeiro(DECL_CONSTANTES_MESMO_TIPO).contains(tokenAtual.getTipo())) {
            decl_constantes_mesmo_tipo();
            declaracao_constantes();
        }
        else if (tokenAtual.getTipo() != TokenType.FECHACHAVE && tokenAtual.getTipo() != TokenType.EOF) throw new ErroSintaticoException();
    }

    private void decl_constantes_mesmo_tipo()
    {
        try {
            String tipo = tipo_variavel();
            lista_decl_constantes(tipo);
            match(TokenType.PONTOVIRGULA);
        }
        catch (ErroSintaticoException ex) {
            panico(conjuntoSequencia.getConjunto(DECL_CONSTANTES_MESMO_TIPO));
        }
    }

    private String tipo_variavel()
    {
        if ( tokenAtual.getTipo() == TokenType.INTEIRO ||
             tokenAtual.getTipo() == TokenType.REAL ||
             tokenAtual.getTipo() == TokenType.LOGICO ||
             tokenAtual.getTipo() == TokenType.CARACTERE ||
             tokenAtual.getTipo() == TokenType.CADEIA  ) proxToken();

        else throw new ErroSintaticoException();

        return tokens.get(ponteiro-1).getLexema();
    }

    private void lista_decl_constantes(String tipo)
    {
        atribuicao_constante(tipo);
        loop_lista_decl_constantes(tipo);
    }

    private void loop_lista_decl_constantes(String tipo)
    {
        if (tokenAtual.getTipo() == TokenType.VIRGULA) {
            match(TokenType.VIRGULA);
            lista_decl_constantes(tipo);
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
                String tipo = tipo_variavel();
                lista_decl_variaveis(tipo);
                match(TokenType.PONTOVIRGULA);
            }
            else if(tokenAtual.getTipo() == TokenType.ID) {
                match(TokenType.ID);

                checarSeClasseFoiDefinida(tokens.get(ponteiro-1).getLexema()); //metodo semantico

                match(TokenType.ID);

                Variavel obj = new Variavel(tokens.get(ponteiro-1).getLexema(),tokens.get(ponteiro-2).getLexema()); //método semantico
                addVariavel(obj); //método semantico

                complemento_variavel_instanciar_obj();
                match(TokenType.PONTOVIRGULA);
            }
            else throw new ErroSintaticoException();
        }
        catch (ErroSintaticoException ex) {
            panico(conjuntoSequencia.getConjunto(DECL_VARIAVEIS_MESMO_TIPO));
        }
    }

    private void lista_decl_variaveis(String tipo)
    {
        if (tokenAtual.getTipo() == TokenType.ID) {
            match(TokenType.ID);
            complemento_decl_variavel(tipo);
        }
        else throw new ErroSintaticoException();
    }

    private void complemento_decl_variavel(String tipo)
    {
        switch( tokenAtual.getTipo() )
        {
            case VIRGULA: {
                Variavel variavel = new Variavel(tokens.get(ponteiro-1).getLexema(), tipo);
                addVariavel(variavel);//método semântico
                prox_trecho_lista_decl_variaveis(tipo);
                break;
            }
            case PONTOVIRGULA: {
                Variavel variavel = new Variavel(tokens.get(ponteiro-1).getLexema(), tipo);
                addVariavel(variavel);//método semântico
                prox_trecho_lista_decl_variaveis(tipo);
                break;
            }
            case ATRIB: {
                Variavel variavel = new Variavel(tokens.get(ponteiro-1).getLexema(), tipo);
                addVariavel(variavel);//método semântico
                match(TokenType.ATRIB);
                segundo_membro_atribuicao();
                prox_trecho_lista_decl_variaveis(tipo);
                break;
            }
            case ABRECOLCH: {
                match(TokenType.ABRECOLCH);
                expressao_aritmetica();
                match(TokenType.FECHACOLCH);
                prox_trecho_lista_decl_variaveis(tipo);
                break;
            }
            default: {
                Variavel variavel = new Variavel(tokens.get(ponteiro-1).getLexema(), tipo);
                addVariavel(variavel);//método semântico
            }
        }
    }

    private void prox_trecho_lista_decl_variaveis(String tipo)
    {
        if( tokenAtual.getTipo() == TokenType.VIRGULA ) {
            match(TokenType.VIRGULA);
            lista_decl_variaveis(tipo);
        }
    }


// --------- DECLARAÇÃO DE CLASSES ---------------------------------------------

    private void classes()
    {
        if(tokenAtual.getTipo() == TokenType.CLASSE) {
           classe();
           classes();
        }
        else if(tokenAtual.getTipo()!=TokenType.EOF) throw new ErroSintaticoException();
    }

    private void classe()
    {
        try{
            match(TokenType.CLASSE);
            match(TokenType.ID);
            complemento_decl_classe();
        }
        catch (ErroSintaticoException ex) {
            panico(conjuntoSequencia.getConjunto(CLASSE));
        }
    }

    private void complemento_decl_classe()
    {
         if(tokenAtual.getTipo() == TokenType.ABRECHAVE)
         {
           Classe classe = new Classe(tokens.get(ponteiro-1).getLexema());
           addClasse(classe); //metodo semantico
           tabelaDeSimbolos.empilharNovoEscopo(classe); //metodo semântico classe

           match(TokenType.ABRECHAVE);
           blocos_classe();
           match(TokenType.FECHACHAVE);

           tabelaDeSimbolos.desempilharEscopo();
        }
         else if(tokenAtual.getTipo() == TokenType.HERDA_DE)
         {
             Classe classe = new Classe(tokens.get(ponteiro-1).getLexema());
             addClasse(classe); //metodo semantico
             tabelaDeSimbolos.empilharNovoEscopo(classe); //metodo semântico classe

             match(TokenType.HERDA_DE);
             match(TokenType.ID); 

             Classe pai = checarSeClasseFoiDefinida(tokens.get(ponteiro-1).getLexema()); //método semantico
             if(pai!=null) classe.setEscopoPai(pai); //método semantico

             match(TokenType.ABRECHAVE);
             blocos_classe();
             match(TokenType.FECHACHAVE);
             tabelaDeSimbolos.desempilharEscopo();
         }
         else throw new ErroSintaticoException();
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

// -------------- DECLARAÇÃO DE MÉTODOS ----------------------------------------

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

           String tipo = tipo_metodo_menos_vazio();
           declaracao_metodo(tipo);
           declaracao_metodos();
        }
        else if( tokenAtual.getTipo() == TokenType.VAZIO ) {
            match(TokenType.VAZIO);
            declaracao_metodo_vazio();
        }
    }

    private void declaracao_metodo(String tipo)
    {
        try {
            match(TokenType.ID);
            
            Metodo metodo = new Metodo(tokens.get(ponteiro-1).getLexema(),tipo);
            addMetodo(metodo); //método semantico
            tabelaDeSimbolos.empilharNovoEscopo(metodo); //método semantico
            
            match(TokenType.ABREPAR);
            parametros_formais(metodo);
            match(TokenType.FECHAPAR);
            match(TokenType.ABRECHAVE);
            dec_var_metodo();
            comandos();
            match(TokenType.FECHACHAVE);

            tabelaDeSimbolos.desempilharEscopo(); //método semantico
        }
         catch(ErroSintaticoException ex) {
            panico(conjuntoSequencia.getConjunto(DECLARACAO_METODO));
        }
    }

    private void dec_var_metodo()
    {
        if(tokenAtual.getTipo() == TokenType.VARIAVEIS) {
            bloco_variaveis();
        }
    }


    private String tipo_metodo_menos_vazio()
    {
        if ( tokenAtual.getTipo() == TokenType.INTEIRO ||
             tokenAtual.getTipo() == TokenType.REAL ||
             tokenAtual.getTipo() == TokenType.LOGICO ||
             tokenAtual.getTipo() == TokenType.CARACTERE ||
             tokenAtual.getTipo() == TokenType.CADEIA ) proxToken();
        else throw new ErroSintaticoException();

        return tokens.get(ponteiro-1).getLexema();
    }


    private void declaracao_metodo_vazio()
    {
        try {
            if (tokenAtual.getTipo() == TokenType.ID) {
                declaracao_metodo("vazio");
                declaracao_metodos();
            } else if (tokenAtual.getTipo() == TokenType.PRINCIPAL) {
                
                Metodo principal = new Metodo("principal","vazio");
                addMetodoPrincipal(principal); //método semantico
                tabelaDeSimbolos.empilharNovoEscopo(principal); //método semantico

                match(TokenType.PRINCIPAL);
                match(TokenType.ABREPAR);
                match(TokenType.VAZIO);
                match(TokenType.FECHAPAR);
                match(TokenType.ABRECHAVE);
                dec_var_metodo();
                comandos();
                match(TokenType.FECHACHAVE);

                tabelaDeSimbolos.desempilharEscopo();
            }
        }
        catch(ErroSintaticoException ex) {
            panico(conjuntoSequencia.getConjunto(DECLARACAO_METODO));
        }
    }

    private void parametros_formais(Metodo metodo)
    {
        if ( tokenAtual.getTipo() == TokenType.INTEIRO ||
             tokenAtual.getTipo() == TokenType.REAL ||
             tokenAtual.getTipo() == TokenType.LOGICO ||
             tokenAtual.getTipo() == TokenType.CARACTERE ||
             tokenAtual.getTipo() == TokenType.CADEIA ) {

            parametros_mesmo_tipo(metodo);
            complemento_parametros_mesmo_tipo(metodo);
        }
        else if (tokenAtual.getTipo() == TokenType.VAZIO) {
             match(TokenType.VAZIO);
        }
        else throw new ErroSintaticoException();
    }

    private void complemento_parametros_mesmo_tipo(Metodo metodo)
    {
        if ( tokenAtual.getTipo() == TokenType.PONTOVIRGULA ) {
            match(TokenType.PONTOVIRGULA);
            loop_parametros_mesmo_tipo(metodo);
        }
    }

    private void loop_parametros_mesmo_tipo(Metodo metodo)
    {
        if ( tokenAtual.getTipo() == TokenType.INTEIRO ||
             tokenAtual.getTipo() == TokenType.REAL ||
             tokenAtual.getTipo() == TokenType.LOGICO ||
             tokenAtual.getTipo() == TokenType.CARACTERE ||
             tokenAtual.getTipo() == TokenType.VAZIO ||
             tokenAtual.getTipo() == TokenType.CADEIA ) {

            parametros_formais(metodo);
        }
        else throw new ErroSintaticoException();
    }

    private void parametros_mesmo_tipo(Metodo metodo)
    {
        try {
            String tipo = tipo_variavel();
            lista_parametros(metodo, tipo);
        }
        catch(ErroSintaticoException ex) {
            panico(conjuntoSequencia.getConjunto(PARAMETROS_MESMO_TIPO));
        }

    }

    private void lista_parametros(Metodo metodo, String tipo)
    {
        match(TokenType.ID);

        Variavel param = new Variavel(tokens.get(ponteiro-1).getLexema(),tipo); //método semantico
        addParametro(param); //método semantico

        loop_lista_parametros(metodo, tipo);
    }

    private void loop_lista_parametros(Metodo metodo, String tipo)
    {
        if (tokenAtual.getTipo() == TokenType.VIRGULA) {
            match(TokenType.VIRGULA);
            lista_parametros(metodo, tipo);
        }
    }

 // ------------------- DECLARAÇÃO DE OBJETOS ----------------------------------

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

    private void loop_parametros_reais_instanciar_obj()
    {
        if(tokenAtual.getTipo() == TokenType.VIRGULA) {
            match(TokenType.VIRGULA);
            parametros_reais_instanciar_obj();
        }
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
                throw new ErroSintaticoException();
            }
        }
        catch(ErroSintaticoException ex) {
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
                checarSeIdentificadorFoiDeclarado(tokenAtual.getLexema()); //método semântico
                match(TokenType.ID);
                complemento_variavel_comando();
                break;
            }
            default: ;
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

    private void complemento_variavel_comando()
    {
        switch(tokenAtual.getTipo())
        {
            case PONTO: {
                acesso_objeto_comando();
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
            default: throw new ErroSintaticoException();
        }
    }

    private void acesso_objeto_comando()
    {
        if ( tokenAtual.getTipo() == TokenType.PONTO ) {
            match(TokenType.PONTO);
            match(TokenType.ID);
            loop_acesso_objeto_comando();
        }
    }

    private void loop_acesso_objeto_comando()
    {
        if ( tokenAtual.getTipo() == TokenType.PONTO  ) {
            acesso_objeto_comando();
         }
        else if( tokenAtual.getTipo() == TokenType.ABREPAR ) {
            match( TokenType.ABREPAR );
            parametros_reais();
            match( TokenType.FECHAPAR );
        }
        else if( tokenAtual.getTipo() == TokenType.ATRIB ) {
            match( TokenType.ATRIB );
            segundo_membro_atribuicao();
        }
        else throw new ErroSintaticoException();
    }

    private void comando_se()
    {
        match(TokenType.SE);
        match(TokenType.ABREPAR);
        condicao_comandos();
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
        condicao_comandos();
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
        condicao_comandos();
        match(TokenType.FECHAPAR);
        match(TokenType.ABRECHAVE);
        comandos();
        match(TokenType.FECHACHAVE);
    }

    private void condicao_comandos()
    {
        if (primeiro(EXPRESSAO_ARITMETICA).contains(tokenAtual.getTipo())) {
            expressao_relacional();
            prox_trecho_expl();
        }
        else if(tokenAtual.getTipo() == TokenType.VERDADEIRO || tokenAtual.getTipo() == TokenType.FALSO) {
            expressao_booleana();
            op_relacional_igualdade();
            match(TokenType.ID);
        }
        else throw new ErroSintaticoException();
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
        else throw new ErroSintaticoException();
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
        param_retorno();
        match(TokenType.FECHAPAR);
    }

    private void param_retorno()
    {
        if( tokenAtual.getTipo() == TokenType.ID ||
            tokenAtual.getTipo() == TokenType.NUM ||
            tokenAtual.getTipo() == TokenType.VERDADEIRO ||
            tokenAtual.getTipo() == TokenType.FALSO ||
            tokenAtual.getTipo() == TokenType.ABREPAR ) {
            expressao();
        }
        else if (  tokenAtual.getTipo() == TokenType.LITERAL ||
                   tokenAtual.getTipo() == TokenType.CARACTER  ) proxToken();

        else throw new ErroSintaticoException();
    }

    private void incremento_decremento()
    {
        if(tokenAtual.getTipo() == TokenType.INCR || tokenAtual.getTipo() == TokenType.DECR) proxToken();
        else throw new ErroSintaticoException();
    }


// ------------------- EXPRESSÕES ----------------------------------------------

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
        else throw new ErroSintaticoException();
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

        else throw new ErroSintaticoException();
    }

    private void op_relacional_igualdade()
    {
        if ( tokenAtual.getTipo() == TokenType.IGUAL ||
             tokenAtual.getTipo() == TokenType.DIF  ) proxToken();

        else throw new ErroSintaticoException();
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
        else throw new ErroSintaticoException();
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
        else throw new ErroSintaticoException();
    }

    private void complemento_fator()
    {
        if ( tokenAtual.getTipo() == TokenType.MULT || tokenAtual.getTipo() == TokenType.DIV ) {
            operador_multiplicacao();
            termo_aritm();
        }
    }

    private void complemento_referencia_variavel()
    {
        if( tokenAtual.getTipo() == TokenType.PONTO || tokenAtual.getTipo() == TokenType.ABREPAR ) {
            acesso_objeto();
        }
        else if( tokenAtual.getTipo() == TokenType.ABRECOLCH ) {
            match(TokenType.ABRECOLCH);
            expressao_aritmetica();
            match(TokenType.FECHACOLCH);
        }
        else checarSeIdentificadorFoiDeclarado(tokens.get(ponteiro-1).getLexema()); //método semântico
    }

    private void acesso_objeto()
    {
        if( tokenAtual.getTipo() == TokenType.PONTO ) {
            match(TokenType.PONTO);
            match(TokenType.ID);
            loop_acesso_objeto();
        }
        else if( tokenAtual.getTipo() == TokenType.ABREPAR ) {
            checarSeIdentificadorFoiDeclarado(tokens.get(ponteiro-1).getLexema()); //método semântico
            match(TokenType.ABREPAR);
            parametros_reais();
            match(TokenType.FECHAPAR);
        }
        else throw new ErroSintaticoException();
    }

    private void loop_acesso_objeto()
    {
         if( tokenAtual.getTipo() == TokenType.PONTO ) {
            acesso_objeto();
        }
         else if( tokenAtual.getTipo() == TokenType.ABREPAR ) {
            match(TokenType.ABREPAR);
            parametros_reais();
            match(TokenType.FECHAPAR);
        }
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

     private void parametro_real()
    {
        if ( tokenAtual.getTipo() == TokenType.ID ||
             tokenAtual.getTipo() == TokenType.NUM ||
             tokenAtual.getTipo() == TokenType.LITERAL ||
             tokenAtual.getTipo() == TokenType.CARACTER ||
             tokenAtual.getTipo() == TokenType.VERDADEIRO ||
             tokenAtual.getTipo() == TokenType.FALSO  ) proxToken();

        else throw new ErroSintaticoException();
    }

    private void operador_multiplicacao()
    {
        if ( tokenAtual.getTipo() == TokenType.MULT ||
             tokenAtual.getTipo() == TokenType.DIV  ) proxToken();

        else throw new ErroSintaticoException();
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

        else throw new ErroSintaticoException();
    }


    private void expressao_booleana()
    {
        if ( tokenAtual.getTipo() == TokenType.VERDADEIRO || tokenAtual.getTipo() == TokenType.FALSO  ) {
             proxToken();
         }
    }


// ------------------- ATRIBUIÇÃO ----------------------------------------------

    private void atribuicao()
    {
        if ( tokenAtual.getTipo() == TokenType.ID  ) {
            match( TokenType.ID );
            checarSeIdentificadorFoiDeclarado(tokens.get(ponteiro-1).getLexema()); //método semântico
            complemento_id_atribuicao();
         }
        else if( tokenAtual.getTipo() == TokenType.INCR || tokenAtual.getTipo() == TokenType.DECR ) {
            incremento_decremento();
            match( TokenType.ID );
        }
        else throw new ErroSintaticoException();
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
        else throw new ErroSintaticoException();
    }

    private void loop_acesso_atributo_obj()
    {
        if ( tokenAtual.getTipo() == TokenType.PONTO  ) {
            match(TokenType.PONTO);
            complemento_ponto_comando();
        }

    }

    private void complemento_ponto_comando()
    {
        match(TokenType.ID);
        loop_acesso_atributo_obj();
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
          else throw new ErroSintaticoException();
    }

    private void atribuicao_constante(String tipo)
    {
        match(TokenType.ID);
        Constante constante = new Constante(tokens.get(ponteiro-1).getLexema(), tipo);
        addConstante(constante); //método semântico
        match(TokenType.ATRIB);
        segundo_membro_atribuicao();
    }

//-------- MÉTODO QUE RETORNA O CONJUNTO PRIMEIRO DE UMA DADA PRODUÇÃO ---------

    private Set<TokenType> primeiro(String producao)
    {
        return conjuntoPrimeiro.getConjunto(producao);
    }

//------------------ MÉTODO DE RECUPERAÇÃO DE ERRO - PÂNICO --------------------

    private void panico(Set<TokenType> conjuntoSincronizacao)
    {
        while(!conjuntoSincronizacao.contains(tokenAtual.getTipo())) proxToken();
        if(tokenAtual.getTipo() == TokenType.EOF) throw new FinalArquivoException();
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
        else throw new ErroSintaticoException();
    }

    public void imprimirSaida()
    {
        if (temErros()) janela.imprimirCabecalhoErrosSemanticos();

        for (ErroSemantico erro : erros) {
            janela.imprimirErroSemantico(erro);
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

/******************************************************************************
 ********************* MÉTODOS DO ANALISADOR SEMANTICO ************************
/******************************************************************************/


//------------------- DECLARAÇÃO DE IDENTIFICADORES ----------------------------

    private void addConstante(Constante con)
    {
        if(jaFoiDeclaradoNoBlocoAtual(con.getId())) {
            erroSemantico("Identificador já foi declarado: " + "'" + con.getId() + "'");
        } else {
            tabelaDeSimbolos.addConstante(con);
        }
    }

    private void addVariavel(Variavel var)
    {
        if(jaFoiDeclaradoNoBlocoAtual(var.getId())) {
            erroSemantico("Identificador já foi declarado: " + "'" + var.getId() + "'");
        } else {
            tabelaDeSimbolos.addVariavel(var);
        }
    }

    private void addClasse(Classe classe)
    {
        if(jaFoiDeclaradoNoEscopo(classe.getId())) {
            erroSemantico("Identificador já foi declarado: " + "'" + classe.getId() + "'");
        } else {
            tabelaDeSimbolos.addClasse(classe);
        }
    }

    private void addMetodo(Metodo metodo)
    {
        if(jaFoiDeclaradoNoEscopo(metodo.getId())) {
            erroSemantico("Identificador já foi declarado: " + "'" + metodo.getId() + "'");
        } else {
            tabelaDeSimbolos.addMetodo(metodo);
        }
    }

    private void addMetodoPrincipal(Metodo metodo)
    {
        if(tabelaDeSimbolos.metodoPrincipalFoiDeclarado()) {
            erroSemantico("Método Principal não pode ser declarado mais de uma vez!");
        } else {
            tabelaDeSimbolos.addMetodo(metodo);
            tabelaDeSimbolos.setMetodoPrincipal(true);
        }
    }

    private void addParametro(Variavel param)
    {
        if(jaFoiDeclaradoNoEscopo(param.getId())) {
            erroSemantico("Identificador já foi declarado: " + "'" + param.getId() + "'");
        } else {
            tabelaDeSimbolos.addParametro(param);
        }
    }


//------------------- CHECAGEM DE IDENTIFICADORES ------------------------------

    private void checarSeIdentificadorFoiDeclarado(String id)
    {
        Simbolo simbolo = tabelaDeSimbolos.getSimbolo(id);
        if (simbolo == null) {
            erroSemantico("indentificador '" + id + "' não declarado");
        }
        else if(!(simbolo instanceof Variavel || simbolo instanceof Constante || simbolo instanceof Metodo)) {
            erroSemantico("indentificador '" + id + "' não declarado");
        }
    }

    private Classe checarSeClasseFoiDefinida(String classeID)
    {
        Simbolo simbolo = tabelaDeSimbolos.getSimbolo(classeID);
        if(simbolo!=null) {
            if(!(simbolo instanceof Classe)) erroSemantico("classe '" + classeID + "' não declarada");
            else return (Classe) simbolo;
        }
        else erroSemantico("classe '" + classeID + "' não declarada");
        return null;
    }

    private boolean jaFoiDeclaradoNoEscopo(String id)
    {
        return tabelaDeSimbolos.jaFoiDeclaradoNoEscopo(id);
    }
    
    private boolean jaFoiDeclaradoNoBlocoAtual(String id)
    {
        return tabelaDeSimbolos.jaFoiDeclaradoNoBlocoAtual(id);
    }

    private void erroSemantico(String msg)
    {
        erros.add(new ErroSemantico(msg,tokenAtual.getLinha()));
    }
}

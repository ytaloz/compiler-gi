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
import compilador.tabeladesimbolos.Escopo;
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

                Classe classe = checarSeClasseFoiDefinida(tokens.get(ponteiro-1).getLexema()); //metodo semantico

                match(TokenType.ID);

                Variavel obj = new Variavel(tokens.get(ponteiro-1).getLexema(),tokens.get(ponteiro-2).getLexema()); //método semantico
                addVariavel(obj); //método semantico

                complemento_variavel_instanciar_obj(classe);
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
                Variavel variavel = new Variavel(tokens.get(ponteiro-1).getLexema(), tipo + "[]");
                addVariavel(variavel);//método semântico
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
      
            tabelaDeSimbolos.empilharNovoEscopo(metodo); //método semantico
            
            match(TokenType.ABREPAR);
            parametros_formais(metodo);
            match(TokenType.FECHAPAR);

            addMetodo(metodo); //método semantico

            match(TokenType.ABRECHAVE);
            dec_var_metodo();
            comandos();
            match(TokenType.FECHACHAVE);

            if(!metodo.getTipoDado().equals("vazio") && !metodo.temComandoRetorno()) erroSemantico("faltando comando de retorno no método '" + metodo.getId() + "()'");
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

    private void complemento_variavel_instanciar_obj(Classe classe)
    {
        if(tokenAtual.getTipo() == TokenType.ABREPAR) {
           match(TokenType.ABREPAR);
           parametros_reais_instanciar_obj(classe, -1);
           if(classe.getConstrutor()==null && classe!=null) erroSemantico("construtor da classe '" + classe.getId() + "' não foi definido");
           match(TokenType.FECHAPAR);
        }
    }

    private void parametros_reais_instanciar_obj(Classe classe, int index)
    {
        index++; //índice do parametro a ser checado o tipo
        parametro_real(classe.getConstrutor(), index);
        loop_parametros_reais_instanciar_obj(classe, index);
    }

    private void loop_parametros_reais_instanciar_obj(Classe classe, int index)
    {
        if(tokenAtual.getTipo() == TokenType.VIRGULA) {
            match(TokenType.VIRGULA);
            parametros_reais_instanciar_obj(classe, index);
        }
        else if(classe.getConstrutor()!=null) {
            if(classe.getConstrutor().getTotalParametros() > index+1) {
                String msgErro = "construtor '" + classe.getConstrutor().getId() + "()' exige parametros: ";
                for (int i = 0; i < classe.getConstrutor().getTotalParametros(); i++) {
                    msgErro += classe.getConstrutor().getParametro(i).getTipoDado() + ", ";
                }
                erroSemantico(msgErro);
            }
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
                //checarSeIdentificadorFoiDeclarado(tokenAtual.getLexema()); //método semântico
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
        Simbolo objAtual = tabelaDeSimbolos.getSimbolo(tokens.get(ponteiro-1).getLexema());

        switch(tokenAtual.getTipo())
        {
            case PONTO: {         
                Classe classeAcessada = checarClasseDoObjetoAtual(objAtual); //método semantico
                acesso_objeto_comando(classeAcessada);
                break;
            }
            case ABRECOLCH: { 
                Simbolo ultimoId = checarSeVetorFoiDeclarado(tokens.get(ponteiro-1).getLexema());
                match(TokenType.ABRECOLCH);
                expressao_aritmetica();
                match(TokenType.FECHACOLCH);
                match(TokenType.ATRIB);
                String tipoAtribuicao = segundo_membro_atribuicao();
                if(ultimoId!=null) {
                     checarTipoAtribuicao(ultimoId.getTipoDado().substring(0, ultimoId.getTipoDado().length()-2), tipoAtribuicao);
                }
                break;
            }
            case ATRIB: {
                match(TokenType.ATRIB);
                Simbolo ultimoId = checarSeIdentificadorFoiDeclarado(tokens.get(ponteiro-2).getLexema());
                String tipoAtribuicao = segundo_membro_atribuicao();
                if(ultimoId!=null) {
                     checarTipoAtribuicao(ultimoId.getTipoDado(), tipoAtribuicao);
                }
                break;
            }
            case ABREPAR: {
                Metodo metodo = checarSeIdentificadorAtualEhMetodo(objAtual);
                match(TokenType.ABREPAR);
                parametros_reais(metodo, -1);
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

    private void acesso_objeto_comando(Classe classeAcessada)
    {
        if ( tokenAtual.getTipo() == TokenType.PONTO ) {
            match(TokenType.PONTO);
            match(TokenType.ID);

            String propriedade = tokens.get(ponteiro-1).getLexema();
            Simbolo propriedadeSimb = checarSeClassePossuiPropriedade(classeAcessada,propriedade); //método semantico

            loop_acesso_objeto_comando(propriedadeSimb);
        }
    }

    private void loop_acesso_objeto_comando(Simbolo objAtual)
    {
        if ( tokenAtual.getTipo() == TokenType.PONTO  ) {
            Classe classe = checarClasseDoObjetoAtual(objAtual);
            acesso_objeto_comando(classe);
         }
        else if( tokenAtual.getTipo() == TokenType.ABREPAR ) {
            Metodo metodo = checarSeIdentificadorAtualEhMetodo(objAtual);
            match( TokenType.ABREPAR );
            parametros_reais(metodo, -1);
            match( TokenType.FECHAPAR );
        }
        else if( tokenAtual.getTipo() == TokenType.ATRIB ) {
            checarSeIdentificadorAtualEhVariavel(objAtual);
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

    private String condicao_comandos()
    {
        String tipo = "erro";

        if (primeiro(EXPRESSAO_ARITMETICA).contains(tokenAtual.getTipo())) {
            String tipoOp1 = expressao_relacional();
            tipo = prox_trecho_expl(tipoOp1);
        }
        else if(tokenAtual.getTipo() == TokenType.VERDADEIRO || tokenAtual.getTipo() == TokenType.FALSO) {
            expressao_booleana();
            op_relacional_igualdade();
            match(TokenType.ID);
            Simbolo ultimoId = checarSeIdentificadorFoiDeclarado(tokens.get(ponteiro-1).getLexema());
            if(ultimoId != null) {
                if(ultimoId.getTipoDado().equals("logico")) tipo = "logico";
            }
        }
        else throw new ErroSintaticoException();

        return tipo;
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
        String tipoRetorno = param_retorno();
        Metodo metodoAtual = (Metodo) tabelaDeSimbolos.getEscopoAtual();
        metodoAtual.setComandoRetorno(true);
        String tipoMetodoAtual = metodoAtual.getTipoDado();
        if( !tipoMetodoAtual.equals(tipoRetorno)) {
            erroSemantico("tipo incompatível no retorno; esperava " + tipoMetodoAtual + ", obteve " + tipoRetorno);
        }
        match(TokenType.FECHAPAR);
    }

    private String param_retorno()
    {
        String tipo = "erro";

        if( tokenAtual.getTipo() == TokenType.ID ||
            tokenAtual.getTipo() == TokenType.NUM ||
            tokenAtual.getTipo() == TokenType.VERDADEIRO ||
            tokenAtual.getTipo() == TokenType.FALSO ||
            tokenAtual.getTipo() == TokenType.ABREPAR ) {
            tipo = expressao();
        }
        else if (  tokenAtual.getTipo() == TokenType.LITERAL ||
                   tokenAtual.getTipo() == TokenType.CARACTER  ) {
            proxToken();
            tipo = tipoDadoToken(tokens.get(ponteiro-1));
        }

        else throw new ErroSintaticoException();

        return tipo;
    }

    private void incremento_decremento()
    {
        if(tokenAtual.getTipo() == TokenType.INCR || tokenAtual.getTipo() == TokenType.DECR) proxToken();
        else throw new ErroSintaticoException();
    }


// ------------------- EXPRESSÕES ----------------------------------------------

    private String expressao()
    {
        String tipo = "erro";

        if (primeiro(EXPRESSAO_ARITMETICA).contains(tokenAtual.getTipo())) {
            String tipoExpAritm = expressao_aritmetica();
            tipo = complemento_exp_aritmetica(tipoExpAritm);
        }
        else if (tokenAtual.getTipo() == TokenType.VERDADEIRO || tokenAtual.getTipo() == TokenType.FALSO) {
            expressao_booleana();
            tipo = prox_trecho_expl("logico"); //se entrou nessa parte, necessariamente expressao_booleana retorna o tipo "logico"
        }

        return tipo;
    }

    private String complemento_exp_aritmetica(String tipoOperandoRel1)
    {
        String tipo = tipoOperandoRel1;

        if (tokenAtual.getTipo() == TokenType.MAIOR ||
            tokenAtual.getTipo() == TokenType.MENOR ||
            tokenAtual.getTipo() == TokenType.MAIORIGUAL ||
            tokenAtual.getTipo() == TokenType.MENORIGUAL ||
            tokenAtual.getTipo() == TokenType.IGUAL ||
            tokenAtual.getTipo() == TokenType.DIF) {

            operador_relacional();
            String tipoOperandoRel2 = expressao_aritmetica();

            if( (tipoOperandoRel1.equals("inteiro") || tipoOperandoRel1.equals("real")) && (tipoOperandoRel2.equals("inteiro") || tipoOperandoRel2.equals("real"))  )
            tipo = "logico";

            tipo = prox_trecho_expl(tipo);
        }

        return tipo;
    }

    private String prox_trecho_expl(String tipoOp1)
    {
        String tipo = tipoOp1;

        if ( tokenAtual.getTipo() == TokenType.OU || tokenAtual.getTipo() == TokenType.E  ) {
             operador_logico();
             String tipoOp2 = termo_l();

             if(tipoOp1.equals("logico") && tipoOp2.equals("logico")) tipo = "logico";
             else tipo = "erro";
        }

        return tipo;
    }

    private void operador_logico()
    {
        if ( tokenAtual.getTipo() == TokenType.OU ||
             tokenAtual.getTipo() == TokenType.E  ) proxToken();
    }

    private String termo_l()
    {
        String tipo = "erro";

         if (primeiro(EXPRESSAO_ARITMETICA).contains(tokenAtual.getTipo())) {
            String tipoOp1 = expressao_relacional();
            tipo = prox_trecho_expl(tipoOp1);
        }
        else if (tokenAtual.getTipo() == TokenType.VERDADEIRO || tokenAtual.getTipo() == TokenType.FALSO) {
            expressao_booleana();
            tipo = prox_trecho_expl("logico");
        }
        else throw new ErroSintaticoException();

        return tipo;
    }

    private String expressao_relacional()
    {
        String tipo = "erro";

        String tipoOp1 = expressao_aritmetica();
        operador_relacional();
        String tipoOp2 = expressao_aritmetica();

        if( (tipoOp1.equals("inteiro") || tipoOp1.equals("real")) && (tipoOp2.equals("inteiro") || tipoOp2.equals("real"))  )
            tipo = "logico";

        return tipo;
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

    private String expressao_aritmetica()
    {
        String tipo = "erro";

        String tipoTermoAritm = termo_aritm();
        tipo = prox_trecho_exp_aritm(tipoTermoAritm);

        return tipo;
    }

    private String termo_aritm()
    {
        String tipo = "erro";
        if ( tokenAtual.getTipo() == TokenType.ID || tokenAtual.getTipo() == TokenType.NUM || tokenAtual.getTipo() == TokenType.ABREPAR ) {
            String tipoFator = fator();
            tipo = complemento_fator(tipoFator);
        }
        else throw new ErroSintaticoException();
        return tipo;
    }

    private String fator()
    {
        String tipo = "erro";
        
        if ( tokenAtual.getTipo() == TokenType.ID ) {
            match( TokenType.ID );
            tipo = complemento_referencia_variavel();
        }
        else if ( tokenAtual.getTipo() == TokenType.NUM ) {
            tipo = tipoDadoToken(tokenAtual); //checagem de tipo
            match( TokenType.NUM );
        }
        else if ( tokenAtual.getTipo() == TokenType.ABREPAR ) {
            match( TokenType.ABREPAR );
            tipo = expressao_aritmetica();
            match( TokenType.FECHAPAR );
        }
        else throw new ErroSintaticoException();

        return tipo;
    }

    private String complemento_fator(String tipoFator)
    {
        String tipo = tipoFator;
        if ( tokenAtual.getTipo() == TokenType.MULT || tokenAtual.getTipo() == TokenType.DIV ) {
            String operador = tokenAtual.getLexema();
            operador_multiplicacao();
            String tipoTermoAritm = termo_aritm();
            tipo = verificarTipoOperacaoAritmetica(tipoFator, tipoTermoAritm, operador);
        }
        return tipo;
    }

    private String complemento_referencia_variavel()
    {
        String tipo = "erro";

        if( tokenAtual.getTipo() == TokenType.PONTO || tokenAtual.getTipo() == TokenType.ABREPAR ) {
            Simbolo objAtual = tabelaDeSimbolos.getSimbolo(tokens.get(ponteiro-1).getLexema());
            Classe classeAcessada = checarClasseDoObjetoAtual(objAtual);
            tipo = acesso_objeto(classeAcessada);
        }
        else if( tokenAtual.getTipo() == TokenType.ABRECOLCH ) {
            Simbolo ultimoId = checarSeIdentificadorFoiDeclarado(tokens.get(ponteiro-1).getLexema());
            if(ultimoId!=null) tipo = ultimoId.getTipoDado();
            match(TokenType.ABRECOLCH);
            expressao_aritmetica();
            match(TokenType.FECHACOLCH);
        }
        else {
            //checarSeIdentificadorFoiDeclarado(tokens.get(ponteiro-1).getLexema()); //método semântico
            tipo = tipoDadoToken(tokens.get(ponteiro-1));
        }

        return tipo;
    }

    private String acesso_objeto(Classe classeAcessada)
    {
        String tipo = "erro";
        if( tokenAtual.getTipo() == TokenType.PONTO ) {
            match(TokenType.PONTO);
            match(TokenType.ID);

            String propriedade = tokens.get(ponteiro-1).getLexema();
            Simbolo propriedadeSimb = checarSeClassePossuiPropriedade(classeAcessada,propriedade); //método semantico

            tipo = loop_acesso_objeto(propriedadeSimb); 
        }
        else if( tokenAtual.getTipo() == TokenType.ABREPAR ) {
            Simbolo objAtual = tabelaDeSimbolos.getSimbolo(tokens.get(ponteiro-1).getLexema());
            Metodo metodo = checarSeIdentificadorAtualEhMetodo(objAtual);
            match(TokenType.ABREPAR);
            parametros_reais(metodo, -1);
            match(TokenType.FECHAPAR);
            tipo = metodo.getTipoDado();
        }
        else throw new ErroSintaticoException();

        return tipo;
    }

    private String loop_acesso_objeto(Simbolo objAtual)
    {
        String tipo = "erro";
        
         if( tokenAtual.getTipo() == TokenType.PONTO ) {
            Classe classe = checarClasseDoObjetoAtual(objAtual);
            tipo = acesso_objeto(classe);
        }
         else if( tokenAtual.getTipo() == TokenType.ABREPAR ) {
            Metodo metodo = checarSeIdentificadorAtualEhMetodo(objAtual);
            if(metodo!=null) tipo = metodo.getTipoDado();
            match(TokenType.ABREPAR);
            parametros_reais(metodo, -1);
            match(TokenType.FECHAPAR);
        }
        else {
             checarSeIdentificadorAtualEhVariavel(objAtual); //método semântico
             if(objAtual != null) tipo = objAtual.getTipoDado();
        }

        return tipo;
    }

    private void parametros_reais(Metodo metodo, int index)
    {
        index++; //índice do parametro a ser checado o tipo
        if (primeiro(PARAMETRO_REAL).contains(tokenAtual.getTipo())) {
            parametro_real(metodo, index);
            loop_parametros_reais(metodo, index);
        }
        else if(metodo!=null) {
            if(metodo.getTotalParametros() > index+1) {
                String msgErro = "método '" + metodo.getId() + "()' exige parametros: ";
                for (int i = 0; i < metodo.getTotalParametros(); i++) {
                    msgErro += metodo.getParametro(i).getTipoDado() + ", ";
                }
                erroSemantico(msgErro);
             }
         }
    }

    private void loop_parametros_reais(Metodo metodo, int index)
    {
        if ( tokenAtual.getTipo() == TokenType.VIRGULA ) {
            match(TokenType.VIRGULA);
            parametros_reais(metodo, index);
        }
        else if(metodo!=null) {
            if(metodo.getTotalParametros() > index+1) {
                String msgErro = "método '" + metodo.getId() + "()' exige parametros: ";
                for (int i = 0; i < metodo.getTotalParametros(); i++) {
                    msgErro += metodo.getParametro(i).getTipoDado() + ", ";
                }
                erroSemantico(msgErro);
            }
        }
    }

     private void parametro_real(Metodo metodo, int index)
    {
        if ( tokenAtual.getTipo() == TokenType.ID ||
             tokenAtual.getTipo() == TokenType.NUM ||
             tokenAtual.getTipo() == TokenType.LITERAL ||
             tokenAtual.getTipo() == TokenType.CARACTER ||
             tokenAtual.getTipo() == TokenType.VERDADEIRO ||
             tokenAtual.getTipo() == TokenType.FALSO  ) proxToken();

        else throw new ErroSintaticoException();

        String tipoParametro = tipoDadoToken(tokens.get(ponteiro-1));
        if(metodo!=null) {
            if(index < metodo.getTotalParametros()) {
                if( !(tipoParametro.equals(metodo.getParametro(index).getTipoDado())) && !tipoParametro.equals("erro") ) {
                    erroSemantico("parametro '" + tokens.get(ponteiro-1).getLexema() + "' é do tipo " + tipoParametro + ", esperava um parametro do tipo " + metodo.getParametro(index).getTipoDado());
                }
            }
            else erroSemantico("método ou construtor '" + metodo.getId() + "' nao possui um " + (index+1) + "º parametro");
        }
        //else erroSemantico("");
    }

    private void operador_multiplicacao()
    {
        if ( tokenAtual.getTipo() == TokenType.MULT ||
             tokenAtual.getTipo() == TokenType.DIV  ) proxToken();

        else throw new ErroSintaticoException();
    }

    private String prox_trecho_exp_aritm(String tipoTermoAritm)
    {
        String tipo = tipoTermoAritm;
        if ( tokenAtual.getTipo() == TokenType.ADICAO || tokenAtual.getTipo() == TokenType.SUB ) {
            String operador = tokenAtual.getLexema();
            operador_soma();
            String tipoExpAritm = expressao_aritmetica();
            tipo = verificarTipoOperacaoAritmetica(tipoTermoAritm, tipoExpAritm, operador);
        }
        return tipo;
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

    private String segundo_membro_atribuicao()
    {
        String tipo = "erro";

          if(tokenAtual.getTipo() == TokenType.ABREPAR ||
           tokenAtual.getTipo() == TokenType.ID ||
           tokenAtual.getTipo() == TokenType.NUM ||
           tokenAtual.getTipo() == TokenType.VERDADEIRO ||
           tokenAtual.getTipo() == TokenType.FALSO)  {
              tipo = expressao();
          }
          else if( tokenAtual.getTipo() == TokenType.CARACTER || tokenAtual.getTipo() == TokenType.LITERAL ) {
              tipo = tipoDadoToken(tokenAtual); 
              proxToken();
          }
          else if(tokenAtual.getTipo() == TokenType.INCR || tokenAtual.getTipo() == TokenType.DECR) {
              incremento_decremento();
              match(TokenType.ID);
              tipo = tipoDadoToken(tokens.get(ponteiro-1));
          }
          else throw new ErroSintaticoException();

        return tipo;
    }

    private void atribuicao_constante(String tipo)
    {
        match(TokenType.ID);
        Constante constante = new Constante(tokens.get(ponteiro-1).getLexema(), tipo);
        addConstante(constante); //método semântico
        match(TokenType.ATRIB);
        String tipoAtrib = segundo_membro_atribuicao();
        checarTipoAtribuicao(constante.getTipoDado(), tipoAtrib);
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
        Classe classe = tabelaDeSimbolos.getClasseAtual();
        if(!classe.possuiMetodo(metodo)) {
            if(classe.getConstante(metodo.getId())==null && classe.getVariavel(metodo.getId())==null) {
                if(ehConstrutor(metodo)) {
                    tabelaDeSimbolos.addConstrutor(metodo);
                } else {
                    tabelaDeSimbolos.addMetodo(metodo);
                }
            }
            else erroSemantico("identificador '" + metodo.getId() + "' já foi declarado na classe");
        }
        else erroSemantico("o método '" + metodo.getId() + "' já foi declarado.");
    }

    private boolean ehConstrutor(Metodo metodo)
    {
        Classe atual = tabelaDeSimbolos.getClasseAtual();
        if (atual.getId().equals(metodo.getId()) && metodo.getTipoDado().equals("vazio")) {
            return true;
        }
        return false;
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
        if(jaFoiDeclaradoNoBlocoAtual(param.getId())) {
            erroSemantico("Identificador já foi declarado: " + "'" + param.getId() + "'");
        } else {
            tabelaDeSimbolos.addParametro(param);
        }
    }


//------------------- CHECAGEM DE IDENTIFICADORES ------------------------------

    private Simbolo checarSeIdentificadorFoiDeclarado(String id)
    {
        Simbolo simbolo = tabelaDeSimbolos.getSimbolo(id);
        if (simbolo == null) {
            erroSemantico("indentificador '" + id + "' não declarado");
            return null;
        }
        else if(!(simbolo instanceof Variavel || simbolo instanceof Constante || simbolo instanceof Metodo) || simbolo.ehVetor()) {
            erroSemantico("indentificador '" + id + "' não declarado");
            return null;
        }
        else return simbolo;
    }

    private Simbolo checarSeVetorFoiDeclarado(String id)
    {
        Simbolo simbolo = tabelaDeSimbolos.getSimbolo(id);
        if (simbolo == null) {
            erroSemantico("vetor '" + id + "' não declarado");
            return null;
        }
        else if(!(simbolo instanceof Variavel) || !simbolo.ehVetor()) {
            erroSemantico("vetor '" + id + "' não declarado");
            return null;
        }
        else return simbolo;
    }
    

    private void checarSeMetodoFoiDeclarado(String id)
    {
        Simbolo simbolo = tabelaDeSimbolos.getSimbolo(id);
        if (simbolo == null) {
            erroSemantico("método '" + id + "' não declarado");
        }
        else if(!(simbolo instanceof Metodo)) {
            erroSemantico("método '" + id + "' não declarado");
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

    private Simbolo checarSeClassePossuiPropriedade(Classe classe, String propriedade)
    {
        if (classe != null)
        {
            if (classe.getSimbolo(propriedade) != null) return classe.getSimbolo(propriedade);
            return null;
        } 
        else
        {
            erroSemantico("A variável '" + tokens.get(ponteiro-3).getLexema() + "' não é uma classe");
            return null;
        }
    }

    //para acesso de objetos pelo operador ponto
    private Classe checarClasseDoObjetoAtual(Simbolo objAtual)
    {
        if(objAtual!=null)
        {
            if(objAtual instanceof Variavel)
            {
                Variavel var = (Variavel) objAtual;
                String tipo = var.getTipoDado();
                
                if(tipo.equals("inteiro") || tipo.equals("real") || tipo.equals("cadeia") || tipo.equals("caractere") || tipo.equals("logico")) {
                    erroSemantico("identificador '" + tokens.get(ponteiro-1).getLexema() + "' não é objeto; operador ponto não permitido");
                    return null;
                }
                return tabelaDeSimbolos.getClasse(tipo);
            }
            else {
                erroSemantico("identificador '" + tokens.get(ponteiro-1).getLexema() + "' não é objeto; operador ponto não permitido");
                return null;
            }
        }
        else return null;
    }

    //para chamada de métodos através de objetos
    private Metodo checarSeIdentificadorAtualEhMetodo(Simbolo idAtual)
    {
        if(idAtual != null) {
            if(!(idAtual instanceof Metodo)) {
                erroSemantico("método '" + idAtual.getId() + "' não declarado;");
                return null;
            }
            else return (Metodo) idAtual;
        }
        else {
            erroSemantico("método '" + tokens.get(ponteiro-1).getLexema() + "' não declarado;");
            return null;
        }
    }

    //para chamada de métodos através de objetos
    private void checarSeIdentificadorAtualEhVariavel(Simbolo idAtual)
    {
        if(idAtual != null) {
            if(!(idAtual instanceof Constante || idAtual instanceof Variavel)) {
                erroSemantico("atributo ou constante '" + idAtual.getId() + "' não declarada;");
            }
        }
        else erroSemantico("atributo ou constante '" + tokens.get(ponteiro-1).getLexema() + "' não declarada;");
    }

//--------------------- MÉTODOS DE VERIFICAÇÃO DE TIPOS ------------------------

   private String verificarTipoOperacaoAritmetica(String tipoOp1, String tipoOp2, String operador)
   {
       if(!tipoOp1.equals("erro") && !tipoOp2.equals("erro"))
       {
           if(tipoOp1.equals("inteiro") && tipoOp2.equals("inteiro")) return "inteiro";
           if(tipoOp1.equals("real") && tipoOp2.equals("real")) return "real";

           erroSemantico("operador " + operador + "não pode ser aplicado aos tipos: " + tipoOp1 + ", " + tipoOp2);

           return "erro";
       }
       else return "erro";
   }

   private void checarTipoAtribuicao(String tipoPrimeiroMembro, String tipoSegundoMembro)
   {
       if(!tipoPrimeiroMembro.equals(tipoSegundoMembro)) {
           erroSemantico("incompatibilidade de tipos na atribuição: esperava " + tipoPrimeiroMembro + ", obteve: " + tipoSegundoMembro);
       }
   }

//   private String teste()
//   {
//       String s = "oi";
//       int[] a = new int[10];
//       //return 2;
//   }


//---------------------------- MÉTODOS AUXILIARES ------------------------------

    private String tipoDadoToken(Token token)
    {
        if(token.getTipo()==TokenType.LITERAL) return "cadeia"; 
        if(token.getTipo()==TokenType.CARACTER) return "caractere";
        if(token.getTipo()==TokenType.VERDADEIRO || token.getTipo()==TokenType.FALSO) return "logico";
        if(token.getTipo()==TokenType.NUM) {
            if ( token.getLexema().indexOf(".")!=-1 ) return "real";
            else return "inteiro";
        }
        if(token.getTipo()==TokenType.ID) {
            Simbolo var = checarSeIdentificadorFoiDeclarado(token.getLexema());
            if(var!=null) {
                return var.getTipoDado();
            } else return "erro";
        }
        return "erro";
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos;

import compilador.tabeladesimbolos.Simbolo.TipoDado;
import compilador.tabeladesimbolos.Simbolo.TipoSimbolo;
import compilador.token.TokenType;
import java.util.HashMap;

/**
 *
 * @author Gabriel
 */
public class TabelaDeSimbolos {

    //palavras chave da linguagem
    private HashMap<String,String> palavrasChave = new HashMap<String,String>();

    //todos os simbolos declarados
    private HashMap<String,Simbolo> simbolos = new HashMap<String,Simbolo>();

    //escopos do programa, recuperados pelo lexema
    private HashMap<String,Escopo> escopos = new HashMap<String,Escopo>();

    //escopo atual no qual os simbolos serão inseridos
    private Escopo escopoAtual = new Escopo(null);



    public TabelaDeSimbolos()
    {
        inicializarPalavrasChave();
        escopos.put("programa",escopoAtual);
    }


//------------------------------ ESCOPOS ---------------------------------------

    //cria novo escopo, aninhado ao escopo atual
    public void aninharNovoEscopo(String id)
    {
        escopoAtual = new Escopo(escopoAtual);
        escopos.put(id,escopoAtual);
    }

    //cria novo escopo, especificando o escopo pai -> usado para herança de classes
    public void aninhaNovoEscopo(String id, String pai)
    {
        escopoAtual = new Escopo(getEscopo(pai));
        escopos.put(id,escopoAtual);
    }

    private Escopo getEscopo(String id)
    {
        return escopos.get(id);
    }


//------------------------------ SIMBOLOS DECLARADOS ---------------------------


    public void addConstante(String id, String tipoDado)
    {
        Simbolo simbolo = new Simbolo(id, TipoSimbolo.CONSTANTE, getTipoDado(tipoDado));
        addSimbolo(simbolo);
    }

    public void addVariavel(String id, String tipoDado)
    {
        Simbolo simbolo = new Simbolo(id, TipoSimbolo.VARIAVEL, getTipoDado(tipoDado));
        addSimbolo(simbolo);
    }

    public void addClasse(String id, String tipoDado)
    {
        Simbolo simbolo = new Simbolo(id, TipoSimbolo.CLASSE, getTipoDado(tipoDado));
        addSimbolo(simbolo);
    }

    public void addMetodo(String id, String tipoDado)
    {
        Simbolo simbolo = new Simbolo(id, TipoSimbolo.METODO, getTipoDado(tipoDado));
        addSimbolo(simbolo);
    }



//--------------------------- MÉTODOS AUXILIARES -------------------------------

    private TipoDado getTipoDado(String tipo)
    {
        if(tipo.equals("inteiro")) return TipoDado.INTEIRO;
        if(tipo.equals("real")) return TipoDado.REAL;
        if(tipo.equals("logico")) return TipoDado.LOGICO;
        if(tipo.equals("cadeia")) return TipoDado.CADEIA;
        if(tipo.equals("caractere")) return TipoDado.CARACTERE;
        else throw new IllegalArgumentException("A String não corresponde a um tipo de dado!");
    }

    public boolean foiDeclarado(String id)
    {
        return simbolos.get(id) != null;
    }

    public boolean ehOperandoValido(String id)
    {
        return ehConstante(id) || ehVariavel(id) || ehMetodo(id);
    }

    private boolean ehConstante(String id)
    {
        if(simbolos.get(id) != null) {
            return simbolos.get(id).getTipoSimbolo() == TipoSimbolo.CONSTANTE;
        }
        return false;
    }

    private boolean ehVariavel(String id)
    {
        if(simbolos.get(id) != null) {
            return simbolos.get(id).getTipoSimbolo() == TipoSimbolo.VARIAVEL;
        }
        return false;
    }

    private boolean ehMetodo(String id)
    {
        if(simbolos.get(id) != null) {
            return simbolos.get(id).getTipoSimbolo() == TipoSimbolo.METODO;
        }
        return false;
    }

    private boolean ehClasse(String id)
    {
        if(simbolos.get(id) != null) {
            return simbolos.get(id).getTipoSimbolo() == TipoSimbolo.CLASSE;
        }
        return false;
    }

    private void addSimbolo(Simbolo simbolo)
    {
        escopoAtual.addSimbolo(simbolo);
        simbolos.put(simbolo.getLexema(), simbolo);
    }

//--------------------------- PALAVRAS CHAVE -----------------------------------

    private void inserirPalavraChave(String lexema)
    {
        palavrasChave.put(lexema, lexema);
    }

    public String getPalavraChave(String chave)
    {
        return palavrasChave.get(chave);
    }

    public TokenType getTokenPalavraChave(String palavraChave)
    {
        if ( palavraChave.equals("variaveis") ) return TokenType.VARIAVEIS;
        if ( palavraChave.equals("metodos") ) return TokenType.METODOS;
        if ( palavraChave.equals("constantes") ) return TokenType.CONSTANTES;
        if ( palavraChave.equals("classe") ) return TokenType.CLASSE;
        if ( palavraChave.equals("retorno") ) return TokenType.RETORNO;
        if ( palavraChave.equals("vazio") ) return TokenType.VAZIO;
        if ( palavraChave.equals("principal") ) return TokenType.PRINCIPAL;
        if ( palavraChave.equals("se") ) return TokenType.SE;
        if ( palavraChave.equals("entao") ) return TokenType.ENTAO;
        if ( palavraChave.equals("senao") ) return TokenType.SENAO;
        if ( palavraChave.equals("enquanto") ) return TokenType.ENQUANTO;
        if ( palavraChave.equals("para") ) return TokenType.PARA;
        if ( palavraChave.equals("leia") ) return TokenType.LEIA;
        if ( palavraChave.equals("escreva") ) return TokenType.ESCREVA;
        if ( palavraChave.equals("inteiro") ) return TokenType.INTEIRO;
        if ( palavraChave.equals("real") ) return TokenType.REAL;
        if ( palavraChave.equals("logico") ) return TokenType.LOGICO;
        if ( palavraChave.equals("caractere") ) return TokenType.CARACTERE;
        if ( palavraChave.equals("cadeia") ) return TokenType.CADEIA;
        if ( palavraChave.equals("verdadeiro") ) return TokenType.VERDADEIRO;
        if ( palavraChave.equals("falso") ) return TokenType.FALSO;
        if ( palavraChave.equals("herda_de") ) return TokenType.HERDA_DE;

        return null;
    }

    private void inicializarPalavrasChave()
    {
        inserirPalavraChave("variaveis");
        inserirPalavraChave("metodos");
        inserirPalavraChave("constantes");
        inserirPalavraChave("classe");
        inserirPalavraChave("retorno");
        inserirPalavraChave("vazio");
        inserirPalavraChave("principal");
        inserirPalavraChave("se");
        inserirPalavraChave("entao");
        inserirPalavraChave("senao");
        inserirPalavraChave("enquanto");
        inserirPalavraChave("para");
        inserirPalavraChave("leia");
        inserirPalavraChave("escreva");
        inserirPalavraChave("inteiro");
        inserirPalavraChave("real");
        inserirPalavraChave("logico");
        inserirPalavraChave("caractere");
        inserirPalavraChave("cadeia");
        inserirPalavraChave("verdadeiro");
        inserirPalavraChave("falso");
        inserirPalavraChave("herda_de");
    }

    


}

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

    //estrutura de dados onde os simbolos serão armazenados e recuperados, levando em consideração o escopo
    private ArvoreDeEscopo arvoreDeEscopo = new ArvoreDeEscopo();



    public TabelaDeSimbolos()
    {
        inicializarPalavrasChave();
    }


//------------------------------ ESCOPOS ---------------------------------------

    

    public boolean foiDeclaradoNoEscopo(String id)
    {
        return arvoreDeEscopo.getSimbolo(id) != null;
    }

    public void empilharNovoEscopo()
    {
        arvoreDeEscopo.empilharNovoEscopo();
    }

    public void desempilharEscopo()
    {
        arvoreDeEscopo.desempilharEscopo();
    }


//------------------------ ADICIONAR SIMBOLOS DECLARADOS -----------------------


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

    public void addClasse(String id)
    {
        Simbolo simbolo = new Simbolo(id, TipoSimbolo.CLASSE, null);
        addSimbolo(simbolo);
    }

    public void addMetodo(String id, String tipoDado)
    {
        Simbolo simbolo = new Simbolo(id, TipoSimbolo.METODO, getTipoDado(tipoDado));
        addSimbolo(simbolo);
    }

    private void addSimbolo(Simbolo simbolo)
    {
        arvoreDeEscopo.addSimbolo(simbolo);
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

    public boolean jaFoiDeclaradoNoEscopo(String id)
    {
        return arvoreDeEscopo.getSimbolo(id)!=null;
    }

    public boolean jaFoiDeclaradoNoBlocoAtual(String id)
    {
        return arvoreDeEscopo.getEscopoAtual().getSimbolo(id) != null;
    }

    public boolean ehConstante(String id)
    {
        if(foiDeclaradoNoEscopo(id)) {
            return arvoreDeEscopo.getSimbolo(id).getTipoSimbolo() == TipoSimbolo.CONSTANTE;
        }
        return false;
    }

    public boolean ehVariavel(String id)
    {
        if(foiDeclaradoNoEscopo(id)) {
            return arvoreDeEscopo.getSimbolo(id).getTipoSimbolo() == TipoSimbolo.VARIAVEL;
        }
        return false;
    }

    public boolean ehMetodo(String id)
    {
        if(foiDeclaradoNoEscopo(id)) {
            return arvoreDeEscopo.getSimbolo(id).getTipoSimbolo() == TipoSimbolo.METODO;
        }
        return false;
    }

    public boolean ehClasse(String id)
    {
        if(foiDeclaradoNoEscopo(id)) {
            return arvoreDeEscopo.getSimbolo(id).getTipoSimbolo() == TipoSimbolo.CLASSE;
        }
        return false;
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

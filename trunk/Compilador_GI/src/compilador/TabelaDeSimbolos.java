/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador;

import compilador.token.TokenType;
import java.util.HashMap;

/**
 *
 * @author Gabriel
 */
public class TabelaDeSimbolos {

    private HashMap<String,String> tabela;

    public TabelaDeSimbolos(){
        tabela = new HashMap<String,String>();
        inicializarPalavrasChave();
    }

    public void inserir(String lexema)
    {
        tabela.put(lexema, lexema);
    }

    public String getSimbolo(String chave)
    {
        return tabela.get(chave);
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
        inserir("variaveis");
        inserir("metodos");
        inserir("constantes");
        inserir("classe");
        inserir("retorno");
        inserir("vazio");
        inserir("principal");
        inserir("se");
        inserir("entao");
        inserir("senao");
        inserir("enquanto");
        inserir("para");
        inserir("leia");
        inserir("escreva");
        inserir("inteiro");
        inserir("real");
        inserir("logico");
        inserir("caractere");
        inserir("cadeia");
        inserir("verdadeiro");
        inserir("falso");
        inserir("herda_de");
    }


}

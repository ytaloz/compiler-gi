/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos;

import compilador.token.TokenType;
import java.util.HashMap;

/**
 *
 * @author Gabriel
 */
public class TabelaDeSimbolos {

    private HashMap<String,String> palavrasChave;

    public TabelaDeSimbolos(){
        palavrasChave = new HashMap<String,String>();
        inicializarPalavrasChave();
    }

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

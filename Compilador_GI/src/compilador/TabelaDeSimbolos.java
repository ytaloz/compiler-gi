/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador;

import java.util.HashMap;

/**
 *
 * @author Gabriel
 */
public class TabelaDeSimbolos {

    private HashMap<String,String> tabela;

    public TabelaDeSimbolos(){
        tabela = new HashMap<String,String>();
    }

    public void inserir(String lexema)
    {
        tabela.put(lexema, lexema);
    }

    public String getSimbolo(String chave)
    {
        return tabela.get(chave);
    }

    public void inicializarPalavrasChave()
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
        inserir("false");
        inserir("herda_de");
    }


}
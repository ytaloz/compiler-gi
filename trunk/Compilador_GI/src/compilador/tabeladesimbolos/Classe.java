/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos;

import java.util.HashMap;

/**
 *
 * @author Gabriel
 */
public class Classe extends Escopo {

    private HashMap<String,Constante> constantes = new HashMap<String,Constante>();

    private HashMap<String,Variavel> variaveis = new HashMap<String,Variavel>();

    private HashMap<String,Metodo> metodos = new HashMap<String,Metodo>();

    protected Classe(Escopo escopoPai)
    {
        super(escopoPai);
    }

}

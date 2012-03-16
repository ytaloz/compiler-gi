/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos.simbolos;

import compilador.tabeladesimbolos.Escopo;
import java.util.HashMap;

/**
 *
 * @author Gabriel
 */
public class Metodo extends Escopo {

     //contantes locais
    private HashMap<String,Constante> constantes = new HashMap<String,Constante>();

    //variaveis locais
    private HashMap<String,Variavel> variaveis = new HashMap<String,Variavel>();

    //parametros do método
    private HashMap<String,Variavel> parametros = new HashMap<String,Variavel>();



    public Metodo(String id, String tipo)
    {
        super(id, tipo);
    }

    public void addConstante(Constante con)
    {
        constantes.put(con.getId(),con);
    }

    public void addVariavel(Variavel var)
    {
        variaveis.put(var.getId(),var);
    }

    public void addParametro(Variavel param)
    {
        parametros.put(param.getId(),param);
    }
}

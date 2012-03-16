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
public class Classe extends Escopo {

    //contantes globais
    private HashMap<String,Constante> constantes = new HashMap<String,Constante>();

    //variaveis globais
    private HashMap<String,Variavel> variaveis = new HashMap<String,Variavel>();

    //classes do programa
    private HashMap<String,Metodo> metodos = new HashMap<String,Metodo>();


    public Classe(String id)
    {
        super(id, Simbolo.TIPO_CLASSE);
    }

    public Classe(String id, Classe pai)
    {
        super(id, Simbolo.TIPO_CLASSE);
        escopoPai = pai;
    }

    public void addConstante(Constante con)
    {
        constantes.put(con.getId(),con);
    }

    public void addVariavel(Variavel var)
    {
        variaveis.put(var.getId(),var);
    }

    public void addMetodo(Metodo met)
    {
        met.setEscopoPai(this);
        addSimbolo(met);
        metodos.put(met.getId(),met);
    }

}

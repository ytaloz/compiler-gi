/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos.simbolos;

import compilador.tabeladesimbolos.Escopo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private List<Metodo> metodos = new ArrayList<Metodo>();

    //construtor da classe
    private Metodo construtor ;


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
        metodos.add(met);
    }

    public void addConstrutor(Metodo construtor)
    {
        this.construtor = construtor;
    }

    public Constante getConstante(String id)
    {
        return constantes.get(id);
    }

    public Variavel getVariavel(String id)
    {
        return variaveis.get(id);
    }

    public boolean possuiMetodo(Metodo met)
    {
        for (Metodo metodo : metodos) {
            if(metodo.equals(met)) return true;
        }
        return false;
    }

    public Metodo getConstrutor()
    {
        return this.construtor;
    }

}

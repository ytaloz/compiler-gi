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
public class Programa extends Escopo {

    //contantes globais
    private HashMap<String,Constante> constantes = new HashMap<String,Constante>();

    //variaveis globais
    private HashMap<String,Variavel> variaveis = new HashMap<String,Variavel>();

    //classes do programa
    private HashMap<String,Classe> classes = new HashMap<String,Classe>();


    public Programa()
    {
        super("$programa","$programa");
    }

    public void addConstante(Constante con)
    {
        constantes.put(con.getId(),con);
    }

    public void addVariavel(Variavel var)
    {
        variaveis.put(var.getId(),var);
    }

    public void addClasse(Classe classe)
    {
        classe.setEscopoPai(this);
        addSimbolo(classe);
        classes.put(classe.getId(),classe);
    }

    public Classe getClasse(String id)
    {
        return classes.get(id);
    }

}

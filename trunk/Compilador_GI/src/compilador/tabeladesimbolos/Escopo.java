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
public class Escopo {

    //escopo no qual está aninhado
    private Escopo escopoPai;

    //simbolos pertencentes a esse escopo
    private HashMap<String,Simbolo> simbolos = new HashMap<String,Simbolo>();



    protected Escopo(Escopo escopoPai)
    {
        this.escopoPai = escopoPai;
    }

    public Escopo getEscopoPai()
    {
        return this.escopoPai;
    }

    public void addSimbolo(Simbolo simbolo)
    {
        simbolos.put(simbolo.getLexema(), simbolo);
    }

    public Simbolo getSimbolo(String id)
    {
        return simbolos.get(id);
    }

}

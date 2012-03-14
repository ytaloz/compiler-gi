/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gabriel
 */
public class Escopo {

    //escopo no qual está aninhado
    private Escopo escopoPai;

    //escopos incluidos nesse escopo
    private List<Escopo> subEscopos = new ArrayList<Escopo>();

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

    public Escopo addSubEscopo()
    {
        Escopo sub = new Escopo(this);
        subEscopos.add(sub);
        return sub;
    }

}

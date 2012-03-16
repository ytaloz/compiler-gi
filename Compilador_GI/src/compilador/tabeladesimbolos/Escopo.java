/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos;

import compilador.tabeladesimbolos.simbolos.Simbolo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gabriel
 */
public abstract class Escopo extends Simbolo{

    //escopo no qual está aninhado
    protected Escopo escopoPai;

    //escopos incluidos nesse escopo
    protected List<Escopo> subEscopos = new ArrayList<Escopo>();

    //simbolos pertencentes a esse escopo
    protected HashMap<String,Simbolo> simbolos = new HashMap<String,Simbolo>();

    
    public Escopo(String id, String tipo)
    {
        super(id,tipo);
    }

    public Escopo getEscopoPai()
    {
        return this.escopoPai;
    }

    public void setEscopoPai(Escopo pai)
    {
        this.escopoPai = pai;
    }

    public void addSimbolo(Simbolo simbolo)
    {
        simbolos.put(simbolo.getId(), simbolo);
    }

    public Simbolo getSimbolo(String id)
    {
        return simbolos.get(id);
    }

    public void addSubEscopo(Escopo sub)
    {
        sub.setEscopoPai(this);
        subEscopos.add(sub);
    }

}

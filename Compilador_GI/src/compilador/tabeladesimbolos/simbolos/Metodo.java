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
public class Metodo extends Escopo {

     //contantes locais
    private HashMap<String,Constante> constantes = new HashMap<String,Constante>();

    //variaveis locais
    private HashMap<String,Variavel> variaveis = new HashMap<String,Variavel>();

    //parametros do método
    private List<Variavel> parametros = new ArrayList<Variavel>();



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
        parametros.add(param);
    }

    public Variavel getParametro(String id)
    {
        for (Variavel param : parametros) {
            if(param.id.equals(id)) return param;
        }
        return null;
    }

    public Variavel getParametro(int i)
    {
        return parametros.get(i);
    }

    public int getTotalParametros()
    {
        return parametros.size();
    }


    @Override
    public boolean equals(Object object)
    {
        if(!(object instanceof Metodo)) return false;
        Metodo outro = (Metodo) object;

        if( (outro.id.equals(this.id)) && (outro.tipoDado.equals(this.tipoDado)) && (outro.parametros.size()==this.parametros.size()))
        {
            for (int i = 0; i < parametros.size(); i++) {
                if( !(outro.parametros.get(i).getTipoDado().equals(this.parametros.get(i).getTipoDado())) ) return false;
            }
            return true;
        }
        else return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.constantes != null ? this.constantes.hashCode() : 0);
        hash = 67 * hash + (this.variaveis != null ? this.variaveis.hashCode() : 0);
        hash = 67 * hash + (this.parametros != null ? this.parametros.hashCode() : 0);
        return hash;
    }
}

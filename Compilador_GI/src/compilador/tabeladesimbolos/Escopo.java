/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos;

/**
 *
 * @author Gabriel
 */
public abstract class Escopo {

    Escopo escopoPai;

    protected Escopo(Escopo escopoPai)
    {
        this.escopoPai = escopoPai;
    }

    public Escopo getEscopoPai()
    {
        return this.escopoPai;
    }

}

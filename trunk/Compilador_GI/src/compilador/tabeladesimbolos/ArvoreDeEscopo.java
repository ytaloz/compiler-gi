/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos;

/**
 *
 * @author Gabriel
 */
public class ArvoreDeEscopo {

    //escopo raiz --> programa
    Escopo raiz;

    //escopo atual sendo considerado na análise
    Escopo escopoAtual;


    public ArvoreDeEscopo()
    {
        raiz = new Escopo(null);
        escopoAtual = raiz;
    }

    public void empilharNovoEscopo()
    {
        escopoAtual = escopoAtual.addSubEscopo();
    }

    public void desempilharEscopo()
    {
        escopoAtual = escopoAtual.getEscopoPai();
    }

    //retorna o simbolo se ele foi declarado no escopo atual, ou null se ele não foi declarado no escopo
    public Simbolo getSimbolo(String id)
    {
        if(escopoAtual.getSimbolo(id) != null) {
            return escopoAtual.getSimbolo(id);
        }
        else {
            Escopo pai = escopoAtual.getEscopoPai();
            if (pai != null) {
                while (pai.getSimbolo(id) == null && pai.getEscopoPai() != null) {
                    pai = pai.getEscopoPai();
                }
                return pai.getSimbolo(id);
            } else return null;
        }
    }

    public void addSimbolo(Simbolo simbolo)
    {
        escopoAtual.addSimbolo(simbolo);
    }

    public Escopo getEscopoAtual()
    {
        return escopoAtual;
    }


}

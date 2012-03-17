/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos;

import compilador.tabeladesimbolos.simbolos.Classe;
import compilador.tabeladesimbolos.simbolos.Constante;
import compilador.tabeladesimbolos.simbolos.Metodo;
import compilador.tabeladesimbolos.simbolos.Simbolo;
import compilador.tabeladesimbolos.simbolos.Programa;
import compilador.tabeladesimbolos.simbolos.Variavel;

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
        raiz = new Programa();
        escopoAtual = raiz;
    }

    public void empilharNovoEscopo(Escopo sub)
    {
        escopoAtual.addSubEscopo(sub);
        escopoAtual = sub;
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

    public Classe getClasse(String id)
    {
        Programa programa = (Programa) raiz;
        return programa.getClasse(id);
    }

    public Classe getClasseAtual()
    {
        if(escopoAtual instanceof Classe) return (Classe)escopoAtual;

        Escopo pai = escopoAtual.getEscopoPai();
            if (pai != null) {
                while (!(pai instanceof Classe) && pai.getEscopoPai() != null) {
                    pai = pai.getEscopoPai();
                }
                return (Classe)pai;
            }
            else return null;
    }

    public void addConstante(Constante con)
    {
        if(escopoAtual instanceof Programa) {
            Programa programa = (Programa) escopoAtual;
            programa.addConstante(con);
        }
        else if(escopoAtual instanceof Classe) {
            Classe classe = (Classe) escopoAtual;
            classe.addConstante(con);
        }
        else throw new RuntimeException("O escopo atual '" + escopoAtual.getId() + "' não permite declaração de constante");

        escopoAtual.addSimbolo(con);
    }


    public void addVariavel(Variavel var)
    {
        if(escopoAtual instanceof Programa) {
            Programa programa = (Programa) escopoAtual;
            programa.addVariavel(var);
        }
        else if(escopoAtual instanceof Classe) {
            Classe classe = (Classe) escopoAtual;
            classe.addVariavel(var);
        }
        else if(escopoAtual instanceof Metodo) {
            Metodo metodo = (Metodo) escopoAtual;
            metodo.addVariavel(var);
        }
        else throw new RuntimeException("O escopo atual '" + escopoAtual.getId() + "' não permite declaração de variável");

        escopoAtual.addSimbolo(var);
    }

    public void addClasse(Classe classe)
    {
        if(escopoAtual instanceof Programa) {
            Programa programa = (Programa) escopoAtual;
            programa.addClasse(classe);
        }
        else throw new RuntimeException("O escopo atual '" + escopoAtual.getId() + "' não permite declaração de classe");

        escopoAtual.addSimbolo(classe);
    }

    public void addMetodo(Metodo met)
    {
//        if(escopoAtual instanceof Classe) {
//            Classe classe = (Classe) escopoAtual;
//            classe.addMetodo(met);
//        }
//        else throw new RuntimeException("O escopo atual '" + escopoAtual.getId() + "' não permite declaração de método");
//
//        escopoAtual.addSimbolo(met);


        Classe classe = getClasseAtual();
        classe.addMetodo(met);

        escopoAtual.addSimbolo(met);
    }

    public void addConstrutor(Metodo construtor)
    {
//        if(escopoAtual instanceof Classe) {
//            Classe classe = (Classe) escopoAtual;
//            classe.addConstrutor(construtor);
//        }
//        else throw new RuntimeException("O escopo atual '" + escopoAtual.getId() + "' não permite declaração de construtor");

        Classe classe = getClasseAtual();
        classe.addConstrutor(construtor);

        escopoAtual.addSimbolo(construtor);
    }

    public void addParametro(Variavel param)
    {
        if(escopoAtual instanceof Metodo) {
            Metodo metodo = (Metodo) escopoAtual;
            metodo.addParametro(param);
        }
        else throw new RuntimeException("O escopo atual '" + escopoAtual.getId() + "' não permite declaração de parâmetro");

        escopoAtual.addSimbolo(param);
    }

    public Escopo getEscopoAtual()
    {
        return escopoAtual;
    }


}

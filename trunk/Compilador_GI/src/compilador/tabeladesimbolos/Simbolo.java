/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos;

/**
 *
 * @author Gabriel
 */
public class Simbolo {


    private String lexema;

    private TipoSimbolo tipoSimbolo;

    private TipoDado tipoDado;

    public Simbolo(String lexema, TipoSimbolo tipoSimbolo, TipoDado tipoDado)
    {
        this.lexema = lexema;
        this.tipoSimbolo = tipoSimbolo;
        this.tipoDado = tipoDado;
    }

    public String getLexema()
    {
        return lexema;
    }

    enum TipoSimbolo
    {
        CONSTANTE, VARIAVEL, METODO, CLASSE
    }

    enum TipoDado
    {
        INTEIRO, REAL, LOGICO, CADEIA, CARACTER
    }

}

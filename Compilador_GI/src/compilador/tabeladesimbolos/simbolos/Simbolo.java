/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos.simbolos;

/**
 *
 * @author Gabriel
 */
public abstract class Simbolo {

    //tipos estáticos associados aos simbolos
    public static String TIPO_CLASSE = "tipo_classe";
    public static String TIPO_INTEIRO = "tipo_inteiro";
    public static String TIPO_REAL = "tipo_real";
    public static String TIPO_CADEIA = "tipo_cadeia";
    public static String TIPO_CARACTERE = "tipo_caractere";

    protected String id;

    protected String tipoDado;

    public Simbolo(String id, String tipoDado)
    {
        this.id = id;
        this.tipoDado = tipoDado;
    }

    public String getId()
    {
        return id;
    }

    public String getTipoDado()
    {
        return this.tipoDado;
    }

    public boolean ehVetor()
    {
        return tipoDado.substring(tipoDado.length()-2).equals("[]");
    }

}

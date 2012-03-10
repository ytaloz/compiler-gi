/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.semantico;

/**
 *
 * @author Gabriel
 */
public class ErroSemantico {

    private String mensagem;
    private int linha;

    public ErroSemantico(String msg, int linha)
    {
        this.mensagem = msg + "\t" + "linha: " + linha;
        this.linha = linha;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

}

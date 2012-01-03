/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.sintatico;

/**
 *
 * @author Gabriel
 */
public class ErroSintatico {

    private String mensagem;
    private int linha;

    public ErroSintatico(String msg, int linha)
    {
        this.mensagem = msg;
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

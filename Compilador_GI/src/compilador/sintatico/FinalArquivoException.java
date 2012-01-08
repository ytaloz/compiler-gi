/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.sintatico;

/**
 *
 * @author Gabriel
 */
public class FinalArquivoException extends RuntimeException {

    @Override
    public String getMessage() {
        return "final inesperado de arquivo!";
    }
}

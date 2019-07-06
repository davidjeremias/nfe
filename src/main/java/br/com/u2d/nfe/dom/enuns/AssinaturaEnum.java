package br.com.u2d.nfe.dom.enuns;

/**
 * @author David Jeremias - u2dtecnologia@gmail.com
 * Data: 02/03/2019 - 19:55
 */
public enum AssinaturaEnum {

    NFE("NFe","infNFe"),
    INUTILIZACAO("infInut","infInut"),
    EVENTO("evento","infEvento");

    private final String tipo;
    private final String tag;

    AssinaturaEnum(String tipo,String tag) {
        this.tipo = tipo;
        this.tag = tag;
    }

    public String getTipo() {
        return tipo;
    }
    public String getTag() {
        return tag;
    }
}

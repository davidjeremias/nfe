package br.com.u2d.nfe.dom.enuns;

/**
 * @author David Jeremias - u2dtecnologia@gmail.com
 * Data: 02/03/2019 - 22:31
 */
public enum EventosEnum {

    CANCELAMENTO("110111"),
    CANCELAMENTO_SUBSTITUICAO("110112"),
    CCE("110110"),
    MANIFESTACAO("000000"),
    EPEC("110140");

    private final String codigo;

    EventosEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}

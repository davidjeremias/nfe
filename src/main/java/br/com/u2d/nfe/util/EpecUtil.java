package br.com.u2d.nfe.util;

import javax.xml.bind.JAXBException;

import br.com.u2d.nfe.Assinar;
import br.com.u2d.nfe.dom.ConfiguracoesNfe;
import br.com.u2d.nfe.dom.Evento;
import br.com.u2d.nfe.dom.enuns.AssinaturaEnum;
import br.com.u2d.nfe.dom.enuns.EventosEnum;
import br.com.u2d.nfe.exception.NfeException;
import br.com.u2d.nfe.schema.envEpec.*;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

/**
 * @author David Jeremias - u2dtecnologia@gmail.com
 * Data: 02/03/2019 - 22:51
 */
public class EpecUtil {

    /**
     * MOnta o Evento de epec Lote
     *
     * @param epec
     * @param configuracao
     * @return
     * @throws NfeException
     */
    public static TEnvEvento montaEpec(Evento epec, ConfiguracoesNfe configuracao, ZoneId zoneId) throws NfeException {
        return montaEpec(Collections.singletonList(epec),configuracao,zoneId);
    }
    /**
     * MOnta o Evento de epec Lote
     *
     * @param epec
     * @param configuracao
     * @return
     * @throws NfeException
     */
    public static TEnvEvento montaEpec(Evento epec, ConfiguracoesNfe configuracao) throws NfeException {
        return montaEpec(Collections.singletonList(epec),configuracao);
    }
    /**
     * MOnta o Evento de epec Lote
     *
     * @param listaEpec
     * @param configuracao
     * @return
     * @throws NfeException
     */
    public static TEnvEvento montaEpec(List<Evento> listaEpec, ConfiguracoesNfe configuracao) throws NfeException {
        return montaEpec(listaEpec,configuracao,null);
    }
    /**
     * MOnta o Evento de epec Lote
     *
     * @param listaEpec
     * @param configuracao
     * @return
     * @throws NfeException
     */
    public static TEnvEvento montaEpec(List<Evento> listaEpec, ConfiguracoesNfe configuracao, ZoneId zoneId) throws NfeException {


        if (listaEpec.size() > 20) {
            throw new NfeException("Podem ser enviados no máximo 20 eventos no Lote.");
        }

        TEnvEvento enviEvento = new TEnvEvento();
        enviEvento.setVersao(ConstantesUtil.VERSAO.EVENTO_EPEC);
        enviEvento.setIdLote("1");

        listaEpec.forEach(epec -> {

            String id = "ID" + EventosEnum.EPEC.getCodigo() + epec.getChave() + "01";

            TEvento eventoEpec = new TEvento();
            eventoEpec.setVersao(ConstantesUtil.VERSAO.EVENTO_EPEC);

            TEvento.InfEvento infoEvento = new TEvento.InfEvento();
            infoEvento.setId(id);
            infoEvento.setCOrgao(String.valueOf(configuracao.getEstado().getCodigoUF()));
            infoEvento.setTpAmb(configuracao.getAmbiente().getCodigo());
            infoEvento.setCNPJ(epec.getCnpj());
            infoEvento.setChNFe(epec.getChave());
            infoEvento.setDhEvento(XmlNfeUtil.dataNfe(epec.getDataEvento(),zoneId));
            infoEvento.setTpEvento(EventosEnum.EPEC.getCodigo());
            infoEvento.setNSeqEvento("1");
            infoEvento.setVerEvento(ConstantesUtil.VERSAO.EVENTO_EPEC);

            TEvento.InfEvento.DetEvento detEvento = new TEvento.InfEvento.DetEvento();
            detEvento.setVersao(ConstantesUtil.VERSAO.EVENTO_EPEC);
            detEvento.setDescEvento("EPEC");
            detEvento.setCOrgaoAutor(configuracao.getEstado().getCodigoUF());
            detEvento.setTpAutor("1");
            detEvento.setVerAplic("1.0.0");
            detEvento.setDhEmi(XmlNfeUtil.dataNfe(epec.getDataEvento(),zoneId));
            detEvento.setTpNF(epec.getEventoEpec().getTipoNF());
            detEvento.setIE(epec.getEventoEpec().getIeEmitente());

            TEvento.InfEvento.DetEvento.Dest dest = new TEvento.InfEvento.DetEvento.Dest();
            dest.setUF(TUf.valueOf(epec.getEventoEpec().getEstadoDestinatario().toString()));
            dest.setCNPJ(epec.getEventoEpec().getCnpjDestinatario());
            dest.setIE(epec.getEventoEpec().getIeDestinatario());
            dest.setVNF(epec.getEventoEpec().getvNF());
            dest.setVICMS(epec.getEventoEpec().getvICMS());
            dest.setVST(epec.getEventoEpec().getvST());
            detEvento.setDest(dest);

            infoEvento.setDetEvento(detEvento);
            eventoEpec.setInfEvento(infoEvento);

            enviEvento.getEvento().add(eventoEpec);

        });

        return enviEvento;
    }

    /**
     * Cria o ProcEvento de CCe
     *
     * @param config
     * @param enviEvento
     * @param retorno
     * @return
     * @throws JAXBException
     * @throws NfeException
     */
    public static String criaProcEventoEpec(ConfiguracoesNfe config, TEnvEvento enviEvento, TRetEnvEvento retorno) throws JAXBException, NfeException {

        String xml = XmlNfeUtil.objectToXml(enviEvento);
        xml = xml.replaceAll(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
        xml = xml.replaceAll("<evento v", "<evento xmlns=\"http://www.portalfiscal.inf.br/nfe\" v");

        String assinado = Assinar.assinaNfe(ConfiguracoesUtil.iniciaConfiguracoes(config), xml, AssinaturaEnum.EVENTO);

        TProcEvento procEvento = new TProcEvento();
        procEvento.setEvento(XmlNfeUtil.xmlToObject(assinado, TEnvEvento.class).getEvento().get(0));
        procEvento.setRetEvento(retorno.getRetEvento().get(0));
        procEvento.setVersao(ConstantesUtil.VERSAO.EVENTO_EPEC);

        return XmlNfeUtil.objectToXml(procEvento);
    }

}

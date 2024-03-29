package br.com.u2d.nfe;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.transport.http.HTTPConstants;

import br.com.u2d.nfe.dom.ConfiguracoesNfe;
import br.com.u2d.nfe.dom.enuns.DocumentoEnum;
import br.com.u2d.nfe.dom.enuns.ServicosEnum;
import br.com.u2d.nfe.exception.NfeException;
import br.com.u2d.nfe.schema_4.consSitNFe.TConsSitNFe;
import br.com.u2d.nfe.schema_4.retConsSitNFe.TRetConsSitNFe;
import br.com.u2d.nfe.util.*;
import br.com.u2d.nfe.wsdl.NFeConsultaProtocolo.NFeConsultaProtocolo4Stub;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;

/**
 * Classe responsavel por Consultar a Situaçao do XML na SEFAZ.
 *
 * @author David Jeremias - u2dtecnologia@gmail.com - www.swconsultoria.com.br
 */

class ConsultaXml {

    /**
     * Classe Reponsavel Por Consultar o status da NFE na SEFAZ
     *
     * @param chave
     * @param tipoDocumento
     * @return
     * @throws NfeException
     */
    static TRetConsSitNFe consultaXml(ConfiguracoesNfe config, String chave, DocumentoEnum tipoDocumento) throws NfeException {

        try {

            TConsSitNFe consSitNFe = new TConsSitNFe();
            consSitNFe.setVersao(ConstantesUtil.VERSAO.NFE);
            consSitNFe.setTpAmb(config.getAmbiente().getCodigo());
            consSitNFe.setXServ("CONSULTAR");
            consSitNFe.setChNFe(chave);

            String xml = XmlNfeUtil.objectToXml(consSitNFe);

            LoggerUtil.log(ConsultaXml.class,"[XML-ENVIO]: " +xml);

            OMElement ome = AXIOMUtil.stringToOM(xml);

            NFeConsultaProtocolo4Stub.NfeDadosMsg dadosMsg = new NFeConsultaProtocolo4Stub.NfeDadosMsg();
            dadosMsg.setExtraElement(ome);

            NFeConsultaProtocolo4Stub stub = new NFeConsultaProtocolo4Stub(
                    WebServiceUtil.getUrl(config, tipoDocumento, ServicosEnum.CONSULTA_XML));

            // Timeout
            if (ObjetoUtil.verifica(config.getTimeout()).isPresent()) {
                stub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, config.getTimeout());
                stub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT,
                        config.getTimeout());
            }
            NFeConsultaProtocolo4Stub.NfeResultMsg result = stub.nfeConsultaNF(dadosMsg);

            LoggerUtil.log(ConsultaXml.class,"[XML-RETORNO]: " +result.getExtraElement().toString());
            return XmlNfeUtil.xmlToObject(result.getExtraElement().toString(), TRetConsSitNFe.class);

        } catch (RemoteException | XMLStreamException | JAXBException e) {
            throw new NfeException(e.getMessage());
        }

    }

}
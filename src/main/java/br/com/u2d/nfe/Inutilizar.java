package br.com.u2d.nfe;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.transport.http.HTTPConstants;

import br.com.u2d.nfe.dom.ConfiguracoesNfe;
import br.com.u2d.nfe.dom.enuns.AssinaturaEnum;
import br.com.u2d.nfe.dom.enuns.DocumentoEnum;
import br.com.u2d.nfe.dom.enuns.ServicosEnum;
import br.com.u2d.nfe.exception.NfeException;
import br.com.u2d.nfe.schema_4.inutNFe.TInutNFe;
import br.com.u2d.nfe.schema_4.inutNFe.TRetInutNFe;
import br.com.u2d.nfe.util.LoggerUtil;
import br.com.u2d.nfe.util.ObjetoUtil;
import br.com.u2d.nfe.util.WebServiceUtil;
import br.com.u2d.nfe.util.XmlNfeUtil;
import br.com.u2d.nfe.wsdl.NFeInutilizacao.NFeInutilizacao4Stub;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;

/**
 * Classe Responsavel por inutilizar uma Faixa de numeracao da Nfe.
 *
 * @author David Jeremias - u2dtecnologia@gmail.com - www.swconsultoria.com.br
 */
class Inutilizar {

    static TRetInutNFe inutiliza(ConfiguracoesNfe config, TInutNFe inutNFe, DocumentoEnum tipoDocumento, boolean validar)
            throws NfeException {

		try {

			String xml = XmlNfeUtil.objectToXml(inutNFe);
			xml = xml.replaceAll(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
			xml = Assinar.assinaNfe(config, xml, AssinaturaEnum.INUTILIZACAO);

			LoggerUtil.log(Inutilizar.class, "[XML-ENVIO]: " + xml);

			if (validar) {
				new Validar().validaXml(config, xml, ServicosEnum.INUTILIZACAO);
			}

			OMElement ome = AXIOMUtil.stringToOM(xml);

			NFeInutilizacao4Stub.NfeDadosMsg dadosMsg = new NFeInutilizacao4Stub.NfeDadosMsg();
			dadosMsg.setExtraElement(ome);

            NFeInutilizacao4Stub stub = new NFeInutilizacao4Stub(
                    WebServiceUtil.getUrl(config, tipoDocumento, ServicosEnum.INUTILIZACAO));

				// Timeout
				if (ObjetoUtil.verifica(config.getTimeout()).isPresent()) {
					stub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, config.getTimeout());
					stub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, config.getTimeout());
				}
			NFeInutilizacao4Stub.NfeResultMsg result = stub.nfeInutilizacaoNF(dadosMsg);

			LoggerUtil.log(Inutilizar.class, "[XML-RETORNO]: " + result.getExtraElement().toString());
			return XmlNfeUtil.xmlToObject(result.getExtraElement().toString(), TRetInutNFe.class);
		} catch (RemoteException | XMLStreamException | JAXBException e) {
			throw new NfeException(e.getMessage());
		}

	}

}
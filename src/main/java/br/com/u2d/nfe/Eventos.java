package br.com.u2d.nfe;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.transport.http.HTTPConstants;

import br.com.u2d.nfe.dom.ConfiguracoesNfe;
import br.com.u2d.nfe.dom.enuns.AssinaturaEnum;
import br.com.u2d.nfe.dom.enuns.DocumentoEnum;
import br.com.u2d.nfe.dom.enuns.ServicosEnum;
import br.com.u2d.nfe.exception.NfeException;
import br.com.u2d.nfe.util.LoggerUtil;
import br.com.u2d.nfe.util.ObjetoUtil;
import br.com.u2d.nfe.util.WebServiceUtil;
import br.com.u2d.nfe.wsdl.NFeRecepcaoEvento.NFeRecepcaoEvento4Stub;

import javax.xml.stream.XMLStreamException;
import java.rmi.RemoteException;

class Eventos {

    static String enviarEvento(ConfiguracoesNfe config, String xml, ServicosEnum tipoEvento, boolean valida, DocumentoEnum tipoDocumento)
            throws NfeException {

		try {

			xml = Assinar.assinaNfe(config, xml, AssinaturaEnum.EVENTO);

			LoggerUtil.log(Eventos.class, "[XML-ENVIO-" + tipoEvento + "]: " + xml);

			if (valida) {
				new Validar().validaXml(config, xml, tipoEvento);
			}

			OMElement ome = AXIOMUtil.stringToOM(xml);

			NFeRecepcaoEvento4Stub.NfeDadosMsg dadosMsg = new NFeRecepcaoEvento4Stub.NfeDadosMsg();
			dadosMsg.setExtraElement(ome);

			String url = WebServiceUtil.getUrl(config, tipoDocumento, tipoEvento);

            NFeRecepcaoEvento4Stub stub = new NFeRecepcaoEvento4Stub(url);
				// Timeout
				if (ObjetoUtil.verifica(config.getTimeout()).isPresent()) {
					stub._getServiceClient().getOptions().setProperty(HTTPConstants.SO_TIMEOUT, config.getTimeout());
					stub._getServiceClient().getOptions().setProperty(HTTPConstants.CONNECTION_TIMEOUT, config.getTimeout());
				}
			NFeRecepcaoEvento4Stub.NfeResultMsg result = stub.nfeRecepcaoEvento(dadosMsg);

			LoggerUtil.log(Eventos.class, "[XML-RETORNO-" + tipoEvento + "]: " + result.getExtraElement().toString());
			return result.getExtraElement().toString();
		} catch (RemoteException | XMLStreamException e) {
			throw new NfeException(e.getMessage());
		}

	}
}
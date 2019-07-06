package br.com.u2d.nfe;

import javax.xml.bind.JAXBException;

import br.com.u2d.nfe.dom.ConfiguracoesNfe;
import br.com.u2d.nfe.dom.enuns.DocumentoEnum;
import br.com.u2d.nfe.dom.enuns.ServicosEnum;
import br.com.u2d.nfe.exception.NfeException;
import br.com.u2d.nfe.schema.envConfRecebto.TEnvEvento;
import br.com.u2d.nfe.schema.envConfRecebto.TRetEnvEvento;
import br.com.u2d.nfe.util.XmlNfeUtil;

/**
 * @author David Jeremias - u2dtecnologia@gmail.com
 * Data: 28/09/2017 - 11:11
 */
class ManifestacaoDestinatario {

	static TRetEnvEvento eventoManifestacao(ConfiguracoesNfe config, TEnvEvento envEvento , boolean valida) throws NfeException {
		try {

            String xml = XmlNfeUtil.objectToXml(envEvento);
            xml = xml.replaceAll(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
            xml = xml.replaceAll("<evento v", "<evento xmlns=\"http://www.portalfiscal.inf.br/nfe\" v");

            xml = Eventos.enviarEvento(config, xml, ServicosEnum.MANIFESTACAO, valida, DocumentoEnum.NFE);

            return XmlNfeUtil.xmlToObject(xml, TRetEnvEvento.class);

		} catch (JAXBException e) {
			throw new NfeException(e.getMessage());
		}
	}

}


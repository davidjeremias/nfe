package br.com.u2d.nfe.mock;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

import br.com.u2d.nfe.schema_4.retConsStatServ.TConsStatServ;
import br.com.u2d.nfe.schema_4.retConsStatServ.TRetConsStatServ;
import br.com.u2d.nfe.util.ConstantesUtil;
import br.com.u2d.nfe.util.XmlNfeUtil;
import br.com.u2d.nfe.wsdl.NFeStatusServico4.NFeStatusServico4Stub;

import java.time.LocalDateTime;

public class MockStatus {

    public static NFeStatusServico4Stub.NfeResultMsg getNfeResultMsg(NFeStatusServico4Stub.NfeDadosMsg dadosMsg, String cStat, String xMotivo) throws Exception {

        TConsStatServ consStatServ = XmlNfeUtil.xmlToObject(dadosMsg.getExtraElement().toString(), TConsStatServ.class);
        String dh = XmlNfeUtil.dataNfe(LocalDateTime.of(2019, 1, 1, 0, 0));

        TRetConsStatServ retorno = new TRetConsStatServ();
        retorno.setVersao(ConstantesUtil.VERSAO.NFE);
        retorno.setTpAmb(consStatServ.getTpAmb());
        retorno.setVerAplic("GO4.0");
        retorno.setCStat(cStat);
        retorno.setXMotivo(xMotivo);
        retorno.setCUF(consStatServ.getCUF());
        retorno.setDhRecbto(dh);
        retorno.setTMed("1");
        retorno.setDhRetorno(dh);
        String retornoStr = XmlNfeUtil.objectToXml(retorno).replaceAll("<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>", "");

        OMElement om = AXIOMUtil.stringToOM("<nfeResultMsg>" + retornoStr + "</nfeResultMsg>");
        return NFeStatusServico4Stub.NfeResultMsg.Factory.parse(om.getXMLStreamReaderWithoutCaching());
    }
}
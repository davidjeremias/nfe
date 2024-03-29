package br.com.u2d.nfe.util;

import javax.xml.bind.JAXBException;

import br.com.u2d.nfe.Assinar;
import br.com.u2d.nfe.dom.ConfiguracoesNfe;
import br.com.u2d.nfe.dom.enuns.AssinaturaEnum;
import br.com.u2d.nfe.dom.enuns.DocumentoEnum;
import br.com.u2d.nfe.exception.NfeException;
import br.com.u2d.nfe.schema_4.inutNFe.TInutNFe;
import br.com.u2d.nfe.schema_4.inutNFe.TProcInutNFe;
import br.com.u2d.nfe.schema_4.inutNFe.TRetInutNFe;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author David Jeremias - u2dtecnologia@gmail.com - samuk.exe@hotmail.com
 * Data: 02/03/2019 - 22:51
 */
public class InutilizacaoUtil {

    /**
     * Monta o Evento de Inutilização
     * @param tipoDocumento
     * @param cnpj
     * @param serie
     * @param numeroInicial
     * @param numeroFinal
     * @param justificativa
     * @param configuracao
     * @return
     * @throws NfeException
     */
    public static TInutNFe montaInutilizacao(DocumentoEnum tipoDocumento, String cnpj, int serie, int numeroInicial, int numeroFinal, String justificativa, LocalDateTime dataInutilizacao, ConfiguracoesNfe configuracao){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy");
        String cUf = configuracao.getEstado().getCodigoUF();
        String ano = dataInutilizacao.format(formatter);
        String serieCompleta = ChaveUtil.completarComZerosAEsquerda(String.valueOf(serie), 3);
        String nInicial = ChaveUtil.completarComZerosAEsquerda(String.valueOf(numeroInicial), 9);
        String nFinal = ChaveUtil.completarComZerosAEsquerda(String.valueOf(numeroFinal), 9);

        String id = "ID" + cUf + ano + cnpj + tipoDocumento.getModelo() + serieCompleta + nInicial + nFinal;

        TInutNFe inutNFe = new TInutNFe();
        inutNFe.setVersao(ConstantesUtil.VERSAO.INUTILIZACAO);

        TInutNFe.InfInut infInut = new TInutNFe.InfInut();
        infInut.setId(id);
        infInut.setTpAmb(configuracao.getAmbiente().getCodigo());
        infInut.setXServ("INUTILIZAR");
        infInut.setCUF(cUf);
        infInut.setAno(ano);

        infInut.setCNPJ(cnpj);
        infInut.setMod(tipoDocumento.getModelo());
        infInut.setSerie(String.valueOf(serie));

        infInut.setNNFIni(String.valueOf(numeroInicial));
        infInut.setNNFFin(String.valueOf(numeroFinal));

        infInut.setXJust(justificativa);
        inutNFe.setInfInut(infInut);

        return inutNFe;

    }

    /**
     * Cria o ProcEvento da Inutilização
     * @param inutNFe
     * @param retorno
     * @param configuracoesNfe
     * @return
     * @throws JAXBException
     * @throws NfeException
     */
    public static String criaProcInutilizacao(ConfiguracoesNfe configuracoesNfe, TInutNFe inutNFe, TRetInutNFe retorno) throws JAXBException, NfeException {


        String xml = XmlNfeUtil.objectToXml(inutNFe);
        xml = xml.replaceAll(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
        xml = Assinar.assinaNfe(configuracoesNfe, xml, AssinaturaEnum.INUTILIZACAO);

        String assinado = Assinar.assinaNfe(ConfiguracoesUtil.iniciaConfiguracoes(configuracoesNfe), xml, AssinaturaEnum.INUTILIZACAO);

        // Criação do ProcInutNfe
        TProcInutNFe procInutNFe = new TProcInutNFe();
        procInutNFe.setInutNFe(XmlNfeUtil.xmlToObject(assinado, TInutNFe.class));
        procInutNFe.setRetInutNFe(retorno);
        procInutNFe.setVersao(ConstantesUtil.VERSAO.INUTILIZACAO);

        return XmlNfeUtil.objectToXml(procInutNFe);
    }

}

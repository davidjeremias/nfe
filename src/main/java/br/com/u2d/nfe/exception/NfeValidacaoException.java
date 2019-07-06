package br.com.u2d.nfe.exception;

/**
 * Exceção a ser lançada na ocorrência de falhas provenientes da validação da Nota Fiscal Eletronica.
 * 
 * @author David Jeremias - u2dtecnologia@gmail.com
 */
public class NfeValidacaoException extends NfeException {

	private static final long serialVersionUID = 2224963351733125955L;
	String message;
	
	/**
	 * Construtor da classe.
	 * 
	 * @param e
	 */
	public NfeValidacaoException(Throwable e) {
		super(e);
	}

	
	/**
	 * Construtor da classe.
	 * 
	 * @param message
	 */
	public NfeValidacaoException(String message) {
		this((Throwable) null);
		this.message = message;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	
}
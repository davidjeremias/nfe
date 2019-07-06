package br.com.u2d.nfe.exception;

/**
 * Exceção a ser lançada na ocorrência de falhas provenientes da Nota Fiscal Eletronica.
 * 
 * @author David Jeremias - u2dtecnologia@gmail.com
 */
public class NfeException extends Exception {

	private static final long serialVersionUID = -5054900660251852366L;
	
	String message;
	
	/**
	 * Construtor da classe.
	 * 
	 * @param e
	 */
	public NfeException(Throwable e) {
		super(e);
	}

	
	/**
	 * Construtor da classe.
	 * 
	 * @param message
	 */
	public NfeException(String message) {
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
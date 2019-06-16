package ir.justro.commons.code_gen.model.exception;

public class HibernateDocumentLoadException extends Exception {
	private static final long serialVersionUID = 1L;

	public HibernateDocumentLoadException() {
	}

	public HibernateDocumentLoadException(String message) {
		super(message);
	}

	public HibernateDocumentLoadException(Throwable cause) {
		super(cause);
	}

	public HibernateDocumentLoadException(String message, Throwable cause) {
		super(message, cause);
	}

}

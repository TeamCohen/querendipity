package nies.exceptions;

public class BadConfigurationError extends RuntimeException {
	public BadConfigurationError(String msg) {
		super(msg);
	}
}

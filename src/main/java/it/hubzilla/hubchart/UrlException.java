package it.hubzilla.hubchart;

public class UrlException extends Exception {
	private static final long serialVersionUID = 8618269701736198769L;

	private String message;

	public UrlException() {
		super();
		message="";
	}
	
	public UrlException(String message) {
		super(message);
		this.message=message;
	}
	
	public UrlException(String message, Throwable e) {
		super(message, e);
		this.message=message;
	}
	
	public String getMessage() {
		return message;
	}

}

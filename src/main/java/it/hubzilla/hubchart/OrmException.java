package it.hubzilla.hubchart;

public class OrmException extends Exception {
	private static final long serialVersionUID = 8618269701736198769L;

	private String message;

	public OrmException() {
		super();
		message="";
	}
	
	public OrmException(String message) {
		super(message);
		this.message=message;
	}
	
	public OrmException(String message, Throwable e) {
		super(message, e);
		this.message=message;
	}
	
	public String getMessage() {
		return message;
	}

}

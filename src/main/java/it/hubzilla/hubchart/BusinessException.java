package it.hubzilla.hubchart;

public class BusinessException extends Exception {
	private static final long serialVersionUID = 8618269701736198769L;

	private String message;

	public BusinessException() {
		super();
		message="";
	}
	
	public BusinessException(String message) {
		super(message);
		this.message=message;
	}
	
	public BusinessException(String message, Throwable e) {
		super(message, e);
		this.message=message;
	}
	
	public String getMessage() {
		return message;
	}

}

package winflex.util.lifecycle;

public class LifeCycleException extends Exception {

	private static final long serialVersionUID = 3035381941405667208L;

	public LifeCycleException() {
		super();
	}

	public LifeCycleException(String message, Throwable cause) {
		super(message, cause);
	}

	public LifeCycleException(String message) {
		super(message);
	}

	public LifeCycleException(Throwable cause) {
		super(cause);
	}

}

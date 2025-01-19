package com.pinecone.framework.util.id;

public class GuidGenerateException extends RuntimeException {
    private static final long serialVersionUID = -27048199131316992L;

    public GuidGenerateException() {
        super();
    }

    public GuidGenerateException( String message, Throwable cause ) {
        super(message, cause);
    }

    public GuidGenerateException( String message ) {
        super(message);
    }

    public GuidGenerateException( String msgFormat, Object... args ) {
        super(String.format(msgFormat, args));
    }

    public GuidGenerateException( Throwable cause ) {
        super(cause);
    }

}

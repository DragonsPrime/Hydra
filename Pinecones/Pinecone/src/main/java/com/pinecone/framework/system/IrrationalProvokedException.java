package com.pinecone.framework.system;

public class IrrationalProvokedException extends PineRuntimeException {
    protected IrrationalProvokedType irrationalProvokedType;

    public IrrationalProvokedException() {
        this( IrrationalProvokedType.Aberration );
    }

    public IrrationalProvokedException( IrrationalProvokedType type ) {
        super();

        this.irrationalProvokedType = type;
    }

    public IrrationalProvokedException( String message, IrrationalProvokedType type ) {
        super( message );

        this.irrationalProvokedType = type;
    }

    public IrrationalProvokedException( String message ) {
        this( message, IrrationalProvokedType.Aberration );
    }

    public IrrationalProvokedException( String message, Throwable cause, IrrationalProvokedType type ) {
        super( message, cause );

        this.irrationalProvokedType = type;
    }

    public IrrationalProvokedException( String message, Throwable cause ) {
        this( message, cause, IrrationalProvokedType.Aberration );
    }

    public IrrationalProvokedException( Throwable cause, IrrationalProvokedType type ) {
        super(cause);

        this.irrationalProvokedType = type;
    }

    public IrrationalProvokedException( Throwable cause ) {
        this( cause, IrrationalProvokedType.Aberration );
    }

    public IrrationalProvokedType getIrrationalProvokedType() {
        return this.irrationalProvokedType;
    }
}

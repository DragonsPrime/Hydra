package com.pinecone.hydra.umct.husky;

public final class HuskyCTPConstants {

    public static final long HCTP_DUP_CONTROL_MASK               = 0xFFBEA000L;

    public static final long HCTP_DUP_CONTROL_REGISTER           = HCTP_DUP_CONTROL_MASK ^ 0x00000001L;

    public static final long HCTP_DUP_CONTROL_ALIVE              = HCTP_DUP_CONTROL_MASK ^ 0x00000002L;

    public static final long HCTP_DUP_CONTROL_PASSIVE_REQUEST    = HCTP_DUP_CONTROL_MASK ^ 0x00000010L;

    public static final long HCTP_DUP_CONTROL_PASSIVE_RESPONSE   = HCTP_DUP_CONTROL_MASK ^ 0x00000011L;
}

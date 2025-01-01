package com.pinecone.framework.system.executum;

import com.pinecone.framework.system.RuntimeSystem;
import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.system.prototype.Summoner;

import java.util.Map;

public interface TaskManager extends Pinenut, Summoner {
    Processum     getParentProcessum();

    RuntimeSystem getSystem();

    ClassLoader   getClassLoader();

    Map<Integer, VitalResource > getVitalResources();

    void     executeZionSequence(); // No exception

    void     sendApoptosisSignal();

    void     terminate();            // Instant kill all subordinate executums, will no a negotiation.

    void     suspendAll();

    void     resumeAll();

    int      size();

    boolean  isPooled();

    long     getVitalizeCount();

    long     getFatalityCount();

    Executum add  ( Executum that );

    void     erase( Executum that );

    Executum addSync   ( Executum that );

    void     eraseSync ( Executum that );

    void     purge();

    boolean isTerminated();

    // Synchronized currently thread, waiting for all tasks be terminated.
    void syncWaitingTerminated() throws Exception;

    Executum summon         ( String szClassPath, Object... args ) throws Exception ;

    void     kill           ( Executum that );

    void     apoptosis      ( Executum that );

    void     commitSuicide  ( Executum that );

    boolean  autopsy        ( Executum that ); // Check if is dead.

    String   nomenclature   ( Thread   that );

    // Object clearance rate, help load balance and dispatch. [e.g. Using priority queue.]
    default double getClearanceRate() {
        double nFatality = this.getFatalityCount();
        double nVitalize = this.getVitalizeCount();
        return nFatality / ( nVitalize + nFatality );
    }
}

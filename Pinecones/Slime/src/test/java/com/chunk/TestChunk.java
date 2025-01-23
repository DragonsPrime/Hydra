package com.chunk;


import com.pinecone.slime.chunk.marshaling.*;
import com.pinecone.Pinecone;
import com.pinecone.framework.util.Debug;
import com.pinecone.slime.chunk.Chunk;
import com.pinecone.slime.chunk.ContiguousPage;
import com.pinecone.slime.chunk.RangedChunk64;
import com.pinecone.slime.chunk.RangedPage64;
import com.pinecone.slime.chunk.scheduler.DirectPagePool;
import com.pinecone.slime.chunk.scheduler.FixedPageDivider64;
import com.pinecone.slime.chunk.scheduler.LocalBatchActivePageScheduler64;

import java.util.List;


public class TestChunk {
    public static void testPool() {
        RangedPage64   page64   = new RangedPage64( 0, 850,0 );
        DirectPagePool pagePool = new DirectPagePool( RangedPage64.class );

        FixedPageDivider64 divider64 = new FixedPageDivider64( page64, pagePool, 100 );

        Debug.trace( divider64.getMaxAllocations() );

        for ( int i = 0; i < divider64.getMaxAllocations(); i++ ) {
            RangedPage64 page = (RangedPage64) divider64.allocate();
            Debug.trace( page.getRange(), ( (RangedPage64)page.parent() ).getRange(), page.getId(), page.size() );
        }
    }

    public static void testPartition() {
        RangedPage64                      page64       = new RangedPage64( 0, 850,0 );
        DirectPagePool                    pagePool     = new DirectPagePool( RangedPage64.class );
        PreparedPageDividerPartition64 partition64  = new PreparedPageDividerPartition64( page64, 0, 100 );


        FixedPageDivider64 divider64 = new FixedPageDivider64( partition64, pagePool );

        Debug.trace( divider64.getMaxAllocations() );

        for ( int i = 0; i < divider64.getMaxAllocations(); i++ ) {
            RangedPage64 page = (RangedPage64) divider64.allocate();
            Debug.trace( page.getRange(), ( (RangedChunk64)page.parent() ).getRange(), page.getId(), page.size() );
        }

        Debug.trace( partition64 );
    }

    public static void testPartitioner() {
        RangedPage64                      page64        = new RangedPage64( 0, 1000,0 );
        PreparedEvenSeqPagePartitioner64 partitioner64 = new PreparedEvenSeqPagePartitioner64( page64, 5 );

        SequentialPagePartitionGroup64 group64 = partitioner64.partition();
        List<Chunk > l = group64.getSequentialChunks();
        Debug.trace( ( (PreparedPageDividerPartition64)l.get(0)).eachPerPage() );

        BuddyPrepPartitionDividerStrategy64 strategy64 = new BuddyPrepPartitionDividerStrategy64( 100, 2, 1 );
        strategy64.assignment( group64 );

        for ( int i = 0; i < l.size();++ i ) {
            Debug.trace( ((PreparedPageDividerPartition64)l.get(i)).eachPerPage() );
        }
    }

    public static void testPartitionablePageDivider() {
        RangedPage64                      page64        = new RangedPage64( 0, 1000,0 );
        PreparedEvenSeqPagePartitioner64  partitioner64 = new PreparedEvenSeqPagePartitioner64( page64, 6 );

        SequentialPagePartitionGroup64 group64 = partitioner64.partition();
        List<Chunk > l = group64.getSequentialChunks();
        Debug.trace( ( (PreparedPageDividerPartition64)l.get(0)).eachPerPage() );

        BuddyPrepPartitionDividerStrategy64 strategy64 = new BuddyPrepPartitionDividerStrategy64( 100, 2, 1 );
        strategy64.assignment( group64 );


        DirectPagePool pagePool = new DirectPagePool( RangedPage64.class );
        PartitionablePageDivider64 divider64 = new PartitionablePageDivider64( page64, pagePool, group64 );

        Debug.trace( divider64.getMaxAllocations() );

        for ( int i = 0; i < divider64.getMaxAllocations(); i++ ) {
            RangedPage64 page = (RangedPage64) divider64.allocate();
            Debug.trace( page.getRange(), page.getId(), page.size() );
        }
    }

    public static void testSimpleScheduler() {
        RangedPage64   page64   = new RangedPage64( 0, 850,0 );
        DirectPagePool pagePool = new DirectPagePool( RangedPage64.class );

        LocalBatchActivePageScheduler64 scheduler64 = new LocalBatchActivePageScheduler64( new FixedPageDivider64( page64, pagePool, 100 ), page64.getId() + 1, 4 );
        ContiguousPage[] pages = scheduler64.activates();

        Debug.trace( scheduler64.getDivider().getMaxAllocations() );
        for ( int i = 0; i < pages.length; i++ ) {
            Debug.trace( pages[i].getRange(), pages[i].getId() );
        }

        //scheduler64.deactivate( pages[1] );
        scheduler64.deactivate( pages );
        Debug.hhf();

        pages = scheduler64.activates();
        for ( int i = 0; i < pages.length; i++ ) {
            Debug.trace( pages[i].getRange(), pages[i].getId() );
        }

        scheduler64.deactivate( pages );
        Debug.hhf();

        pages = scheduler64.activates();
        for ( int i = 0; i < pages.length; i++ ) {
            Debug.trace( pages[i].getRange(), pages[i].getId() );
        }
    }


    public static void main( String[] args ) throws Exception {
        //String szJson = FileUtils.readAll("J:/120KWordsPhonetics.json5");
        Pinecone.init( (Object...cfg )->{

            //TestBasicTransaction.testSequential();
            //TestBasicTransaction.testParallel();
            //TestChunk.testPool();
            TestChunk.testSimpleScheduler();
            //TestChunk.testPartition();
            //TestChunk.testPartitioner();
            //TestChunk.testPartitionablePageDivider();



//            double factor = 0.2;
//            int page = 1000;
//            int stratum = 2;
//            int start = 0;
//            int end = 10000;
//
//            splitInterval(start, end, factor, page, stratum);

            return 0;
        }, (Object[]) args );
    }


    public static void splitInterval(int start, int end, double factor, int page, int stratum) {
        int totalRange = end - start;
        int subIntervalSize = (int) (totalRange * factor);
        int numSubIntervals = (int) (1 / factor);

        for (int i = 0; i < numSubIntervals; i++) {
            int subStart = start + i * subIntervalSize;
            int subEnd = Math.min(subStart + subIntervalSize, end);
            splitSubInterval(subStart, subEnd, page, stratum);
            page = Math.max(10, page / stratum); // Update the page size for the next interval
        }
    }

    public static void splitSubInterval(int start, int end, int page, int stratum) {
        int currentStart = start;

        while (currentStart < end) {
            int currentEnd = Math.min(currentStart + page, end);
            System.out.println(currentStart + ", " + currentEnd);
            currentStart = currentEnd;
        }
    }
}


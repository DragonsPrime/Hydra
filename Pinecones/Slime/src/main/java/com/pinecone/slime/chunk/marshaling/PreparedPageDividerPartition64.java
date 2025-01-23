package com.pinecone.slime.chunk.marshaling;

import com.pinecone.slime.chunk.RangedChunk64;
import com.pinecone.slime.chunk.RangedPage;
import com.pinecone.slime.unitization.MinMaxRange;
import com.pinecone.slime.chunk.ContiguousPage;

public class PreparedPageDividerPartition64 extends RangedChunk64 implements PageDividerPartition64 {
    protected long     mnPagesSize;
    protected long     mnEachPerPage;

    public PreparedPageDividerPartition64( long nStart, long nEnd, long id, long each, RangedChunk64 parent ) {
        super( nStart, nEnd, id, parent );

        this.mnEachPerPage = each;
        this.update_page_size();
    }

    public PreparedPageDividerPartition64(ContiguousPage inheritedIntegratedPage, long id, long each, RangedChunk64 parent ) {
        this( 0, 0, id, each, parent );

        this.inheritRange( inheritedIntegratedPage );
        this.update_page_size();
    }

    public PreparedPageDividerPartition64(ContiguousPage inheritedIntegratedPage, long id, long each ) {
        this( inheritedIntegratedPage, id, each, null );
    }

    protected void update_page_size() {
        this.mnPagesSize   = (this.getRange().span() + this.mnEachPerPage - 1) / this.mnEachPerPage;
    }

    @Override
    public long pagesSize() {
        return this.mnPagesSize;
    }

    @Override
    public long eachPerPage() {
        return this.mnEachPerPage;
    }

    @Override
    public void setEachPerPage( long eachPerPage ) {
        this.mnEachPerPage = eachPerPage;
    }

    @Override
    public void inheritRange( ContiguousPage that ) {
        MinMaxRange range      = (MinMaxRange) that.getRange();
        this.mRange.setRange( range.getMin(), range.getMax() );
    }

    @Override
    public PageDividerPartition64 parent() {
        return (PageDividerPartition64)this.mParent;
    }

    @Override
    public boolean hasOwnPage( ContiguousPage that ) {
        RangedPage rangedPage = (RangedPage) that;
        return this.getRange().contains( rangedPage.getRange() );
    }
}

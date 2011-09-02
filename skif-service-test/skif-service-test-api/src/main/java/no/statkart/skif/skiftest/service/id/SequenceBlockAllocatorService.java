package no.statkart.skif.skiftest.service.id;

/**
 * Interface that sequence generators must implement. Enables the caller to obtain unique sequence ids.
 *
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface SequenceBlockAllocatorService {

    /**
     * Allocates a block of sequences numbers from a specified sequence. The method executes in a separate transaction.
     * The <code>sequenceName</code> is implementation specific. Typically implemenations would use table names or class names.
     * An implementation that only supports a single sequence would ignore the <code>sequenceName</code>
     *
     * @param sequenceName the name of the sequence that the number is drawn from.
     * @param blockSize    the size of the block allocated
     * @return the last free sequence number in the block allocated (allokert sekvens er: [returnvalue-blockSize+1, returnvalue])
     */
    public long allocateSequenceBlock(String sequenceName, int blockSize);

}

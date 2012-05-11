package no.statkart.skif.service.sequence;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Denne klassen implementerer <code>IdService</code>, se dokumentasjon på {@link IdService denne}
 *
 * @author Henrik Fredholm
 */
@Singleton
public class IdServiceImpl implements IdService {
    protected Logger logger = LoggerFactory.getLogger(IdServiceImpl.class);

    private final SequenceBlockAllocatorService sequenceBlockAllocatorService;

    @Inject
    public IdServiceImpl(SequenceBlockAllocatorService sequenceBlockAllocatorService) {
        this.sequenceBlockAllocatorService = sequenceBlockAllocatorService;
    }

    private class Entry {
        long lastUsed;
        long last;
    };

    private Map<String, Entry> sequences = new HashMap();
    private int blockSize = 10;

    public <T extends BubbleId<?>> T getNextId(Class<T> idClass) {
        return BubbleIds.createInstance(idClass,getNextValue(idClass), SnapshotVersion.CURRENT);
    }

    public  synchronized <T extends BubbleId<?>> Object getNextValue(Class<T> aClass) {
        long value = 0;
        String sequenceName = getSequenceName(aClass);
        Entry entry = (Entry) sequences.get(sequenceName);

        if (entry == null) {
            // add an entry to the sequence table
            entry = new Entry();
            sequences.put(sequenceName, entry);
        }
        if (entry.lastUsed == entry.last) {
            entry.last = sequenceBlockAllocatorService.allocateSequenceBlock(sequenceName, blockSize);
            entry.lastUsed = entry.last - blockSize;
        }
        entry.lastUsed += 1;
        value = entry.lastUsed;
        if (logger.isDebugEnabled()) logger.debug("Allocating id for class" + aClass.getName() + " Id=" + value);
        return new Long(value);
    }

    /**
     * Angir hvilke sekvensnavn som skal brukes for gitt {@code BubbleId} klasse.
     * <p/>
     * Default implementasjonen bruker samme sekvensnavn, {@code GLOBAL_SEQUENCE} for alle klasser
     */
    protected <T extends BubbleId<?>> String getSequenceName(Class<T> idClass) {
        String sequenceName = "GLOBAL_SEQUENCE";
        if (logger.isDebugEnabled()) logger.debug("Sekvensnavn for " + idClass.getName() + " er " + sequenceName);

        return sequenceName;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        if (blockSize <= 0)
            throw new RuntimeException("Blocksize for sequence block allocation must be positive: " + blockSize);
        this.blockSize = blockSize;
    }

    public synchronized void clear() {
        logger.debug("Clearing cached sequences");
        sequences = new HashMap();

    }
}

package no.statkart.skif.storetest.domain.tinglysing;

/**
 * @author Knut Inge Bøe
 */
public class PrioritetIkkeTinglystDokumentVeketFor extends PrioritetIkkeTinglystDokument {
    @Override
    public PrioritetIkkeTinglystDokumentVeketForId<?> getId() {
        return (PrioritetIkkeTinglystDokumentVeketForId<?>) super.getId();
    }
}

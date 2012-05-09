package no.statkart.skif.storetest.domain.tinglysing;

/**
 * @author Knut Inge Bøe
 */
public class PrioritetIkkeTinglystDokumentSidestiltMed extends PrioritetIkkeTinglystDokument {
    @Override
    public PrioritetIkkeTinglystDokumentSidestiltMedId<?> getId() {
        return (PrioritetIkkeTinglystDokumentSidestiltMedId<?>) super.getId();
    }
}
package no.statkart.skif.storetest.service.histtest;

import no.statkart.skif.service.annotation.ServiceContextMapped;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.domain.basic.HistWithRelationId;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service som tester ut kodestandard for metoder som skal fungere riktig med historikk. Utfordringen er at bubbleid-er
 * i java api-et inneholder snapshotversion mens at wsapi har en eksplisitt context parameter som inneholder
 * snapshotversion. Dette gjør at det ikke er en en-til-en match mellom java api og wsapi og det er nødvendig med en
 * mapping regel for metodekall.
 * <p/>
 * Følgende regel brukes for mapping av java api metoder til wsapi metoder
 * <ul>
 *     <li>Hvis ingen av metodens parameter er av type  {@code BubbleId}, collection av {@code BubbleId} eller
 *     {@code SnapshotVersion} eller hvis alle slike paramtre er annotert med {@code @SuppressSnapshotVersionMapping}
 *     så mappes metoden til wsapi-metode med samme parametre plus context hvor snapshotversion i context er satt
 *     til {@code SnapshotVersion.CURRENT} TODO: vurdere å ta verdi fra client context istedet
 *     </li>
 *     <li>Hvis metoden har nettopp en parameter av type {@code SnapshotVersion} og denne står sist i parameterlisten
 *     så mappes metoden til wsapi-metode med samme parameter minus snapshotversion parametren og plus contekst
 *     parameteren. SnapshotVersion i context parameteren settes til snapshotversion fra parameteren som ikke mappes.
 *     </li>
 *     <li>Hvis metoden har nettopp en parameter av type collection av type {@code BubbleId} og ingen
 *     parametre av type {@code SnapshotVersion} som ikke er annotert med {@code @SuppressSnapshotVersionMapping} så mappes kallet til
 *     wsapi-metode med samme parametre plus context hvor snapshotversion bestemmes av utfra id-ene i listen ut fra
 *     følgende regel: hvis listen ikke er tom må alle id ha samme snapshotversion og verdi fra første element anvendes.
 *     Hvis listen er tom brukes {@code SnapshotVersion.CURRENT} TODO: vurdere å ta verdi fra client context istedet
 *     </li>
 *     <li>Hvis metoden har en eller flere parametre av type {@code BubbleId} som ikke er annotert med
 *     {@code @SuppressSnapshotVersionMapping} og alle andre parameter av type collection av {@code BubbleId} og
 *     {@code SnapshotVersion} er annotert med {@code @SuppressSnapshotVersionMapping} så mappes kallet til
 *     wsapi-metode med samme parametre plus context hvor snapshotversion bestemmes av utfra første parameter av
 *     type {@code BubbleId} som ikke er annotert med {@code @IgnoreShapshotVersion}.
 *     </li>
 *     <li>
 *         Hivs ingen av ovenstående regler kan anvendes kan metoden ikke mappes og det kastes en exception.
 *     </li>
 * </ul>
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public interface HistTestService {

    /**
     * Finner {@code HistSimple} objekter med text lik {@code text} hørende til mockup testsett {@code testsettNumber}
     * for gitt tidspunkt {@code snapshotVersion}.
     * <p/>
     * Denne metode har en eksplisitt SnapshotVersion parameter som siste parameter som styrer hvilken snapshotVersion
     * som brukes for søket.
     *
     * @param text
     * @param testsettNummer
     * @param snapshotVersion
     * @return id-er på objekter som ble funnet. Id-er har SnapshotVersion {@code snapshotVersion}
     */
    Set<HistSimpleId<?>> findHistSimpleIdsForTextUsingJDBC(String text, int testsettNummer, @ServiceContextMapped SnapshotVersion snapshotVersion);

    /**
     * Finner {@code HistSimple} objekter med text lik {@code text} hørende til mockup testsett {@code testsettNumber}
     * for gitt tidspunkt {@code snapshotVersion}.
     * <p/>
     * Samme funksjonalitet som {@link #findHistSimpleIdsForTextUsingJDBC} bare implementert via hibernate
     * <p/>
     * Denne metode har en eksplisitt SnapshotVersion parameter som siste parameter som styrer hvilken snapshotVersion
     * som brukes for søket.
     *
     * @param text
     * @param testsettNummer
     * @param snapshotVersion
     * @return id-er på objekter som ble funnet. Id-er  har SnapshotVersion {@code snapshotVersion}
     */
    Set<HistSimpleId<?>> findHistSimpleIdsForTextUsingHibernate(String text, int testsettNummer, @ServiceContextMapped SnapshotVersion snapshotVersion);

    /**
     * Finner {@code HistWithRelation} objekter som peker på et {@code HistSimple} objekt som har text lik {@code text} hørende til
     * mockup testsett {@code testsettNumber} for gitt tidspunkt {@code snapshotVersion}
     * <p/>
     * Denne metode har en eksplisitt SnapshotVersion parameter som siste parameter som styrer hvilken snapshotVersion
     * som brukes for søket.
     *
     * @param text
     * @param testsettNummer
     * @return id-er på objekter som ble funnet. Id-er  har SnapshotVersion {@code snapshotVersion}
     */
    Set<HistWithRelationId<?>> findHistWithRelationIdsRelatedToHistSimpleWithText(String text, int testsettNummer, SnapshotVersion snapshotVersion);

    /**
     * Finner {@code HistWithRelation} objekter som har text lik {@code text} hørende til mockup testsett {@code testsettNumber}
     * og som er relatert til et {@code HistSimple} objekt med id lik {@code histSimpleId} for gitt tidspunkt {@code snapshotVersion}.
     * Fordi {@code histSimpleId} kan  være null må metoden ha en eksplisitt SnapshotVersion parameter som siste argument.
     * <p/>
     * Denne metode har en eksplisitt SnapshotVersion parameter som siste parameter som styrer hvilken snapshotVersion
     * som brukes for søket. Dette er nødvendig siden {@code histSimpleId} kan være null og vi ønsker å kunne angi
     * SnapshotVersion for søket. SnapshotVersion i {@code histSimpleId} ignoreres.
     *
     * @param text
     * @param histSimpleId
     * @param snapshotVersion
     * @return id-er på objekter som ble funnet. Id-er har SnapshotVersion {@code snapshotVersion}
     */
    Set<HistWithRelationId<?>> findHistWithRelationIdsWithTextRelatedToHistSimpleId(String text, @Nullable HistSimpleId<?> histSimpleId, SnapshotVersion snapshotVersion);

    /**
     * Finner {@code HistWithRelation} objekter som har text lik {@code text} hørende til mockup testsett {@code testsettNumber}
     * og som er relatert til et {@code HistSimple} objekt med id lik {@code histSimpleIds}.
     * <p/>
     * Denne metode har ikke en eksplisitt SnapshotVersion parameter fordi snapthotversion kan bestemmes ut fra  {@code histSimpleIds}
     *
     * @param text
     * @param histSimpleIds
     * @param snapshotVersion
     * @return id-er på objekter som ble funnet. Id-er har SnapshotVersion {@code snapshotVersion}
     */
    Map<HistSimpleId<?>, Set<HistWithRelationId<?>>> findHistWithRelationIdsWithTextRelatedToHistSimpleIds(String text, Collection<HistSimpleId<?>> histSimpleIds, @Deprecated SnapshotVersion snapshotVersion);

    /**
     * Finner id-ene til alle HistSimple objekter i {@code histSimpleIds} og som eksisterte på gitt snapshot-tidspunkt.
     * For denne metoden angis kan snapshotVersion eksplisitt.
     *
     * @param histSimpleIds   alle ids må ha samme SnapshotVersion
     * @param snapshotVersion
     * @return
     * @since 2.1
     */
    List<HistSimpleId<?>> findHistSimpleIdsAliveAtSnapshotUsingOracleArray(Collection<HistSimpleId<?>> histSimpleIds, SnapshotVersion snapshotVersion);

}

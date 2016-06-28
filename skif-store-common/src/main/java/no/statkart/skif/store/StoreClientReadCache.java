package no.statkart.skif.store;

import com.google.common.collect.ImmutableMap;
import com.google.inject.ImplementedBy;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * En trådsikker read cache som kan deles mellom flere Store instanser på klienten slik at bobler ikke
 * lastes på nytt fra server hvis boblen allerede er lastet av en Store instans som deler samme read cache. Read
 * cachen antar at bobler som legges inn i cachen ikke endres, slik at det ikke er nødvendig å ta en ekstra kopi ifm
 * innleggelse i cachen. Innleggelse av bobler i cachen har dermed lav overhead. Hvis en Store instans skal endre i en
 * boblen så forventes det at Store instansen selv lager en kopi til bruk for editeringen. Bobleinstanser i
 * cachen danner utgangspunkt for nye kopier som cachen gir ut når det spørs etter bobler i cachen. Read cachen gir
 * aldri ut samme instans som har blitt lagt inn, da en bobleinstans kun kan tilhøre en Store instans.
 *
 * TODO: Implementere evict konsistens.
 * Ved evict bør read cachen garantere at bobler som gis ut er lastet etter at evict ble kallt. Dvs objekter som har
 * påbegynt lasting før evict ble kallt legges ikke inn. For å få dette til må read cachen implementere en evict
 * barrier (for eksempel teller som telles opp ved evict) som må angis når det legges objekter inn i cachen.
 *
 *
 */
@ImplementedBy(StoreClientReadCacheImpl.class)
public interface StoreClientReadCache {
    public void put(BubbleObject bubbleObject);

    void putAll(Collection<? extends BubbleObject> objects);

    @Nullable
    <T extends BubbleObject> T get(@Nullable BubbleId<? extends T> id);

    public ImmutableMap<BubbleId<?>, BubbleObject> getAll(Iterable<?> ids);

    public void evict(@Nullable BubbleId<?> id);

    public void evictAll();

}

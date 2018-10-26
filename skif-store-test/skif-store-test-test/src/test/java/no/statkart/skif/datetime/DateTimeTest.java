package no.statkart.skif.datetime;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.storetest.domain.basic.BubbleWithLocalDate;
import no.statkart.skif.storetest.domain.basic.BubbleWithLocalDateId;
import no.statkart.skif.storetest.domain.basic.BubbleWithLocalDateTime;
import no.statkart.skif.storetest.domain.basic.BubbleWithLocalDateTimeId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.service.store.StoreService;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.assertj.core.api.Assertions;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.testng.annotations.Test;

/**
 * Tester {@link no.statkart.skif.persistence.hibernate.type.OraclePersistentLocalDate} og
 * {@link no.statkart.skif.persistence.hibernate.type.OraclePersistentLocalDateTime}.
 *
 * @author Tor Egil R. Strand
 * @since 2.5.0
 */
@Test(groups = "singlevm-required")
public class DateTimeTest extends StoreTestTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Inject
    private StoreService storeService;

    @Inject
    private StoreUpdateService storeUpdateService;

    public void testLocalDate() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

        BubbleWithLocalDateId<?> bubbleWithLocalDate1Id = mockupFacade.getIdService().getNextId(BubbleWithLocalDateId.class);
        BubbleWithLocalDateId<?> bubbleWithLocalDate2Id = mockupFacade.getIdService().getNextId(BubbleWithLocalDateId.class);

        BubbleWithLocalDate bubbleWithLocalDate1Org = new BubbleWithLocalDate();
        bubbleWithLocalDate1Org.setId(bubbleWithLocalDate1Id);
        bubbleWithLocalDate1Org.setDato(new LocalDate(1999, 6, 7));
        BubbleWithLocalDate bubbleWithLocalDate2Org = new BubbleWithLocalDate();
        bubbleWithLocalDate2Org.setId(bubbleWithLocalDate2Id);
        bubbleWithLocalDate2Org.setDato(new LocalDate(1, 1, 1));

        UnitOfWorkTransfer transfer = new UnitOfWorkTransfer(ImmutableList.of(bubbleWithLocalDate1Org, bubbleWithLocalDate2Org), ImmutableList.<BubbleObject>of(), ImmutableList.<BubbleObject>of());
        storeUpdateService.saveTransfer(transfer);

        BubbleWithLocalDate bubbleWithLocalDate1Read = storeService.getObject(bubbleWithLocalDate1Id);
        BubbleWithLocalDate bubbleWithLocalDate2Read = storeService.getObject(bubbleWithLocalDate2Id);

        Assertions.assertThat(bubbleWithLocalDate1Read.getDato()).isEqualTo(bubbleWithLocalDate1Org.getDato());
        Assertions.assertThat(bubbleWithLocalDate2Read.getDato()).isEqualTo(bubbleWithLocalDate2Org.getDato());
    }

    public void testLocalDateTime() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getEmptyMockupFacade();

        BubbleWithLocalDateTimeId<?> bubbleWithLocalDateTime1Id = mockupFacade.getIdService().getNextId(BubbleWithLocalDateTimeId.class);
        BubbleWithLocalDateTimeId<?> bubbleWithLocalDateTime2Id = mockupFacade.getIdService().getNextId(BubbleWithLocalDateTimeId.class);

        BubbleWithLocalDateTime bubbleWithLocalDateTime1Org = new BubbleWithLocalDateTime();
        bubbleWithLocalDateTime1Org.setId(bubbleWithLocalDateTime1Id);
        bubbleWithLocalDateTime1Org.setTidspunkt(new LocalDateTime(1999, 6, 7, 8, 9, 10, 999));
        BubbleWithLocalDateTime bubbleWithLocalDateTime2Org = new BubbleWithLocalDateTime();
        bubbleWithLocalDateTime2Org.setId(bubbleWithLocalDateTime2Id);
        bubbleWithLocalDateTime2Org.setTidspunkt(new LocalDateTime(1, 1, 1, 3, 4, 5, 123));

        UnitOfWorkTransfer transfer = new UnitOfWorkTransfer(ImmutableList.of(bubbleWithLocalDateTime1Org, bubbleWithLocalDateTime2Org), ImmutableList.<BubbleObject>of(), ImmutableList.<BubbleObject>of());
        storeUpdateService.saveTransfer(transfer);

        BubbleWithLocalDateTime bubbleWithLocalDateTime1Read = storeService.getObject(bubbleWithLocalDateTime1Id);
        BubbleWithLocalDateTime bubbleWithLocalDateTime2Read = storeService.getObject(bubbleWithLocalDateTime2Id);

        Assertions.assertThat(bubbleWithLocalDateTime1Read.getTidspunkt()).isEqualTo(bubbleWithLocalDateTime1Org.getTidspunkt());
        Assertions.assertThat(bubbleWithLocalDateTime2Read.getTidspunkt()).isEqualTo(bubbleWithLocalDateTime2Org.getTidspunkt());
    }
}

package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.AbstractEntityComponent;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;

import java.util.Date;

/**
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
public class BubbleWithListComponent extends AbstractEntityComponent {
    private Long id;
    private String componentName;
    private BubbleWithList bubbleWithList;
    private AEnumKodeId aEnumKodeId;
    private Date oppdateringsdato;
    private Date sluttdato;
    private long versjonId;

    public Date getOppdateringsdato() {
        return oppdateringsdato;
    }

    public void setOppdateringsdato(Date oppdateringsdato) {
        this.oppdateringsdato = oppdateringsdato;
    }

    public Date getSluttdato() {
        return sluttdato;
    }

    public void setSluttdato(Date sluttdato) {
        this.sluttdato = sluttdato;
    }

    public long getVersjonId() {
        return versjonId;
    }

    public void setVersjonId(long versjonId) {
        this.versjonId = versjonId;
    }

    public AEnumKodeId getaEnumKodeId() {
        return aEnumKodeId;
    }

    public void setaEnumKodeId(AEnumKodeId aEnumKodeId) {
        this.aEnumKodeId = aEnumKodeId;
    }

    public BubbleWithList getBubbleWithList() {
        return bubbleWithList;
    }

    public void setBubbleWithList(BubbleWithList bubbleWithList) {
        this.bubbleWithList = bubbleWithList;
    }

    @SuppressWarnings("unused") // Hibernate
    public BubbleWithListComponent() {
    }

    public BubbleWithListComponent(Long id, String componentName, AEnumKodeId aEnumKodeId) {
        this.id = id;
        this.componentName = componentName;
        this.aEnumKodeId = aEnumKodeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}

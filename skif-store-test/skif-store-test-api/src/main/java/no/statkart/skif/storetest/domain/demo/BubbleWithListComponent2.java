package no.statkart.skif.storetest.domain.demo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Roar Ingebrigtsen
 * @since 3.0
 */
public class BubbleWithListComponent2 implements Serializable{

    private Long id;
    private String componentName;
    private BubbleWithList bubbleWithList;
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

    public BubbleWithList getBubbleWithList() {
        return bubbleWithList;
    }

    public void setBubbleWithList(BubbleWithList bubbleWithList) {
        this.bubbleWithList = bubbleWithList;
    }

    public BubbleWithListComponent2() {
    }

    public BubbleWithListComponent2(Long id, String componentName) {
        this.id = id;
        this.componentName = componentName;
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

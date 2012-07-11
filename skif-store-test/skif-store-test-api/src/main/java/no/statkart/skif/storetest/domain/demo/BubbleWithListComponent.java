package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Roar Ingebrigtsen
 * @since 2.1
 */
public class BubbleWithListComponent implements Serializable{
    private Long id;
    private String componentName;
    private BubbleWithList bubbleWithList;
    private AEnumKodeId aEnumKodeId;
    private Date tBegin;
    private Date tEnd;
    private long tVersion;

    public Date gettBegin() {
        return tBegin;
    }

    public void settBegin(Date tBegin) {
        this.tBegin = tBegin;
    }

    public Date gettEnd() {
        return tEnd;
    }

    public void settEnd(Date tEnd) {
        this.tEnd = tEnd;
    }

    public long gettVersion() {
        return tVersion;
    }

    public void settVersion(long tVersion) {
        this.tVersion = tVersion;
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

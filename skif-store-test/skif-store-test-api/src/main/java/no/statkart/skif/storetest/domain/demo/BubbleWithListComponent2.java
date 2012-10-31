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

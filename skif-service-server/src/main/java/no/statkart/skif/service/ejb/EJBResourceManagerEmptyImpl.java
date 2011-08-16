package no.statkart.skif.service.ejb;

/**
 * @author Henrik Fredholm
 */
public class EJBResourceManagerEmptyImpl implements EJBResourceManager{
    @Override
    public void complete() {
        System.out.println("complete");
    }

    @Override
    public void abort() {
        System.out.println("abort");
    }
}

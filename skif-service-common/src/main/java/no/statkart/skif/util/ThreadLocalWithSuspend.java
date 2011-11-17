package no.statkart.skif.util;

import java.util.Stack;

/**
 * A <code>ThreadLocal</code> class that supports supend and resume operation.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ThreadLocalWithSuspend<T> extends ThreadLocal<T> {
    private ThreadLocalStack<T> suspended = new ThreadLocalStack<T>();


    /**
     * Suspends the object associated with this <code>ThreadLocal</code> by placing
     * it in an internal threadlocal stack. Sets the current object of this
     * <code>ThreadLocal</code> to null.
     */
    public void suspend() {
        T current = get();
        suspended.getStack().push(current);
        set(null);
    }

    /**
     * Associates a previously suspended object with this <code>ThreadLocal</code>.
     *
     * @throws IllegalStateException         if the current value of this <code>ThreadLocal</code> is not null.
     * @throws java.util.EmptyStackException if the threadlocal stack of suspended objects is empty.
     */
    public void resumeSuspended() {
        if (get() != null)
            throw new IllegalStateException("Cannot resume suspended ThreadLocal. ThreadLocal is not null.");
        T newCurrent = suspended.getStack().pop();
        set(newCurrent);
    }

    /**
     * Returns the number of suspended objects for this thread.
     *
     * @return the number of suspended objects for this thred.
     */
    public int size() {
        return suspended.getStack().size();
    }

    /**
     * A <code>ThreadLocal</code> which contains a stack as initial value.
     */
    private static class ThreadLocalStack<T> extends ThreadLocal<Stack<T>> {
        @Override
        protected Stack<T> initialValue() {
            return new Stack<T>();
        }

        public Stack<T> getStack() {
            return get();
        }
    }
}

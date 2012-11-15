package no.statkart.skif.store;

/**
 * Facade to singleton MemeoryLocker. Each instance of MemoryLockerFacade will access the same MemoryLocker.
 * @author  Henrik Fredholm
 */
public class MemoryLockerSingleton {
   private static MemoryLocker instance = new MemoryLocker();

   public static MemoryLocker getInstance() {
      return instance;
   }

   private MemoryLockerSingleton() {
   }
}


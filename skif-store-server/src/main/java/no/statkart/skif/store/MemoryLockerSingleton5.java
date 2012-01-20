package no.statkart.skif.store;

/**
 * Facade to singleton MemeoryLocker. Each instance of MemoryLockerFacade will access the same MemoryLocker.
 * @author  Henrik Fredholm
 */
public class MemoryLockerSingleton5 {
   private static MemoryLocker5 instance = new MemoryLocker5();

   public static LockerService5 getInstance() {
      return instance;
   }

   private MemoryLockerSingleton5() {
   }
}


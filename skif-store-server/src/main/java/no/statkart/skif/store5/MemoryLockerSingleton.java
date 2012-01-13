package no.statkart.skif.store5;

/**
 * Facade to singleton MemeoryLocker. Each instance of MemoryLockerFacade will access the same MemoryLocker.
 * @author  Henrik Fredholm
 */
public class MemoryLockerSingleton  {
   private static MemoryLocker instance = new MemoryLocker();

   public static LockerService getInstance() {
      return instance;
   }

   private MemoryLockerSingleton() {
   }
}


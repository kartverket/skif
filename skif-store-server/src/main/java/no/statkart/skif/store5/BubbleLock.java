package no.statkart.skif.store5;

import no.statkart.skif.store.BubbleId;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Henrik Fredholm
 */
public class BubbleLock implements Serializable {
   /** Id of locked object */
   private BubbleId id;
   /** Owner of the lock */
   private String key;
   /** When the lock expires and may be taken by other users*/
   private Timestamp expires;

   /** True if the lock must released on transaction rollback*/
   private boolean isNew;

   /** Ident string for ident hvis den finnes ellers null. */
   private String identString;

   public BubbleLock(BubbleId id, String key) {
      this(id, key, null, false);
   }

   public BubbleLock(BubbleId id, String key, Timestamp expires, boolean isNew) {
      this.id = id;
      this.key = key;
      this.expires = expires;
      this.isNew  = isNew;
   }

   public String getIdentString() {
      return identString;
   }

   public void setIdentString(String identString) {
      this.identString = identString;
   }

   public BubbleId getId() {
      return id;
   }

   public void setId(BubbleId id) {
      this.id = id;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public boolean isOwnedBy(String key) {
      return this.key.equals(key);
   }

   public boolean expired() {
      return System.currentTimeMillis() > expires.getTime();
   }

   public boolean expiresBefore(long milliseconds) {
      return System.currentTimeMillis() + milliseconds > expires.getTime();
   }
   public Timestamp getExpires() {
      return expires;
   }

   public boolean isNew() {
      return isNew;
   }

   public String toString() {
      return "Id=" + id + " identString=" + identString + " Lockedby=" + key + " Expires=" + expires;
   }
}

# Hibernate 6 Upgrade Progress

This file summarizes the work done so far to move the codebase from Hibernate 5 to 6.0.2.Final. It focuses on the intent and reasoning behind each change, not just the diff.

## Completed Changes

1) Flush-time collection owner fix for composite components
- What changed: Added a dedicated flush listener that ensures composite-component collections resolve their owner correctly, including fallbacks when the owner is null, and avoiding entity components.
- Why: Hibernate 6 is stricter about collection ownership and can throw PropertyAccessException during flush for composite components. The listener repairs ownership deterministically.
- Why this is good: It keeps ownership resolution centralized and avoids sprinkling workarounds across entity models or mappers.

2) Safer subtype change detection for entity components
- What changed: Subtype checks now resolve proxy classes consistently, and the discriminator check falls back to a stateless lookup when the discriminator column lookup fails. This prevents a stale update from escaping into flush.
- Why: Hibernate 6 returns different proxy types and behaves differently around loaded-state comparisons. The previous checks could miss a subtype mismatch and fail later with a stale state error.
- Why this is good: The change restores explicit, early validation and aligns with the expected behavior of "Attempted to change class" errors.

3) Update path avoids unsafe flushes on attached instances
- What changed: The update flow now flushes only when the instance is truly detached and the session is a Hibernate SessionImpl.
- Why: Hibernate 6 is more sensitive to flushing attached state; extra flushes can surface constraint issues and orphan handling regressions.
- Why this is good: It narrows flush to the intended detached-update use case, reducing side effects on attached state.

4) Oracle ARRAY support for object-array element types
- What changed: Oracle ARRAY creation now uses `OracleConnection.createStruct` for object elements and `createOracleArray` for the ARRAY itself, and the AnyBubbleId converter uses the NUMBER_STRING list type with numeric IDs.
- Why: The older `STRUCT/ARRAY` constructors are deprecated and can produce mismatched element typing under Hibernate 6; AnyBubbleId values are stored as NUMBER in Oracle.
- Why this is good: It aligns with Oracle’s current JDBC APIs and fixes array element typing to match the schema, which should restore object-array query matches.

5) Ensure embeddable-backed fields rehydrate lazily
- What changed: `BubbleWithAnyBubbleRef` now reconstructs `anyId`/`someIdent` from embeddables on access, and reconstructs embeddables from `anyId`/`someIdent` on access when they are missing.
- Why: Hibernate 6 can bypass setters during hydration and transfers may serialize without embedded fields, leaving only the transient fields populated.
- Why this is good: It keeps persisted columns and in-memory state in sync without changing mappings or tests, fixing nulls in both read and write paths.

6) Collection snapshot initialization for detached updates
- What changed: When attaching persistent collections, uninitialized snapshots are now initialized before snapshot reuse.
- Why: Hibernate 6 can keep collection snapshots uninitialized longer; reusing an uninitialized snapshot can lead to incorrect insert/delete diffs.
- Why this is good: It stabilizes collection update behavior during detached updates, especially for sets.

7) Test support adjustments to avoid Hibernate 6 metadata pitfalls
- What changed: A test-only Hibernate setting was added to avoid JDBC metadata lookups when the test is using mocked infrastructure.
- Why: Hibernate 6 does more metadata introspection; this avoids “no JDBC connection” failures in tests that do not require a live DB.
- Why this is good: It keeps tests focused on behavior rather than infrastructure.

## Known Failures (Need Fixes)

These were reported after the last full test run:
- `OracleArrayTest.testOracleArrayAnyBubbleIdConverter`
- `OracleArrayTest.testOracleArrayConcatenatedFieldsConverter`
- `OracleArrayTest.testOracleArrayStringStringConverter`
- `SubtypedEntityComponentTest.updateWithSubtypeChangeIncorrectUse`
- `StoreEvictAndDeleteBubbleWithComponentlistTest.testLockAndDeleteObject`
- `StoreTest.testHentBubbleViaAnyBubbleRef`

Notes:
- Oracle array tests currently return empty result sets, which points to mismatched ARRAY element construction or SQL type bindings.
- The subtype-change test still throws stale update errors, which means the subtype mismatch is not being surfaced early enough in this path.
- The lock/delete test fails with a unique constraint violation in the join table during flush, likely due to collection snapshot state or duplicate insert/delete ordering.
- The any-bubble reference test returns null for `anyId`, likely due to mapping or lazy hydration differences in Hibernate 6.

## Next Steps

1) Fix Oracle ARRAY element binding and re-run `OracleArrayTest`.
2) Ensure subtype mismatches always throw before flush (no stale state errors).
3) Resolve collection snapshot/state issues causing duplicate key violations during delete.
4) Confirm `BubbleWithAnyBubbleRef` reads back `anyId` correctly in Store tests.

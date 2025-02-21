/**
 * Test domeneklasser for standalone tester som ikke brukes i StoreServerModule og som ikke bruker mockup-rammeverket
 * for opprettelse av testobjekter.
 *
 * <P>Hibernate konfigureres via {@code StandAloneTestHelper}. Predefinerte testobjekter opprettes via
 * sql, hvor id på objekter er mindre eller lik 100. Dynamisk opprettet objekter slettes automatisk før
 * testmetode kalles ved at alle objekter i domenet med id > 100 slettes.
 */
package no.statkart.skif.storetest.domain.standalone;
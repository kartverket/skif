-- Script for manuell opprettelse av brukere i SKIF sikkerhetsskjema basert på dokumentasjon fra SKIF-394.
ALTER SESSION SET CURRENT_SCHEMA = "SKIF_USERS";

INSERT INTO "USERS" VALUES ('admin', '{Sha-1}Rqk6y/Woa5q+Xcoejfprtu1cgaowe5s=', 'Initielt Opprettet Administrator');
INSERT INTO "USERS" VALUES ('testUser', '{SHA-1}j7QeGMBydbR5qvpVd2psUKXvqTdc4tI=',	'test user for SKIF tester. Har rolle Innsyn.'); -- password test66User

INSERT INTO "GROUPS" VALUES ('bruker', 'Laveste felles nevner i Grunnboken');
INSERT INTO "GROUPS" VALUES ('Innsyn', 'Laveste felles nevner i SKIF'); -- Kreves av SKIF

INSERT INTO "GROUPMEMBERS" VALUES ('bruker', 'admin');
INSERT INTO "GROUPMEMBERS" VALUES ('bruker', 'testUser'); --Kreves av grunnbok
INSERT INTO "GROUPMEMBERS" VALUES ('Innsyn', 'testUser'); --Kreves av SKIF
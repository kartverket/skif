CREATE TABLE FOO (
    id                   NUMBER(19,0) NOT NULL ENABLE,
    nr                   number(10,0),
    navn                 VARCHAR2(255 BYTE),
    PRIMARY KEY (ID)
);

CREATE TABLE BAR (
    id                   NUMBER(19,0) NOT NULL ENABLE,
    husnr                number(10,0),
    bokstav              VARCHAR2(255 BYTE),
    fooId                number(19,0) not null,
    bazId                number(19,0),
    PRIMARY KEY (ID)
);

CREATE TABLE Baz (
    id                   NUMBER(19,0) NOT NULL ENABLE,
    text                 VARCHAR2(255 BYTE),
    fooId                number(19,0) not null,
    testAEnumKodeId      number(10,0) not null,
    testC2DbKodeId       number(19,0) not null,
    PRIMARY KEY (id)
);
CREATE OR REPLACE PACKAGE SNAPSHOT_TIME
AS
    FUNCTION Get_T_CURRENT Return TIMESTAMP;
    FUNCTION Get_T RETURN TIMESTAMP;
    FUNCTION Set_T(newValue IN TIMESTAMP) RETURN TIMESTAMP;
    FUNCTION Get_T_Trans RETURN TIMESTAMP;
    FUNCTION To_T(timestampAsString IN VARCHAR2) RETURN TIMESTAMP;
    FUNCTION T_Between(t_Begin IN TIMESTAMP, t_End IN TIMESTAMP) RETURN NUMBER;
END SNAPSHOT_TIME;
/

CREATE OR REPLACE PACKAGE BODY SNAPSHOT_TIME
As
    -- Betegner timestamp for gjeldende snapshot versjon av objektet. Kan betegnes som den versjonen som er 'levende'. Settes til tEnd på levende snapshot.
    t_Current TIMESTAMP := SNAPSHOT_TIME.To_T('9999-01-01 00:00:00.00');

    -- initialiserer t slik at man får oppdaterte data i views som standard (se T_Between())
    t TIMESTAMP := t_Current;

    t_Trans TIMESTAMP;

    FUNCTION Get_T_CURRENT
    RETURN TIMESTAMP
    IS
    BEGIN
      RETURN t_Current;
    END Get_T_CURRENT;

    FUNCTION Get_T
    RETURN TIMESTAMP
    IS
    BEGIN
      RETURN t;
    END Get_T;

    FUNCTION Set_T(newValue IN TIMESTAMP)
    RETURN TIMESTAMP
    IS
    BEGIN
      t:= newValue;
      RETURN t;
    END Set_T;

    FUNCTION Get_T_Trans
    RETURN TIMESTAMP
    IS
    tVal TIMESTAMP;
    BEGIN
      BEGIN
        SELECT v INTO t_Trans FROM SNAPSHOT_TRANS;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
           t_Trans := NULL;
       END;
       IF t_Trans IS NULL THEN
         t_Trans := LOCALTIMESTAMP;
         INSERT INTO SNAPSHOT_TRANS VALUES(t_Trans);
       END IF;
      RETURN t_Trans;
    END Get_T_Trans;

    FUNCTION To_T(timestampAsString IN VARCHAR2)
    RETURN TIMESTAMP
    IS
    BEGIN
      RETURN To_Timestamp (timestampAsString, 'YYYY-MM-DD HH24:MI:SS.FF');
    END To_T;

    FUNCTION T_Between(t_Begin IN TIMESTAMP, t_End IN TIMESTAMP)
    RETURN NUMBER
    IS
    retVal NUMBER;
    BEGIN
                IF (t_Begin<=t AND (t<t_End OR t_End=t_Current))
                THEN
                    retVal := 1;
                ELSE
                    retVal := 0;
                END IF;
                RETURN retVal;
    END T_Between;
END SNAPSHOT_TIME;
/
CREATE OR REPLACE PACKAGE SNAPSHOT_TIME
AS
    FUNCTION Get_T_CURRENT Return TIMESTAMP WITH LOCAL TIME ZONE;
    FUNCTION Get_T RETURN TIMESTAMP WITH LOCAL TIME ZONE;
    FUNCTION Set_T(newValue IN TIMESTAMP WITH LOCAL TIME ZONE) RETURN TIMESTAMP WITH LOCAL TIME ZONE;
    FUNCTION Get_T_Trans RETURN TIMESTAMP WITH LOCAL TIME ZONE;
    FUNCTION To_T(timestampAsString IN VARCHAR2) RETURN TIMESTAMP WITH LOCAL TIME ZONE;
    FUNCTION T_Between(t_Begin IN TIMESTAMP WITH LOCAL TIME ZONE, t_End IN TIMESTAMP WITH LOCAL TIME ZONE) RETURN NUMBER;
END SNAPSHOT_TIME;
/

CREATE OR REPLACE PACKAGE BODY SNAPSHOT_TIME
As
    -- Betegner timestamp for gjeldende snapshot versjon av objektet. Kan betegnes som den versjonen som er 'levende'. Settes til sluttdato på levende snapshot.
    t_Current TIMESTAMP WITH LOCAL TIME ZONE := SNAPSHOT_TIME.To_T('9999-01-01 00:00:00.00 +01:00');

    -- initialiserer t slik at man får oppdaterte data i views som standard (se T_Between())
    t TIMESTAMP WITH LOCAL TIME ZONE := t_Current;

    t_Trans TIMESTAMP WITH LOCAL TIME ZONE;

    FUNCTION Get_T_CURRENT
    RETURN TIMESTAMP WITH LOCAL TIME ZONE
    IS
    BEGIN
      RETURN t_Current;
    END Get_T_CURRENT;

    FUNCTION Get_T
    RETURN TIMESTAMP WITH LOCAL TIME ZONE
    IS
    BEGIN
      RETURN t;
    END Get_T;

    FUNCTION Set_T(newValue IN TIMESTAMP WITH LOCAL TIME ZONE)
    RETURN TIMESTAMP WITH LOCAL TIME ZONE
    IS
    BEGIN
      t:= newValue;
      RETURN t;
    END Set_T;

    FUNCTION Get_T_Trans
    RETURN TIMESTAMP WITH LOCAL TIME ZONE
    IS
    tVal TIMESTAMP WITH LOCAL TIME ZONE;
    BEGIN
      BEGIN
        SELECT v INTO t_Trans FROM SNAPSHOT_TRANS;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
           t_Trans := NULL;
       END;
       IF t_Trans IS NULL THEN
         t_Trans := SYSTIMESTAMP;
         INSERT INTO SNAPSHOT_TRANS VALUES(t_Trans);
       END IF;
      RETURN t_Trans;
    END Get_T_Trans;

    FUNCTION To_T(timestampAsString IN VARCHAR2)
    RETURN TIMESTAMP WITH LOCAL TIME ZONE
    IS
    BEGIN
      RETURN TO_TIMESTAMP_TZ (timestampAsString, 'YYYY-MM-DD HH24:MI:SS.FF TZR TZD');
    END To_T;

    FUNCTION T_Between(t_Begin IN TIMESTAMP WITH LOCAL TIME ZONE, t_End IN TIMESTAMP WITH LOCAL TIME ZONE)
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
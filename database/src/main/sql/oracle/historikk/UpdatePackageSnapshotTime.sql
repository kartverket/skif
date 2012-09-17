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
    T_CURRENT TIMESTAMP := SNAPSHOT_TIME.to_t('9999-01-01 00:00:00.00');

    t TIMESTAMP;
    t_Trans TIMESTAMP := T_CURRENT;

    FUNCTION Get_T_CURRENT
    RETURN TIMESTAMP
    IS
    BEGIN
      RETURN T_CURRENT;
    End Get_T_CURRENT;

    FUNCTION Get_T
    RETURN TIMESTAMP
    IS
    BEGIN
      RETURN t;
    End Get_T;

    FUNCTION Set_T(newValue IN TIMESTAMP)
    RETURN TIMESTAMP
    IS
    Begin
      t:= newValue;
      RETURN t;
    End Set_T;

    FUNCTION Get_T_Trans
    RETURN TIMESTAMP
    IS
    tVal TIMESTAMP;
    BEGIN
      BEGIN
        select v into t_Trans from SNAPSHOT_TRANS;
        exception
        when NO_DATA_FOUND THEN
           t_Trans := NULL;
       END;
       IF t_Trans is NULL THEN
         t_Trans := LOCALTIMESTAMP;
         insert into SNAPSHOT_TRANS values(t_Trans);
       END IF;
      RETURN t_Trans;
    End Get_T_Trans;

    FUNCTION To_T(timestampAsString IN VARCHAR2)
    RETURN TIMESTAMP
    Is
    Begin
      RETURN To_Timestamp (timestampAsString, 'YYYY-MM-DD HH24:MI:SS.FF');
    end To_T;

    FUNCTION T_Between(t_Begin IN TIMESTAMP, t_End IN TIMESTAMP)
    RETURN NUMBER
    Is
    retVal NUMBER;
    BEGIN
                IF (t_Begin<=T AND (T<t_End OR t_End=T_CURRENT))
                THEN
                    retVal := 1;
                ELSE
                    retVal := 0;
                END IF;
                RETURN retVal;
    End T_Between;
END SNAPSHOT_TIME;
/
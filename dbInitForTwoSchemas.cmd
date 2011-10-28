call setenv.cmd

call gradle -Pdb_username=system -Pdb_password=matrikkeladm dbInitAsSystem 

call gradle -Pdb_username=HANSTE_HIST -Pdb_password=HANSTE_HIST dbInitAsHistUser

call gradle dbInit


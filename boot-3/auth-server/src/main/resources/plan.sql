-- explain plan for SELECT
--                      s.*
--                  FROM
--                      ADVANCED_OAUTH2_CLIENT   c
--                     ,ADVANCED_OAUTH2_CLIENT_SCOPE_LIST             cs
--                     ,ADVANCED_OAUTH2_SCOPE_LIST         scl
--                     ,ADVANCED_OAUTH2_SCOPE_LIST_SCOPE   slsc
--                     ,ADVANCED_OAUTH2_SCOPE                s
--                  WHERE
--                          c.pk_id=cs.CLIENT_PK_ID
--                    and cs.SCOPE_LIST_PK_ID = scl.pk_id
--                    AND scl.pk_id = slsc.SCOPE_LIST_PK_ID
--                    AND slsc.SCOPE_PK_ID = s.pk_id
--                    AND c.client_id= '1110400054';
-- /
-- SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY());
-- /
-- BEGIN
--     FOR t IN (SELECT table_name FROM all_tables WHERE owner = 'mbp_load') LOOP
--             DBMS_STATS.GATHER_TABLE_STATS(ownname => 'mbp_load', tabname => t.table_name);
--         END LOOP;
-- END;
-- /
-- BEGIN
--     FOR idx IN (SELECT owner, index_name, table_name FROM all_indexes WHERE owner = 'mbp_load') LOOP
--             DBMS_STATS.GATHER_INDEX_STATS(
--                     ownname => idx.owner,
--                     indname => idx.index_name
--                 );
--         END LOOP;
-- END;

-- with 10 M data
-- Plan hash value: 4274213708
--
------------------------------------------------------------------------------------------------------------------------
-- | Id  | Operation                              | Name                              | Rows  | Bytes | Cost (%CPU)| Time     |
----------------------------------------------------------------------------------------------------------------------
-- |   0 | SELECT STATEMENT                       |                                   |     4 |   460 |    10   (0)| 00:00:01 |
-- |   1 |  NESTED LOOPS                          |                                   |     4 |   460 |    10   (0)| 00:00:01 |
-- |   2 |   MERGE JOIN CARTESIAN                 |                                   |     6 |   654 |    10   (0)| 00:00:01 |
-- |   3 |    NESTED LOOPS                        |                                   |     2 |   176 |     7   (0)| 00:00:01 |
-- |   4 |     TABLE ACCESS BY INDEX ROWID        | ADVANCED_OAUTH2_CLIENT            |     1 |    48 |     3   (0)| 00:00:01 |
-- |*  5 |      INDEX UNIQUE SCAN                 | IDX_1                             |     1 |       |     2   (0)| 00:00:01 |
-- |   6 |     TABLE ACCESS BY INDEX ROWID BATCHED| ADVANCED_OAUTH2_CLIENT_SCOPE_LIST |     2 |    80 |     5   (0)| 00:00:01 |
-- |*  7 |      INDEX RANGE SCAN                  | IDX_2                             |     2 |       |     3   (0)| 00:00:01 |
-- |   8 |    BUFFER SORT                         |                                   |     3 |    63 |     5   (0)| 00:00:01 |
-- |   9 |     TABLE ACCESS FULL                  | ADVANCED_OAUTH2_SCOPE             |     3 |    63 |     2   (0)| 00:00:01 |
-- |* 10 |   INDEX UNIQUE SCAN                    | IDX_6                             |     1 |     6 |     0   (0)| 00:00:01 |
--------------------------------------------------------------------------------------------------------------------------
--
-- Predicate Information (identified by operation id):
-------------------------------------------------
--
-- "   5 - access(""C"".""CLIENT_ID""='1110400054')"
-- "   7 - access(""C"".""PK_ID""=""CS"".""CLIENT_PK_ID"")"
-- "  10 - access(""CS"".""SCOPE_LIST_PK_ID""=""SLSC"".""SCOPE_LIST_PK_ID"" AND ""SLSC"".""SCOPE_PK_ID""=""S"".""PK_ID"")"
--
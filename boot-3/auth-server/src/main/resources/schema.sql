create table ADVANCED_OAUTH2_CLIENT
(
    PK_ID                         NUMBER(19) not null
        primary key,
    TOKEN_EXPIRE_SECONDS          NUMBER(19),
    AUTHORIZATION_GRANT_TYPES     VARCHAR2(1000 char),
    CLIENT_AUTHENTICATION_METHODS VARCHAR2(1000 char),
    CLIENT_ID                     VARCHAR2(100 char),
    CLIENT_NAME                   VARCHAR2(200 char),
    CLIENT_SECRET                 VARCHAR2(200 char),
    ENABLED                       NUMBER(1)
        check (enabled in (0, 1)),
    REFRESH_EXPIRE_SECONDS        NUMBER(19)
)
/
create table ADVANCED_OAUTH2_SCOPE_LIST
(
    PK_ID NUMBER(19) not null
        primary key,
    NAME  VARCHAR2(100 char)
        constraint TBL_OAUTH2_SCOPE_LIST_COL_NAME_UNQ_IDX
            unique
)
/
create table ADVANCED_OAUTH2_SCOPE
(
    PK_ID       NUMBER(19) not null
        primary key,
    NAME        VARCHAR2(100 char),
    URL_PATTERN VARCHAR2(2000 char)
)
/
create table ADVANCED_OAUTH2_CLIENT_SCOPE_LIST
(
    CLIENT_PK_ID     NUMBER(19) not null
        constraint FK_TBL_ADVANCED_OAUTH2_CLIENT_COL_PK_ID
            references ADVANCED_OAUTH2_CLIENT,
    SCOPE_LIST_PK_ID NUMBER(19) not null
        constraint FK_TBL_ADVANCED_OAUTH2_SCOPE_LIST_COL_PK_ID
            references ADVANCED_OAUTH2_SCOPE_LIST
)
/
create table ADVANCED_OAUTH2_SCOPE_LIST_SCOPE
(
    SCOPE_LIST_PK_ID NUMBER(19) not null
        constraint FK_ADVANCED_OAUTH2_SCOPE_LIST_COL_PK_ID
            references ADVANCED_OAUTH2_SCOPE_LIST,
    SCOPE_PK_ID      NUMBER(19) not null
        constraint FK_ADVANCED_OAUTH2_SCOPE_COL_PK_ID
            references ADVANCED_OAUTH2_SCOPE
)
/

create unique index TBL_ADVANCED_OAUTH2_CLIENT_COL_CLIENT_ID_UNQ_IDX
    on ADVANCED_OAUTH2_CLIENT (CLIENT_ID)
/
create unique index TBL_SCOPE_COL_NAME_UNQ_IDX
    on ADVANCED_OAUTH2_SCOPE (NAME)
/

create index TBL_ADVANCED_OAUTH2_CLIENT_SCOPE_LIST_COL_CLIENT_PK_ID_IDX
    on ADVANCED_OAUTH2_CLIENT_SCOPE_LIST (CLIENT_PK_ID)
/
create bitmap index TBL_ADVANCED_OAUTH2_CLIENT_SCOPE_LIST_COL_SCOPE_LIST_PK_ID_BT_IDX
    on ADVANCED_OAUTH2_CLIENT_SCOPE_LIST (SCOPE_LIST_PK_ID)
/
create unique index TBL_ADVANCED_OAUTH2_CLIENT_SCOPE_LIST_COL_ALL_UNQ_IDX
    on ADVANCED_OAUTH2_CLIENT_SCOPE_LIST (SCOPE_LIST_PK_ID, CLIENT_PK_ID)
/
create unique index TBL_ADVANCED_OAUTH2_SCOPE_LIST_SCOPE_COL_ALL_UNQ_IDX
    on ADVANCED_OAUTH2_SCOPE_LIST_SCOPE (SCOPE_LIST_PK_ID, SCOPE_PK_ID)
/
create index TBL_ADVANCED_OAUTH2_SCOPE_LIST_SCOPE_COL_SCOPE_LIST_PK_ID_IDX
    on ADVANCED_OAUTH2_SCOPE_LIST_SCOPE (SCOPE_LIST_PK_ID)
/
create index TBL_ADVANCED_OAUTH2_SCOPE_LIST_SCOPE_COL_SCOPE_PK_ID_IDX
    on ADVANCED_OAUTH2_SCOPE_LIST_SCOPE (SCOPE_PK_ID)
/
CREATE SEQUENCE ADVANCED_CLIENT_SEQ INCREMENT BY 1 START WITH 100 CACHE 20000;
/
CREATE SEQUENCE ADVANCED_SCOPE_SEQ INCREMENT BY 1 START WITH 100 CACHE 20000;

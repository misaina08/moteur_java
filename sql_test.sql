/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     30/07/2016 19:48:05                          */
/*==============================================================*/


create sequence seq_cours;

create sequence seq_prof;

/*==============================================================*/
/* Table: cours                                                 */
/*==============================================================*/
create table cours  (
   id                   varchar2(10)                       not null,
   intitule             varchar2(50),
   constraint pk_cours primary key (id)
);

/*==============================================================*/
/* Table: coursprof                                             */
/*==============================================================*/
create table coursprof  (
   idcours              varchar2(10)                       not null,
   idemp                varchar2(10)                      not null,
   datecours            date,
   heuredebut           date,
   heurefin             date,
   constraint pk_coursprof primary key (idcours, idemp)
);

/*==============================================================*/
/* Index: association_1_fk                                      */
/*==============================================================*/
create index association_1_fk on coursprof (
   idcours asc
);

/*==============================================================*/
/* Index: association_2_fk                                      */
/*==============================================================*/
create index association_2_fk on coursprof (
   idemp asc
);

/*==============================================================*/
/* Table: prof                                                  */
/*==============================================================*/
create table prof  (
   idemp                varchar2(10)                      not null,
   nom                  varchar2(30),
   sal                  float,
   conge                smallint,
   constraint pk_prof primary key (idemp)
);

alter table coursprof
   add constraint fk_courspro_associati_cours foreign key (idcours)
      references cours (id);

alter table coursprof
   add constraint fk_courspro_associati_prof foreign key (idemp)
      references prof (idemp);


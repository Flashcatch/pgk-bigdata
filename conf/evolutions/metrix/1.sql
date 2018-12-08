# --- Init structure

# --- !Ups
create table if not exists freight
(
  key serial not null
    constraint freight_pk
    primary key,
  name text,
  class text,
  "group" bigint,
  etsng text,
  gng text,
  etsng6 text
);
create table if not exists station
(
  station_id	serial not null
    constraint station_pk
    primary key,
  name	text,
  esr6	text,
  rw_id	bigint,
  cn_id	bigint,
  dp_id	bigint,
  beg_date	timestamp,
  end_date timestamp
);
create table if not exists si_stat_indicator
(
  stat_indicator_id	serial not null
    constraint si_stat_indicator_pk
    primary key,
  mnemo	text,
  note	text
);
create table if not exists si_grouping_set_elem
(
  grouping_set_id bigint,
  attribute_list_id bigint
);
create table if not exists si_grouping_set
(
  grouping_set_id	serial not null
    constraint si_grouping_set_pk
    primary key,
  stat_indicator_id	bigint,
  str	text,
  level_val	bigint,
  sql_select text
);
create table if not exists si_calculation
(
  grouping_set_id	bigint,
  year_month	text,
  snd_cn_id	bigint,
  rsv_cn_id	bigint,
  snd_dp_id	bigint,
  rsv_dp_id	bigint,
  snd_rw_id	bigint,
  rsv_rw_id	bigint,
  snd_st_id	bigint,
  rsv_st_id	bigint,
  snd_org_id	bigint,
  rsv_org_id	bigint,
  fr_id	bigint,
  fr_group_id	bigint,
  isload	bigint,
  rod_id	bigint,
  send_kind_id	bigint,
  park_sign	bigint,
  route_send_sign	bigint,
  model_property_id	bigint,
  st_id	bigint,
  client_id	bigint,
  vid_podgotovki	text,
  vid_zabrakovki	bigint,
  is_tech_st	bigint,
  agg_median	float
);
create table if not exists attribute_list
(
  attribute_list_id	serial not null
    constraint si_attribute_list_pk
    primary key,
  mnemo	text,
  sql_calc_name	text
);
# --- !Downs
drop table if exists freight;
drop table if exists station;
drop table if exists si_stat_indicator;
drop table if exists si_grouping_set_elem;
drop table if exists si_grouping_set;
drop table if exists si_calculation;
drop table if exists attribute_list;
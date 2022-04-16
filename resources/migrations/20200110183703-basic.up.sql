-- :disable-transaction
create table if not exists "public"."company" (
"id" uuid not null,
"name" text not null,
"address" text,
"phone" text not null,
"incorp_date" date not null,
"website" text not null,
"email" text not null,
"password" text not null,
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());
--;;
create table if not exists "public"."doc_type" (
"id" text not null,
"name" text not null);
--;;
create table if not exists "public"."company_doc" (
"id" uuid not null,
"company_id" uuid not null,
"doc_type_id" text not null,
"filename" text not null,
"url" text not null,
"version" integer not null default 0,
"date_created" timestamp not null default now(),
"last_updated" timestamp not null default now());

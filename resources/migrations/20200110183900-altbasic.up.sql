-- :disable-transaction
alter table "public"."company" add constraint "pk_com_id" primary key ("id");
--;;
alter table "public"."company" add constraint "uq_com_name" unique ("name");
--;;
alter table "public"."company" add constraint "uq_com_email" unique ("email");
--;;
alter table "public"."doc_type" add constraint "pk_doc_type_id" primary key ("id");
--;;
alter table "public"."doc_type" add constraint "uk_doc_type_name" unique ("name");
--;;
alter table "public"."company_doc" add constraint "pk_com_doc_id" primary key ("id");
--;;
alter table "public"."company_doc" add constraint "fk_com_doc_com_id" foreign key ("company_id") references "public"."company" ("id") match full;
--;;
alter table "public"."company_doc" add constraint "fk_com_doc_doc_type" foreign key ("doc_type_id") references "public"."doc_type" ("id") match full;
--;;
alter table "public"."company_doc" add constraint "uq_com_doc_url" unique ("url");

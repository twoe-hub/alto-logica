insert into "public"."company" ("id", "name", "address", "phone", "incorp_date", "website", "email", "password") values
('fde78682-419e-423f-bdc6-32f48a7be64a', 'ABC Incorp.', 'Karachi, Pakistan', '92-21-31111111', '2001-01-15', 'abc.com.pk', 'info@abc.com.pk', 'bcrypt+sha512$831449f081553f767a0d6b9d1c0ee316$12$28df12eab139d770682d0dbedd6274df67ecc380b56f1f88'),
('fde78682-419e-423f-bdc6-32f48a7be64b', 'XYZ Incorp.', 'Lahore, Pakistan', '92-42-31111111', '2011-01-15', 'xyz.com.pk', 'info@xyz.com.pk', 'bcrypt+sha512$831449f081553f767a0d6b9d1c0ee316$12$28df12eab139d770682d0dbedd6274df67ecc380b56f1f88'),
('fde78682-419e-423f-bdc6-32f48a7be64c', 'PQR Incorp.', 'Islamabad, Pakistan', '92-51-31111111', '2016-01-15', 'pqr.com.pk', 'info@pqr.com.pk', 'bcrypt+sha512$831449f081553f767a0d6b9d1c0ee316$12$28df12eab139d770682d0dbedd6274df67ecc380b56f1f88');
--;;
insert into "public"."doc_type" ("id", "name") values
('INCORP_DOC', 'Incorporation Docs'),
('SSL_CERT', 'SSL Certificate'),
('COPY_OWN_ID', 'Copy of Owner ID');
--;;
insert into "public".company_doc ("id", "company_id", "doc_type_id", "filename", "url") values
('e8685bc3-e0f9-4a64-963a-0b8bb5594da0', 'fde78682-419e-423f-bdc6-32f48a7be64a', 'INCORP_DOC', 'incorp-doc.pdf', '#1'),
('87fe5c16-9e8e-4142-aecd-2b26110e7c7d', 'fde78682-419e-423f-bdc6-32f48a7be64a', 'SSL_CERT', 'ssl-cert.pem', '#2'),
('c7ae8681-832f-46eb-8a0c-e7816f6dd412', 'fde78682-419e-423f-bdc6-32f48a7be64a', 'COPY_OWN_ID', 'copy-owner-id.png', '#3');

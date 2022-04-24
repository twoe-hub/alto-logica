-- :name auth! :? :1
-- :doc select user for authentication
select c.email, c.password
from public.company c
where c.email = :email

-- :name get-user :? :1
-- :doc select user
select c.*
from public.company c
where c.email = :email

-- :name get-company :? :1
-- :doc selects company info
select c.email, c.name, c.address, c.phone, c.incorp_date, c.website
from public.company c
where c.email = :email

-- :name create-company! :! :n
-- :doc creates a company, along with the user
insert into public.company (id, email, password, name, address, phone, website, incorp_date) values
(uuid_generate_v4(), :email, :password, :name, :address, :phone, :website, :incorp-date)

-- :name update-company! :! :n
-- :doc updates an existing company info
update public.company
set name = :name, address = :address, phone = :phone, incorp_date = :incorp-date, website = :website
where email = :email

-- :name get-docs :? :*
-- :doc selects docs by email
select doc.id, doc.filename, doc.url, tp.id, tp.name
from public.company_doc doc
join public.company com on com.id = doc.company_id
join public.doc_type tp on tp.id = doc.doc_type_id
where com.email = :email

-- :name create-doc! :! :n
-- :doc creates a given doc for the given company
insert into public.company_doc (id, company_id, doc_type_id, filename, url) values
(:id, :company-id, :doc-type-id, :filename, :url)

-- :name update-doc! :! :n
-- :doc creates a given doc for the given company
update public.company_doc
set url = :uri, filename = :filename
where doc_type_id = :doc-type-id
and company_id = :company-id

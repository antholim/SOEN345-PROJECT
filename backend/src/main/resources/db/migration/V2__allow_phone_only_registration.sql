alter table users
    alter column email drop not null;

alter table users
    drop constraint if exists chk_users_contact;

alter table users
    add constraint chk_users_contact
    check (
        nullif(btrim(coalesce(email, '')), '') is not null
        or nullif(btrim(coalesce(phone_number, '')), '') is not null
    );

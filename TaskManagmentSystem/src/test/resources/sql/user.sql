TRUNCATE TABLE t_user CASCADE;
TRUNCATE TABLE user_role CASCADE;

insert into t_user
values ('34783ace-f846-475e-b6d7-5e27c2ce8a3e', 'AVAILABLE', '$2a$10$4sUIiJDpZZM9EPlOkiF49ek4Xfh2BI4dUXO6FLFUx3T5CrJgLEVda',
        'test2@mail.ru');
insert into user_role
values ('34783ace-f846-475e-b6d7-5e27c2ce8a3e', 'USER');
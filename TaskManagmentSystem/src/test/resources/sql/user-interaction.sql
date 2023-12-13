truncate table t_user CASCADE;
truncate table t_task CASCADE;

insert into t_user
values ('b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'AVAILABLE',
        '$2a$10$LwH5ntW8utSRIdUtXsM2ZuOcNev.5IDUkjR1PBqQBgMSjSrD4JJpm', 'test3@mail.ru'),
       ('32e22dc9-5acd-4dd8-b129-2b699ea8c35a', 'AVAILABLE',
        '$2a$10$5fsZuy68I1P3meIixaOW2.uyYzfPlzVFOY4rPWPpaVdr1jLgHySlW', 'mailWhoWillBeAnExecutor@mail.ru');

insert into t_task
values ('45b5a1fa-5680-402c-a928-05818f165146', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'AVAILABLE', 'cool4', null,
        'IN_PROCESS', 'LOW', 'test appoint an executor', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70'),
       ('a6471dab-5dd0-44fd-99c0-2d43aaa73817', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'AVAILABLE', 'cool5',
        'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'IN_PROCESS', 'LOW', 'test change task status', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70'),
       ('ec3d6250-5aca-4765-8048-922ac779847d', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'AVAILABLE', 'cool6',
        'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'IN_PROCESS', 'LOW', 'test change task status', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70'),
       ('1bcb949c-4b52-4c45-9dcd-c3eea707d0d8', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'AVAILABLE', 'cool7',
        'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'IN_PROCESS', 'LOW', 'test change task status', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70');

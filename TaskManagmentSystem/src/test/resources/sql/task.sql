truncate table t_task CASCADE;
truncate table t_user CASCADE;

insert into t_user
values ('b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'AVAILABLE', '$2a$10$LwH5ntW8utSRIdUtXsM2ZuOcNev.5IDUkjR1PBqQBgMSjSrD4JJpm',
        'test3@mail.ru');

insert into t_task
values ('545e8f38-ef6e-4121-a8b3-3e2715a048b0', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'AVAILABLE', 'cool-test',
        'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'IN_PROCESS', 'LOW', 'test', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70'),
        ('23f89650-b57d-4743-b6a1-9712e9caa6e7', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'AVAILABLE',
         'cool1', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'IN_PROCESS', 'LOW', 'test1', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70'),
       ('934e4154-aeec-45ee-a065-94c541a3b120', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'AVAILABLE',
        'cool2', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'IN_PROCESS', 'LOW', 'test2', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70'),
        ('4d9bc8fc-467b-4f2e-9be4-509fd3d0579f', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'AVAILABLE',
         'cool3', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70', 'IN_PROCESS', 'LOW', 'test3', 'b808e96b-dec4-4f7a-9c2e-9222abc4db70');
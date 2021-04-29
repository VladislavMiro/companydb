--------------------------------------Создание таблицы с пользователями--------------------------------------------------

create table users (id serial, login varchar(30), password varchar(255), role int, primary key(id));

--Процедура добавления пользователя с хешированием пароля с солью
create or replace procedure addUser(login varchar(30), password varchar(255), role int) as $$
declare
    hashPass varchar(255);
begin
    hashPass = crypt(password, gen_salt('md5'));
    insert into users (login, password, role) values(login, hashPass, role);
end;
$$ language plpgsql;

--Аутентификация пользователя
create or replace function checkPassword(_login varchar(30), _password varchar(255)) returns boolean
as $$
declare
    result boolean;
begin 
    select (u.password = crypt(_password, u.password)) into result from users u where u.login=_login;
    return result;
end;
$$ language plpgsql;

--------------------------Функции и процедуры для работы с таблицей "Отделы"-----------------------------------------------

--Получуние данных из таблицы "Отделы"
create or replace function getDepartments() returns TABLE(id int, name varchar(20)) as $$
begin
    return query
    SELECT d.id, d.name FROM departments d;
end;
$$ language plpgsql;

--Вставка записи в таблицу "Отделы"
create or replace procedure insertDepartments(name varchar(20)) as $$
begin
    INSERT INTO departments (name) VALUES(name);
end;
$$ language plpgsql;

--Редактирование записи из таблицы "Отделы"
create or replace procedure updateDepartments(_name varchar(20), _id int) as $$
begin
    UPDATE departments SET name=_name WHERE departments.id=_id;
end;
$$ language plpgsql;

--Удаление записи из таблицы "Отделы"
create or replace procedure deleteDepartment(_id int) as $$
begin
    DELETE FROM departments WHERE id=_id;
end;
$$ language plpgsql;

--------------------------Функции и процедуры для работы с таблицей "Сотрудники"-----------------------------------------------

-- Процедуры и функции взаимодействуют так же с таблицей "department_employees"

--Получуние данных из таблицы "Сотрудники"
create or replace function getEmployees() 
returns TABLE(id int, first_name varchar(20), last_name varchar(20), pather_name varchar(20), _position varchar(50), salary numeric(18,2), dep_id int, dep_name varchar(20)) as $$
begin
    return query
    SELECT e.id, e.first_name, e.last_name, e.pather_name, e.position, e.salary, d.id, d.name
        FROM employees e LEFT JOIN department_employees de ON e.id=de.employee_id
        LEFT JOIN departments d ON de.department_id=d.id;
end;
$$ language plpgsql;

--Вставка записи в таблицу "Сотрудники"
create or replace procedure insertEmployee(first_name varchar(20), last_name varchar(20), pather_name varchar(20), _position varchar(50), salary numeric(18,2), dep_id int) as $$
declare
    emp_id int;
begin
    INSERT INTO employees (first_name, last_name, pather_name, position, salary) 
    VALUES(first_name, last_name, pather_name, _position, salary) RETURNING id INTO emp_id;
    IF dep_id IS NOT NULL THEN
        INSERT INTO department_employees (department_id, employee_id) VALUES (dep_id, emp_id);
    END IF;
end;
$$ language plpgsql;

--Редактирование записи из таблицы "Сотрудники"
create or replace procedure updateEmployee(_id int, _first_name varchar(20), _last_name varchar(20), _pather_name varchar(20), _position varchar(50), _salary numeric(18,2), dep_id int) as $$
begin
    UPDATE employees e SET first_name=_first_name, last_name=_last_name, pather_name=_pather_name, position=_position, salary=_salary 
    WHERE e.id=_id;
    IF dep_id IS NOT NULL THEN
        IF EXISTS(SELECT * FROM department_employees de WHERE de.employee_id=_id)
        THEN
            UPDATE department_employees de SET department_id=dep_id WHERE de.employee_id=_id;
        ELSE
            INSERT INTO department_employees (department_id, employee_id) VALUES (dep_id, _id);
        END IF;
    END IF;
end;
$$ language plpgsql;

--Удаление записи из таблицы "Сотрудники"
create or replace procedure deleteEmployee(_id int) as $$
begin
    DELETE FROM employees e WHERE e.id=_id; 
end;
$$ language plpgsql;

--------------------------Функции и процедуры для работы с таблицей "Сотрудники"-----------------------------------------------

--Получуние данных из таблицы "Сотрудники"
create or replace function getProjects() 
returns TABLE(id int, name varchar(200), cost numeric(18,2), department_id int, 
dep_name varchar(20), date_beg date, date_end date, 
date_end_real date) as $$
begin
    return query
    SELECT p.id, p.name, p.cost, d.id, d.name, p.date_beg, p.date_end, p.date_end_real
        FROM projects p LEFT JOIN departments d on p.department_id=d.id;
end;
$$ language plpgsql;

--Вставка записи в таблицу "Сотрудники"
create or replace procedure insertProject(_name varchar(200), _cost numeric(18,2), _department_id int, _date_beg date, _date_end date) as $$
begin
    INSERT INTO projects (name, cost, department_id, date_beg, date_end) 
    VALUES (_name, _cost, _department_id, _date_beg, _date_end); 
end;
$$ language plpgsql;

--Редактирование записи из таблицы "Сотрудники"
create or replace procedure updateProject(_id int, _name varchar(200), _cost numeric(18,2), _department_id int, _date_beg date, _date_end date, _date_end_real date) as $$
begin
    UPDATE projects p SET name=_name, cost=_cost, department_id=_department_id, date_beg=_date_beg, date_end=_date_end, date_end_real=_date_end_real WHERE p.id=_id;
end;
$$ language plpgsql;

--Удаление записи из таблицы "Сотрудники"
create or replace procedure deleteProject(_id int) as $$
begin
    DELETE FROM projects p WHERE p.id=_id; 
end;
$$ language plpgsql;

-------------------------------------Формирование отчета-------------------------------------------------------------------------------

--Хранимая процедура для расчета суммы прибыли от завершенных к настоящему времени проектов за период
/*
Необходимо реализовать хранимую процедуру, рассчитывающую сумму прибыли, полученную фирмой за некоторый период. 
Хранимая процедура должна иметь один входной параметр, задающий время, с которого будем считать доход и один выходной, 
в котором возвращать размер прибыли.
Предлагаемый алгоритм: создаем курсор, который пробегает по проектам, реальная дата завершения которых меньше текущего времени, 
но больше дате из входного параметра. 
Для каждой строки рассчитываем сумму прибыли: вычисляем сколько было потрачено на проект и вычитаем эту сумму из суммы стоимости проекта. 
Суммируем полученный результат в некоторой переменной, значение которой по окончании работы курсора будет выдано в качестве выходного параметра.
*/
CREATE OR REPLACE FUNCTION profitAt(timeStart DATE) RETURNS NUMERIC(18,2)
AS $$
DECLARE
    cur CURSOR (_key DATE) FOR SELECT p.cost, SUM(e.salary) sal FROM projects p 
    JOIN department_employees de ON de.department_id=p.department_id 
    JOIN employees e ON e.id=de.employee_id WHERE p.date_end_real < CURRENT_DATE AND p.date_end_real > _key GROUP BY p.id;
    costProject NUMERIC(18,2);
    spending NUMERIC(18,2);
    totalCost NUMERIC(18,2);
BEGIN
    OPEN cur(timeStart);
    totalCost=0;
    LOOP
        FETCH cur INTO costProject, spending;
        IF NOT FOUND THEN EXIT; END IF; 
        totalCost=totalCost+(costProject-spending);
    END LOOP;
    RETURN totalCost;
END;
$$ LANGUAGE plpgsql;

SELECT * FROM profitAt('2020-04-01');

--Возвращает таблицу для курсора расчета прибыли за период
create or replace function profitAtTable(timeStart DATE) 
returns TABLE(_name varchar(200), _cost numeric(18,2), _date_beg date, _date_end date, _date_end_real date) as $$
begin
    return query
    SELECT p.name, p.cost, p.date_beg, p.date_end, p.date_end_real 
    FROM projects p JOIN department_employees de ON de.department_id=p.department_id 
    JOIN employees e ON e.id=de.employee_id
    WHERE p.date_end_real < CURRENT_DATE AND p.date_end_real > timeStart GROUP BY p.id;
end;
$$ language plpgsql;

--Функция рассчета предполагаемой прибыли
create or replace function futureProfit()
returns TABLE(_name varchar(200), _cost numeric(18,2), _date_beg date, _date_end date, _profit numeric(18,2)) as $$
begin
    return query
    SELECT p.name, p.cost, p.date_beg, p.date_end, p.cost-(SUM(e.salary)*((p.date_end-p.date_beg)/30)) profit
    FROM projects p JOIN department_employees de ON de.department_id=p.department_id 
    JOIN employees e ON e.id=de.employee_id 
    WHERE p.date_end_real IS NULL GROUP BY p.name, p.cost, p.date_end, p.date_beg;
end;
$$ language plpgsql;
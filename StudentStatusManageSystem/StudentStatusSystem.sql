create database StudentStatusSystem;
use StudentStatusSystem;
drop table if exists Student;
create table Student(
    studentID int auto_increment primary key ,
    name varchar(40) not null,
    gender char(1) not null check ( gender in ('男','女') ),
    birthday date not null
);
drop table if exists Class;
create table Class(
    classID int auto_increment primary key ,
    major varchar(40) not null,
    entryTime date default(current_date)
);
drop table if exists StudentToClass;
create table StudentToClass(
    studentID int references Student(studentID) on delete cascade on update cascade ,
    classID int references Class(classID) on delete cascade on update cascade ,
    constraint StudentToClass_pkey primary key StudentToClass(studentID,classID)
);
insert into Student (name, gender, birthday)
values ('罗伯特·莱万多夫斯基','男','1988-08-21'),
       ('凯文·德布劳内','男','1991-06-28'),
       ('埃尔林·哈兰德','男','2000-07-21'),
       ('米娅·哈姆','女','1972-03-17'),
       ('斯蒂芬·库里','男','1988-03-14'),
       ('卢卡·东契奇','男','1999-02-28'),
       ('尼古拉·约基奇','男','1995-02-19'),
       ('苏·伯德','男','1980-10-06');
insert into Class (major)
values ('足球'),('篮球');
insert into StudenttoClass (studentID, classID)
values (1,1),(2,1),(3,1),(4,1),(5,2),(6,2),(7,2),(8,2);
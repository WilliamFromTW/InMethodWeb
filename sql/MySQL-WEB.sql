# for MySQL 5.x

CREATE DATABASE IF NOT EXISTS WEB  DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci ;

use WEB;


CREATE TABLE USERS(
  USER_NAME CHAR(30) NOT NULL PRIMARY KEY, #
  USER_PASS CHAR(30) NOT NULL,  #
  USER_VALIDATE CHAR(1) NOT NULL DEFAULT 'N',
  USER_DESC CHAR(30) NOT NULL DEFAULT ' '
) CHARACTER SET utf8;

CREATE TABLE USER_ROLES (

  USER_NAME CHAR(30) NOT NULL,

  ROLE_NAME CHAR(30) NOT NULL,

  PRIMARY KEY (USER_NAME, ROLE_NAME)

) CHARACTER SET utf8;

CREATE TABLE ROLE_AUTHENTICATED_PERMISSION(

  ROLE_NAME CHAR(30) NOT NULL,
  FUNCTION_NAME CHAR(250) NOT NULL,
  PRIMARY KEY(ROLE_NAME,FUNCTION_NAME)
) CHARACTER SET utf8;

CREATE TABLE FUNCTION_INFO(
  FUNCTION_NAME CHAR(100) NOT NULL PRIMARY KEY,
  FUNCTION_URL CHAR(250),
  FUNCTION_DESC CHAR(250)
) CHARACTER SET utf8;

CREATE TABLE ROLE_AUTHORIZED_PERMISSION(
  FUNCTION_ROLE CHAR(30) NOT NULL,
  FUNCTION_NAME CHAR(100) NOT NULL,
  FUNCTION_GROUP CHAR(250) NOT NULL,
  FUNCTION_DELETE CHAR(1) NOT NULL,
  FUNCTION_INSERT CHAR(1) NOT NULL,
  FUNCTION_UPDATE CHAR(1) NOT NULL,
  FUNCTION_QUERY CHAR(1) NOT NULL,
  FUNCTION_VISIBLE CHAR(1) NOT NULL,
  PRIMARY KEY(FUNCTION_ROLE,FUNCTION_NAME)
) CHARACTER SET utf8;

CREATE TABLE ROLE_LIST(
  ROLE_NAME CHAR(30) NOT NULL PRIMARY KEY,
  ROLE_DESC CHAR(250),
  ROLE_CATALOG_ID CHAR(250),
  INDEX(ROLE_CATALOG_ID)
) CHARACTER SET utf8;

CREATE TABLE ROLE_CATALOG(
  ROLE_CATALOG_ID CHAR(250) NOT NULL PRIMARY KEY,
  ROLE_CATALOG_DESC CHAR(250)
) CHARACTER SET utf8;

CREATE TABLE DEPARTMENT(					
					
  DEPT_ID  CHAR(40) NOT NULL,					
  DEPT_NAME CHAR(100) ,					
  DEPT_LEADER_ID  CHAR(40),					
  PARENTDEPT_ID CHAR(40),					
  DEPT_VALIDATE CHAR(1) NOT NULL DEFAULT 'Y',					
  CATALOG  INT(3) NOT NULL, 
  PRIMARY KEY(CATALOG,DEPT_ID),					
  INDEX(CATALOG)
) CHARACTER SET UTF8;					


CREATE TABLE EMPLOYEE(			
			
 USER_ID  CHAR(40) NOT NULL,			
 USER_NAME CHAR(40) NOT NULL,			
 USER_ENGLISH_NAME  CHAR(40),			
 USER_DEPT_ID CHAR(40) NOT NULL,			
 USER_MAIL  CHAR(50),			
 USER_ONBORAD_DATE  CHAR(8) ,			
 USER_DEPARTURE_DATE  CHAR(8) ,			
 USER_LANGUAGE CHAR(12), 
 CATALOG  INT(3) NOT NULL, 
  PRIMARY KEY(USER_ID),
  INDEX(CATALOG)
) CHARACTER SET UTF8;			

CREATE TABLE DOWNLOAD_INFO(
  FILE_ID CHAR(40) NOT NULL PRIMARY KEY,
  PRG_ID CHAR(40) NOT NULL,
  FILE_NAME CHAR(100) NOT NULL,
  FILE_PATH CHAR(100) NOT NULL,
  CREATE_TIMESTAMP CHAR(14) NOT NULL  
) CHARACTER SET UTF8;

CREATE TABLE DOWNLOAD_LOG(
  USER_ID CHAR(40) NOT NULL,	
  FILE_ID  CHAR(40) NOT NULL,
  DOWNLOAD_TIMESTAMP CHAR(14) NOT NULL,
  INDEX(USER_ID),
  INDEX(FILE_ID),
  INDEX(USER_ID,FILE_ID)
) CHARACTER SET UTF8;

CREATE TABLE NEWS(
  OID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  CATALOG INT(3) NOT NULL,  #
  SUBJECT VARCHAR(200) NOT NULL,
  MESSAGE TEXT NOT NULL,
  UPLOAD_TYPE INT(3),          #1: Document  2: Form 3: Others
  UPLOAD_NAME VARCHAR(200),
  UPLOAD_PATH VARCHAR(200),
  UPDATE_DT VARCHAR(13) NOT NULL,
  INDEX(CATALOG)
) CHARACTER SET utf8;

CREATE TABLE DOC(
  OID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  CATALOG INT(3) NOT NULL,  #
  MESSAGE TEXT NOT NULL,
  UPLOAD_TYPE INT(3),          #1: Document  2: Form 3: Others
  UPLOAD_NAME VARCHAR(200),
  UPLOAD_PATH VARCHAR(200),
  UPDATE_DT VARCHAR(13) NOT NULL,
  INDEX(CATALOG)
) CHARACTER SET utf8;

insert into FUNCTION_INFO values('AuthenticationControlServlet','inmethod/auth/AuthenticationControlServlet','登入認證使用(不需顯示)');
insert into FUNCTION_INFO values('RoleAuthenticatedPermissionControlServlet','inmethod/auth/RoleAuthenticatedPermissionControl.jsp','角色執行權限');
insert into FUNCTION_INFO values('RoleAuthorizedPermissionControlServlet','inmethod/auth/RoleAuthorizedPermissionControl.jsp','角色功能權限');
insert into FUNCTION_INFO values('modifyPassword.jsp','inmethod/auth/modifyPassword.jsp','ModifyPassword(Only Inner System)');
insert into FUNCTION_INFO values('UserRolesControlServlet','inmethod/auth/UserRolesControl.jsp','使用者角色');
insert into FUNCTION_INFO values('UsersControlServlet','inmethod/auth/UsersControl.jsp','使用者帳號(密碼僅支援內建帳號)');
insert into FUNCTION_INFO values('FunctionInfoControlServlet','inmethod/auth/FunctionInfoControl.jsp','程式基本資料');
insert into FUNCTION_INFO values('RoleListControlServlet','inmethod/auth/RoleListControl.jsp','角色基本資料');
insert into FUNCTION_INFO values('RoleCatalogControlServlet','inmethod/auth/RoleCatalogControl.jsp','角色分類');
insert into FUNCTION_INFO values('DepartmentControlServlet','inmethod/hr/DepartmentControl.jsp','部門基本資料');
insert into FUNCTION_INFO values('EmployeeControlServlet','inmethod/hr/EmployeeControl.jsp','員工基本資料');
insert into FUNCTION_INFO values('GenEmployeeExcel','inmethod/frontend/example/GenEmployeeExcel.jsp','員工資料(Excel)');
insert into FUNCTION_INFO values('GenEmployeeTable','inmethod/frontend/example/GenEmployeeTable.jsp','員工資料(WEB)');
insert into FUNCTION_INFO values('GenEmployeePdf','inmethod/frontend/example/GenEmployeePdf.jsp','員工資料(PDF)');
insert into FUNCTION_INFO values('NewsControlServlet','inmethod/news/NewsControl.jsp','公告管理');
insert into FUNCTION_INFO values('DocControlServlet','inmethod/doc/DocControl.jsp','檔案管理');

## authentication
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','AuthenticationControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','RoleAuthenticatedPermissionControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','RoleAuthorizedPermissionControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','UserRolesControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','UsersControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','FunctionInfoControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','RoleListControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','RoleCatalogControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','DepartmentControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','EmployeeControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','GenEmployeeExcel');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','GenEmployeeTable');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','GenEmployeePdf');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','NewsControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('admin','DocControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('common','AuthenticationControlServlet');
insert into ROLE_AUTHENTICATED_PERMISSION values('common','modifyPassword.jsp');

## authorization
insert into ROLE_AUTHORIZED_PERMISSION values('admin','FunctionInfoControlServlet','系統管理-基本資料','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','RoleListControlServlet','系統管理-基本資料','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','RoleCatalogControlServlet','系統管理-基本資料','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','RoleAuthenticatedPermissionControlServlet','系統管理-基本資料','Y','Y','Y','Y','N');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','RoleAuthorizedPermissionControlServlet','系統管理-基本資料','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','UsersControlServlet','系統管理-帳號','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','UserRolesControlServlet','系統管理-帳號','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','EmployeeControlServlet','系統管理-人事','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','DepartmentControlServlet','系統管理-人事','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','GenEmployeeExcel','系統管理-人事報表','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','GenEmployeeTable','系統管理-人事報表','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','GenEmployeePdf','系統管理-人事報表','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','NewsControlServlet','系統管理-人事','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('admin','DocControlServlet','系統管理-檔案','Y','Y','Y','Y','Y');
insert into ROLE_AUTHORIZED_PERMISSION values('common','modifyPassword.jsp','General','N','N','Y','Y','Y');


## default user 'admin'
INSERT INTO DEPARTMENT (DEPT_ID, DEPT_NAME, DEPT_LEADER_ID, PARENTDEPT_ID, DEPT_VALIDATE,CATALOG) VALUES ('07','管理處','admin','','Y',1);
INSERT INTO DEPARTMENT (DEPT_ID, DEPT_NAME, DEPT_LEADER_ID, PARENTDEPT_ID, DEPT_VALIDATE,CATALOG) VALUES ('74', '資訊部', 'admin', '07', 'Y',1);
INSERT INTO USERS (USER_NAME, USER_PASS, USER_VALIDATE, USER_DESC) VALUES ('admin', 'inmethod', 'Y', '癮方法');
INSERT INTO EMPLOYEE (USER_ID, USER_NAME, USER_ENGLISH_NAME, USER_DEPT_ID, USER_MAIL, USER_ONBORAD_DATE, USER_DEPARTURE_DATE,USER_LANGUAGE,CATALOG) VALUES ('admin', '系統管理者', 'admin', '07', 'mis@hlmt.com.tw', '20150924', '','1',1);
INSERT INTO DOC value(1,11,'開發環境',1,'開發環境','/opt/files/doc/1651064542457.odt','19740325');
INSERT INTO DOWNLOAD_INFO value('1651064542457.odt','DocControlServlet?Catalog=11','開發環境','/opt/files/doc/1651064542457.odt','1651064542457');

## default role settins
INSERT INTO ROLE_CATALOG VALUES('1','系統管理類');
INSERT INTO ROLE_CATALOG VALUES('2','一般類');
INSERT INTO ROLE_LIST VALUES('admin','管理者','1');
INSERT INTO ROLE_LIST VALUES('common','一般使用者','2');
INSERT INTO ROLE_LIST VALUES('hr','HR Admin','2');
INSERT INTO ROLE_LIST VALUES('suhr','SU HR','2');
INSERT INTO ROLE_LIST VALUES('vnhr','VN HR','2');
INSERT INTO USER_ROLES (USER_NAME, ROLE_NAME) VALUES ('admin', 'admin');
INSERT INTO USER_ROLES (USER_NAME, ROLE_NAME) VALUES ('admin', 'common');
INSERT INTO USER_ROLES (USER_NAME, ROLE_NAME) VALUES ('admin', 'hr');
INSERT INTO USER_ROLES (USER_NAME, ROLE_NAME) VALUES ('admin', 'suhr');
INSERT INTO USER_ROLES (USER_NAME, ROLE_NAME) VALUES ('admin', 'vnhr');





# ROLE 'hr', 'suhr', 'vnhr'
## News(公告)
insert into FUNCTION_INFO values('NewsControlServlet?Catalog=11','inmethod/news/NewsControl.jsp?Catalog=11','一般公告(TW1)');
insert into ROLE_AUTHENTICATED_PERMISSION values('hr','NewsControlServlet?Catalog=11');
insert into ROLE_AUTHORIZED_PERMISSION values('hr','NewsControlServlet?Catalog=11','人事-公告','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('NewsControlServlet?Catalog=12','inmethod/news/NewsControl.jsp?Catalog=12','綜合公告(TW2)');
insert into ROLE_AUTHENTICATED_PERMISSION values('hr','NewsControlServlet?Catalog=12');
insert into ROLE_AUTHORIZED_PERMISSION values('hr','NewsControlServlet?Catalog=12','人事-公告','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('NewsControlServlet?Catalog=21','inmethod/news/NewsControl.jsp?Catalog=21','一般公告(SU1)');
insert into ROLE_AUTHENTICATED_PERMISSION values('suhr','NewsControlServlet?Catalog=21');
insert into ROLE_AUTHORIZED_PERMISSION values('suhr','NewsControlServlet?Catalog=21','人事-公告','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('NewsControlServlet?Catalog=22','inmethod/news/NewsControl.jsp?Catalog=22','綜合公告(SU2)');
insert into ROLE_AUTHENTICATED_PERMISSION values('suhr','NewsControlServlet?Catalog=22');
insert into ROLE_AUTHORIZED_PERMISSION values('suhr','NewsControlServlet?Catalog=22','人事-公告','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('NewsControlServlet?Catalog=31','inmethod/news/NewsControl.jsp?Catalog=31','thông báo1(VN1)');
insert into ROLE_AUTHENTICATED_PERMISSION values('vnhr','NewsControlServlet?Catalog=31');
insert into ROLE_AUTHORIZED_PERMISSION values('vnhr','NewsControlServlet?Catalog=31','HR-thông báo','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('NewsControlServlet?Catalog=32','inmethod/news/NewsControl.jsp?Catalog=32','thông báo2(VN2)');
insert into ROLE_AUTHENTICATED_PERMISSION values('vnhr','NewsControlServlet?Catalog=32');
insert into ROLE_AUTHORIZED_PERMISSION values('vnhr','NewsControlServlet?Catalog=32','HR-thông báo','Y','Y','Y','Y','Y');

## Doc(檔案)
insert into FUNCTION_INFO values('DocControlServlet?Catalog=11','inmethod/doc/DocControl.jsp?Catalog=11','規章辦法(TW1)');
insert into ROLE_AUTHENTICATED_PERMISSION values('hr','DocControlServlet?Catalog=11');
insert into ROLE_AUTHORIZED_PERMISSION values('hr','DocControlServlet?Catalog=11','人事-檔案','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('DocControlServlet?Catalog=12','inmethod/doc/DocControl.jsp?Catalog=12','空白表格(TW2)');
insert into ROLE_AUTHENTICATED_PERMISSION values('hr','DocControlServlet?Catalog=12');
insert into ROLE_AUTHORIZED_PERMISSION values('hr','DocControlServlet?Catalog=12','人事-檔案','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('DocControlServlet?Catalog=21','inmethod/doc/DocControl.jsp?Catalog=21','規章辦法(SU1)');
insert into ROLE_AUTHENTICATED_PERMISSION values('suhr','DocControlServlet?Catalog=21');
insert into ROLE_AUTHORIZED_PERMISSION values('suhr','DocControlServlet?Catalog=21','人事-檔案','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('DocControlServlet?Catalog=22','inmethod/doc/DocControl.jsp?Catalog=22','空白表格(SU2)');
insert into ROLE_AUTHENTICATED_PERMISSION values('suhr','DocControlServlet?Catalog=22');
insert into ROLE_AUTHORIZED_PERMISSION values('suhr','DocControlServlet?Catalog=22','人事-檔案','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('DocControlServlet?Catalog=31','inmethod/doc/DocControl.jsp?Catalog=31','quy định(VN1)');
insert into ROLE_AUTHENTICATED_PERMISSION values('vnhr','DocControlServlet?Catalog=31');
insert into ROLE_AUTHORIZED_PERMISSION values('vnhr','DocControlServlet?Catalog=31','HR-tập tin','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('DocControlServlet?Catalog=32','inmethod/doc/DocControl.jsp?Catalog=32','tấm(VN2)');
insert into ROLE_AUTHENTICATED_PERMISSION values('vnhr','DocControlServlet?Catalog=32');
insert into ROLE_AUTHORIZED_PERMISSION values('vnhr','DocControlServlet?Catalog=32','HR-tập tin','Y','Y','Y','Y','Y');

## 員工
insert into FUNCTION_INFO values('EmployeeControlServlet?Catalog=1','inmethod/hr/EmployeeControl.jsp?Catalog=1','員工(TW)');
insert into ROLE_AUTHENTICATED_PERMISSION values('hr','EmployeeControlServlet?Catalog=1');
insert into ROLE_AUTHORIZED_PERMISSION values('hr','EmployeeControlServlet?Catalog=1','人事-基本資料','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('EmployeeControlServlet?Catalog=2','inmethod/hr/EmployeeControl.jsp?Catalog=2','員工(SU)');
insert into ROLE_AUTHENTICATED_PERMISSION values('suhr','EmployeeControlServlet?Catalog=2');
insert into ROLE_AUTHORIZED_PERMISSION values('suhr','EmployeeControlServlet?Catalog=2','人事-基本資料','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('EmployeeControlServlet?Catalog=3','inmethod/hr/EmployeeControl.jsp?Catalog=3','Employee(VN)');
insert into ROLE_AUTHENTICATED_PERMISSION values('vnhr','EmployeeControlServlet?Catalog=3');
insert into ROLE_AUTHORIZED_PERMISSION values('vnhr','EmployeeControlServlet?Catalog=3','HR-Base','Y','Y','Y','Y','Y');

## 部門
insert into FUNCTION_INFO values('DepartmentControlServlet?Catalog=1','inmethod/hr/DepartmentControl.jsp?Catalog=1','部門(TW)');
insert into ROLE_AUTHENTICATED_PERMISSION values('hr','DepartmentControlServlet?Catalog=1');
insert into ROLE_AUTHORIZED_PERMISSION values('hr','DepartmentControlServlet?Catalog=1','人事-基本資料','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('DepartmentControlServlet?Catalog=2','inmethod/hr/DepartmentControl.jsp?Catalog=2','部門(SU)');
insert into ROLE_AUTHENTICATED_PERMISSION values('suhr','DepartmentControlServlet?Catalog=2');
insert into ROLE_AUTHORIZED_PERMISSION values('suhr','DepartmentControlServlet?Catalog=2','人事-基本資料','Y','Y','Y','Y','Y');

insert into FUNCTION_INFO values('DepartmentControlServlet?Catalog=3','inmethod/hr/DepartmentControl.jsp?Catalog=3','Dept.(VN)');
insert into ROLE_AUTHENTICATED_PERMISSION values('vnhr','DepartmentControlServlet?Catalog=3');
insert into ROLE_AUTHORIZED_PERMISSION values('vnhr','DepartmentControlServlet?Catalog=3','HR-Base','Y','Y','Y','Y','Y');

create schema if not exists dd;

CREATE TABLE dd.driver	(
	keyid BIGINT IDENTITY(10,10), 
	lastname VARCHAR(250) NOT NULL,
	firstname VARCHAR(250) NOT NULL,
	secondname VARCHAR(250) NOT NULL,
	birthdate TIMESTAMP not null
);

CREATE TABLE dd.medical_reference	(
	keyid BIGINT IDENTITY(10,10), 
	ref_series VARCHAR(50) NOT NULL,
	ref_number VARCHAR(50) NOT NULL,
	startdate TIMESTAMP not null,
	driver_id BIGINT not null UNIQUE,
	
	FOREIGN KEY (driver_id)  REFERENCES dd.driver(keyid)
);

CREATE TABLE dd.driver_license	(
	keyid BIGINT IDENTITY(10,10), 
	license_series VARCHAR(50) NOT NULL,
	license_number VARCHAR(50) NOT NULL,
	startdate TIMESTAMP not null,
	enddate TIMESTAMP not null,
	driver_id BIGINT not null UNIQUE,
	
	FOREIGN KEY (driver_id)  REFERENCES dd.driver(keyid)
);

CREATE TABLE dd.driver_contact	(
	keyid BIGINT IDENTITY(10,10), 
	contact VARCHAR(250) NOT NULL,
	driver_id BIGINT not null,
	
	FOREIGN KEY (driver_id)  REFERENCES dd.driver(keyid)
);

CREATE TABLE dd.employer	(
	keyid BIGINT IDENTITY(10,10), 
	inn VARCHAR(50) NOT NULL,
	ogrn VARCHAR(50) NOT NULL,
	name VARCHAR(250) NOT NULL,
	address VARCHAR(250) NOT NULL
);

CREATE TABLE dd.employer_license	(
	keyid BIGINT IDENTITY(10,10), 
	license_series VARCHAR(50) NOT NULL,
	license_number VARCHAR(50) NOT NULL,
	startdate TIMESTAMP not null,
	employer_id BIGINT not null UNIQUE,
	
	FOREIGN KEY (employer_id)  REFERENCES dd.employer(keyid)
);

CREATE TABLE dd.car	(
	keyid BIGINT IDENTITY(10,10), 
	plate_no VARCHAR(50) NOT NULL UNIQUE,
	marka VARCHAR(50),
	model VARCHAR(250),
	seats SMALLINT NOT NULL,
	passport_series VARCHAR(50) NOT NULL,
	passport_number VARCHAR(50) NOT NULL,
	certificate_series VARCHAR(50) NOT NULL,
	certificate_number VARCHAR(50) NOT NULL,
	employer_id BIGINT NOT NULL,
	
	FOREIGN KEY (employer_id)  REFERENCES dd.employer(keyid)
);

CREATE TABLE dd.route	(
	keyid BIGINT IDENTITY(10,10), 
	name VARCHAR(250) NOT NULL
);

CREATE TABLE dd.route_employer(
	keyid BIGINT IDENTITY(10,10), 
	route_id BIGINT not null,
	employer_id BIGINT not null
	
	FOREIGN KEY (employer_id)  REFERENCES dd.employer(keyid)
);

































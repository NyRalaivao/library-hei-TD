-- MLD Librairie

CREATE TABLE genre (
    id_genre SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE author (
    id_author SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    biography TEXT
);

CREATE TABLE library (
    id_library SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    address TEXT,
    phone VARCHAR(30)
);

CREATE TABLE book (
    id_book SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    isbn VARCHAR(50) UNIQUE,
    price DECIMAL(10,2),
    stock INT DEFAULT 0,
    publication_date DATE,
    id_genre INT REFERENCES genre(id_genre),
    id_library INT REFERENCES library(id_library)
);

CREATE TABLE book_author (
    id_book INT REFERENCES book(id_book),
    id_author INT REFERENCES author(id_author),
    PRIMARY KEY (id_book, id_author)
);

CREATE TABLE arrival (
    id_arrival SERIAL PRIMARY KEY,
    arrival_date DATE NOT NULL,
    quantity INT NOT NULL,
    id_book INT REFERENCES book(id_book)
);

CREATE TABLE customer (
    id_customer SERIAL PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(30)
);

CREATE TABLE users (
    id_user SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE sale (
    id_sale SERIAL PRIMARY KEY,
    sale_date TIMESTAMP NOT NULL,
    total_amount DECIMAL(10,2),
    id_customer INT REFERENCES customer(id_customer),
    id_user INT REFERENCES users(id_user)
);

CREATE TABLE payment (
    id_payment SERIAL PRIMARY KEY,
    amount DECIMAL(10,2),
    payment_date TIMESTAMP,
    payment_method VARCHAR(50),
    id_sale INT UNIQUE REFERENCES sale(id_sale)
);

CREATE TABLE sale_detail (
    id_sale INT REFERENCES sale(id_sale),
    id_book INT REFERENCES book(id_book),
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_sale, id_book)
);

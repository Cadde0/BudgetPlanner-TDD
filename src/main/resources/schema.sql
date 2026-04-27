CREATE TABLE IF NOT EXISTS category (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    category_limit INT,
    description TEXT
);

CREATE TABLE IF NOT EXISTS income (
    id SERIAL PRIMARY KEY,
    amount INT
);

CREATE TABLE IF NOT EXISTS expenses (
    id SERIAL PRIMARY KEY,
    amount INT NOT NULL,
    category_id INT REFERENCES category(id),
    description TEXT
);
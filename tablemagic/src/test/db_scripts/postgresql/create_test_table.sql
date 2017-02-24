CREATE TABLE quill_test_user (
  id BIGINT PRIMARY KEY,
  name TEXT NOT NULL,
  birth_date TIMESTAMP NOT NULL,
  salary INT,
  male BOOLEAN NOT NULL
);

CREATE TABLE quill_test_user_token (
  token UUID PRIMARY KEY,
  user_id BIGINT NOT NULL,
  CONSTRAINT quill_user_fk FOREIGN KEY (user_id) REFERENCES quill_test_user(id)
);

/* Postgres used to test orm-like functionality, so there are more tables */

CREATE TABLE quill_table_a (
  id BIGINT PRIMARY KEY,
  name TEXT NOT NULL
);

CREATE TABLE quill_table_b (
  id BIGINT PRIMARY KEY,
  quill_table_a_id BIGINT
);

CREATE TABLE quill_table_c (
  id BIGINT PRIMARY KEY,
  quill_table_a_name TEXT
);

CREATE TABLE quill_test_user (
  id NUMBER(19,0) PRIMARY KEY,
  name VARCHAR2(100) NOT NULL,
  birth_date TIMESTAMP NOT NULL,
  salary NUMBER(15, 0),
  male NUMBER(1) NOT NULL
);

CREATE TABLE quill_test_user_token (
  token VARCHAR2(40) PRIMARY KEY,
  user_id NUMBER(19, 0) NOT NULL,
  CONSTRAINT quill_user_fk FOREIGN KEY (user_id) REFERENCES quill_test_user(id)
);
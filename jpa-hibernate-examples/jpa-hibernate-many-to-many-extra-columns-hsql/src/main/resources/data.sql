INSERT INTO publisher ( name) VALUES ('Publisher A');
INSERT INTO publisher ( name) VALUES ('Publisher B');

INSERT INTO book (name) VALUES( 'Book 1');
INSERT INTO book (name) VALUES( 'Book 2');
INSERT INTO book (name) VALUES( 'Book 3');

INSERT INTO book_publisher (book_id, publisher_id, published_date) VALUES( 1, 1, '2024-10-01 14:33:16.463');
INSERT INTO book_publisher (book_id, publisher_id, published_date) VALUES( 1, 2, '2024-10-01 14:34:16.463');
INSERT INTO book_publisher (book_id, publisher_id, published_date) VALUES( 2, 1, '2024-10-01 14:37:16.463');
INSERT INTO book_publisher (book_id, publisher_id, published_date) VALUES( 3, 2, '2024-10-01 14:42:16.463');
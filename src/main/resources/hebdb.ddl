use hebdb;
CREATE TABLE images (
                        checksum varchar(56) NOT NULL,
                        filename varchar(56),
                        PRIMARY KEY (checksum)
);

CREATE TABLE tags (
                      id int NOT NULL AUTO_INCREMENT,
                      name varchar(64),
                      confidence DECIMAL(5,2),
                      image varchar(56) NOT NULL,
                      PRIMARY KEY (id),
                      FOREIGN KEY (image) REFERENCES images(checksum)
);

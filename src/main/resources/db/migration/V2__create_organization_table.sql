CREATE TABLE organizations
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255),
    website     VARCHAR(255),
    country     VARCHAR(255),
    description VARCHAR(255),
    founded     VARCHAR(255),
    industry    VARCHAR(255),
    employees   BIGINT,
    CONSTRAINT pk_organizations PRIMARY KEY (id)
);
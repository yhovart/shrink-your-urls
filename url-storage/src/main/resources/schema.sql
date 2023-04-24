CREATE TABLE urls(
    id UUID NOT NULL,
    url VARCHAR(2048) NOT NULL,
    code char(9) NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    modified_date TIMESTAMP NOT NULL,
    creator VARCHAR(255) NOT NULL,
    modified_by VARCHAR(255) NOT NULL,
    constraint urls_pk primary key (id),
    constraint urls_code_ak unique (code)
);

create index idx_urls_url_code on urls (code, url, id);
create index idx_urls_url on urls (url, id);
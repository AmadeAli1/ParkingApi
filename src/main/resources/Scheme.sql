create table if not exists Utente
(
    id          int primary key generated always as identity,
    nome        varchar(50) not null,
    email       varchar     not null unique,
    password    varchar     not null,
    type        varchar     not null,
    paymentType varchar     not null,
    divida      double precision default 0
);

create table if not exists Spot
(
    number      int primary key generated always as identity,
    isAvailable boolean not null default true,
    utenteId    int unique references Utente (id) on delete set null on update cascade
);

create table if not exists Parking
(
    id           int primary key generated always as identity,
    entranceTime timestamp not null,
    utenteId     int references Utente (id) on delete cascade on update cascade
);


create table if not exists Payment
(
    id            int primary key generated always as identity,
    entranceTime  timestamp not null,
    exitTime      timestamp not null,
    timeInParking varchar   not null,
    amountPerHour int       not null,
    amount        float     not null,
    discount      float     not null,
    utenteId      int       not null references Utente (id) on delete set null on update cascade
);

create table if not exists Subscription
(
    id              int primary key generated always as identity,
    divida          double precision not null,
    paymentDateTime timestamp        not null,
    utenteId        int              not null references Utente (id) on delete set null on update cascade
);
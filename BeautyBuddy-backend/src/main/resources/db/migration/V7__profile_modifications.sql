ALTER TABLE account
    ADD NONNULL COLUMN country VARCHAR(100),
    ADD COLUMN pronouns VARCHAR(50),
    ADD COLUMN date_of_birth DATE,
    ADD COLUMN bio VARCHAR(500),
    ADD COLUMN first_name VARCHAR(100)

CREATE ENUM pronouns_enum AS ENUM ('he/him', 'she/her', 'they/them', 'other');

CREATE ENUM country_enum AS ENUM (
    'United States', 'Canada', 'United Kingdom', 'Australia', 'Germany', 
    'France', 'Italy', 'Spain', 'India', 'China', 'Japan', 'Brazil', 
    'Mexico', 'Russia', 'South Africa', 'Other'
);
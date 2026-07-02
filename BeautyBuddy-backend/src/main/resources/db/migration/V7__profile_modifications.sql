ALTER TABLE account
    ADD NONNULL COLUMN country VARCHAR(100),
    ADD COLUMN pronouns pronouns_enum,
    ADD COLUMN date_of_birth DATE,
    ADD COLUMN bio VARCHAR(500),
    ADD COLUMN first_name VARCHAR(100),
    ADD COLUMN hair_texture hair_texture_enum,
    ADD COLUMN hair_density hair_density_enum,
    ADD COLUMN skin_type skin_type_enum,
    ADD COLUMN skin_condition skin_condition_enum;

CREATE ENUM pronouns_enum AS ENUM ('he/him', 'she/her', 'they/them', 'other');

CREATE ENUM country_enum AS ENUM (
    'United States', 'Canada', 'United Kingdom', 'Australia', 'Germany', 
    'France', 'Italy', 'Spain', 'India', 'China', 'Japan', 'Brazil', 
    'Mexico', 'Russia', 'South Africa', 'Other'
);

CREATE ENUM hair_texture_enum AS ENUM ('straight', 'wavy', 'curly', 'coily', 'other');
CREATE ENUM hair_density_enum AS ENUM ('thin', 'medium', 'thick', 'other');

CREATE ENUM skin_type_enum AS ENUM ('oily', 'dry', 'combination', 'other');
CREATE ENUM skin_condition_enum AS ENUM ('sensitive', 'normal', 'acne-prone');
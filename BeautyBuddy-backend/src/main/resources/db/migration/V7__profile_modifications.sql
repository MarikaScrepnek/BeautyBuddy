CREATE TYPE pronouns_enum AS ENUM ('he/him', 'she/her', 'they/them', 'other');

CREATE TYPE country_enum AS ENUM (
    'United States', 'Canada', 'United Kingdom', 'Australia', 'Germany',
    'France', 'Italy', 'Spain', 'India', 'China', 'Japan', 'Brazil',
    'Mexico', 'Russia', 'South Africa', 'Other'
);

CREATE TYPE hair_texture_enum AS ENUM ('straight', 'wavy', 'curly', 'coily', 'other');
CREATE TYPE hair_density_enum AS ENUM ('thin', 'medium', 'thick', 'other');

CREATE TYPE skin_type_enum AS ENUM ('oily', 'dry', 'combination', 'other');
CREATE TYPE skin_condition_enum AS ENUM ('sensitive', 'normal', 'acne-prone');

ALTER TABLE account
    ADD COLUMN country country_enum,
    ADD COLUMN pronouns pronouns_enum,
    ADD COLUMN date_of_birth DATE,
    ADD COLUMN bio VARCHAR(500),
    ADD COLUMN first_name VARCHAR(100),
    ADD COLUMN hair_texture hair_texture_enum,
    ADD COLUMN hair_density hair_density_enum,
    ADD COLUMN skin_type skin_type_enum,
    ADD COLUMN skin_condition skin_condition_enum;
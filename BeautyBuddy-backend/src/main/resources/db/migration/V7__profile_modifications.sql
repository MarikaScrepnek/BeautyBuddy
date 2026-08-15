CREATE TYPE pronouns_enum AS ENUM ('HE_HIM', 'SHE_HER', 'THEY_THEM', 'OTHER');

CREATE TYPE country_enum AS ENUM (
    'USA', 'CANADA', 'UK', 'AUSTRALIA', 'GERMANY',
    'FRANCE', 'ITALY', 'SPAIN', 'INDIA', 'CHINA', 'JAPAN', 'BRAZIL',
    'MEXICO', 'RUSSIA', 'SOUTH_AFRICA', 'OTHER'
);

CREATE TYPE hair_texture_enum AS ENUM ('STRAIGHT', 'WAVY', 'CURLY', 'COILY', 'OTHER');
CREATE TYPE hair_density_enum AS ENUM ('THIN', 'MEDIUM', 'THICK', 'OTHER');

CREATE TYPE skin_type_enum AS ENUM ('OILY', 'DRY', 'COMBINATION', 'OTHER');
CREATE TYPE skin_concern_enum AS ENUM ('SENSITIVITY', 'ACNE', 'AGING', 'HYPERPIGMENTATION');

ALTER TABLE account
    ADD COLUMN country country_enum,
    ADD COLUMN pronouns pronouns_enum,
    ADD COLUMN date_of_birth DATE,
    ADD COLUMN bio VARCHAR(500),
    ADD COLUMN first_name VARCHAR(100),
    ADD COLUMN hair_texture hair_texture_enum,
    ADD COLUMN hair_density hair_density_enum,
    ADD COLUMN skin_type skin_type_enum,
    ADD COLUMN skin_concerns skin_concern_enum[];
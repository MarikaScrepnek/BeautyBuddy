-- ================================================================
-- SEED DATA
-- ================================================================

-- Insert brands
INSERT INTO brand(name) VALUES
('L''Oréal Paris')
ON CONFLICT (name) DO NOTHING;

-- Insert top-level categories
INSERT INTO category (name, parent_category_id) VALUES
('Makeup', NULL),
('Skincare', NULL),
('Haircare', NULL),
('Bodycare', NULL)
ON CONFLICT (name) DO NOTHING;

-- Insert subcategories
INSERT INTO category (name, parent_category_id) VALUES
('Primer', (SELECT id FROM category WHERE name='Makeup')),
('Foundation', (SELECT id FROM category WHERE name='Makeup')),
('Concealer', (SELECT id FROM category WHERE name='Makeup')),
('Contour', (SELECT id FROM category WHERE name='Makeup')),
('Blush', (SELECT id FROM category WHERE name='Makeup')),
('Bronzer', (SELECT id FROM category WHERE name='Makeup')),
('Highlighter', (SELECT id FROM category WHERE name='Makeup')),
('Powder', (SELECT id FROM category WHERE name='Makeup')),
('Eyebrow Pencil', (SELECT id FROM category WHERE name='Makeup')),
('Eyebrow Gel', (SELECT id FROM category WHERE name='Makeup')),
('Eyeshadow', (SELECT id FROM category WHERE name='Makeup')),
('Eyeliner', (SELECT id FROM category WHERE name='Makeup')),
('Lip Liner', (SELECT id FROM category WHERE name='Makeup')),
('Lipstick', (SELECT id FROM category WHERE name='Makeup')),
('Lip Gloss', (SELECT id FROM category WHERE name='Makeup')),
('Lip Balm', (SELECT id FROM category WHERE name='Makeup')),
('Setting Spray', (SELECT id FROM category WHERE name='Makeup')),
('Mascara', (SELECT id FROM category WHERE name='Makeup'))
ON CONFLICT (name) DO NOTHING;

-- Insert canonical ingredients (base names)
INSERT INTO ingredient (name, canonical_id) VALUES
('water', NULL),
('cera alba', NULL),
('cera carnauba', NULL),
('acacia senegal', NULL)
ON CONFLICT (name) DO NOTHING;

-- Insert ingredient aliases (variants that reference canonical)
INSERT INTO ingredient (name, canonical_id) VALUES
('aqua', (SELECT id FROM ingredient WHERE name='water')),
('eau', (SELECT id FROM ingredient WHERE name='water')),
('beeswax', (SELECT id FROM ingredient WHERE name='cera alba')),
('cire d''abeille', (SELECT id FROM ingredient WHERE name='cera alba')),
('carnauba wax', (SELECT id FROM ingredient WHERE name='cera carnauba')),
('cire de carnauba', (SELECT id FROM ingredient WHERE name='cera carnauba')),
('acacia senegal gum', (SELECT id FROM ingredient WHERE name='acacia senegal'))
ON CONFLICT (name) DO NOTHING;

-- Insert product
INSERT INTO product (name, brand_id, category_id, price, image_link, product_link, rating, raw_ingredients, may_contain_raw_ingredients) VALUES
('Telescopic Original Mascara', 
 (SELECT id FROM brand WHERE name='L''Oréal Paris'),
 (SELECT id FROM category WHERE name='Mascara'),
 17.99, 
 'https://www.lorealparis.ca/-/media/project/loreal/brand-sites/oap/americas/ca/products/makeup/eyes/telescopic-original/blackest-black/071249104743_01.png',
 'https://www.lorealparis.ca/en-ca/telescopic-eye-collection/telescopic-original-mascara-blackest-black',
 NULL,
'AQUA / WATER / EAU , PARAFFIN , CERA ALBA / BEESWAX / CIRE D''ABEILLE , STEARIC ACID , CERA CARNAUBA / CARNAUBA WAX / CIRE DE CARNAUBA , ACACIA SENEGAL / ACACIA SENEGAL GUM , PALMITIC ACID , TRIETHANOLAMINE , HYDROXYETHYLCELLULOSE , AMINOMETHYL , PROPANEDIOL , PEG-40 STEARATE , SODIUM POLYMETHACRYLATE , METHYLPARABEN , PROPYLPARABEN , HYDROGENATED JOJOBA OIL , HYDROGENATED PALM OIL , SIMETHICONE , BHT , POLYQUATERNIUM-10 , PANTHENOL',
'CI 77492, CI 77499, CI 77491 / IRON OXIDES , CI 77266 / BLACK 2 , CI 77007 / ULTRAMARINES , CI 77288 / CHROMIUM OXIDE GREENS , CI 77289 / CHROMIUM HYDROXIDE GREEN , MICA , CI 77891 / TITANIUM DIOXIDE , CI 75470 / CARMINE , CI 77510 / FERRIC FERROCYANIDE'
)
ON CONFLICT (name, brand_id) DO NOTHING;

-- Insert product shades
INSERT INTO product_shade (product_id, shade_name, shade_hex_code, shade_number, image_link, product_link) VALUES
(
(SELECT id FROM product WHERE name='Telescopic Original Mascara' AND brand_id=(SELECT id FROM brand WHERE name='L''Oréal Paris')),
'Blackest Black',
'#000000',
1,
(SELECT image_link FROM product WHERE name='Telescopic Original Mascara' AND brand_id=(SELECT id FROM brand WHERE name='L''Oréal Paris')),
(SELECT product_link FROM product WHERE name='Telescopic Original Mascara' AND brand_id=(SELECT id FROM brand WHERE name='L''Oréal Paris'))
),
(
(SELECT id FROM product WHERE name='Telescopic Original Mascara' AND brand_id=(SELECT id FROM brand WHERE name='L''Oréal Paris')),
'Carbon Black',
'#000000',
2,
'https://www.lorealparis.ca/-/media/project/loreal/brand-sites/oap/americas/ca/products/makeup/eyes/telescopic-original/carbon-black/071249137093_01.png',
'https://www.lorealparis.ca/en-ca/telescopic-eye-collection/telescopic-original-mascara-carbon-black'
),
(
(SELECT id FROM product WHERE name='Telescopic Original Mascara' AND brand_id=(SELECT id FROM brand WHERE name='L''Oréal Paris')),
'Black',
'#000000',
3,
'https://www.lorealparis.ca/-/media/project/loreal/brand-sites/oap/americas/ca/products/makeup/eyes/telescopic-original/black/071249104729_01.png',
'https://www.lorealparis.ca/en-ca/telescopic-eye-collection/telescopic-original-mascara-black'
),
(
(SELECT id FROM product WHERE name='Telescopic Original Mascara' AND brand_id=(SELECT id FROM brand WHERE name='L''Oréal Paris')),
'Waterproof Black',
'#000000',
4,
'https://www.lorealparis.ca/-/media/project/loreal/brand-sites/oap/americas/ca/products/makeup/eyes/telescopic-original/waterproof-black/071249390740_01.png',
'https://www.lorealparis.ca/en-ca/telescopic-eye-collection/telescopic-original-mascara-waterproof-black'
)
ON CONFLICT (product_id, shade_name) DO NOTHING;
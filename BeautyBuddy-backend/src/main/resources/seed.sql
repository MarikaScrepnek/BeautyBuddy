INSERT INTO brand(name) VALUES
('L''Oréal Paris')
ON CONFLICT (name) DO NOTHING;

INSERT INTO category (name, parent_category_id) VALUES
('makeup', NULL),
('skincare', NULL),
('haircare', NULL),
('bodycare', NULL)
ON CONFLICT (name) DO NOTHING;

INSERT INTO category (name, parent_category_id) VALUES
('primer', (SELECT category_id FROM category WHERE name='makeup')),
('foundation', (SELECT category_id FROM category WHERE name='makeup')),
('concealer', (SELECT category_id FROM category WHERE name='makeup')),
('contour', (SELECT category_id FROM category WHERE name='makeup')),
('blush', (SELECT category_id FROM category WHERE name='makeup')),
('bronzer', (SELECT category_id FROM category WHERE name='makeup')),
('highlighter', (SELECT category_id FROM category WHERE name='makeup')),
('powder', (SELECT category_id FROM category WHERE name='makeup')),
('eyebrow pencil', (SELECT category_id FROM category WHERE name='makeup')),
('eyebrow gel', (SELECT category_id FROM category WHERE name='makeup')),
('eyeshadow', (SELECT category_id FROM category WHERE name='makeup')),
('eyeliner', (SELECT category_id FROM category WHERE name='makeup')),
('lip liner', (SELECT category_id FROM category WHERE name='makeup')),
('lipstick', (SELECT category_id FROM category WHERE name='makeup')),
('lip gloss', (SELECT category_id FROM category WHERE name='makeup')),
('lip balm', (SELECT category_id FROM category WHERE name='makeup')),
('setting spray', (SELECT category_id FROM category WHERE name='makeup')),
('mascara', (SELECT category_id FROM category WHERE name='makeup'))
ON CONFLICT (name) DO NOTHING;

INSERT INTO ingredient (name, canonical_id) VALUES
('water', NULL),
('cera alba', NULL),
('cera carnauba', NULL),
('acacia senegal', NULL)
ON CONFLICT (name) DO NOTHING;

INSERT INTO ingredient (name, canonical_id) VALUES
('aqua', (SELECT ingredient_id FROM ingredient WHERE name='water')),
('eau', (SELECT ingredient_id FROM ingredient WHERE name='water')),
('beeswax', (SELECT ingredient_id FROM ingredient WHERE name='cera alba')),
('cire d''abeille', (SELECT ingredient_id FROM ingredient WHERE name='cera alba')),
('carnauba wax', (SELECT ingredient_id FROM ingredient WHERE name='cera carnauba')),
('cire de carnauba', (SELECT ingredient_id FROM ingredient WHERE name='cera carnauba')),
('acacia senegal gum', (SELECT ingredient_id FROM ingredient WHERE name='acacia senegal'))
ON CONFLICT (name) DO NOTHING;

INSERT INTO product (name, brand_id, category_id, price, image_link, product_link, rating, raw_ingredients, may_contain_raw_ingredients) VALUES
('Telescopic Original Mascara', 
 (SELECT brand_id FROM brand WHERE name='L''Oréal Paris'),
 (SELECT category_id FROM category WHERE name='mascara'),
 NULL, 
 'https://www.lorealparis.ca/-/media/project/loreal/brand-sites/oap/americas/ca/products/makeup/eyes/telescopic-original/blackest-black/071249104743_01.png',
 'https://www.lorealparis.ca/en-ca/telescopic-eye-collection/telescopic-original-mascara-blackest-black',
 NULL,
'AQUA / WATER / EAU , PARAFFIN , CERA ALBA / BEESWAX / CIRE D''ABEILLE , STEARIC ACID , CERA CARNAUBA / CARNAUBA WAX / CIRE DE CARNAUBA , ACACIA SENEGAL / ACACIA SENEGAL GUM , PALMITIC ACID , TRIETHANOLAMINE , HYDROXYETHYLCELLULOSE , AMINOMETHYL , PROPANEDIOL , PEG-40 STEARATE , SODIUM POLYMETHACRYLATE , METHYLPARABEN , PROPYLPARABEN , HYDROGENATED JOJOBA OIL , HYDROGENATED PALM OIL , SIMETHICONE , BHT , POLYQUATERNIUM-10 , PANTHENOL',
'CI 77492, CI 77499, CI 77491 / IRON OXIDES , CI 77266 / BLACK 2 , CI 77007 / ULTRAMARINES , CI 77288 / CHROMIUM OXIDE GREENS , CI 77289 / CHROMIUM HYDROXIDE GREEN , MICA , CI 77891 / TITANIUM DIOXIDE , CI 75470 / CARMINE , CI 77510 / FERRIC FERROCYANIDE'
)
ON CONFLICT (name, brand_id) DO NOTHING;
-- ================================================================
-- SEED DATA
-- ================================================================

-- Insert brands
INSERT INTO brand(name) VALUES
('Elf'),
('La Roche-Posay'),
('Vanicream'),

('L''Oréal Paris'),
('Maybelline'),
('Nyx Professional Makeup'),
('Quo'),
('Fenty Beauty'),
('Physicians Formula'),
('Wet n Wild')
ON CONFLICT (name) DO NOTHING;

-- Insert top-level categories
INSERT INTO category (name, parent_category_id) VALUES
('Makeup', NULL),
('Skincare', NULL),
('Haircare', NULL)
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
('Mascara', (SELECT id FROM category WHERE name='Makeup')),

('Cleansing Balm', (SELECT id FROM category WHERE name='Skincare')),
('Cleanser', (SELECT id FROM category WHERE name='Skincare')),
('Moisturizer', (SELECT id FROM category WHERE name='Skincare')),
('Sunscreen', (SELECT id FROM category WHERE name='Skincare'))
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
('Holy Hydration! Makeup Melting Cleansing Balm', 
 (SELECT id FROM brand WHERE name='Elf'),
 (SELECT id FROM category WHERE name='Cleansing Balm'),
 15.00, 
 'https://www.elfcosmetics.com/dw/image/v2/BBXC_PRD/on/demandware.static/-/Sites-elf-master/default/dw44d0f80d/2022/HolyHydration!NewPackaging/59910_SCREM_Closed_R.jpg?sfrm=png&sw=780&q=90&strip=false',
 'https://www.elfcosmetics.com/en_CA/holy-hydration-makeup-melting-cleansing-balm/59910.html',
 NULL,
 'cetyl ethylhexanoate, caprylic/capric triglyceride, hydrogenated polydecene, peg-20 glyceryl triisostearate, peg-10 isostearate, helianthus annuus (sunflower) seed oil, hydrogenated sunflower seed oil, palmitoyl tripeptide-1, palmitoyl tetrapeptide-7, ceramide 3, ceramide 6 ii, ceramide 1, hydrolyzed sodium hyaluronate, phytosphingosine, glycerin, butylene glycol, water (aqua), polysorbate 20, carbomer, xanthan gum, cholesterol, sodium lauroyl lactylate, phenoxyethanol, ethylhexylglycerin, caprylyl glycol, geraniol, linalool, fragrance (parfum)',
 NULL
),
(
    'Toleriane Dermo-Cleanser',
    (SELECT id FROM brand WHERE name='La Roche-Posay'),
    (SELECT id FROM category WHERE name='Cleanser'),
    27.00,
    'https://www.laroche-posay.ca/dw/image/v2/AATL_PRD/on/demandware.static/-/Sites-larocheposay-master-catalog/default/dwd991da26/2022/3433422406599/lrp-toleriane-dermo-cleanser-200ml-3433422406599-00.jpg?sw=720&sh=720&sm=cut&sfrm=jpg&q=70',
    'https://www.laroche-posay.ca/en_CA/face-care/face-skin-concerns/skincare-for-dry-skin/toleriane-dermo-cleanser/toleriane-dermo-cleanser.html?dwvar_toleriane-dermo-cleanser_size=200_ml',
    NULL,
    'Aqua, Ethylhexyl Palmitate, Glycerine, Dipropylene Glycol, Carbomer, Sodium Hydroxide, Ethylhexylglycerin, Capryl Glycol',
    NULL
),
(
    'Daily Facial Moisturizer ',
    (SELECT id FROM brand WHERE name='Vanicream'),
    (SELECT id FROM category WHERE name='Moisturizer'),
    15.00,
    'https://www.vanicream.com/dynamic-media/product/images/dfm-at24g-front-2671-ret-crop.jpg?gravity=center&v=galleryZoom&k=mvaGy9Fmo84Lz5jINUACoA',
    'https://www.vanicream.com/product/vanicream-daily-facial-moisturizer',
    NULL,
    'water, squalane, glycerin, pentylene glycol, polyglyceryl-2 stearate, glyceryl stearate, stearyl alcohol, hyaluronic acid, ceramide EOP, ceramide NG, ceramide NP, ceramide AS, ceramide AP, carnosine, hydrogenated lecithin, phytosterols, caprylyl glycol, polyacrylate crosspolymer-11, 12-hexanediol',
    NULL
),
(
    'Anthelios Ultra-Fluid SPF 50+ Facial Sunscreen',
    (SELECT id FROM brand WHERE name='La Roche-Posay'),
    (SELECT id FROM category WHERE name='Sunscreen'),
    35.95,
    'https://www.laroche-posay.ca/dw/image/v2/AATL_PRD/on/demandware.static/-/Sites-larocheposay-master-catalog/default/dw4e525f92/2021/03337875709361/LRP-Sun-Protection-Anthelios-Ultra-Fluid-Face-Lotion-SPF50+-50ml-03337875709361.jpg?sw=720&sh=720&sm=cut&sfrm=jpg&q=70',
    'https://www.laroche-posay.ca/en_CA/sunscreen/sun-product-type/sun-fluid/anthelios-ultra-fluid-spf-50-facial-sunscreen/3337875709361.html',
    NULL,
    'Aqua, Dicaprylyl Carbonate, Alcohol Denat., Ethylhexyl Salicylate, Diethylamino Hydroxybenzoyl Hexyl Benzoate, Bis-Ethylhexyloxyphenol Methoxyphenyl Triazine, Glycerin, Butylene Glycol, C12-15 Alkyl Benzoate, Dimethicone, Polyglyceryl-3 Methylglucose Distearate, Caprylyl Glycol, Sodium Hydroxide, Phenoxyethanol, Disodium EDTA',
    NULL
),
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
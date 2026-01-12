INSERT INTO brand(name) VALUES
('L''Oréal Paris')
ON CONFLICT (name) DO NOTHING;

INSERT INTO category (name, parent_category_id) VALUES
('makeup', NULL),
('skincare', NULL),
('haircare', NULL),
('bodycare', NULL),
('primer', (SELECT category_id FROM category WHERE name='makeup')),
('foundation', (SELECT category_id FROM category WHERE name='makeup')),
('concealer', (SELECT category_id FROM category WHERE name='makeup')),
('contour', (SELECT category_id FROM category WHERE name='makeup')),
('blush', (SELECT category_id FROM category WHERE name='makeup')),
('bronzer', (SELECT category_id FROM category WHERE name='makeup')),
('highlighter', (SELECT category_id FROM category WHERE name='makeup')),
('powder', (SELECT category_id FROM category WHERE name='makeup')),
('eyebrow product', (SELECT category_id FROM category WHERE name='makeup')),
('eyebrow gel', (SELECT category_id FROM category WHERE name='eyebrow product')),
('eyeshadow', (SELECT category_id FROM category WHERE name='makeup')),
('foundation', (SELECT category_id FROM category WHERE name='makeup')),
('concealer', (SELECT category_id FROM category WHERE name='makeup')),
('contour', (SELECT category_id FROM category WHERE name='makeup')),
('blush', (SELECT category_id FROM category WHERE name='makeup')),
('bronzer', (SELECT category_id FROM category WHERE name='makeup')),
('highlighter', (SELECT category_id FROM category WHERE name='makeup')),
('powder', (SELECT category_id FROM category WHERE name='makeup')),
('eyebrow product', (SELECT category_id FROM category WHERE name='makeup')),
('eyebrow gel', (SELECT category_id FROM category WHERE name='eyebrow product')),
('eyeshadow', (SELECT category_id FROM category WHERE name='makeup')),
('eyeliner', (SELECT category_id FROM category WHERE name='makeup')),
('mascara', (SELECT category_id FROM category WHERE name='makeup')),
('setting spray', (SELECT category_id FROM category WHERE name='makeup'))
ON CONFLICT (name) DO NOTHING;

INSERT INTO product (name, brand_id, category_id, price, image_link, product_link, description, rating) VALUES
('Telescopic Original Mascara', 
 (SELECT brand_id FROM brand WHERE name='L''Oréal Paris'),
 (SELECT category_id FROM category WHERE name='mascara'),
 NULL, 
 'https://www.lorealparis.ca/-/media/project/loreal/brand-sites/oap/americas/ca/products/makeup/eyes/telescopic-original/blackest-black/071249104743_01.png',
 'https://www.lorealparis.ca/en-ca/telescopic-eye-collection/telescopic-original-mascara-blackest-black',
 NULL,
 NULL
)
ON CONFLICT (name, brand_id) DO NOTHING;
INSERT INTO brand(name) VALUES
('L''Oréal Paris')
ON CONFLICT (name) DO NOTHING;

INSERT INTO category (name, parent_category_id) VALUES
('makeup', NULL),
('skincare', NULL),
('haircare', NULL),
('bodycare', NULL),
('primer', 'makeup'),
('foundation', 'makeup'),
('concealer', 'makeup'),
('contour', 'makeup'),
('blush', 'makeup'),
('bronzer', 'makeup'),
('highlighter', 'makeup'),
('powder', 'makeup'),
('eyebrow product', 'makeup'),
('eyebrow gel', 'eyebrow product'),
('eyeshadow', 'makeup'),
('foundation', 'makeup'),
('concealer', 'makeup'),
('contour', 'makeup'),
('blush', 'makeup'),
('bronzer', 'makeup'),
('highlighter', 'makeup'),
('powder', 'makeup'),
('eyebrow product', 'makeup'),
('eyebrow gel', 'eyebrow product'),
('eyeshadow', 'makeup'),
('eyeliner', 'makeup'),
('mascara', 'makeup'),
('setting spray', 'makeup')
ON CONFLICT (name) DO NOTHING;

INSERT INTO product (name, brand_id, category_id, price, image_link, product_link, description, rating) VALUES
('Telescopic Original Mascara', 
 1, 
 18, 
 NULL, 
 'https://www.lorealparis.ca/-/media/project/loreal/brand-sites/oap/americas/ca/products/makeup/eyes/telescopic-original/blackest-black/071249104743_01.png',
 'https://www.lorealparis.ca/en-ca/telescopic-eye-collection/telescopic-original-mascara-blackest-black',
 NULL,
 NULL
)
ON CONFLICT (name, brand_id) DO NOTHING;
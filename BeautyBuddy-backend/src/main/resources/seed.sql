INSERT INTO brand(name) VALUES
('L''Oréal Paris')
ON CONFLICT (name) DO NOTHING;

INSERT INTO category (category_id, name, parent_category_id) VALUES
(1, 'makeup', NULL),
(2, 'skincare', NULL),
(3, 'haircare', NULL),
(4, 'bodycare', NULL),
(5, 'primer', 1),
(6, 'foundation', 1),
(7, 'concealer', 1),
(8, 'contour', 1),
(9, 'blush', 1),
(10, 'bronzer', 1),
(11, 'highlighter', 1),
(13, 'powder', 1),
(14, 'eyebrow product', 1),
(15, 'eyebrow gel', 1),
(16, 'eyeshadow', 1),
(17, 'eyeliner', 1),
(18, 'mascara', 1),
(19, 'setting spray', 1)
ON CONFLICT (category_id) DO NOTHING;

INSERT INTO product (product_id, name, brand_id, category_id, price, image_link, product_link, description, rating) VALUES
(1, 
 'Telescopic Original Mascara', 
 1, 
 18, 
 NULL, 
 'https://www.lorealparis.ca/-/media/project/loreal/brand-sites/oap/americas/ca/products/makeup/eyes/telescopic-original/blackest-black/071249104743_01.png',
 'https://www.lorealparis.ca/en-ca/telescopic-eye-collection/telescopic-original-mascara-blackest-black',
 NULL,
 NULL
)
ON CONFLICT (product_id) DO NOTHING;
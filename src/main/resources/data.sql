INSERT INTO category (id, name, category_limit, description) 
VALUES (1, 'Test Category', 100, 'Test Description')
ON CONFLICT (id) DO NOTHING;

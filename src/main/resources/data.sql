-- Insert admin user (password: admin123)
INSERT INTO users (username, password, email, full_name, phone_number, role, enabled, created_at, updated_at) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFGdMyTI.8O1AEqx.R8y6R6', 'admin@gftour.com', 'System Administrator', '010-1234-5678', 'ADMIN', true, NOW(), NOW());

-- Insert test users (password: user123)
INSERT INTO users (username, password, email, full_name, phone_number, role, enabled, created_at, updated_at) VALUES
('user1', '$2a$10$9Y4daC2jW2Bb.1F.V.A.4OSVqJpcoVvOZmcjv7C2JRfFeLh2z3r8G', 'user1@example.com', 'John Doe', '010-2345-6789', 'USER', true, NOW(), NOW()),
('user2', '$2a$10$9Y4daC2jW2Bb.1F.V.A.4OSVqJpcoVvOZmcjv7C2JRfFeLh2z3r8G', 'user2@example.com', 'Jane Smith', '010-3456-7890', 'USER', true, NOW(), NOW()),
('user3', '$2a$10$9Y4daC2jW2Bb.1F.V.A.4OSVqJpcoVvOZmcjv7C2JRfFeLh2z3r8G', 'user3@example.com', 'Bob Johnson', '010-4567-8901', 'USER', true, NOW(), NOW());

-- Insert sample tours
INSERT INTO tours (title, description, location, price, max_participants, duration, category, status, start_date, end_date, created_at, updated_at, created_by_id) VALUES
('Jeju Island Nature Walk', 'Beautiful hiking tour around Jeju Island''s most scenic spots including Hallasan mountain and coastal trails.', 'Jeju Island', 150000.00, 20, 480, 'NATURE', 'ACTIVE', '2024-04-01', '2024-10-31', NOW(), NOW(), 1),
('Seoul Palace Cultural Tour', 'Explore the rich history of Seoul by visiting Gyeongbokgung, Changdeokgung, and other historical palaces.', 'Seoul', 80000.00, 15, 360, 'CULTURE', 'ACTIVE', '2024-03-15', '2024-12-15', NOW(), NOW(), 1),
('Busan Coastal Adventure', 'Thrilling coastal adventure including surfing, rock climbing, and beach activities in beautiful Busan.', 'Busan', 200000.00, 12, 600, 'ADVENTURE', 'ACTIVE', '2024-05-01', '2024-09-30', NOW(), NOW(), 1),
('Korean Traditional Food Tour', 'Discover authentic Korean cuisine with hands-on cooking classes and market visits in Insadong.', 'Seoul', 120000.00, 10, 300, 'FOOD', 'ACTIVE', '2024-04-01', '2024-11-30', NOW(), NOW(), 1),
('Seoraksan Mountain Hiking', 'Challenge yourself with a guided hiking tour of Seoraksan National Park with stunning autumn views.', 'Sokcho', 180000.00, 25, 540, 'NATURE', 'ACTIVE', '2024-09-01', '2024-11-15', NOW(), NOW(), 1);

-- Insert sample bookings
INSERT INTO bookings (tour_id, user_id, participant_count, total_price, booking_date, status, special_requests, created_at, updated_at) VALUES
(1, 2, 2, 300000.00, '2024-03-10 14:30:00', 'CONFIRMED', 'Need vegetarian meal options', NOW(), NOW()),
(2, 3, 1, 80000.00, '2024-03-11 10:15:00', 'CONFIRMED', '', NOW(), NOW()),
(3, 4, 3, 600000.00, '2024-03-12 16:45:00', 'PENDING', 'First time surfers in group', NOW(), NOW()),
(4, 2, 2, 240000.00, '2024-03-13 09:20:00', 'CONFIRMED', 'Allergic to shellfish', NOW(), NOW());

-- Insert sample reviews
INSERT INTO reviews (tour_id, user_id, booking_id, rating, comment, created_at, updated_at) VALUES
(1, 2, 1, 5, 'Amazing experience! The guide was very knowledgeable and the scenery was breathtaking. Highly recommend this tour to anyone visiting Jeju.', NOW(), NOW()),
(2, 3, 2, 4, 'Great cultural tour with lots of historical information. The palaces were beautiful, though it was a bit crowded during peak hours.', NOW(), NOW()),
(4, 2, 4, 5, 'Fantastic food tour! Learned so much about Korean cooking techniques and got to taste amazing traditional dishes. Will definitely book again!', NOW(), NOW());
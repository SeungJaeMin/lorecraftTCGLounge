-- V007__add_dummy_users_final.sql
-- Fix existing users and add dummy data with proper JOINED inheritance based on actual table structure

-- First, fix existing testuser and testowner by adding them to their respective child tables
-- Check if testuser exists in gamers table, if not insert
INSERT INTO gamers (uid, current_rating, highest_rating, total_wins, total_losses, total_draws, usable_point, used_point)
SELECT 
    u.uid,
    1200,  -- current_rating
    1200,  -- highest_rating
    10,    -- total_wins
    5,     -- total_losses
    2,     -- total_draws
    1000,  -- usable_point
    0      -- used_point
FROM users u
WHERE u.userid = 'testuser' 
    AND u.user_type = 'GAMER'
    AND NOT EXISTS (SELECT 1 FROM gamers g WHERE g.uid = u.uid);

-- Check if testowner exists in store_owners table, if not insert
INSERT INTO store_owners (uid, store_name, business_license, store_location, contact_number, store_zipcode)
SELECT 
    u.uid,
    'Test Game Store',
    '123-45-67890',
    '서울시 강남구 테헤란로 123',
    '02-1234-5678',
    '06234'
FROM users u
WHERE u.userid = 'testowner' 
    AND u.user_type = 'STORE_OWNER'
    AND NOT EXISTS (SELECT 1 FROM store_owners so WHERE so.uid = u.uid);

-- Now add 30 new users (25 gamers + 5 store owners)
-- Password for all dummy users is: password123 (BCrypt encoded)
SET @password = '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW';

-- Insert 25 gamer users
INSERT INTO users (userid, password, email, user_type, nickname, is_active, register_date, updated_at)
VALUES 
    ('gamer01', @password, 'gamer01@example.com', 'GAMER', '초보게이머', 1, NOW(), NOW()),
    ('gamer02', @password, 'gamer02@example.com', 'GAMER', '중수게이머', 1, NOW(), NOW()),
    ('gamer03', @password, 'gamer03@example.com', 'GAMER', '고수게이머', 1, NOW(), NOW()),
    ('gamer04', @password, 'gamer04@example.com', 'GAMER', '프로게이머', 1, NOW(), NOW()),
    ('gamer05', @password, 'gamer05@example.com', 'GAMER', '마스터게이머', 1, NOW(), NOW()),
    ('dragonmaster', @password, 'dragonmaster@example.com', 'GAMER', '드래곤마스터', 1, NOW(), NOW()),
    ('shadowhunter', @password, 'shadowhunter@example.com', 'GAMER', '그림자사냥꾼', 1, NOW(), NOW()),
    ('crystalwizard', @password, 'crystalwizard@example.com', 'GAMER', '크리스탈위저드', 1, NOW(), NOW()),
    ('firestorm', @password, 'firestorm@example.com', 'GAMER', '파이어스톰', 1, NOW(), NOW()),
    ('icequeen', @password, 'icequeen@example.com', 'GAMER', '얼음여왕', 1, NOW(), NOW()),
    ('thunderbolt', @password, 'thunderbolt@example.com', 'GAMER', '썬더볼트', 1, NOW(), NOW()),
    ('windwalker', @password, 'windwalker@example.com', 'GAMER', '바람걷기', 1, NOW(), NOW()),
    ('earthshaker', @password, 'earthshaker@example.com', 'GAMER', '대지진동', 1, NOW(), NOW()),
    ('mysticmage', @password, 'mysticmage@example.com', 'GAMER', '신비마법사', 1, NOW(), NOW()),
    ('darknight', @password, 'darknight@example.com', 'GAMER', '다크나이트', 1, NOW(), NOW()),
    ('lightbringer', @password, 'lightbringer@example.com', 'GAMER', '빛의전달자', 1, NOW(), NOW()),
    ('voidseeker', @password, 'voidseeker@example.com', 'GAMER', '공허탐색자', 1, NOW(), NOW()),
    ('timebender', @password, 'timebender@example.com', 'GAMER', '시간조작자', 1, NOW(), NOW()),
    ('soulreaper', @password, 'soulreaper@example.com', 'GAMER', '영혼수확자', 1, NOW(), NOW()),
    ('stargazer', @password, 'stargazer@example.com', 'GAMER', '별관측자', 1, NOW(), NOW()),
    ('moonshadow', @password, 'moonshadow@example.com', 'GAMER', '달그림자', 1, NOW(), NOW()),
    ('sunblade', @password, 'sunblade@example.com', 'GAMER', '태양검', 1, NOW(), NOW()),
    ('stormcaller', @password, 'stormcaller@example.com', 'GAMER', '폭풍소환사', 1, NOW(), NOW()),
    ('frostbite', @password, 'frostbite@example.com', 'GAMER', '동상', 1, NOW(), NOW()),
    ('noviceplayer', @password, 'noviceplayer@example.com', 'GAMER', '초보자', 1, NOW(), NOW());

-- Insert 5 store owner users
INSERT INTO users (userid, password, email, user_type, nickname, is_active, register_date, updated_at)
VALUES 
    ('store_gangnam', @password, 'gangnam@gamestore.com', 'STORE_OWNER', '강남게임스토어', 1, NOW(), NOW()),
    ('store_hongdae', @password, 'hongdae@gamestore.com', 'STORE_OWNER', '홍대TCG샵', 1, NOW(), NOW()),
    ('store_busan', @password, 'busan@gamestore.com', 'STORE_OWNER', '부산카드게임존', 1, NOW(), NOW()),
    ('store_daegu', @password, 'daegu@gamestore.com', 'STORE_OWNER', '대구게임플레이스', 1, NOW(), NOW()),
    ('store_incheon', @password, 'incheon@gamestore.com', 'STORE_OWNER', '인천TCG마켓', 1, NOW(), NOW());

-- Insert corresponding gamer data for the 25 new gamers
INSERT INTO gamers (uid, current_rating, highest_rating, total_wins, total_losses, total_draws, usable_point, used_point)
SELECT 
    u.uid,
    FLOOR(RAND() * 2000) + 800,  -- current_rating between 800-2800
    FLOOR(RAND() * 2000) + 900,  -- highest_rating between 900-2900
    FLOOR(RAND() * 200),         -- total_wins 0-200
    FLOOR(RAND() * 150),         -- total_losses 0-150
    FLOOR(RAND() * 50),          -- total_draws 0-50
    FLOOR(RAND() * 50000),       -- usable_point 0-50000
    FLOOR(RAND() * 10000)        -- used_point 0-10000
FROM users u
WHERE u.user_type = 'GAMER' 
    AND u.userid IN (
        'gamer01', 'gamer02', 'gamer03', 'gamer04', 'gamer05',
        'dragonmaster', 'shadowhunter', 'crystalwizard', 'firestorm', 'icequeen',
        'thunderbolt', 'windwalker', 'earthshaker', 'mysticmage', 'darknight',
        'lightbringer', 'voidseeker', 'timebender', 'soulreaper', 'stargazer',
        'moonshadow', 'sunblade', 'stormcaller', 'frostbite', 'noviceplayer'
    );

-- Insert corresponding store owner data for the 5 new store owners
INSERT INTO store_owners (uid, store_name, business_license, store_location, contact_number, store_zipcode)
SELECT 
    u.uid,
    CASE u.userid
        WHEN 'store_gangnam' THEN '강남 게임 스토어'
        WHEN 'store_hongdae' THEN '홍대 TCG 샵'
        WHEN 'store_busan' THEN '부산 카드 게임존'
        WHEN 'store_daegu' THEN '대구 게임 플레이스'
        WHEN 'store_incheon' THEN '인천 TCG 마켓'
        ELSE 'Game Store'
    END,
    CONCAT(
        FLOOR(RAND() * 900) + 100, '-',
        FLOOR(RAND() * 90) + 10, '-',
        FLOOR(RAND() * 90000) + 10000
    ),
    CASE u.userid
        WHEN 'store_gangnam' THEN '서울시 강남구 테헤란로 456'
        WHEN 'store_hongdae' THEN '서울시 마포구 홍대입구역 3번출구'
        WHEN 'store_busan' THEN '부산시 해운대구 마린시티 789'
        WHEN 'store_daegu' THEN '대구시 중구 동성로 321'
        WHEN 'store_incheon' THEN '인천시 연수구 송도동 654'
        ELSE '서울시 중구'
    END,
    CASE u.userid
        WHEN 'store_gangnam' THEN '02-555-1234'
        WHEN 'store_hongdae' THEN '02-333-5678'
        WHEN 'store_busan' THEN '051-777-9012'
        WHEN 'store_daegu' THEN '053-222-3456'
        WHEN 'store_incheon' THEN '032-888-7890'
        ELSE '02-000-0000'
    END,
    CASE u.userid
        WHEN 'store_gangnam' THEN '06234'
        WHEN 'store_hongdae' THEN '04104'
        WHEN 'store_busan' THEN '48099'
        WHEN 'store_daegu' THEN '41943'
        WHEN 'store_incheon' THEN '21990'
        ELSE '04563'
    END
FROM users u
WHERE u.user_type = 'STORE_OWNER' 
    AND u.userid IN ('store_gangnam', 'store_hongdae', 'store_busan', 'store_daegu', 'store_incheon');
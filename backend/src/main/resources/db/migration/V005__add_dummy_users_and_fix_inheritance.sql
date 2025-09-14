-- V005__add_dummy_users_and_fix_inheritance.sql
-- Fix existing users and add dummy data with proper JOINED inheritance

-- First, fix existing testuser and testowner by adding them to their respective child tables
-- Check if testuser exists in gamers table, if not insert
INSERT INTO gamers (uid, nickname, bio, current_rating, usable_point, lockable_point, experience_points, joined_date)
SELECT 
    u.uid,
    'TestGamer',
    'I love playing TCG games!',
    1200,
    1000,
    0,
    100,
    NOW()
FROM users u
WHERE u.username = 'testuser' 
    AND u.user_type = 'GAMER'
    AND NOT EXISTS (SELECT 1 FROM gamers g WHERE g.uid = u.uid);

-- Check if testowner exists in store_owners table, if not insert
INSERT INTO store_owners (uid, store_name, business_number, store_address, store_phone, store_description, is_official, joined_date)
SELECT 
    u.uid,
    'Test Game Store',
    '123-45-67890',
    '서울시 강남구 테헤란로 123',
    '02-1234-5678',
    'Your friendly local game store',
    true,
    NOW()
FROM users u
WHERE u.username = 'testowner' 
    AND u.user_type = 'STORE_OWNER'
    AND NOT EXISTS (SELECT 1 FROM store_owners so WHERE so.uid = u.uid);

-- Now add 30 new users (25 gamers + 5 store owners)
-- Password for all dummy users is: password123 (BCrypt encoded)
SET @password = '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW';

-- Insert 25 gamer users
INSERT INTO users (username, password, email, user_type, created_at, updated_at)
VALUES 
    ('gamer01', @password, 'gamer01@example.com', 'GAMER', NOW(), NOW()),
    ('gamer02', @password, 'gamer02@example.com', 'GAMER', NOW(), NOW()),
    ('gamer03', @password, 'gamer03@example.com', 'GAMER', NOW(), NOW()),
    ('gamer04', @password, 'gamer04@example.com', 'GAMER', NOW(), NOW()),
    ('gamer05', @password, 'gamer05@example.com', 'GAMER', NOW(), NOW()),
    ('dragonmaster', @password, 'dragonmaster@example.com', 'GAMER', NOW(), NOW()),
    ('shadowhunter', @password, 'shadowhunter@example.com', 'GAMER', NOW(), NOW()),
    ('crystalwizard', @password, 'crystalwizard@example.com', 'GAMER', NOW(), NOW()),
    ('firestorm', @password, 'firestorm@example.com', 'GAMER', NOW(), NOW()),
    ('icequeen', @password, 'icequeen@example.com', 'GAMER', NOW(), NOW()),
    ('thunderbolt', @password, 'thunderbolt@example.com', 'GAMER', NOW(), NOW()),
    ('windwalker', @password, 'windwalker@example.com', 'GAMER', NOW(), NOW()),
    ('earthshaker', @password, 'earthshaker@example.com', 'GAMER', NOW(), NOW()),
    ('mysticmage', @password, 'mysticmage@example.com', 'GAMER', NOW(), NOW()),
    ('darknight', @password, 'darknight@example.com', 'GAMER', NOW(), NOW()),
    ('lightbringer', @password, 'lightbringer@example.com', 'GAMER', NOW(), NOW()),
    ('voidseeker', @password, 'voidseeker@example.com', 'GAMER', NOW(), NOW()),
    ('timebender', @password, 'timebender@example.com', 'GAMER', NOW(), NOW()),
    ('soulreaper', @password, 'soulreaper@example.com', 'GAMER', NOW(), NOW()),
    ('stargazer', @password, 'stargazer@example.com', 'GAMER', NOW(), NOW()),
    ('moonshadow', @password, 'moonshadow@example.com', 'GAMER', NOW(), NOW()),
    ('sunblade', @password, 'sunblade@example.com', 'GAMER', NOW(), NOW()),
    ('stormcaller', @password, 'stormcaller@example.com', 'GAMER', NOW(), NOW()),
    ('frostbite', @password, 'frostbite@example.com', 'GAMER', NOW(), NOW()),
    ('noviceplayer', @password, 'noviceplayer@example.com', 'GAMER', NOW(), NOW());

-- Insert 5 store owner users
INSERT INTO users (username, password, email, user_type, created_at, updated_at)
VALUES 
    ('store_gangnam', @password, 'gangnam@gamestore.com', 'STORE_OWNER', NOW(), NOW()),
    ('store_hongdae', @password, 'hongdae@gamestore.com', 'STORE_OWNER', NOW(), NOW()),
    ('store_busan', @password, 'busan@gamestore.com', 'STORE_OWNER', NOW(), NOW()),
    ('store_daegu', @password, 'daegu@gamestore.com', 'STORE_OWNER', NOW(), NOW()),
    ('store_incheon', @password, 'incheon@gamestore.com', 'STORE_OWNER', NOW(), NOW());

-- Insert corresponding gamer data for the 25 new gamers
INSERT INTO gamers (uid, nickname, bio, current_rating, usable_point, lockable_point, experience_points, joined_date)
SELECT 
    u.uid,
    CASE u.username
        WHEN 'gamer01' THEN '초보게이머'
        WHEN 'gamer02' THEN '중수게이머'
        WHEN 'gamer03' THEN '고수게이머'
        WHEN 'gamer04' THEN '프로게이머'
        WHEN 'gamer05' THEN '마스터게이머'
        WHEN 'dragonmaster' THEN '드래곤마스터'
        WHEN 'shadowhunter' THEN '그림자사냥꾼'
        WHEN 'crystalwizard' THEN '크리스탈위저드'
        WHEN 'firestorm' THEN '파이어스톰'
        WHEN 'icequeen' THEN '얼음여왕'
        WHEN 'thunderbolt' THEN '썬더볼트'
        WHEN 'windwalker' THEN '바람걷기'
        WHEN 'earthshaker' THEN '대지진동'
        WHEN 'mysticmage' THEN '신비마법사'
        WHEN 'darknight' THEN '다크나이트'
        WHEN 'lightbringer' THEN '빛의전달자'
        WHEN 'voidseeker' THEN '공허탐색자'
        WHEN 'timebender' THEN '시간조작자'
        WHEN 'soulreaper' THEN '영혼수확자'
        WHEN 'stargazer' THEN '별관측자'
        WHEN 'moonshadow' THEN '달그림자'
        WHEN 'sunblade' THEN '태양검'
        WHEN 'stormcaller' THEN '폭풍소환사'
        WHEN 'frostbite' THEN '동상'
        WHEN 'noviceplayer' THEN '초보자'
        ELSE u.username
    END,
    CASE 
        WHEN u.username LIKE 'gamer0%' THEN CONCAT('게임 경력 ', FLOOR(RAND() * 5) + 1, '년차 플레이어입니다.')
        WHEN u.username = 'dragonmaster' THEN '드래곤 덱의 달인입니다. 모든 드래곤 카드를 수집했습니다.'
        WHEN u.username = 'shadowhunter' THEN '어둠 속성 덱을 주로 사용합니다. 전략적인 플레이를 좋아합니다.'
        WHEN u.username = 'crystalwizard' THEN '크리스탈 마법을 사용하는 덱을 선호합니다.'
        WHEN u.username = 'firestorm' THEN '공격적인 화염 덱으로 빠른 승부를 즐깁니다.'
        WHEN u.username = 'icequeen' THEN '방어적인 얼음 덱으로 상대를 압박합니다.'
        WHEN u.username = 'noviceplayer' THEN '이제 막 TCG를 시작한 초보자입니다.'
        ELSE '열정적인 TCG 플레이어입니다.'
    END,
    FLOOR(RAND() * 2000) + 800,  -- Rating between 800-2800
    FLOOR(RAND() * 50000),       -- Usable points 0-50000
    FLOOR(RAND() * 10000),       -- Lockable points 0-10000
    FLOOR(RAND() * 10000),       -- Experience 0-10000
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 365) DAY)  -- Joined within last year
FROM users u
WHERE u.user_type = 'GAMER' 
    AND u.username IN (
        'gamer01', 'gamer02', 'gamer03', 'gamer04', 'gamer05',
        'dragonmaster', 'shadowhunter', 'crystalwizard', 'firestorm', 'icequeen',
        'thunderbolt', 'windwalker', 'earthshaker', 'mysticmage', 'darknight',
        'lightbringer', 'voidseeker', 'timebender', 'soulreaper', 'stargazer',
        'moonshadow', 'sunblade', 'stormcaller', 'frostbite', 'noviceplayer'
    );

-- Insert corresponding store owner data for the 5 new store owners
INSERT INTO store_owners (uid, store_name, business_number, store_address, store_phone, store_description, is_official, joined_date)
SELECT 
    u.uid,
    CASE u.username
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
    CASE u.username
        WHEN 'store_gangnam' THEN '서울시 강남구 테헤란로 456'
        WHEN 'store_hongdae' THEN '서울시 마포구 홍대입구역 3번출구'
        WHEN 'store_busan' THEN '부산시 해운대구 마린시티 789'
        WHEN 'store_daegu' THEN '대구시 중구 동성로 321'
        WHEN 'store_incheon' THEN '인천시 연수구 송도동 654'
        ELSE '서울시 중구'
    END,
    CASE u.username
        WHEN 'store_gangnam' THEN '02-555-1234'
        WHEN 'store_hongdae' THEN '02-333-5678'
        WHEN 'store_busan' THEN '051-777-9012'
        WHEN 'store_daegu' THEN '053-222-3456'
        WHEN 'store_incheon' THEN '032-888-7890'
        ELSE '02-000-0000'
    END,
    CASE u.username
        WHEN 'store_gangnam' THEN '강남 최대 규모의 TCG 전문점입니다. 다양한 카드와 이벤트를 제공합니다.'
        WHEN 'store_hongdae' THEN '젊은 층이 많이 찾는 홍대의 TCG 전문점입니다. 매주 토너먼트 개최!'
        WHEN 'store_busan' THEN '부산 해운대의 프리미엄 카드게임 전문점입니다.'
        WHEN 'store_daegu' THEN '대구 시내 중심가의 편리한 위치에 있는 TCG 샵입니다.'
        WHEN 'store_incheon' THEN '송도 신도시의 현대적인 TCG 게임 공간입니다.'
        ELSE 'TCG 전문점입니다.'
    END,
    CASE 
        WHEN u.username IN ('store_gangnam', 'store_hongdae') THEN true
        ELSE false
    END,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 730) DAY)  -- Joined within last 2 years
FROM users u
WHERE u.user_type = 'STORE_OWNER' 
    AND u.username IN ('store_gangnam', 'store_hongdae', 'store_busan', 'store_daegu', 'store_incheon');

-- Add some additional gamer stats for variety
UPDATE gamers g
JOIN users u ON g.uid = u.uid
SET 
    g.total_games = FLOOR(RAND() * 500),
    g.total_wins = FLOOR(g.total_games * (0.3 + RAND() * 0.4)),  -- Win rate between 30-70%
    g.total_losses = g.total_games - g.total_wins
WHERE u.username IN (
    'gamer01', 'gamer02', 'gamer03', 'gamer04', 'gamer05',
    'dragonmaster', 'shadowhunter', 'crystalwizard', 'firestorm', 'icequeen',
    'thunderbolt', 'windwalker', 'earthshaker', 'mysticmage', 'darknight',
    'lightbringer', 'voidseeker', 'timebender', 'soulreaper', 'stargazer',
    'moonshadow', 'sunblade', 'stormcaller', 'frostbite', 'noviceplayer'
);

-- Update ranks based on rating
UPDATE gamers 
SET rank = CASE 
    WHEN current_rating >= 2500 THEN 'GRANDMASTER'
    WHEN current_rating >= 2000 THEN 'MASTER'
    WHEN current_rating >= 1700 THEN 'DIAMOND'
    WHEN current_rating >= 1500 THEN 'PLATINUM'
    WHEN current_rating >= 1300 THEN 'GOLD'
    WHEN current_rating >= 1100 THEN 'SILVER'
    WHEN current_rating >= 900 THEN 'BRONZE'
    ELSE 'IRON'
END;
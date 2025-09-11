-- V004: Update Card Schema to JOINED Inheritance Strategy
-- Based on ERD v0.3 specifications - Each card type has its own table

-- Drop existing foreign key constraints if they exist
SET FOREIGN_KEY_CHECKS = 0;

-- Drop existing tables
DROP TABLE IF EXISTS deck_details;
DROP TABLE IF EXISTS card_decks;
DROP TABLE IF EXISTS cards;

-- Create base cards table (contains common fields only)
CREATE TABLE cards (
    card_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_name VARCHAR(255) NOT NULL,
    card_img VARCHAR(500),
    description TEXT,
    card_color VARCHAR(20),
    rarity VARCHAR(20),
    cost INT,
    card_number VARCHAR(50),
    card_type VARCHAR(31) NOT NULL, -- discriminator
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_card_type (card_type),
    INDEX idx_card_name (card_name),
    INDEX idx_card_color (card_color),
    INDEX idx_rarity (rarity),
    INDEX idx_cost (cost)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create leaders table
CREATE TABLE leaders (
    card_id BIGINT PRIMARY KEY,
    leader_skill TEXT,
    is_awakened BOOLEAN DEFAULT FALSE,
    burst_slot1 INT NOT NULL CHECK (burst_slot1 BETWEEN 1 AND 3),
    burst_slot2 INT NOT NULL CHECK (burst_slot2 BETWEEN 1 AND 3),
    burst_slot3 INT NOT NULL CHECK (burst_slot3 BETWEEN 1 AND 3),
    
    FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create units table
CREATE TABLE units (
    card_id BIGINT PRIMARY KEY,
    power INT NOT NULL,
    burst_value INT NOT NULL CHECK (burst_value BETWEEN 1 AND 3),
    
    FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create items table
CREATE TABLE items (
    card_id BIGINT PRIMARY KEY,
    effect TEXT,
    activation_condition TEXT,
    is_consumable BOOLEAN DEFAULT TRUE,
    burst_value INT NOT NULL CHECK (burst_value BETWEEN 1 AND 3),
    
    FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create fields table
CREATE TABLE fields (
    card_id BIGINT PRIMARY KEY,
    field_effect TEXT,
    affected_colors VARCHAR(255),
    affected_types VARCHAR(255),
    burst_value INT NOT NULL CHECK (burst_value BETWEEN 1 AND 3),
    
    FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create spells table
CREATE TABLE spells (
    card_id BIGINT PRIMARY KEY,
    spell_effect TEXT,
    target_type VARCHAR(100),
    burst_value INT NOT NULL CHECK (burst_value BETWEEN 1 AND 3),
    
    FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Recreate card_decks table
CREATE TABLE card_decks (
    deck_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    deck_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(uid) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_deck_name (deck_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Recreate deck_details table
CREATE TABLE deck_details (
    detail_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    deck_id BIGINT NOT NULL,
    card_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (deck_id) REFERENCES card_decks(deck_id) ON DELETE CASCADE,
    FOREIGN KEY (card_id) REFERENCES cards(card_id) ON DELETE CASCADE,
    UNIQUE KEY uk_deck_card (deck_id, card_id),
    INDEX idx_deck_id (deck_id),
    INDEX idx_card_id (card_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
-- Leader Card
INSERT INTO cards (card_name, card_color, rarity, cost, description, card_type)
VALUES ('Dragon Lord', 'RED', 'LEGENDARY', 0, 'Mighty dragon leader', 'LEADER');
SET @leader_id = LAST_INSERT_ID();
INSERT INTO leaders (card_id, leader_skill, is_awakened, burst_slot1, burst_slot2, burst_slot3)
VALUES (@leader_id, 'All dragon units gain +2 power', FALSE, 1, 2, 3);

-- Unit Card
INSERT INTO cards (card_name, card_color, rarity, cost, description, card_type)
VALUES ('Fire Drake', 'RED', 'RARE', 3, 'A fierce dragon unit', 'UNIT');
SET @unit_id = LAST_INSERT_ID();
INSERT INTO units (card_id, power, burst_value)
VALUES (@unit_id, 5, 2);

-- Item Card
INSERT INTO cards (card_name, card_color, rarity, cost, description, card_type)
VALUES ('Healing Potion', 'COLORLESS', 'COMMON', 2, 'Restore health', 'ITEM');
SET @item_id = LAST_INSERT_ID();
INSERT INTO items (card_id, effect, activation_condition, is_consumable, burst_value)
VALUES (@item_id, 'Restore 3 life points', 'Anytime', TRUE, 1);

-- Field Card
INSERT INTO cards (card_name, card_color, rarity, cost, description, card_type)
VALUES ('Volcanic Arena', 'RED', 'RARE', 4, 'Enhances fire attacks', 'FIELD');
SET @field_id = LAST_INSERT_ID();
INSERT INTO fields (card_id, field_effect, affected_colors, burst_value)
VALUES (@field_id, 'RED units gain +1 power', 'RED', 2);

-- Spell Card
INSERT INTO cards (card_name, card_color, rarity, cost, description, card_type)
VALUES ('Lightning Bolt', 'YELLOW', 'COMMON', 2, 'Direct damage spell', 'SPELL');
SET @spell_id = LAST_INSERT_ID();
INSERT INTO spells (card_id, spell_effect, target_type, burst_value)
VALUES (@spell_id, 'Deal 3 damage to target', 'Any target', 1);

-- Additional sample cards
-- Another Leader
INSERT INTO cards (card_name, card_color, rarity, cost, description, card_type)
VALUES ('Ocean Sage', 'BLUE', 'LEGENDARY', 0, 'Master of the seas', 'LEADER');
SET @leader2_id = LAST_INSERT_ID();
INSERT INTO leaders (card_id, leader_skill, is_awakened, burst_slot1, burst_slot2, burst_slot3)
VALUES (@leader2_id, 'Draw 1 extra card per turn', FALSE, 2, 2, 1);

-- Another Unit
INSERT INTO cards (card_name, card_color, rarity, cost, description, card_type)
VALUES ('Thunder Knight', 'YELLOW', 'SUPER_RARE', 5, 'Lightning-fast warrior', 'UNIT');
SET @unit2_id = LAST_INSERT_ID();
INSERT INTO units (card_id, power, burst_value)
VALUES (@unit2_id, 7, 3);

SET FOREIGN_KEY_CHECKS = 1;

-- Verify data integrity
SELECT 'Cards Table Count:' as Info, COUNT(*) as Count FROM cards
UNION ALL
SELECT 'Leaders Count:', COUNT(*) FROM leaders
UNION ALL
SELECT 'Units Count:', COUNT(*) FROM units
UNION ALL
SELECT 'Items Count:', COUNT(*) FROM items
UNION ALL
SELECT 'Fields Count:', COUNT(*) FROM fields
UNION ALL
SELECT 'Spells Count:', COUNT(*) FROM spells;
-- V003: Update Card Schema to Single Table Inheritance Strategy
-- Based on ERD v0.3 specifications

-- Drop existing foreign key constraints if they exist
SET FOREIGN_KEY_CHECKS = 0;

-- Drop old table structure if exists
DROP TABLE IF EXISTS deck_details;
DROP TABLE IF EXISTS card_decks;
DROP TABLE IF EXISTS leaders;
DROP TABLE IF EXISTS units;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS fields;
DROP TABLE IF EXISTS spells;
DROP TABLE IF EXISTS cards;

-- Create main cards table with Single Table Inheritance
CREATE TABLE cards (
    card_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    card_type VARCHAR(31) NOT NULL, -- Discriminator column
    card_name VARCHAR(255) NOT NULL,
    card_img VARCHAR(500),
    description TEXT,
    card_color VARCHAR(20),
    rarity VARCHAR(20),
    cost INT,
    card_number VARCHAR(50),
    
    -- Leader specific fields
    leader_skill TEXT,
    is_awakened BOOLEAN DEFAULT FALSE,
    burst_slot1 INT,
    burst_slot2 INT,
    burst_slot3 INT,
    
    -- Unit specific fields
    power INT,
    
    -- Item specific fields
    effect TEXT,
    activation_condition TEXT,
    is_consumable BOOLEAN DEFAULT TRUE,
    
    -- Field specific fields
    field_effect TEXT,
    affected_colors VARCHAR(255),
    affected_types VARCHAR(255),
    
    -- Spell specific fields
    spell_effect TEXT,
    target_type VARCHAR(100),
    
    -- Shared burst_value for Unit, Item, Field, Spell
    burst_value INT,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_card_type (card_type),
    INDEX idx_card_name (card_name),
    INDEX idx_card_color (card_color),
    INDEX idx_rarity (rarity),
    INDEX idx_cost (cost)
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

-- Insert sample data for testing
INSERT INTO cards (card_type, card_name, card_color, rarity, cost, description, leader_skill, is_awakened, burst_slot1, burst_slot2, burst_slot3)
VALUES ('LEADER', 'Dragon Lord', 'RED', 'LEGENDARY', 0, 'Mighty dragon leader', 'All dragon units gain +2 power', FALSE, 1, 2, 3);

INSERT INTO cards (card_type, card_name, card_color, rarity, cost, description, power, burst_value)
VALUES ('UNIT', 'Fire Drake', 'RED', 'RARE', 3, 'A fierce dragon unit', 5, 2);

INSERT INTO cards (card_type, card_name, card_color, rarity, cost, description, effect, activation_condition, is_consumable, burst_value)
VALUES ('ITEM', 'Healing Potion', 'COLORLESS', 'COMMON', 2, 'Restore health', 'Restore 3 life points', 'Anytime', TRUE, 1);

INSERT INTO cards (card_type, card_name, card_color, rarity, cost, description, field_effect, affected_colors, burst_value)
VALUES ('FIELD', 'Volcanic Arena', 'RED', 'RARE', 4, 'Enhances fire attacks', 'RED units gain +1 power', 'RED', 2);

INSERT INTO cards (card_type, card_name, card_color, rarity, cost, description, spell_effect, target_type, burst_value)
VALUES ('SPELL', 'Lightning Bolt', 'YELLOW', 'COMMON', 2, 'Direct damage spell', 'Deal 3 damage to target', 'Any target', 1);

SET FOREIGN_KEY_CHECKS = 1;

-- Add constraints after table creation
ALTER TABLE cards
ADD CONSTRAINT chk_leader_burst CHECK (
    card_type != 'LEADER' OR (burst_slot1 BETWEEN 1 AND 3 AND burst_slot2 BETWEEN 1 AND 3 AND burst_slot3 BETWEEN 1 AND 3)
);

ALTER TABLE cards
ADD CONSTRAINT chk_burst_value CHECK (
    card_type = 'LEADER' OR burst_value IS NULL OR burst_value BETWEEN 1 AND 3
);
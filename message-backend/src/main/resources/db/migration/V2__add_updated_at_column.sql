ALTER TABLE messages ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
UPDATE messages SET updated_at = created_at; 
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS user_notifications;
DROP TABLE IF EXISTS feedbacks;
DROP TABLE IF EXISTS wheel_items;
DROP TABLE IF EXISTS custom_wheels;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS outdoor_foods;
DROP TABLE IF EXISTS outdoor_themes;
DROP TABLE IF EXISTS user_fridges;
DROP TABLE IF EXISTS recipe_ingredients;
DROP TABLE IF EXISTS recipes;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

CREATE DATABASE IF NOT EXISTS food_app DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE food_app;

CREATE TABLE users (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255),
                       auth_provider ENUM('local', 'google') DEFAULT 'local',
                       role ENUM('user', 'admin') DEFAULT 'user',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_notifications (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    user_id INT NOT NULL,
                                    message TEXT NOT NULL,
                                    is_read BOOLEAN DEFAULT FALSE,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE feedbacks (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           user_id INT,
                           rating INT CHECK (rating >= 1 AND rating <= 5),
                           comment TEXT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE categories (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE recipes (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         category_id INT,
                         name VARCHAR(255) NOT NULL,
                         prep_time INT COMMENT 'Thời gian nấu (phút)',
                         servings INT COMMENT 'Số người ăn',
                         difficulty ENUM('dễ', 'trung bình', 'khó'),
                         instructions TEXT NOT NULL,
                         video_url VARCHAR(255),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE recipe_ingredients (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    recipe_id INT NOT NULL,
                                    ingredient_name VARCHAR(150) NOT NULL,
                                    quantity VARCHAR(100),
                                    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

CREATE TABLE user_fridges (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT NOT NULL,
                              ingredient_name VARCHAR(150) NOT NULL,
                              purchase_date DATE NOT NULL,
                              expiry_date DATE,
                              notified_flag BOOLEAN DEFAULT FALSE,
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE outdoor_themes (
                                id INT AUTO_INCREMENT PRIMARY KEY,
                                name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE outdoor_foods (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               theme_id INT,
                               name VARCHAR(255) NOT NULL,
                               FOREIGN KEY (theme_id) REFERENCES outdoor_themes(id) ON DELETE SET NULL
);

CREATE TABLE locations (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           food_id INT NOT NULL,
                           name VARCHAR(255) NOT NULL,
                           address VARCHAR(255) NOT NULL,
                           gg_maps_rating FLOAT CHECK (gg_maps_rating >= 0 AND gg_maps_rating <= 5),
                           is_admin_suggested BOOLEAN DEFAULT FALSE,
                           FOREIGN KEY (food_id) REFERENCES outdoor_foods(id) ON DELETE CASCADE
);

CREATE TABLE custom_wheels (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               user_id INT NOT NULL,
                               wheel_type ENUM('indoor', 'outdoor') NOT NULL,
                               name VARCHAR(255),
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE wheel_items (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             wheel_id INT NOT NULL,
                             item_name VARCHAR(255) NOT NULL,
                             is_excluded BOOLEAN DEFAULT FALSE,
                             FOREIGN KEY (wheel_id) REFERENCES custom_wheels(id) ON DELETE CASCADE
);

SET FOREIGN_KEY_CHECKS = 1;

--update db:
USE food_app;

ALTER TABLE users
    ADD COLUMN avatar_url VARCHAR(500) NULL;
ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE recipes
    ADD COLUMN image_url VARCHAR(500) NULL,
    ADD COLUMN submitted_by_user_id INT NULL,
    ADD COLUMN approval_status ENUM('pending', 'approved', 'rejected')
        NOT NULL DEFAULT 'approved',
    ADD CONSTRAINT fk_recipes_submitter
        FOREIGN KEY (submitted_by_user_id)
        REFERENCES users(id) ON DELETE SET NULL;

ALTER TABLE feedbacks
    ADD COLUMN moderation_status ENUM('pending', 'approved') NOT NULL DEFAULT 'approved';

USE food_app;

-- Add only fields missing from recipes in the supplied old schema.
ALTER TABLE recipes
    ADD COLUMN description TEXT NULL,
    ADD COLUMN calories_kcal INT NULL,
    ADD COLUMN cooking_tip TEXT NULL,
    ADD COLUMN is_recommended BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN dish_type ENUM('MAIN_DISH','DRINK','DESSERT') NOT NULL DEFAULT 'MAIN_DISH',
    ADD COLUMN region ENUM('ASIAN','EUROPEAN','AMERICAN','AFRICAN','OCEANIAN','STREET_FOOD') NULL DEFAULT NULL,
    ADD COLUMN country VARCHAR(100) NULL;

-- Preserve and convert existing difficulty values.
ALTER TABLE recipes MODIFY COLUMN difficulty
    ENUM('dễ','trung bình','khó','easy','medium','hard');
UPDATE recipes SET difficulty = CASE difficulty
                                    WHEN 'dễ' THEN 'easy'
                                    WHEN 'trung bình' THEN 'medium'
                                    WHEN 'khó' THEN 'hard'
                                    ELSE difficulty END;
ALTER TABLE recipes MODIFY COLUMN difficulty ENUM('easy','medium','hard');

-- New chatbot tables, absent from the old team schema.
CREATE TABLE IF NOT EXISTS chat_sessions (
                                             id INT AUTO_INCREMENT PRIMARY KEY,
                                             user_id INT NOT NULL,
                                             title VARCHAR(255) NOT NULL DEFAULT 'New conversation',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
CREATE TABLE IF NOT EXISTS chat_messages (
                                             id INT AUTO_INCREMENT PRIMARY KEY,
                                             session_id INT NOT NULL,
                                             sender ENUM('USER','ASSISTANT') NOT NULL,
    content MEDIUMTEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_messages_session (session_id, id),
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id) ON DELETE CASCADE
    );
CREATE TABLE IF NOT EXISTS ai_recipes (
                                          id INT AUTO_INCREMENT PRIMARY KEY,
                                          user_id INT NOT NULL,
                                          source_message_id INT NULL,
                                          name VARCHAR(255) NOT NULL,
    description TEXT,
    prep_time INT,
    servings INT,
    difficulty ENUM('easy', 'medium', 'hard'),
    instructions MEDIUMTEXT NOT NULL,
    category_id INT NULL,
    dish_type ENUM('MAIN_DISH','DRINK','DESSERT') NOT NULL DEFAULT 'MAIN_DISH',
    region ENUM('ASIAN','EUROPEAN','AMERICAN','AFRICAN','OCEANIAN','STREET_FOOD') NULL,
    country VARCHAR(100) NULL,
    image_url VARCHAR(500) NULL,
    video_url VARCHAR(255) NULL,
    calories_kcal INT NULL,
    cooking_tip TEXT NULL,
    published_recipe_id INT NULL,
    UNIQUE KEY uq_ai_published_recipe (published_recipe_id),
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (published_recipe_id) REFERENCES recipes(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ai_recipes_user_created (user_id, created_at),
    CHECK (prep_time IS NULL OR prep_time > 0),
    CHECK (servings IS NULL OR servings > 0),
    FOREIGN KEY (source_message_id) REFERENCES chat_messages(id) ON DELETE SET NULL
    );
CREATE TABLE IF NOT EXISTS ai_recipe_ingredients (
                                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                                     ai_recipe_id INT NOT NULL,
                                                     ingredient_name VARCHAR(150) NOT NULL,
    quantity VARCHAR(100),
    FOREIGN KEY (ai_recipe_id) REFERENCES ai_recipes(id) ON DELETE CASCADE
    );

ALTER TABLE users ADD COLUMN google_sub VARCHAR(255) NULL,
    ADD UNIQUE KEY uq_users_google_sub (google_sub);

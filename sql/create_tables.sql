USE spice_ec_db

CREATE TABLE users(
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone_number VARCHAR(100) NOT NULL,
    active_flag BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_email_format CHECK (email REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'),
    CONSTRAINT check_phone_format CHECK (phone_number REGEXP '^0[0-9]{9,10}$')
);

SHOW WARNINGS;

CREATE TABLE mfa_otp(
    otp_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NULL,
    email VARCHAR(255) NOT NULL UNIQUE, 
    otp INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

SHOW WARNINGS;

CREATE TABLE administrator(
    administrator_id INT PRIMARY KEY AUTO_INCREMENT,
    administrator_name VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

SHOW WARNINGS;

CREATE TABLE address(
    address_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    receiver_name VARCHAR(50) NOT NULL,
    receiver_name_reading VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    postal_code VARCHAR(15) NOT NULL,
    delivery_address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_phone_format_address CHECK (phone_number REGEXP '^0[0-9]{9,10}$'),
    CONSTRAINT check_postal_code_format CHECK (postal_code REGEXP '^[0-9]{3}-[0-9]{4}$'), 
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

SHOW WARNINGS;

CREATE TABLE spices(
    spice_id INT PRIMARY KEY AUTO_INCREMENT,
    spice_name_jp VARCHAR(20) NOT NULL UNIQUE,
    spice_name_en VARCHAR(20) NOT NULL,
    origin_country VARCHAR(50) NOT NULL,
    price_range VARCHAR(20) NOT NULL,
    effect VARCHAR(255) NOT NULL,
    example_dishes VARCHAR(255) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    overview TEXT NOT NULL
);

SHOW WARNINGS;

CREATE TABLE products(
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    api_item_id VARCHAR(255) NOT NULL UNIQUE,
    product_name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    capacity INT NOT NULL,
    origin_country VARCHAR(50),
    image_url VARCHAR(255) NOT NULL,
    product_description TEXT NOT NULL,
    spice_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (spice_id) REFERENCES spices(spice_id)
);

SHOW WARNINGS;

CREATE TABLE product_status(
    status_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    stock_quantity INT UNSIGNED NOT NULL,
    sales_volume INT UNSIGNED NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active_flag BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

SHOW WARNINGS;

CREATE TABLE favorite_products(
    favorite_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    product_id INT,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

SHOW WARNINGS;

CREATE TABLE recipe(
    recipe_id INT PRIMARY KEY AUTO_INCREMENT,
    meal_id VARCHAR(20) NOT NULL,
    recipe_title VARCHAR(255) NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    instructions TEXT NOT NULL,
    ingredients_jp TEXT NOT NULL,
    ingredients_en TEXT NOT NULL,
    source_language VARCHAR(10) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active_flag BOOLEAN DEFAULT TRUE
);

SHOW WARNINGS;

CREATE TABLE favorite_recipes(
    favorite_recipe_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    recipe_id INT,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (recipe_id) REFERENCES recipe(recipe_id)
);

SHOW WARNINGS;

CREATE TABLE cart(
    cart_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    subtotal INT UNSIGNED NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

SHOW WARNINGS;

CREATE TABLE cart_items (
    cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    product_quantity INT UNSIGNED NOT NULL,
    UNIQUE KEY unique_cart_product (cart_id, product_id)
);

SHOW WARNINGS;

CREATE TABLE order_record(
    order_record_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    total_amount INT NOT NULL,
    address_id INT,
    ordered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_flag BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (address_id) REFERENCES address(address_id)
);

SHOW WARNINGS;

CREATE TABLE order_items(
    order_item_id INT PRIMARY KEY AUTO_INCREMENT,
    order_record_id INT,
    product_id INT,
    product_quantity INT UNSIGNED NOT NULL,
    FOREIGN KEY (order_record_id) REFERENCES order_record(order_record_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

SHOW WARNINGS;

CREATE TABLE recipe_spice(
    rel_id INT PRIMARY KEY AUTO_INCREMENT,
    recipe_id INT,
    spice_id INT,
    FOREIGN KEY (recipe_id) REFERENCES recipe(recipe_id),
    FOREIGN KEY (spice_id) REFERENCES spices(spice_id)
);

SHOW WARNINGS;
CREATE TABLE dalcom_user_tm (
                                user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                username VARCHAR(50) NOT NULL UNIQUE,
                                password VARCHAR(100) NOT NULL,
                                email VARCHAR(100),
                                role VARCHAR(50) NOT NULL,
                                reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                mod_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

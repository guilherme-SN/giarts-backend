CREATE TABLE users_roles (
	user_id BIGINT NOT NULL,
	role_id BIGINT NOT NULL,

	PRIMARY KEY (user_id, role_id),
	CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
	CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

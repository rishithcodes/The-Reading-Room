USE reading_room;

-- Run this once if reading_room was created from the earlier seed script.
-- These are BCrypt hashes for the documented demo passwords.
UPDATE users SET password_hash = '$2a$12$M5cQBElnybmKrKpvl8SE..Fh0vRQKr5vZaL8QpujFwvfzWZH9SQby'
WHERE email = 'admin@readingroom.com';
UPDATE users SET password_hash = '$2a$12$3.VRV9xfie67hXPpJlqSCejwcUgMy06OcLvJBcMXccqncqLN99BhW'
WHERE email = 'librarian@readingroom.com';
UPDATE users SET password_hash = '$2a$12$fzU9toL63N/Q3B2iHFSpG.i2LoAu9R1cQgGPDATOeIY7HA2jh9YiS'
WHERE role = 'STUDENT';

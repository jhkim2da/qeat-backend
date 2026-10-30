-- 부하 테스트용 데이터: 승인된 부스 1개, 테이블 100개(토큰 lt0001~lt0100), 메뉴 20개
-- 앱을 한 번 실행해 테이블이 생성된 뒤에 넣습니다. 다시 실행해도 기존 테스트 데이터를 지우고 새로 만듭니다.
-- 실행: docker exec -i qeat-mysql sh -c 'mysql --default-character-set=utf8mb4 -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < loadtest/seed.sql
SET NAMES utf8mb4;
SET @old = (SELECT booth_id FROM `tables` WHERE table_token = 'lt0001' LIMIT 1);
DELETE FROM menus WHERE booth_id = @old;
DELETE FROM `tables` WHERE booth_id = @old;
DELETE FROM booths WHERE id = @old;

INSERT INTO booths (name, description, created_at, owner_id, bank, account_number, booth_status, `open`, open_time, close_time)
VALUES ('[loadtest] 부스', '부하 테스트용 부스', CURDATE(), 999999, 'KB', '000000000000', 'APPROVED', 1, NULL, NULL);
SET @booth = LAST_INSERT_ID();

INSERT INTO `tables` (booth_id, table_number, table_token, qr_image_url, active)
WITH RECURSIVE seq(n) AS (SELECT 1 UNION ALL SELECT n + 1 FROM seq WHERE n < 100)
SELECT @booth, n, CONCAT('lt', LPAD(n, 4, '0')), NULL, 1 FROM seq;

INSERT INTO menus (booth_id, name, description, price, sold_out, image_url, category)
WITH RECURSIVE seq(n) AS (SELECT 1 UNION ALL SELECT n + 1 FROM seq WHERE n < 20)
SELECT @booth, CONCAT('메뉴 ', n), '부하 테스트용 메뉴 설명입니다', 5000 + n * 100, 0, NULL,
       ELT(1 + (n % 3), 'MAIN_FOOD', 'SIDE_FOOD', 'DRINK') FROM seq;

SELECT @booth AS booth_id,
       (SELECT COUNT(*) FROM `tables` WHERE booth_id = @booth) AS tables_count,
       (SELECT COUNT(*) FROM menus WHERE booth_id = @booth) AS menus_count;

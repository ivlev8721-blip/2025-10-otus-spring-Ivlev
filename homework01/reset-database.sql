-- Скрипт для очистки базы данных VinylLib
-- Выполните этот скрипт в PostgreSQL перед перезапуском приложения

-- Удаляем все таблицы
DROP TABLE IF EXISTS review CASCADE;
DROP TABLE IF EXISTS collection_item CASCADE;
DROP TABLE IF EXISTS track CASCADE;
DROP TABLE IF EXISTS album CASCADE;
DROP TABLE IF EXISTS genre CASCADE;
DROP TABLE IF EXISTS user_authority CASCADE;
DROP TABLE IF EXISTS authority CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Удаляем служебные таблицы Spring Batch
DROP TABLE IF EXISTS batch_step_execution_context CASCADE;
DROP TABLE IF EXISTS batch_job_execution_context CASCADE;
DROP TABLE IF EXISTS batch_step_execution CASCADE;
DROP TABLE IF EXISTS batch_job_execution_params CASCADE;
DROP TABLE IF EXISTS batch_job_execution CASCADE;
DROP TABLE IF EXISTS batch_job_instance CASCADE;

-- Удаляем таблицу Liquibase (это ключевой момент!)
DROP TABLE IF EXISTS databasechangeloglock CASCADE;
DROP TABLE IF EXISTS databasechangelog CASCADE;

-- Готово! Теперь перезапустите приложение

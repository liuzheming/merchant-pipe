-- Create database for merchant-pipe
CREATE DATABASE IF NOT EXISTS merchant_pipe
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

-- Use database
USE merchant_pipe;

-- Create tables
SOURCE /Users/lzm/Documents/beikeRepo/merchant-pipe/database/V12.006__ACTION_PIPE.sql;
SOURCE /Users/lzm/Documents/beikeRepo/merchant-pipe/database/V12.022__PROCESS_PIPE.sql;

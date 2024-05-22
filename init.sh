#!/bin/bash
docker-compose down
docker volume rm idgenerator_mysql1_data
docker volume rm idgenerator_mysql2_data
docker volume rm idgenerator_mysql3_data
docker volume rm idgenerator_mysql4_data
docker volume rm idgenerator_mysql5_data
docker volume rm idgenerator_mysql6_data
rm -rf mysql1_data
rm -rf mysql2_data
rm -rf mysql3_data
rm -rf mysql4_data
rm -rf mysql5_data
rm -rf mysql6_data
docker-compose up --force-recreate
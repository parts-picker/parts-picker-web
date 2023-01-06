docker rm -f local_test_db
docker run -d --name local_test_db -p 5433:5432 -e POSTGRES_PASSWORD=local -e POSTGRES_DB=pick_db postgres:13.3-alpine
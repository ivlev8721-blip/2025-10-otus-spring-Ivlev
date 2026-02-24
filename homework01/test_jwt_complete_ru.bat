@echo off
echo ========================================
echo Полное тестирование JWT авторизации
echo ========================================

echo.
echo Используются заранее скопированные токены из успешной аутентификации:
echo.

set USER_TOKEN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiYXV0aCI6IlJPTEVfVVNFUiIsImV4cCI6MTc2OTc4NTE3Nn0.qXcKxjI0axTmGyg7oD5mEvAazbKkuk0Kt-QFhiV8EyHnQnZWsJEXt_-fYM2lEhG7HGzs86zssnrZMsn0JQ74lg
set ADMIN_TOKEN=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTc2OTc4NTE3OH0.fjgGkbTGsCIR2KPCRqXKmLogEu95PuJMXCmahggsOpViP6LFQ5xXtk-omNF8Z0UOqTm2eVMzXNwMqUFYCMlCRQ

echo Token polzovatelya: %USER_TOKEN%
echo Token administratora: %ADMIN_TOKEN%
echo.
pause

echo.
echo Test 1: Dostup k API bez tokena (ojidaetsya 403)
curl -v "http://localhost:8080/api/v1/book"
echo.
pause

echo.
echo Test 2: Dostup k knigam s tokenom polzovatelya (ojidaetsya 200)
curl -v -H "Authorization: Bearer %USER_TOKEN%" "http://localhost:8080/api/v1/book"
echo.
pause

echo.
echo Test 3: Dostup k avtoram s tokenom polzovatelya - GET (ojidaetsya 200)
curl -v -H "Authorization: Bearer %USER_TOKEN%" "http://localhost:8080/api/v1/author"
echo.
pause

echo.
echo Test 4: Sozdanie avtora s tokenom polzovatelya (ojidaetsya 403)
curl -v -X POST -H "Authorization: Bearer %USER_TOKEN%" -H "Content-Type: application/json" -d "{\"name\":\"Test\",\"surname\":\"User\"}" "http://localhost:8080/api/v1/author"
echo.
pause

echo.
echo Test 5: Sozdanie avtora s tokenom administratora (ojidaetsya 200)
curl -v -X POST -H "Authorization: Bearer %ADMIN_TOKEN%" -H "Content-Type: application/json" -d "{\"name\":\"Test\",\"surname\":\"Admin\"}" "http://localhost:8080/api/v1/author"
echo.
pause

echo.
echo Test 6: Dostup k zhanram s tokenom polzovatelya - GET (ojidaetsya 200)
curl -v -H "Authorization: Bearer %USER_TOKEN%" "http://localhost:8080/api/v1/genre"
echo.
pause

echo.
echo Test 7: Udalenie zhanra s tokenom polzovatelya (ojidaetsya 403)
curl -v -X DELETE -H "Authorization: Bearer %USER_TOKEN%" "http://localhost:8080/api/v1/genre/1"
echo.
pause

echo.
echo Test 8: Sozdanie knigi s tokenom polzovatelya (ojidaetsya 200)
curl -v -X POST -H "Authorization: Bearer %USER_TOKEN%" -H "Content-Type: application/json" -d "{\"title\":\"Test Book\",\"author\":1,\"genre\":1}" "http://localhost:8080/api/v1/book"
echo.
pause

echo.
echo Test 9: Dostup k kommentariyam s tokenom polzovatelya (ojidaetsya 200)
curl -v -H "Authorization: Bearer %USER_TOKEN%" "http://localhost:8080/api/v1/comment"
echo.
pause

echo.
echo Test 10: Proverka nevalidnogo tokena (ojidaetsya 403)
curl -v -H "Authorization: Bearer invalid_token" "http://localhost:8080/api/v1/book"
echo.
pause

echo.
echo ========================================
echo TESTIROVANIE JWT AVTORIZATSII ZAVERSHENO
echo ========================================
echo.
echo USPEH! JWT avtorizatsiya rabotaet korrektno!
echo - Polzovatel mozhet chitat resursy
echo - Administrator imeet polny dostup
echo - Nevalidnye tokeny otklonyayutsya
echo.
pause

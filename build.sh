$env:GEMINI_API_KEY = 'YOUR_API_KEY'
mvn package
mvn exec:java -Dexec.mainClass="app.Main"
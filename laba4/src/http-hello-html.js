const http = require ("http");
const sqlite3 = require("sqlite3").verbose();

const dbFilename = 'Music.sqlite'
const httpPort = 3000;

console.log("create db")
const db = new sqlite3.Database(dbFilename);

console.log("create table")
db.run("CREATE TABLE IF NOT EXISTS Music (track TEXT, singer TEXT, album TEXT, length INT);", error => {
    if (error) {
        console.log("cannot create table: " + error);
        return;
    }

    /*console.log("fill table whith data");
    const statement =db.prepare("INSERT INTO Music VALUES (?, ?, ?, ?);");
    statement.run("Primo Victoria", "Sabaton", "Primo Victoria", 251);
    statement.run("Primo Victoria", "RADIO TAPOK", undefined, 251);
    statement.run("Links 2 3 4", "Rammstein", "Mutter", 217);
    statement.run("Stalingrad", "Sabaton", "Primo Victoria", 318);
    statement.run("RADIO", "Rammstein", "RADIO", 277);
    statement.finalize();*/

    console.log("created db at " + dbFilename);

});


function generateResultHtml(music) {
    return `
    <!DOCTYPE html>
    <html lang="ru">
        <head>
            <title>laboratory operation #4.0.3.</title>
            <meta charset="UTF-8">
        </head>
        <body>
            <h1>Static table with http-hello-html schema.</h1>
            <table>
                <thead>Music list:</thead>
                <tr><td>track</td><td>singer</td><td>album</td><td>length</td></tr>
                ${music}
            </table>
        </body>
    </html>
    `;
}

function renderMusic (rows) {
        return rows
            .map(row => `<tr><td>${row.track}</td><td>${row.singer}</td><td>${row.album}</td><td>${row.length}</td></tr>`)
            .reduce((result, track) => result + track + '\n');
}

function writeToHttpResponse (response, s, statusCode, contentType)
{
    response.writeHead (statusCode, {"Content-Type":contentType});
    response.write (s);
    response.end();
}

function processMusic (response)
{
    return (error, rows) => {
        if (error) {
            writeToHttpResponse(response, "Cannot retrieve data from db: " + error, 500, "text/plain");
            return ;
        }
        const music = renderMusic(rows);
        const html = generateResultHtml(music);
        writeToHttpResponse(response, html, 200, "text/html")
    };
}

console.log ("create server");
http.createServer((request, response) => {
    console.log("recived request");
    db.all("SELECT * FROM Music;", processMusic(response));
}).listen(3000);

console.log("Server running at http://localhost:3000/");
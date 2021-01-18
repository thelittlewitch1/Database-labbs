const http = require("http");
const sqlite3 = require("sqlite3").verbose();
const fs = require("fs");

const httpPort = 3000;
const htmlTemplate = './src/ajax-demo.html';
const dbFilename = 'Music.sqlite';

const db = new sqlite3.Database(dbFilename);

http.createServer((request, response) => {

    const url = request.url;
    console.log("Received request: " + url);

    if (url === "/") {
        handleIndexPage(response);
    } else if (url.startsWith("/data")) {
        handleDataFiltering(url, response);
    } else {
        writeToHttpResponse(response, 404, "Page not found!", "text/html; charset=UTF-8")
    }

}).listen(httpPort);

function handleIndexPage(response) {
    fs.readFile(htmlTemplate, (error, content) => {
        if (!error) {
            writeToHttpResponse(response, 200, content);
        } else {
            console.error("Cannot read html template from file: " + error.message);
            writeToHttpResponse(response, 500, error.message);
        }
    });
}

function handleDataFiltering(url, response) {
    const filter = new URL(url, "http://example.com").searchParams.get("filter");
    console.log("Filter = " + filter);
    if (filter) {
        retrieveFilteredMusics(filter, response);
    } else {
        retrieveNonFilteredMusics(response);
    }

}

function retrieveFilteredMusics(filter, response) {
    const statement = db.prepare(
        `SELECT * FROM Music WHERE track LIKE '%${filter}%' 
                                    or singer LIKE '%${filter}%' 
                                    or album LIKE '%${filter}%' 
                                    ORDER BY track;
            `
    );
    statement.all((error, rows) => processLoadedMusic(error, rows, response, "Serialized filtered tracks: "));
}

function retrieveNonFilteredMusics(response) {
    db.all("SELECT * FROM Music ORDER BY track;", (error, rows) => processLoadedMusic(
        error, rows, response, "Serialized non filtered tracks: "
    ));
}

function processLoadedMusic(error, rows, response, logMessage) {
    if (error) {
        console.log("Cannot retrieve tracks form db: " + error.message)
        return;
    }
    const json = JSON.stringify(rows);
    console.log(logMessage + json);
    writeToHttpResponse(response, 200, json, "application/json; charset=UTF-8");
}

function writeToHttpResponse(response, statusCode, content, contentType = "text/html; charset=UTF-8") {
    response.writeHead(statusCode, {"Content-Type": contentType})
    response.end(content);
}

console.log("Started server at http://localhost:" + httpPort + "/");
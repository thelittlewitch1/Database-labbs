const http = require("http");
const cassandra = require("cassandra-driver");
const fs = require("fs");

const httpPort = 3000;
const htmlTemplate = './src/ajax-demo.html';

const client = new cassandra.Client({
    contactPoints: ['127.0.0.1'],
    keyspace: 'tracks_keyspace',
    localDataCenter: 'datacenter1'
});

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
        retrieveFilteredTracks(filter, response);
    } else {
        retrieveNonFilteredTracks(response);
    }

}

function retrieveFilteredTracks(filter, response) {

    const nameFiltered = client.execute(`SELECT * FROM Music WHERE name LIKE '%${filter}%';`);
    const groupFiltered = client.execute(`SELECT * FROM Music WHERE singer LIKE '%${filter}%';`);
    const albumFiltered = client.execute(`SELECT * FROM Music WHERE album LIKE '%${filter}%';`)

    Promise.all([nameFiltered, groupFiltered, albumFiltered])
        .then(results => {
            const named = results[0].rows;
            const grouped = results[1].rows;
            const albumed =  results[2].rows;
            const allTracks = named.concat(grouped).concat(albumed);
            const uniqueTracks = allTracks.filter((v, i, a) => a.findIndex(t => (t.name === v.name && t.singer === v.singer)) === i)
            processLoadedTrack(null, uniqueTracks, response, "Serialized filtered tracks: ");
        })
}

function retrieveNonFilteredTracks(response) {
    client.execute("SELECT * FROM Music;", (error, result) => processLoadedTrack(
        error, result.rows, response, "Serialized non filtered tracks: "
    ))
}

function processLoadedTrack(error, rows, response, logMessage) {
    if (error) {
        console.log("Cannot retrieve tracks form db: " + error.message)
        return;
    }
    console.log("Retrieved rows from db: " + rows);
    const json = JSON.stringify(rows);
    console.log(logMessage + json);
    writeToHttpResponse(response, 200, json, "application/json; charset=UTF-8");
}

function writeToHttpResponse(response, statusCode, content, contentType = "text/html; charset=UTF-8") {
    response.writeHead(statusCode, {"Content-Type": contentType})
    response.end(content);
}

console.log("Started server at http://localhost:" + httpPort + "/");

// run this query on newly created cassandra db and before executing this program
// CREATE KEYSPACE tracks_keyspace WITH REPLICATION = {'class': 'SimpleStrategy', 'replication_factor': 1};

client.connect()
    .then(() => client.execute("CREATE TABLE IF NOT EXISTS Music (id int, name TEXT, singer TEXT, album TEXT, len int, PRIMARY KEY (id, name, singer)) WITH CLUSTERING ORDER BY (name ASC, singer ASC);"))
    .then(() => client.execute("CREATE CUSTOM INDEX IF NOT EXISTS name_idx ON Music (name) USING 'org.apache.cassandra.index.sasi.SASIIndex' WITH OPTIONS = {'mode': 'CONTAINS', 'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer', 'case_sensitive': 'false'};"))
    .then(() => client.execute("CREATE CUSTOM INDEX IF NOT EXISTS grp_idx ON Music (singer) USING 'org.apache.cassandra.index.sasi.SASIIndex' WITH OPTIONS = {'mode': 'CONTAINS', 'analyzer_class': 'org.apache.cassandra.index.sasi.analyzer.StandardAnalyzer', 'case_sensitive': 'false'};"))
    .then(() => client.execute("INSERT INTO Music (id, name, singer, album, len) VALUES (1, 'Primo Victoria', 'Sabaton', 'Primo Victoria', 251);"))
    .then(() => client.execute("INSERT INTO Music (id, name, singer, album, len) VALUES (2, 'Primo Victoria', 'RADIO TAPOK', 'unknown', 251);"))
    .then(() => client.execute("INSERT INTO Music (id, name, singer, album, len) VALUES (3, 'Stalingrad', 'Sabaton', 'Primo Victoria', 318);"))
    .then(() => client.execute("INSERT INTO Music (id, name, singer, album, len) VALUES (4, 'No bullet fly', 'Sabaton', 'Heroes on Tour', 250);"))
    .then(() => client.execute("INSERT INTO Music (id, name, singer, album, len) VALUES (5, 'RADIO', 'Rammstein', 'RADIO', 277);"))
    .then(() => client.execute("INSERT INTO Music (id, name, singer, album, len) VALUES (6, 'Demons are girls best friend', 'Powerwolf', 'Best of the Blessed', 258);"))
    .then(() => client.execute("INSERT INTO Music (id, name, singer, album, len) VALUES (7, 'Demons are girls best friend', 'RADIO TAPOK', 'unknown', 258);"));
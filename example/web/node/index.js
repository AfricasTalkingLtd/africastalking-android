/**
 * Created by aksalj on 8/10/17.
 */

const restify = require('restify');
const ATServer = require('../../../libs/server/node/lib/server');



function respond(req, res, next) {
    res.send('hello ' + req.params.name);
    next();
}

const server = restify.createServer();
server.get('/hello/:name', respond);

server.listen(3001, "0.0.0.0", function() {
    const port = 35897;
    const atServer = new ATServer({
        apiKey: "3cb2185af3e13541cfc38047b463a39e2a255b9ca9e781e9d923ec668a21a07f",
        username: "sandbox",
        format: "json",
        sandbox:true
    });
    atServer.addSipCredentials("android", "salama", "192.168.0.7", "udp");
    atServer.start({
        port,
        insecure: true
    });

    console.log('%s listening at %s', server.name, server.url);
    console.log('%s listening at %s on %s', "SDK Server", server.address().address, port);
});
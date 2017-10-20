/**
 * Created by aksalj on 8/10/17.
 */

const restify = require('restify');
const Server = require('../../../libs/server/node/server');


function respond(req, res, next) {
    res.send('hello ' + req.params.name);
    next();
}

const app = restify.createServer();
app.get('/hello/:name', respond);

app.listen(3001, "0.0.0.0", function() {
    const port = 35897;
    const server = new Server({
        apiKey: "6e44229611d255b5d58f80d057fc2da8708aa95dad0aba6843314fdac3e2d75c",
        username: "sandbox"
    });
    server.addSipCredentials("test.aksalj", "DOPx_9bf185d689", "ke.sip.africastalking.com", 5060, "udp");
    server.start({
        port,
        insecure: true
    });

    console.log('%s listening at %s', app.name, app.url);
    console.log('%s listening at %s on %s', "SDK Server", app.address().address, port);
});
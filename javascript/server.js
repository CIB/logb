var http = require('http');
var test = require('./test');

http.createServer(function (req, res) {
  var html = buildHtml(req);

  res.writeHead(200, {
    'Content-Type': 'text/html',
    'Content-Length': html.length,
    'Expires': new Date().toUTCString()
  });
  res.end(html);
}).listen(8080);

function entityToHTML(entity) {
    var rval = "";
    if(entity.type === "Statement") {
        rval += "<table><tr><td>" + entity.statementType + "</td><td><table>";
        entity.parameters.forEachPair(function(key, value) {
            rval += "<tr>";
            rval += "<td>" + key + "</td>";
            rval += "<td>" + entityToHTML(value) + "</td>";
            rval += "</tr>";
        });
        rval += "</table></td></tr></table>";
    } else if(entity.type === "Variable") {
        rval = "<i>" + entity.name + "</i>";
    } else {
        rval = "?";
    }
    return rval;
}

function blockToHTML(block) {
    var rval = "<table>";
    var i = 0;
    block.statements.forEach(function(statement) {
        rval += "<tr><td>" + i + "</td><td>"+entityToHTML(statement)+"</td></tr>";
        i += 1;
    });
    rval += "</table>";
    return rval;
}

function buildHtml(req) {
  var header = '';
  var body = '';

  // concatenate header string
  // concatenate body string

  body += blockToHTML(test.block);

  return '<!DOCTYPE html>'
       + '<html><header>' + header + '</header><body>' + body + '</body></html>';
};


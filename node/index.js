/*
 * Our node server index.js file for use with our
 * duluthbikes app and the corresponding 
 * dashboard 
 *
 */

// Body Parser for icoming http requests 
var bodyParser = require('body-parser');
// require express
var express = require('express');

// express framework for handling http requests
var app = express();
var http = require('http').Server(app);
var io = require('socket.io')(http);
/*
 * varaible area for any required varaibles we might use
 */

var routeHistory = [];

// setting the port for the app system to use
app.set("port",23405);

// this section tells the body parser what type of data to expect
// for now it is mainly json
// Set limits in order to send images.
app.use(bodyParser.urlencoded({
    limit: '50mb',
    extended: true
}));
app.use(bodyParser.json({limit: '50mb'}));

//Include all files in the public folder to serve to the website
app.use(express.static('public'));

//********MangoDB*******
// Connect to the mongo module
var mongodb = require('./mongoDB.js')();
console.log(mongodb);

app.get('/heatmapfiles',function(req,res){
    res.sendFile(__dirname + '/public/node_modules/heatmap.js/build/heatmap.js');
});

app.get('/heatmapfilesgmaps',function(req,res){
    res.sendFile(__dirname + '/public/node_modules/heatmap.js/plugins/gmaps-heatmap/gmaps-heatmap.js');
});

app.get('/fullRide',function(req,res){
    var rides = printRides('FullRidesRecorded',function(result){
	res.write('<HTML><head><title>Duluth Bikes DashBoard</title></head><BODY>'
		  +'<H1>Full Rides.</H1>');
	result.reverse();
	res.write(JSON.stringify(result));
	res.send();
    });
    console.log('full ride request');
}); 

app.get('/fulllatlng',function(req,res){
    var rides = printRides('FullLatLngsRecorded',function(result){
        res.write('<HTML><head><title>Duluth Bikes DashBoard</title></head><BODY>'
                  +'<H1>Full Rides.</H1>');
        result.reverse();
        res.write(JSON.stringify(result));
        res.send();
    });
    console.log('full ride request');
});

// this next section is for our GET, POST, PUT, DELETE routes
// the first one is the default dashboard route
//
app.get('/raw', function(request, response) {

    //when using Mongo
    var str = printDatabase('RideHistory', function(result) {
	response.write('<HTML><head><title>Duluth Bikes DashBoard</title></head><BODY>' 
		       + '<H1>Duluth Bikes Route Data.</H1>')
	result.reverse();
	response.write(JSON.stringify(result,['point','lat','lng'],'\n') + '</BODY></HTML>');
	response.send();
    });
    
    console.log('DashBoard request received!');
});

app.get('/rides',function(request,response){
    response.sendFile(__dirname +'/public/ride.html');
    printRides('FullRidesRecorded',function(doc){
        io.emit('FullRidesRecorded',doc);
    });
});

app.get('/maps',function(req,res){
    res.sendFile(__dirname + '/public/maps.html');
    printRides('FullRidesRecorded',function(doc){
	io.emit('FullRidesRecorded',doc);
    });
});

app.get('/',function(req,res){
    res.sendFile(__dirname + '/public/duluthBikesBootstrap.html');
});

app.post('/postlocalleaderboard', function(request,response) {

    if (!request.body) return response.sendStatus(400);

    var position = {
	'pos':request.body.pos
    }
    var statData = {
	'date':request.body.date,
	'distance':request.body.distance,
	'time':request.body.time,
	'name':request.body.name
    }
    insertLocalLeaderboard(position,statData);
    console.log('Post Request: postlocalleaderboard');
    response.sendStatus(200);
});

app.post('/postgloballeaderboard', function(request,response) {
    
    if (!request.body) return response.sendStatus(400);
    
    var position = {
	'pos':request.body.pos
    }
    var statData = {
	'date':request.body.date,
	'distance':request.body.distance,
	'time':request.body.time,
	'name':request.body.name
    }
    insertGlobalLeaderboard(position,statData);
    console.log('Post Request: postgloballeaderboard');
    response.sendStatus(200);
});

app.post('/postroute', function(request, response) {

    if (!request.body)return response.sendStatus(400);

    var routeData = {'lat':request.body.lat,
		     'lng':request.body.lang};

    var date = {'Date':request.body.time};

    //Mongo
    insertRoute(routeData);

    console.log('Post Request: postroute');

    response.sendStatus(200);
});

app.post('/postfinish',function(req,res){

    if(!req.body)return res.sendStatus(400);	

    var arr = [];
    arr = req.body.ride;
    insertFullRide(arr);
    if(req.body.heat){
	var latlng = [];
	latlng = req.body.heat;
	insertLatLng(latlng);
	
	io.emit('FullRidesRecorded',doc);
    }
    console.log('Post Full Ride');

    res.sendStatus(200);
});


app.get('/usernames', function(req,res){
    var users = printUsers('UsersSaved',function(result){
        res.write('<HTML><head><title>Duluth Bikes DashBoard</title></head><BODY>'
		  +'<H1>Users </H1>');
        res.write(JSON.stringify(result));
        res.send();
    });
    console.log('users request');
});

app.get('/localleaderboard', function(req, res) {
    var users = printLocalLeaderboard('localLeaderboard',function(result){
	res.write(JSON.stringify(result));
	res.send();
    });
    console.log('local leaderboard request');
});

app.get('/globalleaderboard', function(req, res) {
    var users = printLocalLeaderboard('globalLeaderboard',function(result){
	res.write(JSON.stringify(result));
	res.send();
    });
    console.log('global leaderboard request');
});

app.post('/loginAttempt', function(req,res){
    if(!req.body.userName || !req.body.passWord) return res.sendStatus(400);
    var userObj = { 'user':req.body.userName,
		    'pass':req.body.passWord };
    console.log('loginAttempt');
    if(checkCreds(userObj))
    {   console.log("wtf")
	res.sendStatus(200);
    }
    else {
	res.sendStatus(300);
	console.log("help")
    }
    console.log("success")
});

app.post('/newAccount', function(req,res){
    if(!req.body) return res.sendStatus(400);
    var userObj = { 'user':req.body.userName,
		    'pass':req.body.passWord,
		    'email':req.body.email};
    if(newAccount(userObj))
    {   console.log("wtf")
	res.sendStatus(200);
    }
    else {
	res.sendStatus(300);
	console.log("help")
    }
    console.log("success")
});

app.post('/postpicture', function(req,res){
    if(!req.body.userName || !req.body.passWord) return res.sendStatus(400);
    var picObj = { 'location':req.body.loc,
		   'description':req.body.description,
		   'picture':req.body.picture };
    insertPicture(picObj);
    console.log('Post Picture');
    res.send();

});

app.get('/pictures',function(req,res){
    
    // 1.// THE FOLLOWING IS FOR ACCESSING DB. ( CURRENTLY DOES NOT ACCESS - PICS HARDCODED.)
    res.sendFile(__dirname +'/public/threepics.html'); // Will try and use if we can use Canvas element - HTML5
    printPictures('PicturesSaved',function(doc){
	io.emit('PicturesSaved',doc);
    });

    // 2.// THE FOLLOWING WILL PRINT THE RAW PICTURE DATA STORED IN DB
    //var pics = printPictures('PicturesSaved',function(result){
    //    res.write('<HTML><head><title>Duluth Bikes DashBoard</title></head><BODY>'
    //        +'<H1>Pictures.</H1>');
    //        res.write(JSON.stringify(result));
    //        res.send();
    //    });
    console.log('picture request');
});

app.get('/deleteallthepictures',function(res,req) {
    console.log('deleted all pictures attempt');

    deleteAll('PicturesSaved', function(result) {
	if(result==true)console.log("deleted all pictures");
	else console.log("Did not work");
    });
});


app.get('/deletealltherides',function(res,req){
    console.log('deleted all rides atempt');
    
    deleteAll('FullLatLngsRecorded',function(result){
	if(result==true)console.log("deleted all");
	else console.log("didnt work");
    });
});

app.get('/deleteAllUsers',function(res,req){
    console.log('deleted all users atempt');
    
    deleteAll('UsersSaved',function(result){
	if(result==true)console.log("deleted all");
	else console.log("didnt work");
    });
});

app.get('/resetLocalLeaderboard',function(res,req){
    console.log('deleted all local leaderboard atempt');
    
    deleteAll('localLeaderboard',function(result){
	if(result==true)console.log("deleted all");
	else console.log("didnt work");
    });
});

app.get('/resetGlobalLeaderboard',function(res,req){
    console.log('deleted all global leaderboard atempt');
    
    deleteAll('globalLeaderboard',function(result){
	if(result==true)console.log("deleted all");
	else console.log("didnt work");
    });
});




io.on('connection',function(socket){
    console.log('a socket io connection');

    printRides('FullRidesRecorded',function(doc){
	socket.emit('FullRidesRecorded',doc);
    });
});

function convertBase64ToImage(){

}

app.get('/logout', (req, res)=>{
    res.write("logout");
});



// this last section is to start the app and start listening on 
// the given port for requests
//



http.listen(app.get("port"),function(){
    console.log('duluth bikes node listening on port:',app.get("port"));
});


